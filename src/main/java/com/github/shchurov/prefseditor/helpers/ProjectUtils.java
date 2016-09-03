package com.github.shchurov.prefseditor.helpers;

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

    public static String getApplicationId(Project project) {
        List<AndroidFacet> facets = new ArrayList<>();
        facets.addAll(AndroidUtils.getApplicationFacets(project));
        // TODO: handle if many facets
        AndroidFacet facet = facets.get(0);
        return facet.getAndroidModuleInfo().getPackage();
    }

}
