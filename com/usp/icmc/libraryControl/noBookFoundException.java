package com.usp.icmc.libraryControl;

public class noBookFoundException extends RuntimeException{

    public noBookFoundException(){
        super();
    }

    public noBookFoundException(Book book){
        super("Book "+book+" not found");
    }

}
