package com.github.shchurov.prefseditor.ui;

import com.github.shchurov.prefseditor.helpers.ProjectUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ChooseModuleDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> comboBox;
    private List<AndroidFacet> facets;

    public ChooseModuleDialog(@Nullable Project project) {
        super(project);
        setTitle("Choose Module");
        facets = ProjectUtils.getFacets(project);
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
