package org.fs.excel.parse;

import org.fs.excel.MessageProvider;

import java.util.List;

public class ParseContext {

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";

    private String flowNo;
    private String createTime;
    private String code;
    private String result;
    private String resultMsg;
    private MetaData metaData;
    private WorkData workData;
    private ResultData resultData;

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
