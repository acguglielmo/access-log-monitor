package com.ef.util;

public class ApplicationException extends RuntimeException {

    public ApplicationException(final Throwable throwable){
        super(throwable);
    }
}
