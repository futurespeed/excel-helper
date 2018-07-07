package org.fs.excel;

import org.apache.commons.io.IOUtils;
import org.fs.excel.parse.ParseContext;
import org.fs.excel.parse.PoiExcelParser;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestExcelParser {
    @Test
    public void testParse() throws Throwable {
        PoiExcelParser parser = new PoiExcelParser();
        InputStream in = null;
        try {
            in = new FileInputStream("D:/data/require/营销平台二期优化功能清单-20170705.xlsx");
            ParseContext parseContext = parser.builder()
                    .inputStream(in)
                    .beanClass(TestBean1.class)
                    .continueOnError(true)
                    .beginRow(2)
                    .maxRow(5000)
//                    .pageSize(10)
//                    .eventHandler(new ParseEventAdapter() {
//                        @Override
//                        public void onPageChange(ParseContext context) {
//                            System.out.print("page-" + ((PoiExcelParser.PoiExcelParserWorkData) context.getWorkData()).getCurrentPage() + ": ");
//                            List list = context.getResultData().getDataList();
//                            System.out.println(list);
//                            list.clear();
//                        }
//
//                        @Override
//                        public void onFinish(ParseContext context) {
//                            List list = context.getResultData().getDataList();
//                            System.out.println("finish: " + list);
//                            list.clear();
//                        }
//                    })
                    .getParseContext();
            parser.parse(parseContext);
            System.out.println(parseContext.getResultData().getDataList());
            System.out.println(parseContext.getResultData().getErrorList());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static class TestBean1 {
        @NotNull
        @Pattern(regexp = "^[\\d]{1,32}$")
        @ExcelColumn(seq = 1, name = "c1")
        private String col1;

        @NotNull(message = "列2不能为空")
        @Pattern(regexp = "^[\\d]{1,32}$", message = "列2格式错误")
        @ExcelColumn(seq = 2, name = "c2")
        private String col2;

        @NotNull
        @Pattern(regexp = "^[\\d]{1,32}$")
//        @Spel("@bean1.check(#root)")
        @ExcelColumn(seq = 3, name = "c3")
        private String col3;

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

        public String getCol3() {
            return col3;
        }

        public void setCol3(String col3) {
            this.col3 = col3;
        }
    }
}
