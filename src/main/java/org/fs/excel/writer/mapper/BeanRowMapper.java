package org.fs.excel.writer.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.fs.excel.ExcelColumn;
import org.fs.excel.writer.WriteContext;

import java.lang.reflect.Field;
import java.util.*;

public class BeanRowMapper<T> implements RowMapper<T> {

    private Class<T> clazz;

    private Map<String, String> columnMap;

    private List<String> columnList;

    private Map<String, ExcelColumn> columnAnnoMap;

    public BeanRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        columnMap = new HashMap<String, String>();
        columnList = new ArrayList<String>();
        columnAnnoMap = new HashMap<String, ExcelColumn>();

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz;
        while ((superClass = superClass.getSuperclass()) != Object.class) {
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        Collections.reverse(fieldList);
        for (Field field : fieldList) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (null == excelColumn) {
                continue;
            }
            if (!String.class.equals(field.getType())) {
                throw new RuntimeException("field [" + field.getName() + "] type error, only support field type [java.lang.String]");
            }
            String columnNum = String.valueOf(excelColumn.seq() - 1);
            columnMap.put(columnNum, field.getName());
            columnList.add(columnNum);
            columnAnnoMap.put(columnNum, excelColumn);
        }
    }

    @Override
    public List<String> getColumnList() {
        return columnList;
    }

    @Override
    public String getColumnName(WriteContext writeContext, long column) {
        ExcelColumn excelColumn = columnAnnoMap.get(String.valueOf(column));
        if(excelColumn != null){
            return excelColumn.name();
        }
        return null;
    }

    @Override
    public int getColumnWidth(WriteContext writeContext, long column) {
        ExcelColumn excelColumn = columnAnnoMap.get(String.valueOf(column));
        if(excelColumn != null){
            return excelColumn.width();
        }
        return 30;
    }

    @Override
    public Object getValue(WriteContext writeContext, T t, long column) {
        try {
            String property = columnMap.get(String.valueOf(column));
            if (null == property) {
                return null;
            }
            return PropertyUtils.getProperty(t, property);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
