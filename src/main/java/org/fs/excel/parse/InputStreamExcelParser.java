package org.fs.excel.parse;

import org.fs.excel.MessageProvider;
import org.fs.excel.parse.event.ParseEvent;
import org.fs.excel.parse.event.ParseEventHandler;
import org.fs.excel.parse.mapper.RowMapper;
import org.fs.excel.parse.mapper.SimpleRowMapper;
import org.fs.excel.parse.validate.RowValidator;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class InputStreamExcelParser implements ExcelParser {

    public void parse(ParseContext parseContext) {
        InputStreamExcelWorkData workData = getWorkData(parseContext);
        InputStream in = workData.getInputStream();
        read(parseContext, in);
        onReady(parseContext);
        long currPage = 1L;
        workData.setCurrentPage(currPage);
        long maxRow = getMetaData(parseContext).getMaxRow();
        long pageSize = workData.getPageSize();
        long beginIdx = getMetaData(parseContext).getBeginRow() - 1;
        long rowSize = getRowSize(parseContext);
        if (maxRow > 0 && (rowSize - beginIdx) > maxRow) {
            parseContext.setResult(ParseContext.RESULT_ERROR);
            parseContext.setResultMsg(
                    MessageFormat.format(
                            parseContext.getMetaData().getMessageProvider().getProperty("excel.parse.over-max-row"), maxRow));
        }
        for (long i = beginIdx; i < rowSize; i++) {
            workData.setCurrentRowIdx(i);
            if (!onRowRead(parseContext)) {
                break;
            }
            if (pageSize > 0 && 0 == (i - beginIdx + 1) % pageSize) {
                onPageChange(parseContext);
                currPage++;
                workData.setCurrentPage(currPage);
            }
        }
        onFinish(parseContext);
    }

    protected void onReady(ParseContext parseContext) {
        InputStreamExcelMetaData metaData = getMetaData(parseContext);
        if (null == metaData.getRowMapper()) {
            throw new RuntimeException("please setup RowMapper");
        }
        getWorkData(parseContext).setPageSize(metaData.getPageSize());
        List<Object> list = new ArrayList<>();
        getResultData(parseContext).setDataList(list);
        List<Object> errList = new ArrayList<>();
        getResultData(parseContext).setErrorList(errList);
        processEvent(parseContext, ParseEvent.READY);
    }

    protected void onFinish(ParseContext parseContext) {
        if (null == parseContext.getResult()) {
            parseContext.setResult(ParseContext.RESULT_SUCCESS);
        }
        processEvent(parseContext, ParseEvent.FINISH);
    }

    protected void onPageChange(ParseContext parseContext) {
        processEvent(parseContext, ParseEvent.PAGE_CHANGE);
    }

    protected boolean onRowRead(ParseContext parseContext) {
        boolean result = rowRead(parseContext);
        processEvent(parseContext, ParseEvent.ROW_READ);
        return result;
    }

    protected void processEvent(ParseContext parseContext, ParseEvent parseEvent) {
        List<ParseEventHandler> eventHandlers = getMetaData(parseContext).getEventHandlers();
        if (null == eventHandlers || eventHandlers.isEmpty()) {
            return;
        }
        for (ParseEventHandler eventHandler : eventHandlers) {
            switch (parseEvent) {
                case READY: {
                    eventHandler.onReady(parseContext);
                    break;
                }
                case ROW_READ: {
                    eventHandler.onRowRead(parseContext);
                    break;
                }
                case PAGE_CHANGE: {
                    eventHandler.onPageChange(parseContext);
                    break;
                }
                case FINISH: {
                    eventHandler.onFinish(parseContext);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private InputStreamExcelMetaData getMetaData(ParseContext parseContext) {
        return (InputStreamExcelMetaData) parseContext.getMetaData();
    }

    private InputStreamExcelWorkData getWorkData(ParseContext parseContext) {
        return (InputStreamExcelWorkData) parseContext.getWorkData();
    }

    private InputStreamExcelResultData getResultData(ParseContext parseContext) {
        return (InputStreamExcelResultData) parseContext.getResultData();
    }

    protected abstract void read(ParseContext parseContext, InputStream in);

    protected abstract long getRowSize(ParseContext parseContext);

    protected abstract boolean rowRead(ParseContext parseContext);

    public static class InputStreamExcelMetaData implements ParseContext.MetaData {
        private long beginRow = 2;
        private long maxRow = -1;
        private boolean continueOnError = false;
        private long columnSize = -1;
        private long pageSize = -1;
        private RowMapper rowMapper = new SimpleRowMapper();
        private RowValidator rowValidator;
        private List<ParseEventHandler> eventHandlers = new ArrayList<ParseEventHandler>();
        private MessageProvider messageProvider = new MessageProvider(null);

        public long getBeginRow() {
            return beginRow;
        }

        public void setBeginRow(long beginRow) {
            this.beginRow = beginRow;
        }

        public long getMaxRow() {
            return maxRow;
        }

        public void setMaxRow(long maxRow) {
            this.maxRow = maxRow;
        }

        public boolean isContinueOnError() {
            return continueOnError;
        }

        public void setContinueOnError(boolean continueOnError) {
            this.continueOnError = continueOnError;
        }

        public long getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(long columnSize) {
            this.columnSize = columnSize;
        }

        public long getPageSize() {
            return pageSize;
        }

        public void setPageSize(long pageSize) {
            this.pageSize = pageSize;
        }

        public RowMapper getRowMapper() {
            return rowMapper;
        }

        public void setRowMapper(RowMapper rowMapper) {
            this.rowMapper = rowMapper;
        }

        public RowValidator getRowValidator() {
            return rowValidator;
        }

        public void setRowValidator(RowValidator rowValidator) {
            this.rowValidator = rowValidator;
        }

        public void addEventHandler(ParseEventHandler parseEventHandler) {
            eventHandlers.add(parseEventHandler);
        }

        public void removeEventHandler(ParseEventHandler parseEventHandler) {
            eventHandlers.remove(parseEventHandler);
        }

        public List<ParseEventHandler> getEventHandlers() {
            return eventHandlers;
        }

        public MessageProvider getMessageProvider() {
            return messageProvider;
        }

        public void setMessageProvider(MessageProvider messageProvider) {
            this.messageProvider = messageProvider;
        }
    }

    public static class InputStreamExcelWorkData implements ParseContext.WorkData {
        private InputStream inputStream;
        private long pageSize;
        private long currentPage;
        private long currentRowIdx;
        private long rowSize;
        private long columnSize;

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public long getPageSize() {
            return pageSize;
        }

        public void setPageSize(long pageSize) {
            this.pageSize = pageSize;
        }

        public long getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(long currentPage) {
            this.currentPage = currentPage;
        }

        public long getCurrentRowIdx() {
            return currentRowIdx;
        }

        public void setCurrentRowIdx(long currentRowIdx) {
            this.currentRowIdx = currentRowIdx;
        }

        public long getRowSize() {
            return rowSize;
        }

        public void setRowSize(long rowSize) {
            this.rowSize = rowSize;
        }

        public long getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(long columnSize) {
            this.columnSize = columnSize;
        }
    }

    public static class InputStreamExcelResultData implements ParseContext.ResultData {
        private List<Object> errorList;
        private List<Object> dataList;

        public List<Object> getErrorList() {
            return errorList;
        }

        public void setErrorList(List<Object> errorList) {
            this.errorList = errorList;
        }

        public List<Object> getDataList() {
            return dataList;
        }

        public void setDataList(List<Object> dataList) {
            this.dataList = dataList;
        }
    }
}
