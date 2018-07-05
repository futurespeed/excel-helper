package org.fs.excel.parse.mapper;

public interface RowMapper<T> {
    T newRowItem();
    void setValue(long rowIdx, long colIdx, T t, Object value);
}
