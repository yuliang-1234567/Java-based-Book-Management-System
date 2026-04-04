package com.campus.library.model;

import java.time.LocalDate;

public class LoanRecord {
    private Integer id;
    private Integer bookId;
    private Integer readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int renewCount;
    private String status; // BORROWED | RETURNED | OVERDUE

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }

    public Integer getReaderId() { return readerId; }
    public void setReaderId(Integer readerId) { this.readerId = readerId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getRenewCount() { return renewCount; }
    public void setRenewCount(int renewCount) { this.renewCount = renewCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
