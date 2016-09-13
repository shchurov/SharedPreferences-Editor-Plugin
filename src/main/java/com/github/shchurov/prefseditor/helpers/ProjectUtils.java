package com.github.shchurov.prefseditor.helpers;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;

public class ProjectUtils {

    public static Project getProject(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    public static List<AndroidFacet> getFacets(Project project) {
        return AndroidUtils.getApplicationFacets(project);
    }

    static String getDefaultActivityName(Project project, AndroidFacet facet) {
        Manifest manifest = facet.getManifest();
        if (manifest != null) {
            return DefaultActivityLocator.getDefaultLauncherActivityName(project, facet.getManifest());
        } else {
            return null;
        }
    }

    static String getApplicationId(AndroidFacet facet) {
        return facet.getAndroidModuleInfo().getPackage();
    }

}
