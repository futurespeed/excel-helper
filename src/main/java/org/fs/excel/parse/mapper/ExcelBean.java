package org.fs.excel.parse.mapper;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelBean {
    int seq();
}
