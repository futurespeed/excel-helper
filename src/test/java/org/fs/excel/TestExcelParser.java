package org.fs.excel;

import org.apache.commons.io.IOUtils;
import org.fs.excel.parse.ParseContext;
import org.fs.excel.parse.PoiExcelParser;
import org.fs.excel.parse.event.ParseEventAdapter;
import org.fs.excel.parse.mapper.BeanRowMapper;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class TestExcelParser {
	@Test
	public void testParse() throws Throwable{
		PoiExcelParser parser = new PoiExcelParser();
        InputStream in = null;
        try{
            in = new FileInputStream("D:/data/require/营销平台二期优化功能清单-20170705.xlsx");
            ParseContext parseContext = parser.builder()
                    .inputStream(in)
                    .pageSize(10)
                    .rowMapper(new BeanRowMapper(TestBean1.class))
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
                    }).getParseContext();
            parser.parse(parseContext);
            System.out.println(parseContext.getResultData().getDataList());
        }finally {
            IOUtils.closeQuietly(in);
        }
	}
}
