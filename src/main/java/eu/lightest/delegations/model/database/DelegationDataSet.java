package eu.lightest.delegations.model.database;

import com.google.gson.annotations.SerializedName;

public class DelegationDataSet {

    @SerializedName("id")
    private int mId = 0;

    @SerializedName("hash")
    private String mHash = null;

    @SerializedName("key")
    private String mKey = null;

    @SerializedName("status")
    private String mStatus = null;

    @SerializedName("data")
    private String mData = null;
    private DelegationKeyDataSet mDelegationKey = null;

    public DelegationDataSet(int id, String hash, String key, String data, String status, DelegationKeyDataSet dkds) {
        initialiseData(id, hash, key, data, status, dkds);
    }

    public DelegationDataSet(int id, String hash, String key, String data) {
        initialiseData(id, hash, key, data, "UNKNOWN", null);
    }

    public DelegationDataSet(int id, String hash, String key, String data, String status) {
        initialiseData(id, hash, key, data, status, null);
    }

    private void initialiseData(int id, String hash, String key, String data, String status, DelegationKeyDataSet dkds) {
        mDelegationKey = dkds;
        mId = id;
        mHash = hash;
        mKey = key;
        mData = data;
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public int getId() {
        return mId;
    }

    public String getHash() {
        return mHash;
    }

    public String getKey() {
        return mKey;
    }

    public String getData() {
        return mData;
    }
}
