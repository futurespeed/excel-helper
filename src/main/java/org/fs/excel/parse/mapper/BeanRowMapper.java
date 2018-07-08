package org.fs.excel.parse.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.fs.excel.ExcelColumn;
import org.fs.excel.parse.InputStreamExcelParser;
import org.fs.excel.parse.ParseContext;

import java.lang.reflect.Field;
import java.util.*;

public class BeanRowMapper<T> implements RowMapper<T> {

    private Class<T> clazz;

    private Map<String, String> columnMap;

    private Map<String, ExcelColumn.SeqField> seqFieldMap;

    private long columnSize = -1;

    public BeanRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        columnMap = new HashMap<String, String>();
        seqFieldMap = new HashMap<String, ExcelColumn.SeqField>();

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz;
        while ((superClass = superClass.getSuperclass()) != Object.class) {
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        Collections.reverse(fieldList);
        for (Field field : fieldList) {
            ExcelColumn.SeqField seqField = field.getAnnotation(ExcelColumn.SeqField.class);
            if (seqField != null) {
                if (!(int.class.equals(field.getType()) || long.class.equals(field.getType())
                        || Integer.class.equals(field.getType()) || Long.class.equals(field.getType()))) {
                    throw new RuntimeException("seq field [" + field.getName() + "] type error, only support field type [java.lang.String]");
                }
                seqFieldMap.put(field.getName(), seqField);
            }
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (null == excelColumn) {
                continue;
            }
            if (!String.class.equals(field.getType())) {
                throw new RuntimeException("field [" + field.getName() + "] type error, only support field type [java.lang.String]");
            }
            if (columnSize < excelColumn.seq()) {
                columnSize = excelColumn.seq();
            }
            columnMap.put(String.valueOf(excelColumn.seq() - 1), field.getName());
        }
    }

    @Override
    public long getColumnSize(ParseContext parseContext) {
        return columnSize;
    }

    @Override
    public T newRowItem(ParseContext parseContext, long rowIdx) {
        try {
            long beginRowIdx = ((InputStreamExcelParser.InputStreamExcelMetaData) parseContext.getMetaData()).getBeginRow() - 1;
            T t = clazz.newInstance();
            for (Map.Entry<String, ExcelColumn.SeqField> entry : seqFieldMap.entrySet()) {
                PropertyUtils.setProperty(t, entry.getKey(), (int) (rowIdx - beginRowIdx + entry.getValue().begin()));
            }
            return t;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value) {
        try {
            String propertyName = columnMap.get(String.valueOf(colIdx));
            if (propertyName != null) {
                PropertyUtils.setProperty(t, propertyName, value);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
