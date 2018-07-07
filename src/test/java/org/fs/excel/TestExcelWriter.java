package org.fs.excel;

import org.apache.commons.io.IOUtils;
import org.fs.excel.writer.PoiExcelWriter;
import org.fs.excel.writer.WriteContext;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

public class TestExcelWriter {
    @Test
    public void testWrite() throws Exception {
        List<TestBean2> list = new ArrayList<TestBean2>();
        for (int i = 0; i < 100; i++) {
            TestBean2 bean = new TestBean2();
            bean.setCol1(i + "");
            bean.setCol2(UUID.randomUUID().toString());
            bean.setCol3("sdf");
            list.add(bean);
        }

        List<Map> list2 = new ArrayList<Map>();
        for (int i = 0; i < 100; i++) {
            Map map = new HashMap();
            map.put("col1", i + "");
            map.put("col2", UUID.randomUUID().toString());
            map.put("col3", "map1234");
            list2.add(map);
        }

        PoiExcelWriter writer = new PoiExcelWriter();
        OutputStream out = new FileOutputStream(new File("d:/temp/1.xlsx"));
        try {
            WriteContext context = writer.builder()
                    .beanClass(TestBean2.class)
                    .outputStream(out)
                    .dataList(list2)
                    .getWriteContext();
            writer.write(context);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static class TestBean2 {

        @ExcelColumn(seq = 1, name = "c1", width = 10)
        private String col1;

        @ExcelColumn(seq = 2, name = "c2", width = 50)
        private String col2;

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
