package com.campus.library.dao;

import com.campus.library.model.LoanRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRecordDao {
    int insert(LoanRecord l);
    int updateReturn(int id, LocalDate returnDate);
    int updateRenew(int id, LocalDate newDueDate, int newRenewCount);
    Optional<LoanRecord> findById(int id);
    List<LoanRecord> findCurrentLoansByReader(int readerId);
    List<LoanRecord> findHistoryByReader(int readerId);
    long countBorrowedByReader(int readerId);
    long countBorrowedByBook(int bookId);
    List<Object[]> countByCategory();
}
