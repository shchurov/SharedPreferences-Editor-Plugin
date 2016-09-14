package com.github.shchurov.prefseditor;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.shchurov.prefseditor.helpers.*;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.adb.AdbShellHelper;
import com.github.shchurov.prefseditor.helpers.exceptions.*;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.github.shchurov.prefseditor.model.Preference;
import com.github.shchurov.prefseditor.ui.ChooseDialog;
import com.github.shchurov.prefseditor.ui.ErrorDialog;
import com.github.shchurov.prefseditor.ui.FileContentDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.android.util.AndroidUtils;

import java.io.File;
import java.util.*;

public class OpenEditorAction extends AnAction {

    private Project project;

    @Override
    public void actionPerformed(AnActionEvent action) {
        try {
            project = action.getData(PlatformDataKeys.PROJECT);
            if (project == null) {
                showError("Can't load Project data");
                return;
            }
            IDevice device = chooseDevice();
            if (device == null) {
                return;
            }
            AndroidFacet facet = chooseModule();
            if (facet == null) {
                return;
            }
            AdbShellHelper shellHelper = new AdbShellHelper(new AdbCommandExecutor(), device.getSerialNumber());
            DirectoriesBundle dirBundle = new DirectoriesCreator(project, shellHelper).createDirectories();
            Map<String, String> unifiedNamesMap = new FilesPuller(project, shellHelper, facet).pullFiles(dirBundle);
            String fileName = chooseFileName(unifiedNamesMap.keySet());
            if (fileName == null) {
                return;
            }
            String selectedFile = dirBundle.localUnifiedDir + File.separator + unifiedNamesMap.get(fileName);
            List<Preference> preferences = new PreferencesParser().parse(selectedFile);
            boolean save = new FileContentDialog(project, preferences).showAndGet();
            if (!save) {
                return;
            }
            new PreferencesUnparser().unparse(preferences, selectedFile);
            new FilesPusher(project, shellHelper, facet).pushFiles(unifiedNamesMap, dirBundle);
        } catch (CreateDirectoriesException e) {
            showError("Error while creating temporary directories");
            e.printStackTrace();
        } catch (ParsePreferencesException e) {
            showError("Error while parsing SharedPreferences file");
            e.printStackTrace();
        } catch (PullFilesException e) {
            showError("Error while pulling SharedPreferences files");
            e.printStackTrace();
        } catch (PushFilesException e) {
            showError("Error while pushing SharedPreferences files");
            e.printStackTrace();
        } catch (UnparsePreferencesException e) {
            showError("Error while un-parsing SharedPreferences file");
            e.printStackTrace();
        }
    }

    private void showError(String text) {
        new ErrorDialog(project, text).show();
    }

    private IDevice chooseDevice() {
        AndroidDebugBridge adb = AndroidSdkUtils.getDebugBridge(project);
        if (adb == null) {
            showError("ADB connection error");
            return null;
        }
        List<IDevice> devices = Arrays.asList(adb.getDevices());
        if (devices.isEmpty()) {
            showError("Can't find any devices");
            return null;
        }
        if (devices.size() == 1) {
            return devices.get(0);
        }
        return new ChooseDialog<IDevice>(project, "Choose Device", devices) {
            @Override
            protected String getItemName(IDevice d) {
                return d.getProperty(IDevice.PROP_DEVICE_MANUFACTURER) + " " + d.getProperty(IDevice.PROP_DEVICE_MODEL)
                        + " [" + d.getSerialNumber() + "]";
            }
        }
                .showAndGetResult();
    }

    private AndroidFacet chooseModule() {
        List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);
        if (facets.isEmpty()) {
            showError("Can't find any modules");
            return null;
        }
        if (facets.size() == 1) {
            return facets.get(0);
        }
        return new ChooseDialog<AndroidFacet>(project, "Choose Module", facets) {
            @Override
            protected String getItemName(AndroidFacet item) {
                return item.getModule().getName();
            }
        }
                .showAndGetResult();
    }

    private String chooseFileName(Collection<String> fileNames) {
        List<String> items = new ArrayList<>(fileNames);
        if (items.isEmpty()) {
            showError("Can't find any SharedPreferences files");
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        return new ChooseDialog<String>(project, "Choose File", items) {
            @Override
            protected String getItemName(String item) {
                return item;
            }
        }
                .showAndGetResult();
    }

}
