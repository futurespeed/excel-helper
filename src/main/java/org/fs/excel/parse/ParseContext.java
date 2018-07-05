package org.fs.excel.parse;

import java.util.List;

public class ParseContext {
	private String flowNo;
	private String createTime;
	private String code;
	private String result;
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

    public interface MetaData{}
	public interface WorkData{}
	public interface ResultData{
        List<Object> getDataList();
        List<Object> getErrorList();
    }
}
