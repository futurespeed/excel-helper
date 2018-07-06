package org.fs.excel.parse.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.fs.excel.ExcelColumn;
import org.fs.excel.parse.ParseContext;

import java.lang.reflect.Field;
import java.util.*;

public class BeanRowMapper<T> implements RowMapper<T> {

    private Class<T> clazz;

    private Map<String, String> columnMap;

    public BeanRowMapper(Class<T> clazz){
        this.clazz = clazz;
        columnMap = new HashMap<String, String>();

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz;
        while((superClass = superClass.getSuperclass()) != Object.class){
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        Collections.reverse(fieldList);
        for(Field field: fieldList){
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if(null == excelColumn){
                continue;
            }
            if(!String.class.equals(field.getType())){
                throw new RuntimeException("field [" + field.getName() + "] type error, only support field type [java.lang.String]");
            }
            columnMap.put(String.valueOf(excelColumn.seq() - 1), field.getName());
        }
    }

    @Override
    public T newRowItem(ParseContext parseContext) {
        try {
            return clazz.newInstance();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value) {
        try {
            String propertyName = columnMap.get(String.valueOf(colIdx));
            if(propertyName != null) {
                PropertyUtils.setProperty(t, propertyName, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }
}
