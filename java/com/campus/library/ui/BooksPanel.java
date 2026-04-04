package com.campus.library.ui;

import com.campus.library.model.Book;
import com.campus.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BooksPanel extends JPanel {
    private final BookService service = new BookService();
    private final JTextField tfKeyword = new JTextField(15);
    private final JTextField tfCategory = new JTextField(10);
    private final JTable table = new JTable();
    private final List<Integer> rowIds = new ArrayList<>();

    private final JTextField tfCode = new JTextField(10);
    private final JTextField tfTitle = new JTextField(12);
    private final JTextField tfAuthor = new JTextField(10);
    private final JTextField tfBCategory = new JTextField(8);
    private final JSpinner spTotal = new JSpinner(new SpinnerNumberModel(1,1,999,1));
    private final JSpinner spAvail = new JSpinner(new SpinnerNumberModel(1,0,999,1));

    private Integer editingId = null;

    public BooksPanel() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("关键词:")); top.add(tfKeyword);
        top.add(new JLabel("分类:")); top.add(tfCategory);
        JButton btnSearch = new JButton("查询");
        JButton btnRefresh = new JButton("刷新");
        btnSearch.addActionListener(e -> load());
        btnRefresh.addActionListener(e -> { tfKeyword.setText(""); tfCategory.setText(""); load(); });
        top.add(btnSearch); top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"编号","名称","作者","分类","总册","可借"}){
            public boolean isCellEditable(int r,int c){return false;}
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("编号:")); form.add(tfCode);
        form.add(new JLabel("名称:")); form.add(tfTitle);
        form.add(new JLabel("作者:")); form.add(tfAuthor);
        form.add(new JLabel("分类:")); form.add(tfBCategory);
        form.add(new JLabel("总册:")); form.add(spTotal);
        form.add(new JLabel("可借:")); form.add(spAvail);
        JButton btnSave = new JButton("保存");
        JButton btnNew = new JButton("新建");
        JButton btnDelete = new JButton("删除");
        btnSave.addActionListener(e -> save());
        btnNew.addActionListener(e -> clearForm());
        btnDelete.addActionListener(e -> deleteSelected());
        form.add(btnSave); form.add(btnNew); form.add(btnDelete);
        add(form, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        load();
    }

    private void load(){
        List<Book> list = service.list(tfKeyword.getText().trim(), tfCategory.getText().trim());
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        rowIds.clear();
        for (Book b : list) {
            m.addRow(new Object[]{b.getCode(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getTotalCopies(), b.getAvailableCopies()});
            rowIds.add(b.getId());
        }
    }

    private void fillFormFromSelection(){
        int row = table.getSelectedRow();
        if (row < 0) return;
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        int modelRow = table.convertRowIndexToModel(row);
        editingId = rowIds.get(modelRow);
        tfCode.setText(String.valueOf(m.getValueAt(modelRow,0)));
        tfTitle.setText(String.valueOf(m.getValueAt(modelRow,1)));
        tfAuthor.setText(String.valueOf(m.getValueAt(modelRow,2)));
        tfBCategory.setText(String.valueOf(m.getValueAt(modelRow,3)));
        spTotal.setValue(m.getValueAt(modelRow,4));
        spAvail.setValue(m.getValueAt(modelRow,5));
    }

    private void clearForm(){
        editingId = null;
        tfCode.setText(""); tfTitle.setText(""); tfAuthor.setText(""); tfBCategory.setText("");
        spTotal.setValue(1); spAvail.setValue(1);
        table.clearSelection();
    }

    private void save(){
        if (tfCode.getText().isBlank() || tfTitle.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "编号和名称必填");
            return;
        }
        Book b = new Book();
        b.setId(editingId);
        b.setCode(tfCode.getText().trim());
        b.setTitle(tfTitle.getText().trim());
        b.setAuthor(tfAuthor.getText().trim());
        b.setCategory(tfBCategory.getText().trim());
        b.setTotalCopies((Integer) spTotal.getValue());
        b.setAvailableCopies((Integer) spAvail.getValue());
        try {
            service.save(b);
            load();
            clearForm();
            JOptionPane.showMessageDialog(this, "保存成功");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }

    private void deleteSelected(){
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请选择一行"); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int id = rowIds.get(modelRow);
        if (JOptionPane.showConfirmDialog(this, "确认删除?", "提示", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try { service.delete(id); load(); clearForm(); } catch (Exception ex){ JOptionPane.showMessageDialog(this, "删除失败: "+ex.getMessage()); }
        }
    }
}
