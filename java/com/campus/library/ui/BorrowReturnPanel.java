package com.campus.library.ui;

import com.campus.library.model.LoanRecord;
import com.campus.library.model.Reader;
import com.campus.library.service.LoanService;
import com.campus.library.service.ReaderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class BorrowReturnPanel extends JPanel {
    private final LoanService loanService = new LoanService();
    private final ReaderService readerService = new ReaderService();

    private final JTextField tfReaderNo = new JTextField(12);
    private final JTextField tfBookCode = new JTextField(12);
    private final JTable table = new JTable();
    private Integer currentReaderId = null;

    public BorrowReturnPanel(){
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("读者学/工号:")); top.add(tfReaderNo);
        JButton btnLoad = new JButton("加载读者借阅");
        btnLoad.addActionListener(e -> loadCurrentLoans());
        top.add(btnLoad);
        top.add(new JLabel("图书编号:")); top.add(tfBookCode);
        JButton btnBorrow = new JButton("借阅"); btnBorrow.addActionListener(e -> borrow());
        top.add(btnBorrow);
        add(top, BorderLayout.NORTH);

        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"序号","记录ID","图书ID","借出日","应还日","状态","续借次数","归还日"}){ public boolean isCellEditable(int r,int c){return false;} });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnReturn = new JButton("归还"); btnReturn.addActionListener(e -> doReturn());
        JButton btnRenew = new JButton("续借"); btnRenew.addActionListener(e -> doRenew());
        bottom.add(btnReturn); bottom.add(btnRenew);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadCurrentLoans(){
        String no = tfReaderNo.getText().trim();
        if (no.isBlank()) { JOptionPane.showMessageDialog(this, "请输入读者学/工号"); return; }
        Optional<Reader> ro = readerService.findByNo(no);
        if (ro.isEmpty()) { JOptionPane.showMessageDialog(this, "读者不存在"); return; }
        currentReaderId = ro.get().getId();
        List<LoanRecord> list = loanService.currentLoans(currentReaderId);
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        int idx = 1;
        for (LoanRecord l : list) {
            m.addRow(new Object[]{idx++, l.getId(), l.getBookId(), l.getBorrowDate(), l.getDueDate(), l.getStatus(), l.getRenewCount(), l.getReturnDate()});
        }
    }

    private void borrow(){
        if (tfReaderNo.getText().trim().isBlank() || tfBookCode.getText().trim().isBlank()){
            JOptionPane.showMessageDialog(this, "请输入读者学/工号与图书编号"); return;
        }
        String msg = loanService.borrow(tfReaderNo.getText().trim(), tfBookCode.getText().trim());
        JOptionPane.showMessageDialog(this, msg);
        loadCurrentLoans();
    }

    private void doReturn(){
        int row = table.getSelectedRow(); if (row<0) { JOptionPane.showMessageDialog(this, "请选择一条记录"); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int loanId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow,1);
        String msg = loanService.returnBook(loanId);
        JOptionPane.showMessageDialog(this, msg);
        loadCurrentLoans();
    }

    private void doRenew(){
        int row = table.getSelectedRow(); if (row<0) { JOptionPane.showMessageDialog(this, "请选择一条记录"); return; }
        int modelRow2 = table.convertRowIndexToModel(row);
        int loanId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow2,1);
        String msg = loanService.renew(loanId);
        JOptionPane.showMessageDialog(this, msg);
        loadCurrentLoans();
    }
}
