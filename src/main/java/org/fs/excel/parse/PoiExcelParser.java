package org.fs.excel.parse;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.fs.excel.parse.event.ParseEventHandler;
import org.fs.excel.parse.mapper.RowMapper;
import org.fs.excel.parse.validate.RowValidator;

public class PoiExcelParser extends InputStreamExcelParser {

    public PoiExcelParserContextBuilder builder(){
        return PoiExcelParserContextBuilder.build();
    }

    protected PoiExcelParserMetaData getMetaData(ParseContext parseContext){
        return (PoiExcelParserMetaData) parseContext.getMetaData();
    }

    protected PoiExcelParserWorkData getWorkData(ParseContext parseContext){
        return (PoiExcelParserWorkData) parseContext.getWorkData();
    }

    public PoiExcelParserResultData getResultData(ParseContext parseContext){
        return (PoiExcelParserResultData) parseContext.getResultData();
    }
	
	protected void read(ParseContext parseContext, InputStream in) {
		try{
			Workbook wb = WorkbookFactory.create(in);
			Sheet sheet = wb.getSheetAt(getMetaData(parseContext).getSheetIdx());
            getWorkData(parseContext).setSheet(sheet);
		}catch(Throwable e){
			throw new RuntimeException(e);
		}
	}
	
	protected long getRowSize(ParseContext parseContext) {
        Sheet sheet = getWorkData(parseContext).getSheet();
        Long rowSize = (long) sheet.getLastRowNum();
        if(rowSize >= 0){
            rowSize++;
        }
        getWorkData(parseContext).setRowSize(rowSize);
		return rowSize;
	}
	
	protected long getColumnSize(ParseContext parseContext){
		long columnSize = getWorkData(parseContext).getColumnSize();
		if(columnSize > 0){
		    return columnSize;
        }
        columnSize = getMetaData(parseContext).getColumnSize();
        if(columnSize > 0){
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
		long rowIdx = getWorkData(parseContext).getCurrentRowIdx();
		Sheet sheet = getWorkData(parseContext).getSheet();
		List<Object> list = getResultData(parseContext).getDataList();
		List<Object> errorList = getResultData(parseContext).getErrorList();
		Row row = sheet.getRow((int) rowIdx);
		RowMapper mapper = getMetaData(parseContext).getRowMapper();
		RowValidator validator = getMetaData(parseContext).getRowValidator();
		Object rowItem = mapper.newRowItem();
        Object validateResult = null;
        for(long i = 0, len = getColumnSize(parseContext); i < len; i++){
			Cell cell = row.getCell((int) i);
			Object value = columnRead(cell, i);
			if(validator != null) {
                validateResult = validator.validateColumn(rowIdx, i, value, rowItem, validateResult);
                break;
            }
            mapper.setValue(rowIdx, i, rowItem, value);
		}
        if(validator != null) {
            validateResult = validator.validateRow(rowIdx, rowItem, validateResult);
            if (validateResult != null) {
                errorList.add(validateResult);
                if (!getMetaData(parseContext).isContinueOnError()) {
                    parseContext.setResult("error");
                    return false;
                }
            }
        }
		list.add(rowItem);
		return true;
	}
	
	protected Object columnRead(Cell cell, long colIdx) {
		if(null == cell){
			return "";
		}
		CellType cellType = cell.getCellTypeEnum();
		if(CellType.STRING.equals(cellType)){
			return cell.getStringCellValue();
		}
		if(CellType.NUMERIC.equals(cellType)){
			//FIXME NUMERIC
			return String.valueOf(cell.getNumericCellValue());
		}
		if(CellType.BOOLEAN.equals(cellType)){
			return cell.getBooleanCellValue() ? "1" : "0";
		}
		return "";
	}

    public static class PoiExcelParserMetaData extends InputStreamExcelMetaData{
        private int sheetIdx = 0;

        public int getSheetIdx() {
            return sheetIdx;
        }

        public void setSheetIdx(int sheetIdx) {
            this.sheetIdx = sheetIdx;
        }
    }

	public static class PoiExcelParserWorkData extends InputStreamExcelParser.InputStreamExcelWorkData{
	    private Sheet sheet;

        public Sheet getSheet() {
            return sheet;
        }

        public void setSheet(Sheet sheet) {
            this.sheet = sheet;
        }
    }

    public static class PoiExcelParserResultData extends InputStreamExcelResultData{

    }

    public static class PoiExcelParserContextBuilder{
        private ParseContext parseContext = new ParseContext();
        private PoiExcelParserContextBuilder(){}
        public static PoiExcelParserContextBuilder build(){
            PoiExcelParserContextBuilder builder = new PoiExcelParserContextBuilder();
            builder.parseContext.setMetaData(new PoiExcelParserMetaData());
            builder.parseContext.setWorkData(new PoiExcelParserWorkData());
            builder.parseContext.setResultData(new PoiExcelParserResultData());
            return builder;
        }

        public ParseContext getParseContext() {
            return parseContext;
        }

        public PoiExcelParserContextBuilder inputStream(InputStream in){
            ((PoiExcelParserWorkData) parseContext.getWorkData()).setInputStream(in);
            return this;
        }

        public PoiExcelParserContextBuilder columnSize(long columnSize){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setColumnSize(columnSize);
            return this;
        }

        public PoiExcelParserContextBuilder pageSize(long pageSize){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setPageSize(pageSize);
            return this;
        }

        public PoiExcelParserContextBuilder rowMapper(RowMapper rowMapper){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowMapper(rowMapper);
            return this;
        }

        public PoiExcelParserContextBuilder rowValidator(RowValidator rowValidator){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setRowValidator(rowValidator);
            return this;
        }

        public PoiExcelParserContextBuilder continueOnError(boolean continueOnError){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setContinueOnError(continueOnError);
            return this;
        }

        public PoiExcelParserContextBuilder sheetIdx(int sheetIdx){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).setSheetIdx(sheetIdx);
            return this;
        }

        public PoiExcelParserContextBuilder eventHandler(ParseEventHandler parseEventHandler){
            ((PoiExcelParserMetaData) parseContext.getMetaData()).addEventHandler(parseEventHandler);
            return this;
        }
    }
}
