package xyz.xenondevs.inventoryaccess.version;

import xyz.xenondevs.inventoryaccess.util.VersionUtils;

public enum InventoryAccessRevision {

    // Only Paper is supported now
    PAPER("paper", "1.21.0");

    public static final InventoryAccessRevision REQUIRED_REVISION = PAPER;

    private final String packageName;
    private final int[] since;

    InventoryAccessRevision(String packageName, String since) {
        this.packageName = packageName;
        this.since = VersionUtils.toMajorMinorPatch(since);
    }

    public String getPackageName() {
        return packageName;
    }

    public int[] getSince() {
        return since;
    }

}
