package org.fs.excel.parse;

import org.apache.poi.ss.usermodel.*;
import org.fs.excel.MessageProvider;
import org.fs.excel.parse.event.ParseEventHandler;
import org.fs.excel.parse.mapper.BeanRowMapper;
import org.fs.excel.parse.mapper.RowMapper;
import org.fs.excel.parse.validate.BeanRowValidator;
import org.fs.excel.parse.validate.RowValidator;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PoiExcelParser extends InputStreamExcelParser {

    public PoiExcelParserContextBuilder builder() {
        return PoiExcelParserContextBuilder.build();
    }

    protected PoiExcelParserMetaData getMetaData(ParseContext parseContext) {
        return (PoiExcelParserMetaData) parseContext.getMetaData();
    }

    protected PoiExcelParserWorkData getWorkData(ParseContext parseContext) {
        return (PoiExcelParserWorkData) parseContext.getWorkData();
    }

    public PoiExcelParserResultData getResultData(ParseContext parseContext) {
        return (PoiExcelParserResultData) parseContext.getResultData();
    }

    protected void read(ParseContext parseContext, InputStream in) {
        try {
            Workbook wb = WorkbookFactory.create(in);
            Sheet sheet = wb.getSheetAt(getMetaData(parseContext).getSheetIdx());
            getWorkData(parseContext).setSheet(sheet);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected long getRowSize(ParseContext parseContext) {
        Sheet sheet = getWorkData(parseContext).getSheet();
        Long rowSize = (long) sheet.getLastRowNum();
        if (rowSize >= 0) {
            rowSize++;
        }
        getWorkData(parseContext).setRowSize(rowSize);
        return rowSize;
    }

    protected long getColumnSize(ParseContext parseContext) {
        long columnSize = getWorkData(parseContext).getColumnSize();
        if (columnSize > 0) {
            return columnSize;
        }
        columnSize = getMetaData(parseContext).getColumnSize();
        if (columnSize > 0) {
            getWorkData(parseContext).setColumnSize(columnSize);
            return columnSize;
        }
        Sheet sheet = getWorkData(parseContext).getSheet();
        Row nameRow = sheet.getRow(1);
        columnSize = (long) nameRow.getLastCellNum();
        getWorkData(parseContext).setColumnSize(columnSize);
        return columnSize;
    }

    protected boolean rowRead(ParseContext parseContext) {
        boolean continueOnError = getMetaData(parseContext).isContinueOnError();
        long rowIdx = getWorkData(parseContext).getCurrentRowIdx();
        Sheet sheet = getWorkData(parseContext).getSheet();
        List<Object> list = getResultData(parseContext).getDataList();
        List<Object> errorList = getResultData(parseContext).getErrorList();
        Row row = sheet.getRow((int) rowIdx);
        RowMapper mapper = getMetaData(parseContext).getRowMapper();
        RowValidator validator = getMetaData(parseContext).getRowValidator();
        Object rowItem = mapper.newRowItem(parseContext);
        Object validateResult = null;
        for (long i = 0, len = getColumnSize(parseContext); i < len; i++) {
            Cell cell = row.getCell((int) i);
            Object value = columnRead(cell, i);
            if (validator != null) {
                validateResult = validator.validateColumn(parseContext, rowIdx, i, value, rowItem, validateResult);
                if (validateResult != null) {
                    parseContext.setResult(ParseContext.RESULT_ERROR);
                    if (!continueOnError) {
                        break;
                    }
                }
            }
            mapper.setValue(parseContext, rowIdx, i, rowItem, value);
        }
        if (validator != null) {
            validateResult = validator.validateRow(parseContext, rowIdx, rowItem, validateResult);
            if (validateResult != null) {
                errorList.add(validateResult);
                if (!continueOnError) {
                    parseContext.setResult(ParseContext.RESULT_ERROR);
                    return false;
                }
            }
        }
        list.add(rowItem);
        return true;
    }

    protected String columnRead(Cell cell, long colIdx) {
        if (cell == null) {
            return null;
        }
        String value = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN: {
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            }
            case Cell.CELL_TYPE_NUMERIC: {
                if (!org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    DecimalFormat df = new DecimalFormat("#.######");
                    value = df.format(cell.getNumericCellValue());
                } else {
                    java.util.Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) cell.getNumericCellValue());
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                }
                break;
            }
            case Cell.CELL_TYPE_STRING: {
                value = cell.getStringCellValue().trim();
                break;
            }
            case Cell.CELL_TYPE_ERROR: {
                break;
            }
            case Cell.CELL_TYPE_BLANK: {
                break;
            }
            case Cell.CELL_TYPE_FORMULA: {
                value = cell.getCellFormula();
                break;
            }
            default: {
                break;
            }
        }
        return value;
    }

    public static class PoiExcelParserMetaData extends InputStreamExcelMetaData {
        private int sheetIdx = 0;

        public int getSheetIdx() {
            return sheetIdx;
        }

        public void setSheetIdx(int sheetIdx) {
            this.sheetIdx = sheetIdx;
        }
    }

    public static class PoiExcelParserWorkData extends InputStreamExcelParser.InputStreamExcelWorkData {
        private Sheet sheet;

        public Sheet getSheet() {
            return sheet;
        }

        public void setSheet(Sheet sheet) {
            this.sheet = sheet;
        }
    }

    public static class PoiExcelParserResultData extends InputStreamExcelResultData {

    }

    public static class PoiExcelParserContextBuilder {
        private ParseContext parseContext = new ParseContext();

        private PoiExcelParserContextBuilder() {
        }

        public static PoiExcelParserContextBuilder build() {
            PoiExcelParserContextBuilder builder = new PoiExcelParserContextBuilder();
            builder.parseContext.setMetaData(new PoiExcelParserMetaData());
            builder.parseContext.setWorkData(new PoiExcelParserWorkData());
            builder.parseContext.setResultData(new PoiExcelParserResultData());
            return builder;
        }

        public ParseContext getParseContext() {
            return parseContext;
        }

        public PoiExcelParserContextBuilder inputStream(InputStream in) {
            ((PoiExcelParserWorkData) parseContext.getWorkData()).setInputStream(in);
            return this;
        }

        public PoiExcelParserContextBuilder beginRow(long beginRow) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setBeginRow(beginRow);
            return this;
        }

        public PoiExcelParserContextBuilder maxRow(long maxRow) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setMaxRow(maxRow);
            return this;
        }

        public PoiExcelParserContextBuilder columnSize(long columnSize) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setColumnSize(columnSize);
            return this;
        }

        public PoiExcelParserContextBuilder pageSize(long pageSize) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setPageSize(pageSize);
            return this;
        }

        public PoiExcelParserContextBuilder beanClass(Class<?> clazz) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowMapper(new BeanRowMapper(clazz));
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowValidator(new BeanRowValidator(clazz));
            return this;
        }

        public PoiExcelParserContextBuilder rowMapper(RowMapper rowMapper) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowMapper(rowMapper);
            return this;
        }

        public PoiExcelParserContextBuilder rowValidator(RowValidator rowValidator) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowValidator(rowValidator);
            return this;
        }

        public PoiExcelParserContextBuilder continueOnError(boolean continueOnError) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setContinueOnError(continueOnError);
            return this;
        }

        public PoiExcelParserContextBuilder sheetIdx(int sheetIdx) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setSheetIdx(sheetIdx);
            return this;
        }

        public PoiExcelParserContextBuilder eventHandler(ParseEventHandler parseEventHandler) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).addEventHandler(parseEventHandler);
            return this;
        }

        public PoiExcelParserContextBuilder messageProvider(MessageProvider messageProvider) {
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setMessageProvider(messageProvider);
            return this;
        }
    }
}
