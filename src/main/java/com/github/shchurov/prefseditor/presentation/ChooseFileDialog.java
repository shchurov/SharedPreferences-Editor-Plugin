package com.github.shchurov.prefseditor.presentation;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class ChooseFileDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> comboBox;

    public ChooseFileDialog(@Nullable Project project, Collection<String> fileNames) {
        super(project);
        setTitle("Choose File");
        setupComboBox(fileNames);
        init();
    }

    private void setupComboBox(Collection<String> fileNames) {
        for (String name : fileNames) {
            comboBox.addItem(name);
        }
        comboBox.setSelectedIndex(0);
    }

    public String showAndGetFileName() {
        show();
        return isOK() ? (String) comboBox.getSelectedItem() : null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
