package com.github.shchurov.prefseditor.presentation;

import com.github.shchurov.prefseditor.model.Preference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EditValueDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JTextField valueTextField;
    private Preference preference;

    EditValueDialog(@Nullable Project project, Preference preference) {
        super(project);
        this.preference = preference;
        setTitle("Edit Value");
        valueTextField.setText(preference.getValue().toString());
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return valueTextField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        try {
            parseNewValue();
        } catch (NumberFormatException e) {
            return new ValidationInfo("Invalid value", valueTextField);
        }
        return null;
    }

    private Object parseNewValue() throws NumberFormatException {
        String text = valueTextField.getText();
        switch (preference.getType()) {
            case STRING:
                return text;
            case FLOAT:
                return Float.parseFloat(text);
            case INTEGER:
                return Integer.parseInt(text);
            case LONG:
                return Long.parseLong(text);
            default:
                throw new IllegalArgumentException(preference.getType().toString());
        }
    }

    @Override
    protected void doOKAction() {
        preference.setValue(parseNewValue());
        super.doOKAction();
    }

}
