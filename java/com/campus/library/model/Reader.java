package com.campus.library.model;

public class Reader {
    private Integer id;
    private String no; // 学号/工号
    private String name;
    private String role; // student | staff
    private int maxBorrow;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNo() { return no; }
    public void setNo(String no) { this.no = no; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getMaxBorrow() { return maxBorrow; }
    public void setMaxBorrow(int maxBorrow) { this.maxBorrow = maxBorrow; }
}
