package com.fangtan.hourse.config;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<CustomParam, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (null == s || s.isEmpty()) {
            return true;
        }
        if (s.equals("tanglei")) {
            return true;
        } else {
            error(constraintValidatorContext, "Invalid params: " + s);
            return false;
        }
    }

    @Override
    public void initialize(CustomParam constraintAnnotation) {
    }

    private static void error(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}