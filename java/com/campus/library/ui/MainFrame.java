package com.campus.library.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("校园图书借阅管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("图书管理", new BooksPanel());
        tabs.addTab("读者管理", new ReadersPanel());
        tabs.addTab("借阅归还", new BorrowReturnPanel());
        tabs.addTab("统计", new StatsPanel());

        add(tabs, BorderLayout.CENTER);
    }
}
