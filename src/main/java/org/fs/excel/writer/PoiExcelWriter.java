package org.fs.excel.writer;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.fs.excel.writer.mapper.RowMapper;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class PoiExcelWriter extends OutputStreamExcelWriter {

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
            long rowIndex = 0;//FIXME
            for (Object data : dataList) {
                Row row = sheet.createRow((int) rowIndex);
                for (String column : columnList) {
                    Cell cell = row.createCell(Integer.valueOf(column) - 1);
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

    private void writeTitle(WriteContext writeContext, Workbook wb, Sheet sheet){
        RowMapper rowMapper = getWorkData(writeContext).getRowMapper();
        List<String> columnList = rowMapper.getColumnList();
        Row row = sheet.createRow(0);
        XSSFCellStyle titleStyle = getTitleCellStyle(wb);
        for (String column : columnList) {
            int width = 200;
            int columnIndex = Integer.valueOf(column) - 1;
            sheet.setColumnWidth(columnIndex, width * 256);
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(column);//FIXME
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
        private boolean writeTitle;
    }
}
