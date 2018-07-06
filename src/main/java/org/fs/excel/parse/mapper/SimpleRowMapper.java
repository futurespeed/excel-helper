package org.fs.excel.parse.mapper;

import org.fs.excel.parse.ParseContext;

import java.util.HashMap;
import java.util.Map;

public class SimpleRowMapper implements RowMapper<Map> {
    @Override
    public Map newRowItem(ParseContext parseContext) {
        return new HashMap<String, Object>();
    }

    @Override
    public void setValue(ParseContext parseContext, long rowIdx, long colIdx, Map map, Object value) {
        map.put(String.valueOf(colIdx + 1), value);
    }
}
