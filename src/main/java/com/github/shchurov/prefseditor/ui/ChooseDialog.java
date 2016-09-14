package com.github.shchurov.prefseditor.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class ChooseDialog<T> extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> comboBox;
    private List<T> items;

    public ChooseDialog(@Nullable Project project, String title, List<T> items) {
        super(project);
        setTitle(title);
        this.items = items;
        setupComboBox();
        init();
    }

    private void setupComboBox() {
        for (T item : items) {
            comboBox.addItem(getItemName(item));
        }
        comboBox.setSelectedIndex(0);
    }

    protected abstract String getItemName(T item);

    public T showAndGetResult() {
        show();
        return isOK() ? items.get(comboBox.getSelectedIndex()) : null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
