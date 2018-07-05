package org.fs.excel.parse.validate;

import org.fs.excel.parse.ExcelBean;

import java.lang.reflect.Field;
import java.util.*;

public class BeanRowValidator<T> implements RowValidator<T, RowErrorInfo> {

    private Class<T> clazz;

    private Map<String, Field> columnMap;

    public BeanRowValidator(Class<T> clazz){
        this.clazz = clazz;
        columnMap = new HashMap<String, Field>();

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz;
        while((superClass = superClass.getSuperclass()) != Object.class){
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        Collections.reverse(fieldList);
        for(Field field: fieldList){
            ExcelBean excelBean = field.getAnnotation(ExcelBean.class);
            if(null == excelBean){
                continue;
            }
            columnMap.put(String.valueOf(excelBean.seq() - 1), field);
        }
    }

    @Override
    public RowErrorInfo validateColumn(long rowIdx, long colIdx, Object o, Object value, RowErrorInfo rowErrorInfo) {
        //TODO
        return rowErrorInfo;
    }

    @Override
    public RowErrorInfo validateRow(long rowIdx, Object o, RowErrorInfo rowErrorInfo) {
        //TODO
        return rowErrorInfo;
    }
}
