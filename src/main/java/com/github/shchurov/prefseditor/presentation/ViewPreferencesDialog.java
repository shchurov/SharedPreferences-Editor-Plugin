package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.helpers.PreferencesParser;
import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ViewPreferencesDialog extends DialogWrapper {

    private Project project;
    private JPanel rootPanel;
    private JComboBox<String> filesComboBox;
    private JScrollPane scrollPane;
    private JTable contentTable;

    private String filesDir;
    private Map<String, String> unifiedNamesMap;
    private PreferencesTableModel tableModel = new PreferencesTableModel();
    private List<Preference> preferences;

    public ViewPreferencesDialog(@Nullable Project project, String filesDir, Map<String, String> unifiedNamesMap) {
        super(project);
        this.project = project;
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

    private void showFileContent(String fileName) {
        String path = filesDir + File.separator + unifiedNamesMap.get(fileName);
        try {
            preferences = new PreferencesParser().parse(path);
            tableModel.setPreferencesList(preferences);
        } catch (PreferencesParser.ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
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
                        handleValueDoubleClick(contentTable.rowAtPoint(p));
                    }
                }
            }
        });
    }

    private void handleValueDoubleClick(int index) {
        Preference preference = preferences.get(index);
        if (preference.getType() == Preference.Type.BOOLEAN) {
            preference.setValue(!(boolean) preference.getValue());
        } else if (preference.getType() == Preference.Type.STRING_SET) {
            //TODO:
        } else {
            System.out.println("1: " + Thread.currentThread().getName());
            new EditValueDialog(project, preference).show();
            System.out.println("2: " + Thread.currentThread().getName());
        }
        tableModel.fireTableCellUpdated(index, 2);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
