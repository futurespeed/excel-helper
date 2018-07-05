package org.fs.excel.parse.mapper;

import java.util.HashMap;
import java.util.Map;

public class SimpleRowMapper implements RowMapper<Map> {
    @Override
    public Map newRowItem() {
        return new HashMap<String, Object>();
    }

    @Override
    public void setValue(long rowIdx, long colIdx, Map map, Object value) {
        map.put(String.valueOf(colIdx), value);
    }
}
