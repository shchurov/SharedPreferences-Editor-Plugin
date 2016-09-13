package com.github.shchurov.prefseditor;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.shchurov.prefseditor.helpers.*;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.adb.AdbShellHelper;
import com.github.shchurov.prefseditor.helpers.exceptions.*;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.github.shchurov.prefseditor.model.Preference;
import com.github.shchurov.prefseditor.ui.ChooseDeviceDialog;
import com.github.shchurov.prefseditor.ui.ChooseFileDialog;
import com.github.shchurov.prefseditor.ui.ChooseModuleDialog;
import com.github.shchurov.prefseditor.ui.FileContentDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.android.util.AndroidUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OpenEditorAction extends AnAction {

    //TODO: error handling
    //TODO: refactoring

    @Override
    public void actionPerformed(AnActionEvent action) {
        try {
            Project project = action.getData(PlatformDataKeys.PROJECT);
            if (project == null) {
                return;
            }
            AndroidDebugBridge adb = AndroidSdkUtils.getDebugBridge(project);
            if (adb == null) {
                return;
            }
            IDevice[] devices = adb.getDevices();
            if (devices.length == 0) {
                return;
            }
            IDevice device = new ChooseDeviceDialog(project, devices).showAndGetDevice();
            if (device == null) {
                return;
            }
            List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);
            if (facets.isEmpty()) {
                return;
            }
            AndroidFacet facet = new ChooseModuleDialog(project, facets).showAndGetModule();
            if (facet == null) {
                return;
            }
            AdbShellHelper shellHelper = new AdbShellHelper(new AdbCommandExecutor(), device.getSerialNumber());
            DirectoriesBundle dirBundle = new DirectoriesCreator(project, shellHelper).createDirectories();
            Map<String, String> unifiedNamesMap = new FilesPuller(project, shellHelper, facet)
                    .pullFiles(dirBundle);
            String selectedName = new ChooseFileDialog(project, unifiedNamesMap.keySet()).showAndGetFileName();
            if (selectedName == null) {
                return;
            }
            String selectedFile = dirBundle.localUnifiedDir + File.separator + unifiedNamesMap.get(selectedName);
            List<Preference> preferences = new PreferencesParser().parse(selectedFile);
            boolean save = new FileContentDialog(project, preferences).showAndGet();
            if (!save) {
                return;
            }
            new PreferencesUnparser().unparse(preferences, selectedFile);
            new FilesPusher(project, shellHelper, facet).pushFiles(unifiedNamesMap, dirBundle);
        } catch (CreateDirectoriesException | ParsePreferencesException | PullFilesException | PushFilesException
                | UnparsePreferencesException e) {
            e.printStackTrace();
        }
    }


}
