package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FileContentDialog extends DialogWrapper {

    private Project project;
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JTable contentTable;

    private PreferencesTableModel tableModel = new PreferencesTableModel();
    private List<Preference> preferences;

    public FileContentDialog(@Nullable Project project, List<Preference> preferences) {
        super(project);
        this.project = project;
        this.preferences = preferences;
        setTitle("SharedPreferences Editor");
        setupTable();
        init();
    }

    private void setupTable() {
        tableModel.setPreferencesList(preferences);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentTable.setRowHeight(25);
        contentTable.setBorder(BorderFactory.createLineBorder(contentTable.getGridColor(), 1));
        contentTable.getTableHeader().setReorderingAllowed(false);
        contentTable.setModel(tableModel);
        contentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    if (contentTable.columnAtPoint(p) == 2) {
                        handleEditValueClick(contentTable.rowAtPoint(p));
                    }
                }
            }
        });
    }

    private void handleEditValueClick(int index) {
        Preference preference = preferences.get(index);
        if (preference.getType() == Preference.Type.BOOLEAN) {
            preference.setValue(!(boolean) preference.getValue());
        } else if (preference.getType() == Preference.Type.STRING_SET) {
            //TODO:
        } else {
            new EditValueDialog(project, preference).show();
        }
        tableModel.fireTableCellUpdated(index, 2);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    private static class PreferencesTableModel extends AbstractTableModel {
        private static final String[] COLUMN_NAMES = {"Key", "Type", "Value"};

        private List<Preference> preferences;

        void setPreferencesList(List<Preference> preferences) {
            this.preferences = preferences;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return preferences == null ? 0 : preferences.size();
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Preference p = preferences.get(rowIndex);
            if (columnIndex == 0) {
                return p.getKey();
            } else if (columnIndex == 1) {
                return p.getType().name;
            } else if (columnIndex == 2) {
                return p.getValue();
            } else {
                throw new IllegalArgumentException(String.valueOf(columnIndex));
            }
        }
    }

}
