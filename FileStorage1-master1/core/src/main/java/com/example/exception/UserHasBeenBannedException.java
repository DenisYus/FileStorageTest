package com.example.exception;

public class UserHasBeenBannedException extends RuntimeException{
    public UserHasBeenBannedException(String message){
        super(message);
    }
}
