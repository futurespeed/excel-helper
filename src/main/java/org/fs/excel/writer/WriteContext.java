package org.fs.excel.writer;

public class WriteContext {
    private WorkData workData;

    public WorkData getWorkData() {
        return workData;
    }

    public void setWorkData(WorkData workData) {
        this.workData = workData;
    }

    public interface WorkData {
    }
}
