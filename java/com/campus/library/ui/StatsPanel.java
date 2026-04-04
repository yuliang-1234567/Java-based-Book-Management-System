package com.campus.library.ui;

import com.campus.library.dao.LoanRecordDao;
import com.campus.library.dao.impl.LoanRecordDaoImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatsPanel extends JPanel {
    private final LoanRecordDao loanDao = new LoanRecordDaoImpl();

    public StatsPanel(){
        setLayout(new BorderLayout());
        add(buildChartPanel(), BorderLayout.CENTER);
        JButton btnRefresh = new JButton("刷新"); btnRefresh.addActionListener(e -> refresh());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);
    }

    private JPanel buildChartPanel(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Object[]> list = loanDao.countByCategory();
        if (list.isEmpty()) {
            return emptyPanel();
        }
        for (Object[] row : list) {
            String category = (String) row[0];
            Number count = (Number) row[1];
            dataset.addValue(count, "借阅量", category);
        }
        JFreeChart chart = ChartFactory.createBarChart("按分类借阅量","分类","借阅次数", dataset);
        return new ChartPanel(chart);
    }

    private JPanel emptyPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("暂无借阅数据", SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    private void refresh(){
        removeAll();
        add(buildChartPanel(), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}
