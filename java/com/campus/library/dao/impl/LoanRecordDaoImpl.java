package com.campus.library.dao.impl;

import com.campus.library.dao.LoanRecordDao;
import com.campus.library.db.Database;
import com.campus.library.model.LoanRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRecordDaoImpl implements LoanRecordDao {
    @Override
    public int insert(LoanRecord l) {
        String sql = "INSERT INTO loan_record(book_id, reader_id, borrow_date, due_date, return_date, renew_count, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getBookId());
            ps.setInt(2, l.getReaderId());
            ps.setDate(3, Date.valueOf(l.getBorrowDate()));
            ps.setDate(4, Date.valueOf(l.getDueDate()));
            if (l.getReturnDate() == null) ps.setNull(5, Types.DATE); else ps.setDate(5, Date.valueOf(l.getReturnDate()));
            ps.setInt(6, l.getRenewCount());
            ps.setString(7, l.getStatus());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) l.setId(rs.getInt(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public int updateReturn(int id, LocalDate returnDate) {
        String sql = "UPDATE loan_record SET return_date=?, status='RETURNED' WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public int updateRenew(int id, LocalDate newDueDate, int newRenewCount) {
        String sql = "UPDATE loan_record SET due_date=?, renew_count=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(newDueDate));
            ps.setInt(2, newRenewCount);
            ps.setInt(3, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<LoanRecord> findById(int id) {
        String sql = "SELECT * FROM loan_record WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<LoanRecord> findCurrentLoansByReader(int readerId) {
        String sql = "SELECT * FROM loan_record WHERE reader_id=? AND status IN ('BORROWED','OVERDUE') ORDER BY id DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<LoanRecord> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<LoanRecord> findHistoryByReader(int readerId) {
        String sql = "SELECT * FROM loan_record WHERE reader_id=? ORDER BY id DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<LoanRecord> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long countBorrowedByReader(int readerId) {
        String sql = "SELECT COUNT(*) FROM loan_record WHERE reader_id=? AND status IN ('BORROWED','OVERDUE')";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long countBorrowedByBook(int bookId) {
        String sql = "SELECT COUNT(*) FROM loan_record WHERE book_id=? AND status IN ('BORROWED','OVERDUE')";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Object[]> countByCategory() {
        String sql = "SELECT b.category, COUNT(l.id) AS cnt FROM loan_record l JOIN book b ON b.id=l.book_id GROUP BY b.category ORDER BY cnt DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Object[]> list = new ArrayList<>();
                while (rs.next()) list.add(new Object[]{rs.getString(1), rs.getLong(2)});
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private LoanRecord map(ResultSet rs) throws SQLException {
        LoanRecord l = new LoanRecord();
        l.setId(rs.getInt("id"));
        l.setBookId(rs.getInt("book_id"));
        l.setReaderId(rs.getInt("reader_id"));
        l.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        l.setDueDate(rs.getDate("due_date").toLocalDate());
        Date rd = rs.getDate("return_date");
        l.setReturnDate(rd == null ? null : rd.toLocalDate());
        l.setRenewCount(rs.getInt("renew_count"));
        l.setStatus(rs.getString("status"));
        return l;
    }
}
