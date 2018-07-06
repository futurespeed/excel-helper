package org.fs.excel.parse.validate;

public class RowErrorInfo {
    private long row;
    private String msg;

    public long getRow() {
        return row;
    }

    public void setRow(long row) {
        this.row = row;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
