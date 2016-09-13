package com.github.shchurov.prefseditor;

import com.android.ddmlib.IDevice;
import com.github.shchurov.prefseditor.helpers.*;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.exceptions.*;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.github.shchurov.prefseditor.model.Preference;
import com.github.shchurov.prefseditor.ui.ChooseDeviceDialog;
import com.github.shchurov.prefseditor.ui.ChooseFileDialog;
import com.github.shchurov.prefseditor.ui.ChooseModuleDialog;
import com.github.shchurov.prefseditor.ui.FileContentDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OpenEditorAction extends AnAction {

    //TODO: error handling
    //TODO: refactoring

    @Override
    public void actionPerformed(AnActionEvent action) {
        try {
            Project project = ProjectUtils.getProject(action);
            IDevice device = new ChooseDeviceDialog(project).showAndGetDevice();
            if (device == null) {
                return;
            }
            AndroidFacet facet = new ChooseModuleDialog(project).showAndGetModule();
            if (facet == null) {
                return;
            }
            AdbCommandBuilder cmdBuilder = new AdbCommandBuilder(device.getSerialNumber());
            AdbCommandExecutor cmdExecutor = new AdbCommandExecutor();
            DirectoriesBundle dirBundle = new DirectoriesCreator(project, cmdBuilder, cmdExecutor).createDirectories();
            Map<String, String> unifiedNamesMap = new FilesPuller(project, cmdBuilder, cmdExecutor, facet)
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
            new FilesPusher(project, cmdBuilder, cmdExecutor, facet).pushFiles(unifiedNamesMap, dirBundle);
        } catch (CreateDirectoriesException | ParsePreferencesException | PullFilesException | PushFilesException
                | UnparsePreferencesException | GetDeviceListException | NoDeviceFoundException e) {
            e.printStackTrace();
        }
    }


}
