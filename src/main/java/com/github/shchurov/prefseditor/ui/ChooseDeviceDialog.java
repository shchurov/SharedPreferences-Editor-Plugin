package com.github.shchurov.prefseditor.ui;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ChooseDeviceDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> comboBox;
    private IDevice[] devices;

    public ChooseDeviceDialog(@Nullable Project project, IDevice[] devices) {
        super(project);
        setTitle("Choose Device");
        this.devices = devices;
        setupComboBox();
        init();
    }

    private void setupComboBox() {
        for (IDevice d : devices) {
            String name = d.getProperty(IDevice.PROP_DEVICE_MANUFACTURER) + " "
                    + d.getProperty(IDevice.PROP_DEVICE_MODEL) + " [" + d.getSerialNumber() + "]";
            comboBox.addItem(name);
        }
        comboBox.setSelectedIndex(0);
    }

    public IDevice showAndGetDevice() {
        show();
        return isOK() ? devices[comboBox.getSelectedIndex()] : null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

}
