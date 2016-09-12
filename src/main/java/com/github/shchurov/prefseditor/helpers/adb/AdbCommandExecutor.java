package com.github.shchurov.prefseditor.helpers.adb;

import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdbCommandExecutor {

    public String execute(String command) throws ExecuteAdbCommandException {
        System.out.println("Command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (sb.length() != 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return sb.toString();
        } catch (IOException e) {
            throw new ExecuteAdbCommandException(e);
        }
    }

}
