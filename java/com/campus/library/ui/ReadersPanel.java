package com.campus.library.ui;

import com.campus.library.model.Reader;
import com.campus.library.service.ReaderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReadersPanel extends JPanel {
    private final ReaderService service = new ReaderService();
    private final JTextField tfKeyword = new JTextField(15);
    private final JComboBox<String> cbRoleFilter = new JComboBox<>(new String[]{"","student","staff"});
    private final JTable table = new JTable();

    private final JTextField tfNo = new JTextField(10);
    private final JTextField tfName = new JTextField(10);
    private final JComboBox<String> cbRole = new JComboBox<>(new String[]{"student","staff"});
    private final JSpinner spMaxBorrow = new JSpinner(new SpinnerNumberModel(5,1,50,1));

    private Integer editingId = null;

    public ReadersPanel(){
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("关键词:")); top.add(tfKeyword);
        top.add(new JLabel("身份:")); top.add(cbRoleFilter);
        JButton btnSearch = new JButton("查询"); btnSearch.addActionListener(e -> load());
        JButton btnRefresh = new JButton("刷新" ); btnRefresh.addActionListener(e -> { tfKeyword.setText(""); cbRoleFilter.setSelectedIndex(0); load();});
        top.add(btnSearch); top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"ID","学/工号","姓名","身份","上限"}){ public boolean isCellEditable(int r,int c){return false;} });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("学/工号:")); form.add(tfNo);
        form.add(new JLabel("姓名:")); form.add(tfName);
        form.add(new JLabel("身份:")); form.add(cbRole);
        form.add(new JLabel("上限:")); form.add(spMaxBorrow);
        JButton btnSave = new JButton("保存"); btnSave.addActionListener(e -> save());
        JButton btnNew = new JButton("新建"); btnNew.addActionListener(e -> clearForm());
        JButton btnDelete = new JButton("删除"); btnDelete.addActionListener(e -> deleteSelected());
        form.add(btnSave); form.add(btnNew); form.add(btnDelete);
        add(form, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) fillFormFromSelection();});
        load();
    }

    private void load(){
        String role = (String) cbRoleFilter.getSelectedItem();
            List<Reader> list = service.list(tfKeyword.getText().trim(), (String)cbRoleFilter.getSelectedItem());
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
            int idx = 1;
            for (Reader r : list) {
                m.addRow(new Object[]{idx++, r.getNo(), r.getName(), r.getRole(), r.getMaxBorrow(), r.getId()});
            }
            // 隐藏ID列
            if (table.getColumnModel().getColumnCount() == 6) {
                table.getColumnModel().removeColumn(table.getColumnModel().getColumn(5));
            }
    }

    private void fillFormFromSelection(){
        int row = table.getSelectedRow(); if (row<0) return;
        DefaultTableModel m = (DefaultTableModel) table.getModel();
            int modelRow = table.convertRowIndexToModel(row);
            editingId = (Integer)m.getValueAt(modelRow,5);
        tfNo.setText(String.valueOf(m.getValueAt(row,1)));
        tfName.setText(String.valueOf(m.getValueAt(row,2)));
        cbRole.setSelectedItem(String.valueOf(m.getValueAt(row,3)));
        spMaxBorrow.setValue(m.getValueAt(row,4));
    }

    private void clearForm(){ editingId=null; tfNo.setText(""); tfName.setText(""); cbRole.setSelectedIndex(0); spMaxBorrow.setValue(5); table.clearSelection(); }

    private void save(){
        if (tfNo.getText().isBlank() || tfName.getText().isBlank()) { JOptionPane.showMessageDialog(this, "学/工号与姓名必填"); return; }
        Reader r = new Reader();
        r.setId(editingId); r.setNo(tfNo.getText().trim()); r.setName(tfName.getText().trim()); r.setRole((String) cbRole.getSelectedItem()); r.setMaxBorrow((Integer) spMaxBorrow.getValue());
        try { service.save(r); load(); clearForm(); JOptionPane.showMessageDialog(this, "保存成功"); } catch (Exception ex){ JOptionPane.showMessageDialog(this, "保存失败: "+ex.getMessage()); }
    }

    private void deleteSelected(){
        int row = table.getSelectedRow(); if (row<0) { JOptionPane.showMessageDialog(this, "请选择一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (Integer)((DefaultTableModel)table.getModel()).getValueAt(modelRow,5);
        if (JOptionPane.showConfirmDialog(this, "确认删除?", "提示", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try { service.delete(id); load(); clearForm(); } catch (Exception ex){ JOptionPane.showMessageDialog(this, "删除失败: "+ex.getMessage()); }
        }
    }
}
