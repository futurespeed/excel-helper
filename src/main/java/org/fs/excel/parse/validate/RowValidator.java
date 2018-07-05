package org.fs.excel.parse.validate;

public interface RowValidator<T, Result> {
    Result validateColumn(long rowIdx, long colIdx, T t, Object value, Result result);
    Result validateRow(long rowIdx, T t, Result result);
}
