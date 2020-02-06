package eu.lightest.delegations.storage;

public enum StorageSystem {
    database ("database"),
    filesystem ("file");

    private final String mType;

    StorageSystem(String type) {
        this.mType = type;
    }

    public String getType() {
        return mType;
    }
}
