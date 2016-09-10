package com.github.shchurov.prefseditor.ui;

import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class EditStringSetDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton addBtn;
    private JButton removeBtn;
    private Preference preference;
    private List<String> tableItems;
    private Set<String> resultSet;
    private CustomTableModel tableModel = new CustomTableModel();

    @SuppressWarnings("unchecked")
    EditStringSetDialog(@Nullable Project project, Preference preference) {
        super(project);
        this.preference = preference;
        Set<String> set = (Set<String>) preference.getValue();
        tableItems = new ArrayList<>(set);
        resultSet = new TreeSet<>(set);
        setTitle("Edit Value");
        setupOkAction();
        setupTable();
        setupAddButton();
        setupRemoveButton();
        init();
    }

    private void setupOkAction() {
        myOKAction = new OkAction() {
            @Override
            protected void doAction(ActionEvent e) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                super.doAction(e);
            }
        };
    }

    private void setupTable() {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        table.setRowHeight(25);
        table.setSurrendersFocusOnKeystroke(true);
        table.setBorder(BorderFactory.createLineBorder(table.getGridColor(), 1));
        table.setModel(tableModel);
    }

    private void setupAddButton() {
        addBtn.addActionListener(e -> {
            tableItems.add("");
            resultSet.add("");
            int i = tableItems.size() - 1;
            tableModel.fireTableRowsInserted(i, i);
            table.editCellAt(i, 0);
            table.getEditorComponent().requestFocus();
        });
    }

    private void setupRemoveButton() {
        removeBtn.addActionListener(e -> {
            int i = table.getSelectedRow();
            if (i == -1) {
                return;
            }
            String item = tableItems.remove(i);
            if (!tableItems.contains(item)) {
                resultSet.remove(item);
            }
            tableModel.fireTableRowsDeleted(i, i);
            if (tableItems.size() > 0) {
                table.changeSelection(i, 0, false, false);
            }
        });
    }

    @Override
    protected void doOKAction() {
        preference.setValue(resultSet);
        System.out.println(resultSet);
        super.doOKAction();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (resultSet.size() != tableItems.size()) {
            return new ValidationInfo("Set contains duplicates", table);
        }
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    private class CustomTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return tableItems.size();
        }

        @Override
        public String getColumnName(int column) {
            return "Item";
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return tableItems.get(rowIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            resultSet.remove(tableItems.get(rowIndex));
            String value = (String) aValue;
            resultSet.add(value);
            tableItems.set(rowIndex, value);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

}
