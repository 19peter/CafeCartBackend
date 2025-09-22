package com.peters.cafecart.exceptions.CustomExceptions;

public class ValidationException extends  RuntimeException{
    public ValidationException(String err) {
        super(err);
    }
}
