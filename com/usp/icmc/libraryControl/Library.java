package com.usp.icmc.libraryControl;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Library {

    private ArrayList<Book> books;
    private Map<User, Integer> blacklist;

    public Library(String dataDirectory) throws IllegalArgumentException {

        books = new ArrayList<>();
        blacklist = new HashMap<>();

        if (dataDirectory.endsWith("/"))
            dataDirectory = dataDirectory
                    .substring(0, dataDirectory.length() - 2);

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");
        File[] allFiles;

        if (file.exists() && !file.isDirectory())
            throw new IllegalArgumentException("Not a directory");

        if(
                !booksFile.exists() ||
                !blacklistFile.exists() ||
                !usersFile.exists()
        ){
            try {
                booksFile.createNewFile();
                blacklistFile.createNewFile();
                booksDirectory.createNewFile();
                usersDirectory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO proper exception handling
            }
        }else{
            try {
                CSVReader csvReader = new CSVReader(new FileReader(booksFile));
                String[] tokens;
                while ((tokens = csvReader.readNext()) != null){
                    books.add(
                            new Book(
                                    tokens[0],
                                    tokens[1],
                                    tokens[2].equals("true")
                            )
                    );
                }
                csvReader = new CSVReader(new FileReader(blacklistFile));
                while ((tokens = csvReader.readNext()) != null){

                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO proper exception handling
            }
        }


    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void rentBook(User user, Book book) {
        if (!user.canRentBook()) return;
        if (blacklist.containsKey(user)) return;

        user.rentBook(book);
        book.setAvailableForRental(false);
        book.writeRentalLog(user);
    }

    public boolean doesBookExist(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForRental)
                .findFirst();
        return b.isPresent();
    }

    public boolean doesBookExist(int id) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForRental)
                .findFirst();
        return b.isPresent();
    }

    public Book getBook(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForRental)
                .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    public Book getBook(int id) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForRental)
                .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

}
