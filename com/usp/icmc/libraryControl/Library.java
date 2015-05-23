package com.usp.icmc.libraryControl;

import java.io.*;
import java.util.*;

public class Library {

    private ArrayList<Book> books;
    private Map<User, Integer> blacklist;

    public Library(String path){

        books = new ArrayList<>();
        blacklist = new HashMap<>();

        File file = new File(path);
        BufferedReader br = null;
        if(file.exists() && file.canRead()){
            try {
                br = new BufferedReader(new FileReader(file));
                String line;
                // read the books info
                while(!Objects.equals(line = br.readLine(), "")){
                    String[] booksInfo = line.split(",");
                    for(int i = 0; i < booksInfo.length; i += 3){
                        books.add(
                                new Book(
                                    booksInfo[i],
                                    booksInfo[i + 1],
                                    booksInfo[i + 2].equals("true")
                                )
                        );
                    }
                }
                // read the blacklist info
                while((line = br.readLine()) != null){
                    String[] blacklistInfo = line.split(",");
                    for(int i = 0; i < blacklistInfo.length; i += 3){
                        blacklist.put(
                                new User(
                                        blacklistInfo[i],
                                        Integer.parseInt(blacklistInfo[i + 1])
                                ),
                                Integer.parseInt(blacklistInfo[i + 2])
                        );
                    }
                }
            } catch (IOException ignored) {
                // This shouldn't happen because of the if test
            }finally{
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    // File could not be closed
                }
            }
        }
    }

    public void addBook(Book book){
        books.add(book);
    }

    public void rentBook(User user, Book book){
        if(!user.canRentBook()) return;
        if(blacklist.containsKey(user)) return;

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
