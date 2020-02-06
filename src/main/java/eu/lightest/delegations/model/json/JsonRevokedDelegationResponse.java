package eu.lightest.delegations.model.json;

import com.google.gson.annotations.SerializedName;


public class JsonRevokedDelegationResponse implements IJsonRevokedDelegationResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("hash")
    private String mHash;

    @SerializedName("status")
    private String mStatus;

    public JsonRevokedDelegationResponse(int id, String hash, String status) {
        mId = id;
        mHash = hash;
        mStatus = status;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public String getHash() {
        return mHash;
    }

    @Override
    public String getStatus() {
        return mStatus;
    }
}
