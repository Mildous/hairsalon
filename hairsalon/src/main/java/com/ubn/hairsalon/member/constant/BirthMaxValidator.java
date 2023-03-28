package com.ubn.hairsalon.member.constant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;

public class BirthMaxValidator implements ConstraintValidator<BirthMax, LocalDate> {
    @Override
    public void initialize(BirthMax birthMax) {
    }
    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false; // null 값은 검증을 통과하지 않도록 처리
        }
        try {
            LocalDate maxDate = LocalDate.of(2002, 12, 31);
            return birthDate.isBefore(maxDate) || birthDate.equals(maxDate);
        } catch (DateTimeException e) {
            return false;
        }
    }
}
