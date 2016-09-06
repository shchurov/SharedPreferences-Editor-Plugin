package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.model.Preference;

import javax.swing.table.AbstractTableModel;
import java.util.List;

class PreferencesTableModel extends AbstractTableModel {

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