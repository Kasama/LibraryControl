package com.usp.icmc.libraryControl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

        loadFromDataDirectory(dataDirectory);

    }

    private void loadFromDataDirectory(String dataDirectory) {

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

    public void storeToDataDirectory(String dataDirectory){

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        CSVWriter csvWriter;

        try {
            csvWriter = new CSVWriter(new FileWriter(blacklistFile));
            if (!blacklistFile.createNewFile()) {
                blacklistFile.delete();
                blacklistFile.createNewFile();
            }
            for (Map.Entry<User, Date> m : blacklist.entrySet()) {
                User user = m.getKey();
                Date date = m.getValue();
                String[] nextLine;
                nextLine = new String[2];
                nextLine[0] = String.valueOf(user.getId());
                nextLine[1] = String.valueOf(date.getTime());
                csvWriter.writeNext(nextLine);
            }

            if (!usersFile.createNewFile()) {
                usersFile.delete();
                usersFile.createNewFile();
            }
            for (User user : users){
                File userFile = new File(
                    usersDirectory.getPath()+"/"+user.getId()+".csv"
                );
                if (!userFile.createNewFile()) {
                    userFile.delete();
                    userFile.createNewFile();
                }
                csvWriter = new CSVWriter(new FileWriter(userFile));
                Book[] borrowedBooks;
                borrowedBooks = (Book[]) user.getBorrowedBooks().toArray();
                for (Book book : borrowedBooks) {
                    String[] nextLine = new String[1];
                    nextLine[0] = String.valueOf(book.getId());
                    csvWriter.writeNext(nextLine);
                }
                csvWriter = new CSVWriter(new FileWriter(usersFile));
                String[] nextLine = new String[4];
                nextLine[0] = user.getName();
                nextLine[1] = String.valueOf(user.getType());
                nextLine[2] = String.valueOf(user.isBorrowExpired());
                nextLine[3] = String.valueOf(user.getBorrowExpiredDays());
                csvWriter.writeNext(nextLine);

            }

            for (Book book : books){
                csvWriter = new CSVWriter(new FileWriter(
                    new File(
                        booksDirectory.getPath()+"/"+book.getId()+"Log.csv"
                    )
                ));
                for (
                    Map.Entry<User, Map.Entry<Date, Date>> m :
                    book.getBorrowLog().entrySet()
                ){
                    String[] nextLine = new String[3];
                    nextLine[0] = String.valueOf(m.getKey().getId());
                    nextLine[2] = String
                        .valueOf(m.getValue().getKey().getTime());
                    nextLine[3] = String
                        .valueOf(m.getValue().getValue().getTime());
                    csvWriter.writeNext(nextLine);
                }
                csvWriter = new CSVWriter(new FileWriter(booksFile));
                String[] nextLine = new String[3];
                nextLine[0] = book.getAuthor();
                nextLine[1] = book.getTitle();
                nextLine[2] = String.valueOf(book.canBeBorrowedByAnyone());
                csvWriter.writeNext(nextLine);
            }


        } catch (IOException e) {
            e.printStackTrace();
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
