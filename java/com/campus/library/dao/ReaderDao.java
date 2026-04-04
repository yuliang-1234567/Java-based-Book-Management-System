package com.campus.library.dao;

import com.campus.library.model.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderDao {
    int insert(Reader r);
    int update(Reader r);
    int deleteById(int id);
    Optional<Reader> findById(int id);
    Optional<Reader> findByNo(String no);
    List<Reader> findAll(String keyword, String role);
}
