package com.github.shchurov.prefseditor.helpers;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

class Utils {

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

    static <T, E extends Exception> T runWithProgressDialog(Project project, String title,
            Supplier<T> body) throws E {
        return ProgressManager.getInstance().run(new Task.WithResult<T, E>(project, title, false) {
            @Override
            protected T compute(@NotNull ProgressIndicator indicator) throws E {
                indicator.setIndeterminate(true);
                return body.get();
            }
        });
    }

}
