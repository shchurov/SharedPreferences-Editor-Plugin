package com.github.shchurov.prefseditor.helpers;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.function.Supplier;

public class Utils {

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
        return ProgressManager.getInstance().runProcessWithProgressSynchronously((ThrowableComputable<T, E>) body::get,
                title, false, project);
    }

    public static void showErrorNotification(String text) {
        Notification n = new Notification("com.github.shchurov.prefseditor", "SharedPreferencesEditor",
                text, NotificationType.ERROR);
        Notifications.Bus.notify(n);
    }

}
