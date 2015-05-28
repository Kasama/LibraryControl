package com.usp.icmc.library;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Library implements Observer {

    private ArrayList<User> users;
    private ArrayList<Book> books;
    private Map<User, Date> blacklist;
    private TimeController timeController;
    private String dataDirectory;

    public Library(String dataDirectory) {

        books = new ArrayList<>();
        users = new ArrayList<>();
        blacklist = new HashMap<>();
        timeController = TimeController.getInstance();
        timeController.addObserver(this);
        this.dataDirectory = dataDirectory;

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
                "argument is not a dataDirectory path"
            );

        if (
            !booksFile.exists() ||
            !blacklistFile.exists() ||
            !usersFile.exists()
            )
        {
            try {
                usersFile.createNewFile();
                booksFile.createNewFile();
                blacklistFile.createNewFile();
                booksDirectory.mkdir();
                usersDirectory.mkdir();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO proper exception handling
            }
        } else {
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
                                Boolean.getBoolean(tokens[2]),
                                Long.parseLong(tokens[3])
                            )
                        )
                );
                // fill in books borrow logs
                for (Book book : books) {
                    long bookId = book.getId();
                    File bookFile = new File(
                        booksDirectory.getPath() + "/" + bookId + "Log.csv"
                    );

                    parseCSV(
                        bookFile,
                        tokens ->
                            book.getBorrowLog().add(
                                new BorrowedLog(
                                    getUser(
                                        Integer.parseInt(tokens[0])
                                    ), new Date(Long.parseLong(tokens[1])),
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
                            Long.parseLong(tokens[3]),
                            Integer.parseInt(tokens[1])
                        );
                        user.setBorrowExpired(Boolean.getBoolean(tokens[2]));
                        addUser(user);
                    }
                );
                // add borrowed books for each user
                for (User user : users) {
                    long userId = user.getId();
                    File userFile = new File(
                        usersDirectory.getPath() + "/" + userId + ".csv"
                    );

                    parseCSV(
                        userFile,
                        tokens -> {
                            if (doesBookExist(Integer.parseInt(tokens[0])))
                                user.borrowBook(
                                    getBook(Integer.parseInt(tokens[0]))
                                );
                        }
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

    public void storeToDataDirectory(String dataDirectory) {

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        CSVWriter csvWriter;

        try {
            csvWriter = new CSVWriter(new FileWriter(blacklistFile));
            for (Map.Entry<User, Date> m : blacklist.entrySet()) {
                User user = m.getKey();
                Date date = m.getValue();
                String[] nextLine;
                nextLine = new String[2];
                nextLine[0] = String.valueOf(user.getId());
                nextLine[1] = String.valueOf(date.getTime());
                csvWriter.writeNext(nextLine);
            }
            csvWriter.flush();

            csvWriter = new CSVWriter(new FileWriter(usersFile));
            for (User user : users) {
                File userFile = new File(
                    usersDirectory.getPath() + "/" + user.getId() + ".csv"
                );
                CSVWriter writer = new CSVWriter(new FileWriter(userFile));
                ArrayList<Book> borrowedBooks;
                borrowedBooks = user.getBorrowedBooks();
                for (Book book : borrowedBooks) {
                    String[] nextLine = new String[1];
                    nextLine[0] = String.valueOf(book.getId());
                    writer.writeNext(nextLine);
                }
                writer.flush();
                String[] nextLine = new String[4];
                nextLine[0] = user.getName();
                nextLine[1] = String.valueOf(user.getType());
                nextLine[2] = String.valueOf(user.isBorrowExpired());
                nextLine[3] = String.valueOf(user.getId());
                csvWriter.writeNext(nextLine);
            }
            csvWriter.flush();

            csvWriter = new CSVWriter(new FileWriter(booksFile));
            for (Book book : books) {
                CSVWriter writer = new CSVWriter(
                    new FileWriter(
                        new File(
                            booksDirectory.getPath() + "/" + book.getId() +
                            "Log.csv"
                        )
                    )
                );
                for (
                    BorrowedLog borrowedLog :
                    book.getBorrowLog()
                    ) {
                    String[] nextLine = new String[3];
                    nextLine[0] = String.valueOf(borrowedLog.getUser().getId());
                    nextLine[2] = String
                        .valueOf(borrowedLog.getBorrowedDate().getTime());
                    nextLine[3] = String
                        .valueOf(borrowedLog.getReturnDate().getTime());
                    writer.writeNext(nextLine);
                }
                writer.flush();
                String[] nextLine = new String[4];
                nextLine[0] = book.getAuthor();
                nextLine[1] = book.getTitle();
                nextLine[2] = String.valueOf(book.canBeBorrowedByAnyone());
                nextLine[3] = String.valueOf(book.getId());
                csvWriter.writeNext(nextLine);
            }
            csvWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO proper exception handling
        }

    }

    public void removeUserFromBlacklist(User user) {
        blacklist.remove(user);
        user.setBorrowExpired(false);
    }

    private void parseCSV(File file, ReadableField rf)
        throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(file));
        String[] tokens;
        while ((tokens = csvReader.readNext()) != null) {
            rf.read(tokens);
        }

    }

    private void addToBlacklist(User user, long time) {
        if (blacklist.containsKey(user))
            blacklist.replace(user, new Date(time));
        else
            blacklist.put(user, new Date(time));
        user.setBorrowExpired(true);
    }

    public User getUser(long id) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getId() == id
            )
            .findFirst();
        return u.get();
    }

    public void addUser(User user) {
        if(!users.contains(user))
            users.add(user);
    }

    public void addBook(Book book) {
        if (!books.contains(book))
            books.add(book);
    }

    public void borrowBook(User user, Book book) {
        if (!user.canBorrowBook()) return;
        if (isBlacklisted(user)) return;

        user.borrowBook(book);
        book.setAvailableForBorrow(false);
        book.writeBorrowLog(user);
    }

    public void returnBook(User user, Book book) {
        if (user.hasBook(book)) {
            user.returnBook(book);
            book.setAvailableForBorrow(true);
        }
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

    public Book getBook(long id) {
        Optional<Book> b;
        b = books
            .stream()
            .filter(book -> book.getId() == id)
//            .filter(Book::isAvailableForBorrow)
            .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    private void addBlacklistTime(User user, long time) {
        if (isBlacklisted(user)){
            blacklist.replace(
                user,
                timeController.addTime(blacklist.get(user), time)
            );
        }else {
            addToBlacklist(user, time);
        }
    }

    private boolean isBlacklisted(User user) {
        return blacklist.containsKey(user);
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void removeUser(long id) {
        users.remove(getUser(id));
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void removeBook(long id) {
        books.remove(getBook(id));
    }

    @Override
    public void update(Observable o, Object ignored) {
        Date today = timeController.getDate();
        for (User user : users) {
            if (blacklist.get(user).before(today))
                removeUserFromBlacklist(user);
            for (Book book : user.getBorrowedBooks()) {
                int pos = book.getBorrowLog().size();
                BorrowedLog log = book.getBorrowLog().get(pos);
                Date returnDate = log.getReturnDate();
                int retCmpToday = returnDate.compareTo(today);
                if (retCmpToday > 0) {
                    long difference = returnDate.getTime() - today.getTime();
                    addBlacklistTime(user, difference);
                }
            }
        }
    }
}