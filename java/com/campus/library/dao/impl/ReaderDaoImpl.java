package com.campus.library.dao.impl;

import com.campus.library.dao.ReaderDao;
import com.campus.library.db.Database;
import com.campus.library.model.Reader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDaoImpl implements ReaderDao {
    @Override
    public int insert(Reader r) {
        String sql = "INSERT INTO reader(no,name,role,max_borrow) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNo());
            ps.setString(2, r.getName());
            ps.setString(3, r.getRole());
            ps.setInt(4, r.getMaxBorrow());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) r.setId(rs.getInt(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public int update(Reader r) {
        String sql = "UPDATE reader SET no=?, name=?, role=?, max_borrow=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getNo());
            ps.setString(2, r.getName());
            ps.setString(3, r.getRole());
            ps.setInt(4, r.getMaxBorrow());
            ps.setInt(5, r.getId());
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM reader WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Reader> findById(int id) {
        String sql = "SELECT * FROM reader WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Reader> findByNo(String no) {
        String sql = "SELECT * FROM reader WHERE no=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, no);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Reader> findAll(String keyword, String role) {
        StringBuilder sb = new StringBuilder("SELECT * FROM reader WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (no LIKE ? OR name LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw);
        }
        if (role != null && !role.isBlank()) {
            sb.append(" AND role = ?");
            params.add(role);
        }
        sb.append(" ORDER BY id DESC");
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Reader> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Reader map(ResultSet rs) throws SQLException {
        Reader r = new Reader();
        r.setId(rs.getInt("id"));
        r.setNo(rs.getString("no"));
        r.setName(rs.getString("name"));
        r.setRole(rs.getString("role"));
        r.setMaxBorrow(rs.getInt("max_borrow"));
        return r;
    }
}
