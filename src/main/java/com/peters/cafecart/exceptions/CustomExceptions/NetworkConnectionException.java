package com.peters.cafecart.exceptions.CustomExceptions;

public class NetworkConnectionException extends RuntimeException{
    public NetworkConnectionException(String err) {
        super(err);
    }
}
