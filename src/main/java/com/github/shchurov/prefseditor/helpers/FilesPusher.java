package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbShellHelper;
import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;
import com.github.shchurov.prefseditor.helpers.exceptions.PushFilesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.Map;

public class FilesPusher {

    private Project project;
    private AdbShellHelper shellHelper;
    private String applicationId;
    private String activityName;

    public FilesPusher(Project project, AdbShellHelper shellHelper, AndroidFacet facet) {
        this.project = project;
        this.shellHelper = shellHelper;
        applicationId = Utils.getApplicationId(facet);
        activityName = Utils.getDefaultActivityName(project, facet);
    }

    public void pushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) throws PushFilesException {
        Utils.runWithProgressDialog(project, "Applying Changes", () -> {
            try {
                return performPushFiles(unifiedNamesMap, bundle);
            } catch (ExecuteAdbCommandException e) {
                throw new PushFilesException(e);
            }
        });
    }

    private Void performPushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        shellHelper.pushFile(bundle.localUnifiedDir, bundle.deviceMainDir);
        reverseUnifyFileNames(unifiedNamesMap, bundle);
        shellHelper.killApp(applicationId);
        shellHelper.overwritePrefs(bundle.deviceNormalDir, applicationId);
        shellHelper.removeDir(bundle.deviceMainDir);
        shellHelper.startApp(applicationId, activityName);
        return null;
    }

    private void reverseUnifyFileNames(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = bundle.deviceUnifiedDir + "/" + entry.getValue();
            String dst = bundle.deviceNormalDir + "/" + entry.getKey();
            shellHelper.moveFile(src, dst);
        }
    }

}
