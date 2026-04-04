package com.campus.library.dao;

import com.campus.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    int insert(Book b);
    int update(Book b);
    int deleteById(int id);
    Optional<Book> findById(int id);
    Optional<Book> findByCode(String code);
    List<Book> findAll(String keyword, String category);
    int increaseAvailable(int bookId, int delta);
}
