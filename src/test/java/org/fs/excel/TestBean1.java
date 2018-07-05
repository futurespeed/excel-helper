package org.fs.excel;

import org.fs.excel.parse.mapper.ExcelBean;

public class TestBean1 {
    @ExcelBean(seq = 1)
    private String col1;

    @ExcelBean(seq = 2)
    private String col2;

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }
}
