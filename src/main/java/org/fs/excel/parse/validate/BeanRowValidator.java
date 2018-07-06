package org.fs.excel.parse.validate;

import org.fs.excel.ExcelColumn;
import org.fs.excel.parse.ParseContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

public class BeanRowValidator<T> implements RowValidator<T, RowErrorInfo> {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Class<T> clazz;

    private Map<String, Field> columnMap;

    private Map<String, String> fieldNameMap;

    public BeanRowValidator(Class<T> clazz) {
        this.clazz = clazz;
        columnMap = new HashMap<String, Field>();
        fieldNameMap = new HashMap<String, String>();

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
            columnMap.put(String.valueOf(excelColumn.seq() - 1), field);
            fieldNameMap.put(field.getName(), excelColumn.name());
        }
    }

    @Override
    public RowErrorInfo validateColumn(ParseContext parseContext, long rowIdx, long colIdx, T t, Object value, RowErrorInfo rowErrorInfo) {
        return rowErrorInfo;
    }

    @Override
    public RowErrorInfo validateRow(ParseContext parseContext, long rowIdx, T t, RowErrorInfo rowErrorInfo) {
        Set<ConstraintViolation<T>> set = validator.validate(t, Default.class);
        if (null == set || set.isEmpty()) {
            return rowErrorInfo;
        }
        rowErrorInfo = new RowErrorInfo();
        rowErrorInfo.setRow(rowIdx + 1);
        String property = null;
        for (ConstraintViolation<T> cv : set) {
            property = cv.getPropertyPath().toString();
            String msg = cv.getMessage();
            if (cv.getMessageTemplate().startsWith("{")) {
                msg = MessageFormat.format(parseContext.getMetaData().getMessageProvider().getProperty("excel.parse.validate.bean.default-error"), fieldNameMap.get(property));
            }
            if (rowErrorInfo.getMsg() != null) {
                rowErrorInfo.setMsg(rowErrorInfo.getMsg() + "," + msg);
            } else {
                rowErrorInfo.setMsg(msg);
            }
        }
        return rowErrorInfo;
    }
}
