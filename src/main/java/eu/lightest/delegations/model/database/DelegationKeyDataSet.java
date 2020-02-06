package eu.lightest.delegations.model.database;

import com.google.gson.annotations.SerializedName;

public class DelegationKeyDataSet {
    @SerializedName("id")
    private int mId = 0;

    @SerializedName("key")
    private String mKey = null;

    public DelegationKeyDataSet(int id, String key) {
        mId = id;
        mKey = key;
    }

    public int getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }
}
