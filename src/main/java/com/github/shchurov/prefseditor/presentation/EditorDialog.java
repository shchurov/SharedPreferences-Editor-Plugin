package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.helpers.PreferencesParser;
import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.List;
import java.util.Map;

public class EditorDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> filesComboBox;
    private JTable contentTable;
    private String filesDir;
    private Map<String, String> unifiedNamesMap;
    private PreferencesParser parser = new PreferencesParser();
    private ContentTableModel tableModel = new ContentTableModel();

    public EditorDialog(@Nullable Project project, String filesDir, Map<String, String> unifiedNamesMap) {
        super(project);
        this.filesDir = filesDir;
        this.unifiedNamesMap = unifiedNamesMap;
        setTitle("SharedPreferences Editor");
        setupComboBox();
        setupTable();
        init();
    }

    private void setupComboBox() {
        for (String name : unifiedNamesMap.keySet()) {
            filesComboBox.addItem(name);
        }
        filesComboBox.addActionListener(e -> showFileContent((String) filesComboBox.getSelectedItem()));
        filesComboBox.setSelectedIndex(0);
    }

    private void setupTable() {
        contentTable.setModel(tableModel);
        contentTable.getTableHeader().setReorderingAllowed(false);
        contentTable.getTableHeader().setResizingAllowed(false);

    }

    private void showFileContent(String fileName) {
        String path = filesDir + File.separator + unifiedNamesMap.get(fileName);
        try {
            List<Preference> preferences = parser.parse(path);
            tableModel.setPreferences(preferences);
        } catch (PreferencesParser.ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    private class ContentTableModel extends AbstractTableModel {

        private String[] columnNames = {"Key", "Type", "Value"};
        private List<Preference> preferences;

        void setPreferences(List<Preference> preferences) {
            this.preferences = preferences;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return preferences == null ? 0 : preferences.size();
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Preference p = preferences.get(rowIndex);
            if (columnIndex == 0) {
                return p.getKey();
            } else if (columnIndex == 1) {
                return p.getValue().getClass().getSimpleName();
            } else if (columnIndex == 2) {
                return p.getValue();
            } else {
                throw new IllegalArgumentException(String.valueOf(columnIndex));
            }
        }

    }

}
