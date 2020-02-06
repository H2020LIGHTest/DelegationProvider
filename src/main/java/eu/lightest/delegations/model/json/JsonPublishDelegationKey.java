package eu.lightest.delegations.model.json;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class JsonPublishDelegationKey implements IJsonPublishDelegationKey {

    @SerializedName("id")
    public int mId;

    @SerializedName("key")
    public String mKey;

    @SerializedName("public_key_hash")
    public String mPublicKeyHash;

    public static JsonPublishDelegationKey fromString(String data) {
        Gson jsonParser = new Gson();
        return jsonParser.fromJson(data, JsonPublishDelegationKey.class);
    }

    public void setId(int id) {
        mId = id;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setHash(String hash) {
        mPublicKeyHash = hash;
    }

    public String toString() {
        return
                          "{\n"
                + "\tid: " + mId + ",\n"
                + "\tkey: " + mKey + ",\n"
                + "\tpublic_key_hash " + mPublicKeyHash + "\n"
                + "}";
    }

    @Override
    public Integer getId() {
        return mId;
    }

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public String getHash() {
        return mPublicKeyHash;
    }
}
