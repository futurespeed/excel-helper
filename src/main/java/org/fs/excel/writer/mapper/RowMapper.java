package org.fs.excel.writer.mapper;

import org.fs.excel.writer.WriteContext;

import java.util.List;

public interface RowMapper<T> {
    List<String> getColumnList();
    String getColumnName(WriteContext writeContext, long column);
    int getColumnWidth(WriteContext writeContext, long column);
    Object getValue(WriteContext writeContext, T t, long column);
}
