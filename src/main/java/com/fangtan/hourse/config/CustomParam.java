package com.fangtan.hourse.config;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = CustomValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomParam {
    String message() default "name.tanglei.www.validator.CustomArray.defaultMessage";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default { };

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @interface List {
        CustomParam[] value();
    }
}