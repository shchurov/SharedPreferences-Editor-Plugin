package com.github.shchurov.prefseditor;

import com.github.shchurov.prefseditor.helpers.*;
import com.github.shchurov.prefseditor.helpers.exceptions.*;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.github.shchurov.prefseditor.model.Preference;
import com.github.shchurov.prefseditor.presentation.ChooseFileDialog;
import com.github.shchurov.prefseditor.presentation.FileContentDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OpenEditorAction extends AnAction {

    //TODO: handle daemon not started

    @Override
    public void actionPerformed(AnActionEvent action) {
        Project project = ProjectUtils.getProject(action);
        try {
            DirectoriesBundle dirBundle = new DirectoriesCreator(project).createDirectories();
            Map<String, String> unifiedNamesMap = new FilesPuller(project).pullFiles(dirBundle);
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
            new FilesPusher(project).pushFiles(unifiedNamesMap, dirBundle);
        } catch (CreateDirectoriesException | ParsePreferencesException | PullFilesException | PushFilesException
                | UnparsePreferencesException e) {
            //TODO:
            e.printStackTrace();
        }
    }


}
