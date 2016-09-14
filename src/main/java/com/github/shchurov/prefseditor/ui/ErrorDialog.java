package com.github.shchurov.prefseditor.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ErrorDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JLabel label;

    public ErrorDialog(@Nullable Project project, String text) {
        super(project);
        setTitle("SharedPreferences Editor");
        label.setText(text);
        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }
}
