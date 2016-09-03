package com.github.shchurov.prefseditor.helpers.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdbCommandExecutor {

    public String execute(String command) throws IOException {
        System.out.println("Command: " + command);
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            sb.append(line).append("\n");
        }
        if (sb.length() != 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

}
