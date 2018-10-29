package org.fs.excel;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelExportColumn {
    long seq();

    String name();

    int width() default 30;
}
