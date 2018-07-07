package org.fs.excel.writer;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.fs.excel.writer.mapper.BeanRowMapper;
import org.fs.excel.writer.mapper.RowMapper;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class PoiExcelWriter extends OutputStreamExcelWriter {

    public PoiExcelWriterContextBuilder builder(){
        return PoiExcelWriterContextBuilder.build();
    }

    private PoiExcelWriterWorkData getWorkData(WriteContext writeContext){
        return (PoiExcelWriterWorkData) writeContext.getWorkData();
    }

    public void write(WriteContext writeContext) {
        try {
            Workbook wb = new SXSSFWorkbook();
            Sheet sheet = wb.createSheet();
            List<Object> dataList = getWorkData(writeContext).getDataList();
            RowMapper rowMapper = getWorkData(writeContext).getRowMapper();
            List<String> columnList = rowMapper.getColumnList();
            for (String column : columnList) {
                int columnIndex = Integer.valueOf(column);
                int width = rowMapper.getColumnWidth(writeContext, columnIndex);
                sheet.setColumnWidth(columnIndex, width * 256);
            }
            long rowIndex = 0;
            if(getWorkData(writeContext).isWriteHeader()){
                writeHeader(writeContext, wb, sheet);
                rowIndex++;
            }
            for (Object data : dataList) {
                Row row = sheet.createRow((int) rowIndex);
                for (String column : columnList) {
                    Cell cell = row.createCell(Integer.valueOf(column));
                    Object value = rowMapper.getValue(writeContext, data, Long.valueOf(column));
                    setCellValue(cell, value);
                }
                rowIndex++;
            }
            OutputStream out = getWorkData(writeContext).getOutputStream();
            wb.write(out);
            out.flush();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if (null == value) {
            return;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void writeHeader(WriteContext writeContext, Workbook wb, Sheet sheet){
        RowMapper rowMapper = getWorkData(writeContext).getRowMapper();
        List<String> columnList = rowMapper.getColumnList();
        Row row = sheet.createRow(0);
        XSSFCellStyle titleStyle = getTitleCellStyle(wb);
        for (String column : columnList) {
            int columnIndex = Integer.valueOf(column);
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(rowMapper.getColumnName(writeContext, columnIndex));
            columnIndex++;
        }
    }

    protected XSSFCellStyle getTitleCellStyle(Workbook wb) {
        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(159, 213, 183)));
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setColor(HSSFColor.BROWN.index);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleStyle.setFont(font);
        return titleStyle;
    }

    public static class PoiExcelWriterWorkData extends OutputStreamExcelWriter.OutputStreamExcelWriterWorkData {
    }

    public static class PoiExcelWriterContextBuilder{
        private WriteContext writeContext = new WriteContext();

        private PoiExcelWriterContextBuilder(){}

        public static PoiExcelWriterContextBuilder build(){
            PoiExcelWriterContextBuilder builder = new PoiExcelWriterContextBuilder();
            builder.writeContext.setWorkData(new PoiExcelWriterWorkData());
            return builder;
        }

        public PoiExcelWriterContextBuilder outputStream(OutputStream outputStream){
            ((PoiExcelWriterWorkData) writeContext.getWorkData()).setOutputStream(outputStream);
            return this;
        }

        public PoiExcelWriterContextBuilder beanClass(Class<?> clazz) {
            ((PoiExcelWriterWorkData) writeContext.getWorkData()).setRowMapper(new BeanRowMapper(clazz));
            return this;
        }

        public PoiExcelWriterContextBuilder rowMapper(RowMapper rowMapper){
            ((PoiExcelWriterWorkData) writeContext.getWorkData()).setRowMapper(rowMapper);
            return this;
        }

        public PoiExcelWriterContextBuilder writeHeader(boolean writeHeader){
            ((PoiExcelWriterWorkData) writeContext.getWorkData()).setWriteHeader(writeHeader);
            return this;
        }

        public PoiExcelWriterContextBuilder dataList(List dataList){
            ((PoiExcelWriterWorkData) writeContext.getWorkData()).setDataList(dataList);
            return this;
        }

        public WriteContext getWriteContext() {
            return writeContext;
        }
    }
}
