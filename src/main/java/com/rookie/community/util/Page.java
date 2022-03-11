package com.rookie.community.util;

/**
 * 封装分页相关的信息
 */
public class Page {
    //    当前的页面
    public int current = 1;
    //    显示上限
    public int limit = 10;
    //    数据总数
    public int rows;
    //    查询路径（用于复用分页连接）
    public String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1 ) {
            this.current = current;
        }
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
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //    获取当前页的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    //    获取总页数
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    //   起始页标
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    //    结束页标
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
