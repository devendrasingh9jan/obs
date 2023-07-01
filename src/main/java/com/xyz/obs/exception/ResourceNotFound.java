package com.xyz.obs.exception;

public class ResourceNotFound extends RuntimeException{

    public ResourceNotFound(){
        super();
    }
    public ResourceNotFound(String message){
        super(message);
    }
}
