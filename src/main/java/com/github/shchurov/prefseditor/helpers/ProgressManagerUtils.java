package com.github.shchurov.prefseditor.helpers;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ProgressManagerUtils {

    public static <T, E extends Exception> T runWithProgressDialog(Project project, String title,
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
