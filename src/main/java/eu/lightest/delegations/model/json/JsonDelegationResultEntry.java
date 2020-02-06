package eu.lightest.delegations.model.json;

import com.google.gson.annotations.SerializedName;

public class JsonDelegationResultEntry implements IJsonDelegationResultEntry {

    @SerializedName("id")
    private String mId;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("data")
    private String mData;

    public String toString() {
        return
                   "{\n"
                + "\tid: " + mId + ",\n"
                + "\tstatus: " + mStatus + ",\n"
                + "\tdata: " + mData + "\n"
                + "}";
    }

    @Override
    public void setId(String id) {
        mId = id;

    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setStatus(String status) {
        mStatus = status;
    }

    @Override
    public String getStatus() {
        return mStatus;
    }

    @Override
    public void setData(String data) {
        mData = data;
    }

    @Override
    public String getData() {
        return mData;
    }
}
