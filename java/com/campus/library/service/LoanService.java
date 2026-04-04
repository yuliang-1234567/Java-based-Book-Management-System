package com.campus.library.service;

import com.campus.library.Config;
import com.campus.library.dao.BookDao;
import com.campus.library.dao.LoanRecordDao;
import com.campus.library.dao.ReaderDao;
import com.campus.library.dao.impl.BookDaoImpl;
import com.campus.library.dao.impl.LoanRecordDaoImpl;
import com.campus.library.dao.impl.ReaderDaoImpl;
import com.campus.library.model.Book;
import com.campus.library.model.LoanRecord;
import com.campus.library.model.Reader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanService {
    private final BookDao bookDao = new BookDaoImpl();
    private final ReaderDao readerDao = new ReaderDaoImpl();
    private final LoanRecordDao loanDao = new LoanRecordDaoImpl();

    public String borrow(String readerNo, String bookCode) {
        Optional<Reader> ro = readerDao.findByNo(readerNo);
        if (ro.isEmpty()) return "读者不存在";
        Optional<Book> bo = bookDao.findByCode(bookCode);
        if (bo.isEmpty()) return "图书不存在";
        Reader r = ro.get();
        Book b = bo.get();
        if (b.getAvailableCopies() <= 0) return "当前无可借副本";
        long current = loanDao.countBorrowedByReader(r.getId());
        int max = r.getMaxBorrow() > 0 ? r.getMaxBorrow() : Config.maxBorrowForRole(r.getRole());
        if (current >= max) return "超出可借数量上限";

        LocalDate now = LocalDate.now();
        LoanRecord l = new LoanRecord();
        l.setBookId(b.getId());
        l.setReaderId(r.getId());
        l.setBorrowDate(now);
        l.setDueDate(now.plusDays(Config.loanDays()));
        l.setStatus("BORROWED");
        l.setRenewCount(0);
        loanDao.insert(l);
        bookDao.increaseAvailable(b.getId(), -1);
        return "借阅成功，归还截止: " + l.getDueDate();
    }

    public String returnBook(int loanId) {
        Optional<LoanRecord> lo = loanDao.findById(loanId);
        if (lo.isEmpty()) return "借阅记录不存在";
        LoanRecord l = lo.get();
        if (!"BORROWED".equals(l.getStatus()) && !"OVERDUE".equals(l.getStatus())) return "该记录已归还";
        loanDao.updateReturn(l.getId(), LocalDate.now());
        bookDao.increaseAvailable(l.getBookId(), +1);
        return "归还成功";
    }

    public String renew(int loanId) {
        Optional<LoanRecord> lo = loanDao.findById(loanId);
        if (lo.isEmpty()) return "借阅记录不存在";
        LoanRecord l = lo.get();
        if (!"BORROWED".equals(l.getStatus())) return "已归还或状态不允许续借";
        if (l.getRenewCount() >= 1) return "续借次数已达上限(1次)";
        LocalDate newDue = l.getDueDate().plusDays(Config.renewDays());
        loanDao.updateRenew(l.getId(), newDue, l.getRenewCount()+1);
        return "续借成功，新截止: " + newDue;
    }

    public List<LoanRecord> currentLoans(int readerId) { return loanDao.findCurrentLoansByReader(readerId); }
    public List<LoanRecord> history(int readerId) { return loanDao.findHistoryByReader(readerId); }
}
