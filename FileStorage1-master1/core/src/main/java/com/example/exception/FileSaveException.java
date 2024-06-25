package com.example.exception;

public class FileSaveException extends RuntimeException {
    public FileSaveException (String message){
        super(message);
    }
}
