package org.fs.excel.parse.mapper;

import org.fs.excel.parse.ParseContext;

public interface RowMapper<T> {
    T newRowItem(ParseContext parseContext);

    void setValue(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value);
}
