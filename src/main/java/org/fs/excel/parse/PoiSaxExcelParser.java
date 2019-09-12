package org.fs.excel.parse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.fs.excel.parse.mapper.RowMapper;
import org.fs.excel.parse.validate.RowValidator;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PoiSaxExcelParser extends PoiExcelParser {

    @Override
    public void parse(ParseContext parseContext) {
        OPCPackage pkg = null;
        InputStream sheetInputStream = null;
        try {
            onReady(parseContext);

            pkg = OPCPackage.open(getWorkData(parseContext).getInputStream());
            XSSFReader r = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XMLReader parser =
                    XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            ContentHandler handler = new XSSFSheetXMLHandler(r.getStylesTable(), null, strings,
                    new SheetHandler(parseContext), new DataFormatter(), false);
            parser.setContentHandler(handler);
            Iterator<InputStream> ite = r.getSheetsData();
            int sheetIdx = getMetaData(parseContext).getSheetIdx();
            for (int i = -1; i < sheetIdx; i++) {
                sheetInputStream = ite.next();
            }
            parser.parse(new InputSource(sheetInputStream));
        } catch (ParseException e) {
            // interrupt parse
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.closeQuietly(sheetInputStream);
            IOUtils.closeQuietly(pkg);
            onFinish(parseContext);
        }
    }

    @Override
    protected boolean rowRead(ParseContext parseContext) {
        return true;
    }

    protected class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private ParseContext parseContext;

        private int rowIdx;
        private List<String> dataList;

        private long pageSize;
        private long beginRowIdx;
        private long maxRow;
        private RowMapper rowMapper;
        private RowValidator validator;
        private List<Object> errorList;
        private boolean continueOnError;

        public SheetHandler(ParseContext parseContext) {
            this.parseContext = parseContext;
            pageSize = getMetaData(parseContext).getPageSize();
            beginRowIdx = getMetaData(parseContext).getBeginRow() - 1;
            maxRow = getMetaData(parseContext).getMaxRow();
            rowMapper = getMetaData(parseContext).getRowMapper();
            validator = getMetaData(parseContext).getRowValidator();
            errorList = getResultData(parseContext).getErrorList();
            continueOnError = getMetaData(parseContext).isContinueOnError();
        }

        @Override
        public void startRow(int i) {
            if (maxRow > 0 && i > maxRow - 1 + beginRowIdx) {
                parseContext.setResultCode(ParseContext.ERROR_CODE_OVER_MAX_ROW);
                parseContext.setResultMsg(
                        MessageFormat.format(
                                parseContext.getMetaData().getMessageProvider().getProperty(ParseContext.ERROR_CODE_OVER_MAX_ROW), maxRow));
                throw new ParseException(ParseContext.ERROR_CODE_OVER_MAX_ROW);
            }
            rowIdx = i;
            dataList = new ArrayList<>();
        }

        @Override
        public void endRow(int i) {
            if (i < beginRowIdx) {
                return;
            }
            Object rowItem = rowMapper.newRowItem(parseContext, rowIdx);
            Object validateResult = null;
            for (int j = 0, jLen = dataList.size(); j < jLen; j++) {
                Object value = dataList.get(j);
                if (validator != null) {
                    validateResult = validator.validateColumn(parseContext, rowIdx, i, value, rowItem, validateResult);
                    if (validateResult != null) {
                        parseContext.setResult(ParseContext.RESULT_ERROR);
                        if (!continueOnError) {
                            break;
                        }
                    }
                }
                rowMapper.setValue(parseContext, rowIdx, j, rowItem, value);
            }
            if (validator != null) {
                validateResult = validator.validateRow(parseContext, rowIdx, rowItem, validateResult);
                if (validateResult != null) {
                    parseContext.setResult(ParseContext.RESULT_ERROR);
                    errorList.add(validateResult);
                    if (!continueOnError) {
                        throw new ParseException(ParseContext.RESULT_ERROR);
                    }
                }
            }
            getResultData(parseContext).getDataList().add(rowItem);
            onRowRead(parseContext);

            getWorkData(parseContext).setCurrentRowIdx(i);
            if (pageSize > 0 && 0 == (i - beginRowIdx + 1) % pageSize) {
                getWorkData(parseContext).setCurrentPage((i - beginRowIdx) / pageSize);
                onPageChange(parseContext);
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            int colIdx = CellReference.convertColStringToIndex(cellReference.replaceAll("\\d", ""));
            for (int i = dataList.size(); i < colIdx; i++) {
                dataList.add(null);
            }
            dataList.add(formattedValue);
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // do nothing
        }
    }

    protected class ParseException extends RuntimeException {
        private String code;
        private String[] params;

        public ParseException(String code) {
            this.code = code;
        }

        public ParseException(String code, String[] params) {
            this.code = code;
            this.params = params;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String[] getParams() {
            return params;
        }

        public void setParams(String[] params) {
            this.params = params;
        }
    }

}
