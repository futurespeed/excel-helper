package org.fs.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SpelConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Spel {
    String value();

    String message() default "{javax.validation.constraints.Spel.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
