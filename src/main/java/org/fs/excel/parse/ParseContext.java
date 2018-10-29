package org.fs.excel.parse;

import org.fs.excel.MessageProvider;

import java.util.List;

public class ParseContext {

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";
    public static final String ERROR_CODE_OVER_MAX_ROW = "excel.parse.over-max-row";

    private String result;
    private String resultCode;
    private String resultMsg;
    private MetaData metaData;
    private WorkData workData;
    private ResultData resultData;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public WorkData getWorkData() {
        return workData;
    }

    public void setWorkData(WorkData workData) {
        this.workData = workData;
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }

    public interface MetaData {
        MessageProvider getMessageProvider();
    }

    public interface WorkData {
    }

    public interface ResultData {
        List<Object> getDataList();

        List<Object> getErrorList();
    }
}
