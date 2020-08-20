package com.lizehao.community.community;

import java.util.Random;

public class test {
    public static void main(String[] args) {
        Random random = new Random();
        for(int i = 0 ; i < 5 ; i++){
            int a = random.nextInt(135) + 1;
            System.out.println("行数： " + a/6 + " 列数：" + (a % 6 +1) );
        }

    }
}
