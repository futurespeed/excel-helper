package org.fs.excel.writer;

public abstract class OutputStreamExcelWriter implements ExcelWriter {
    @Override
    public void write(WriteContext writeContext) {
        //TODO
    }

    public class OutputStreamExcelWriterWorkData implements WriteContext.WorkData {
    }
}
