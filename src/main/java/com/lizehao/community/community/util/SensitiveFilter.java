package com.lizehao.community.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换常量
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //注解表示这是初始化方法
    @PostConstruct
    public void init() {
        //编译以后所有class和静态资源都会加载到target下
        //.getClass().getClassLoader()是获取调用对象的类加载器
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //把字节流转换成字符流，并且用缓冲
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            //敏感词文件是一行一个敏感词，
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //读到敏感词，添加到前缀树
                this.addKeyword(keyword);

            }


        } catch (IOException e){
            logger.error("读取敏感词文件失败：" + e.getMessage());
        }


    }



    /**
     *把一个敏感词添加到前缀树
     */
    private void addKeyword(String keyword) {
        //相当于一个临时指针，先指向根节点
        TrieNode temptNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);

            //添加子节点指针
            //子节点指针指向 临时指针的 c子节点
            //c子节点不存在就初始化添加子节点，存在就到下一层添加c后面的字符
            TrieNode subNode = temptNode.getSubNode(c);

            if(subNode == null) {
                subNode = new TrieNode();
                temptNode.addSubNode(c, subNode);
            }

            //已经有字节点，或者已经初始化了c字符，所以进入下一层
            temptNode = subNode;

            //设置敏感词结束标志
            if (i == keyword.length() - 1) {
                temptNode.setKeyWoedEnd(true);
            }
        }


    }

    /**
     * 敏感词过滤
     * @param text 传进来一个字符串
     * @return 检索前缀树，把敏感词替换后返回
     * */
    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }
        //前缀树指针
        TrieNode temptNode = rootNode;
        //字符串两个索引
        int begin = 0;
        int end = 0;
        //记录返回的字符串
        StringBuilder sb = new StringBuilder();

        //end先到结尾，所以用end遍历
        //树指针会一直指向end所指字符对应的节点
        while (end <  text.length()) {

            char c = text.charAt(end);
            //跳过符号
            if (isSymbol(c)) {
                if (temptNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //不论end在开头或者中间，都向下走一步
                end++;
                //continue指跳过单次循环，而不是跳过整个循环体
                continue;
            }

            temptNode = temptNode.getSubNode(c);
            //tempt有三种情况，null、不为null但不是结尾、不为null且是结尾
            //不为null但不是结尾的情况，树要继续往下走
            if (temptNode == null) {
                //节点是null
                sb.append(text.charAt(begin));
                end = ++begin;
                //树指针归位
                temptNode = rootNode;
            } else if (temptNode.isKeyWordEnd()) {
                //找到敏感词
                sb.append(REPLACEMENT);
                begin = ++end;
                temptNode = rootNode;

            } else {
                //不是null也不是结束节点，往下走
                //树指针会一直指向end对应的节点
                end++;
                }

            }
        //加最后几个字符
        sb.append(text.substring(begin));

        return sb.toString();

    }

    /**
     *判断字符是否为特殊符号
     */
    private boolean isSymbol(Character c) {
        //isAsciiAlphanumeric()是判断是不是合法字符
        //不是合法字符并且不是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0X2E80 || c > 0x9FFF );
    }


    /**
     * 定义前缀树
     * 这个类描述的是一个节点
     * 这个结构只在这里用，所以可以用内部类
     */
    private class TrieNode {

        //关键词结束标识
        private boolean isKeyWordEnd = false;

        //子节点(key是子字符，value是子节点）
        private Map<Character,TrieNode> subNodes = new HashMap<>();



        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWoedEnd(boolean keyWoedEnd) {
            isKeyWordEnd = keyWoedEnd;
        }

        //添加子节点方法
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
