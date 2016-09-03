package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.helpers.PreferencesParser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class EditorDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> filesComboBox;
    private String filesDir;
    private Map<String, String> unifiedNamesMap;
    private PreferencesParser parser = new PreferencesParser();

    public EditorDialog(@Nullable Project project, String filesDir, Map<String, String> unifiedNamesMap) {
        super(project);
        this.filesDir = filesDir;
        this.unifiedNamesMap = unifiedNamesMap;
        setTitle("SharedPreferences Editor");
        setupComboBox();
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
            Map<String, Object> contentMap = parser.parse(path);
            for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        } catch (PreferencesParser.ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
