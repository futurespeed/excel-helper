package org.fs.excel.parse.validate;

import org.fs.excel.parse.ParseContext;

public interface RowValidator<T, Result> {
    Result validateColumn(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value, Result result);
    Result validateRow(ParseContext parseContext, long rowIdx, T t, Result result);
}
