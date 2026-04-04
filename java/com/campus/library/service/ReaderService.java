package com.campus.library.service;

import com.campus.library.dao.ReaderDao;
import com.campus.library.dao.impl.ReaderDaoImpl;
import com.campus.library.model.Reader;

import java.util.List;
import java.util.Optional;

public class ReaderService {
    private final ReaderDao readerDao = new ReaderDaoImpl();

    public List<Reader> list(String keyword, String role) { return readerDao.findAll(keyword, role); }

    public Optional<Reader> findByNo(String no) { return readerDao.findByNo(no); }

    public void save(Reader r) { if (r.getId()==null) readerDao.insert(r); else readerDao.update(r); }

    public void delete(int id) { readerDao.deleteById(id); }
}
