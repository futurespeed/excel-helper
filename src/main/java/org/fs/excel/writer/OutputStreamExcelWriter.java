package org.fs.excel.writer;

import org.fs.excel.writer.mapper.RowMapper;

import java.io.OutputStream;
import java.util.List;

public abstract class OutputStreamExcelWriter implements ExcelWriter {

    public static class OutputStreamExcelWriterWorkData implements WriteContext.WorkData {

        private OutputStream outputStream;

        private RowMapper rowMapper;

        private List<Object> dataList;

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public void setOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public RowMapper getRowMapper() {
            return rowMapper;
        }

        public void setRowMapper(RowMapper rowMapper) {
            this.rowMapper = rowMapper;
        }

        public List<Object> getDataList() {
            return dataList;
        }

        public void setDataList(List<Object> dataList) {
            this.dataList = dataList;
        }
    }
}
