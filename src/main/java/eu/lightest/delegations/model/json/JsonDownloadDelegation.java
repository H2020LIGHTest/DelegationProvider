package eu.lightest.delegations.model.json;

import com.google.gson.annotations.SerializedName;

public class JsonDownloadDelegation implements IJsonDownloadDelegation {

    @SerializedName("id")
    private String mId;

    @SerializedName("data")
    private String mData;

    @SerializedName("key")
    private String mKey;

    public JsonDownloadDelegation(int id, String data, String key) {
        mId = String.valueOf(id);
        mData = data;
        mKey = key;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getData() {
        return mData;
    }

    @Override
    public String getKey() {
        return mKey;
    }
}
