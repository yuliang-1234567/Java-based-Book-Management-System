package com.campus.library.service;

import com.campus.library.dao.BookDao;
import com.campus.library.dao.impl.BookDaoImpl;
import com.campus.library.model.Book;

import java.util.List;
import java.util.Optional;

public class BookService {
    private final BookDao bookDao = new BookDaoImpl();

    public List<Book> list(String keyword, String category) {
        return bookDao.findAll(keyword, category);
    }

    public Optional<Book> findByCode(String code) { return bookDao.findByCode(code); }

    public void save(Book b) {
        if (b.getId() == null) bookDao.insert(b); else bookDao.update(b);
    }

    public void delete(int id) { bookDao.deleteById(id); }
}
