package org.fs.excel.parse.mapper;

import org.fs.excel.parse.ParseContext;

public interface RowMapper<T> {

    long getColumnSize(ParseContext parseContext);

    T newRowItem(ParseContext parseContext, long rowIdx);

    void setValue(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value);
}
