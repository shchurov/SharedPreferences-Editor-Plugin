package com.github.shchurov.prefseditor.helpers.adb;

import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdbCommandExecutor {

    String execute(String command) throws ExecuteAdbCommandException {
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() != 0) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString().trim();
        } catch (IOException e) {
            throw new ExecuteAdbCommandException(e);
        }
    }

}
