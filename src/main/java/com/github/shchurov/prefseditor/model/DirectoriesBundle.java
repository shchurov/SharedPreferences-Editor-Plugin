package com.github.shchurov.prefseditor.model;

public class DirectoriesBundle {

    public final String deviceMainDir;
    public final String deviceNormalDir;
    public final String deviceUnifiedDir;
    public final String localMainDir;
    public final String localUnifiedDir;

    public DirectoriesBundle(String deviceMainDir, String deviceNormalDir, String deviceUnifiedDir, String localMainDir,
           String localUnifiedDir) {
        this.deviceMainDir = deviceMainDir;
        this.deviceNormalDir = deviceNormalDir;
        this.deviceUnifiedDir = deviceUnifiedDir;
        this.localMainDir = localMainDir;
        this.localUnifiedDir = localUnifiedDir;
    }

}
