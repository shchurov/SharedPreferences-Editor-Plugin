package com.github.shchurov.prefseditor.ui;

import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.TreeSet;

public class CreatePreferenceDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JTextField keyTextField;
    private JComboBox<String> typeComboBox;

    CreatePreferenceDialog(@Nullable Project project) {
        super(project);
        setTitle("Create Preference");
        setupTypeComboBox();
        init();
    }

    private void setupTypeComboBox() {
        for (Preference.Type type : Preference.Type.values()) {
            typeComboBox.addItem(type.name);
        }
        typeComboBox.setSelectedIndex(0);
    }

    Preference showAndGetPreference() {
        show();
        String key = keyTextField.getText();
        Object value = generateDefaultValue();
        return isOK() ? new Preference(key, value) : null;
    }

    private Object generateDefaultValue() {
        Preference.Type type = Preference.Type.values()[typeComboBox.getSelectedIndex()];
        switch (type) {
            case BOOLEAN:
                return false;
            case FLOAT:
                return 0f;
            case INTEGER:
                return 0;
            case LONG:
                return 0L;
            case STRING:
                return "";
            case STRING_SET:
                return new TreeSet<String>();
            default:
                throw new IllegalArgumentException(type.toString());
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
