package com.github.shchurov.prefseditor.helpers;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectUtils {

    public static Project getProject(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    public static String getDefaultActivityName(Project project) {
        AndroidFacet facet = getFacet(project);
        // TODO:
        return DefaultActivityLocator.getDefaultLauncherActivityName(project, facet.getManifest());
    }

    private static AndroidFacet getFacet(Project project) {
        List<AndroidFacet> facets = new ArrayList<>();
        facets.addAll(AndroidUtils.getApplicationFacets(project));
        // TODO: handle if many facets
        return facets.get(0);
    }

    public static String getApplicationId(Project project) {
        AndroidFacet facet = getFacet(project);
        return facet.getAndroidModuleInfo().getPackage();
    }

}
