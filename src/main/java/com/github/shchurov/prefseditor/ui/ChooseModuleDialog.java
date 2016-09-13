package com.github.shchurov.prefseditor.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ChooseModuleDialog extends DialogWrapper {

    private List<AndroidFacet> facets;
    private JPanel rootPanel;
    private JComboBox<String> comboBox;

    public ChooseModuleDialog(@Nullable Project project, List<AndroidFacet> facets) {
        super(project);
        this.facets = facets;
        setTitle("Choose Module");
        setupComboBox();
        init();
    }

    private void setupComboBox() {
        for (AndroidFacet f : facets) {
            comboBox.addItem(f.getModule().getName());
        }
        comboBox.setSelectedIndex(0);
    }

    public AndroidFacet showAndGetModule() {
        show();
        return isOK() ? facets.get(comboBox.getSelectedIndex()) : null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
