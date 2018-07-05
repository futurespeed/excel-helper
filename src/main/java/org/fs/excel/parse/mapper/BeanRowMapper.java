package org.fs.excel.parse.mapper;

import org.apache.commons.beanutils.PropertyUtils;

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
            ExcelBean excelBean = field.getAnnotation(ExcelBean.class);
            if(null == excelBean){
                continue;
            }
            columnMap.put(String.valueOf(excelBean.seq() - 1), field.getName());
        }
    }

    @Override
    public T newRowItem() {
        try {
            return clazz.newInstance();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(long rowIdx, long colIdx, T t, Object value) {
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
