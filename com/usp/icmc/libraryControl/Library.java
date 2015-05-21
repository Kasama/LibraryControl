package com.usp.icmc.libraryControl;

import java.util.ArrayList;
import java.util.Optional;

public class Library {

    private ArrayList<Book> books;
    private ArrayList<User> blacklist;

    public Library(){
        books = new ArrayList<>();
        blacklist = new ArrayList<>();
    }

    public void addBook(Book book){
        books.add(book);
    }

    public void rentBook(User user, Book book){
        if(!user.canRentBook()) return;
        if(blacklist.contains(user)) return;

        user.rentBook(book);
        book.setAvailableForRental(false);
        book.writeRentalLog(user);
    }

    public boolean doesBookExist(String Author, String Title){
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForRental)
                .findFirst();
        return b.isPresent();
    }

    public boolean doesBookExist(int id){
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForRental)
                .findFirst();
        return b.isPresent();
    }

    public Book getBook(String Author, String Title){
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForRental)
                .findFirst();
        if(!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    public Book getBook(int id){
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForRental)
                .findFirst();
        if(!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

}
