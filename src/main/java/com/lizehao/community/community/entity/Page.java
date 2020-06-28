package com.lizehao.community.community.entity;

//封装分页相关信息，利用这个对象让服务器接收页面传入的信息

public class Page {


    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //查数据总数(用来计算总页数)
    private int rows;
    //查询路径（点页面选择的连接）
    private String path;

    /**
     * setter getter方法
     */

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1 )
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {

        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0)
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //获取当前页起始行，因为selectDiscussPosts方法中给数据库传的是当前页起始行
    public int getOffset(){
        return (current-1)*limit;
    }
    /**
     * 返回总页数
     */
    public int getTotal() {

        if (rows % limit == 0){
            return rows / limit;
        }else {
            return rows / limit +1;
        }
    }

    /**
     * 返回显示的页码范围（当前页的附近页）,From起始，To结束
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    public int getTo(){

        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }

}
