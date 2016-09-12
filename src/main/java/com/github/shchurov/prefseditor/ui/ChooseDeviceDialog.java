package com.github.shchurov.prefseditor.ui;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.shchurov.prefseditor.helpers.exceptions.GetDeviceListException;
import com.github.shchurov.prefseditor.helpers.exceptions.NoDeviceFoundException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ChooseDeviceDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JComboBox<String> comboBox;
    private IDevice[] devices;

    public ChooseDeviceDialog(@Nullable Project project) {
        super(project);
        setTitle("Choose Device");
        initDevices(project);
        setupComboBox();
        init();
    }

    private void initDevices(Project project) {
        AndroidDebugBridge adb = AndroidSdkUtils.getDebugBridge(project);
        if (adb == null) {
            throw new GetDeviceListException();
        }
        devices = adb.getDevices();
        if (devices.length == 0) {
            throw new NoDeviceFoundException();
        }
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
