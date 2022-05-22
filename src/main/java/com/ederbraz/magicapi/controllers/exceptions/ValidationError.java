package com.ederbraz.magicapi.controllers.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationError extends StandardError {
    private static final long serialVersionUID = 1L;

    private final List<FieldMessage> errors = new ArrayList<>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationError)) return false;
        if (!super.equals(o)) return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(getErrors(), that.getErrors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getErrors());
    }
}