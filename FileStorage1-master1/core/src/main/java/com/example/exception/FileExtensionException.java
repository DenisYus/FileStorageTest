package com.example.exception;

public class FileExtensionException extends IllegalArgumentException{
    public FileExtensionException (String message){
        super(message);
    }
}
