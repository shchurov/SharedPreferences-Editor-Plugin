package com.github.shchurov.prefseditor;

import com.github.shchurov.prefseditor.helpers.*;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.github.shchurov.prefseditor.model.Preference;
import com.github.shchurov.prefseditor.presentation.ChooseFileDialog;
import com.github.shchurov.prefseditor.presentation.FileContentDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OpenEditorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent action) {
        Project project = ProjectUtils.getProject(action);
        try {
            DirectoriesBundle dirBundle = new DirectoriesCreator().createDirectories();
            Map<String, String> unifiedNamesMap = new PreferencesPuller(project).pullPreferences(dirBundle);
            String selectedName = new ChooseFileDialog(project, unifiedNamesMap.keySet()).showAndGetFileName();
            if (selectedName == null) {
                return;
            }
            String selectedFile = dirBundle.localUnifiedDir + File.separator + unifiedNamesMap.get(selectedName);
            List<Preference> preferences = new PreferencesParser().parse(selectedFile);
            preferences = new FileContentDialog(project, preferences).showAndGetModifiedPreferences();
            if (preferences == null) {
                return;
            }
            new PreferencesUnparser().unparse(preferences, selectedFile);
        } catch (IOException | PreferencesParser.ParseException | PreferencesUnparser.UnparseException e) {
            e.printStackTrace();
        }

//        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
//            try {
//                initDeviceDirs();
//                initLocalDirs();
//                pullSharedPreferences(applicationId);
//                ApplicationManager.getApplication().invokeLater(() -> openViewPreferencesDialog(project));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }, "Pulling Files", false, project);
    }


}
