package com.example.exception;

public class FileNameException extends IllegalArgumentException{
    public FileNameException (String message){
        super(message);
    }
}
