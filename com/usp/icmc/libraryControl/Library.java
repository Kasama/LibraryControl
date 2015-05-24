package com.usp.icmc.libraryControl;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@FunctionalInterface
interface readableField{
    void read(String[] tokens);
}

public class Library {

    private ArrayList<User> users;
    private ArrayList<Book> books;
    private Map<User, Date> blacklist;

    public Library(String dataDirectory) throws IllegalArgumentException {

        books = new ArrayList<>();
        users = new ArrayList<>();
        blacklist = new HashMap<>();

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        if (file.exists() && !file.isDirectory())
            throw new IllegalArgumentException(
                    "argument is not a directory path"
            );

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
                // reads books from the data file the entries are organized as
                // "Author","Title","canBeBorrowedByAnyone"
                parseCSV(
                    booksFile,
                    tokens ->
                        addBook(
                            new Book(
                                tokens[0],
                                tokens[1],
                                Boolean.getBoolean(tokens[2])
                            )
                        )
                );
                // fill in books borrow logs
                for (Book book : books){
                    long bookId = book.getId();
                    File bookFile = new File(
                            booksDirectory.getPath()+"/"+bookId+"Log.csv"
                    );

                    parseCSV(
                        bookFile,
                        tokens ->
                            book.getBorrowLog().put(
                                getUser(Integer.parseInt(tokens[0])),
                                new AbstractMap.SimpleEntry<>(
                                    new Date(Long.parseLong(tokens[1])),
                                    new Date(Long.parseLong(tokens[2]))
                                )
                            )
                    );
                }
                // read all users
                parseCSV(
                    usersFile,
                    tokens -> {
                        User user = new User(
                            tokens[0],
                            Integer.parseInt(tokens[1])
                        );
                        user.setBorrowExpired(Boolean.getBoolean(tokens[2]));
                        user.setBorrowExpiredDays(Integer.parseInt(tokens[3]));
                        addUser(user);
                    }
                );
                // add borrowed books for each user
                for (User user : users){
                    long userId = user.getId();
                    File userFile = new File(
                            usersDirectory.getPath()+"/"+userId+".csv"
                    );

                    parseCSV(
                        userFile,
                        tokens ->
                            user.borrowBook(
                                getBook(Integer.parseInt(tokens[0]))
                            )
                    );
                }
                // read blacklist
                parseCSV(
                    blacklistFile,
                    tokens ->
                        addToBlacklist(
                            getUser(Integer.parseInt(tokens[0])),
                            Long.parseLong(tokens[1])
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
                // TODO proper exception handling
            }
        }

    }

    private void parseCSV(File file, readableField rf)
            throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(file));
        String[] tokens;
        while ((tokens = csvReader.readNext()) != null){
            rf.read(tokens);
        }

    }

    private void addToBlacklist(User user, long time) {
        if (blacklist.containsKey(user))
            blacklist.replace(user, new Date(time));
        else
            blacklist.put(user, new Date(time));
    }

    private User getUser(int id) {
        return users.get(id);
    }

    private void addUser(User user) {
        users.add(user);
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void borrowBook(User user, Book book) {
        if (!user.canBorrowBook()) return;
        if (blacklist.containsKey(user)) return;

        user.borrowBook(book);
        book.setAvailableForBorrow(false);
        book.writeBorrowLog(user);
    }

    public boolean doesBookExist(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForBorrow)
                .findFirst();
        return b.isPresent();
    }

    public boolean doesBookExist(int id) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForBorrow)
                .findFirst();
        return b.isPresent();
    }

    public Book getBook(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getAuthor().equals(Author))
                .filter(book -> book.getTitle().equals(Title))
                .filter(Book::isAvailableForBorrow)
                .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    public Book getBook(int id) {
        Optional<Book> b;
        b = books.stream()
                .filter(book -> book.getId() == id)
                .filter(Book::isAvailableForBorrow)
                .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

}
