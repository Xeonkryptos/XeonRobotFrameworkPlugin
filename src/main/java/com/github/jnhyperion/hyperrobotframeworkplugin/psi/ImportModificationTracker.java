package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.openapi.util.SimpleModificationTracker;

public class ImportModificationTracker extends SimpleModificationTracker {

    private static final ImportModificationTracker INSTANCE = new ImportModificationTracker();

    private ImportModificationTracker() {}

    public static ImportModificationTracker getInstance() {
        return INSTANCE;
    }
}
