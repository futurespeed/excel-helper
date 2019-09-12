package org.fs.excel;

import org.apache.commons.io.IOUtils;
import org.fs.excel.parse.ParseContext;
import org.fs.excel.parse.PoiExcelParser;
import org.fs.excel.parse.PoiSaxExcelParser;
import org.fs.excel.parse.event.ParseEventAdapter;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;

public class TestExcelParser {
    @Test
    public void testParse() throws Throwable {
        PoiExcelParser parser = new PoiSaxExcelParser();
        InputStream in = null;
        try {
            in = TestExcelParser.class.getResourceAsStream("/demo.xlsx");
            ParseContext parseContext = parser.builder()
                    .inputStream(in)
                    .beanClass(TestBean1.class)
                    .continueOnError(true)
                    .beginRow(3)
                    .maxRow(5000)
                    .pageSize(10)
                    .eventHandler(new ParseEventAdapter() {
                        @Override
                        public void onPageChange(ParseContext context) {
                            System.out.print("page-" + ((PoiExcelParser.PoiExcelParserWorkData) context.getWorkData()).getCurrentPage() + ": ");
                            List list = context.getResultData().getDataList();
                            System.out.println(list);
                            list.clear();
                        }

                        @Override
                        public void onFinish(ParseContext context) {
                            List list = context.getResultData().getDataList();
                            System.out.println("finish: " + list);
                            list.clear();
                        }
                    })
                    .getParseContext();
            parser.parse(parseContext);
            System.out.println(parseContext.getResultData().getDataList().size());
            System.out.println(parseContext.getResultData().getErrorList().size());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static class TestBean1 {
        @ExcelColumn.SeqField
        private long seq;

        @ExcelColumn.SeqField(begin = 0)
        private int seq2;

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

        public long getSeq() {
            return seq;
        }

        public void setSeq(long seq) {
            this.seq = seq;
        }

        public int getSeq2() {
            return seq2;
        }

        public void setSeq2(int seq2) {
            this.seq2 = seq2;
        }

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
