package com.example.exception;

public class FileSizeException extends IllegalArgumentException{
    public FileSizeException (String message){
        super(message);
    }
}
