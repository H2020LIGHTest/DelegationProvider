package eu.lightest.delegations.model.json;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class JsonPublishDelegation implements IJsonPublishDelegation {

    @SerializedName("delegation")
    protected String mDelegationData;

    @SerializedName("public_key")
    protected String mPublicKey;

    @SerializedName("public_key_hash")
    protected String mPublicKeyHash;

    public void setData(String data) {
        mDelegationData = data;
    }

    public void setPublicKey(String key) {
        mPublicKey = key;
    }

    public void setPublicKeyHash(String hash) {
        mPublicKeyHash = hash;
    }


    public static JsonPublishDelegation fromString(String data) {
        Gson jsonParser = new Gson();
        return jsonParser.fromJson(data, JsonPublishDelegation.class);
    }

    public String toString() {
        return
                  "{\n"
                + "\tdelegation: " + mDelegationData + ",\n"
                + "\tpublic_key: " + mPublicKey + ",\n"
                + "\tpublic_key_hash: " + mPublicKeyHash + "\n"
                + "}";
    }

    @Override
    public String getData() {
        return mDelegationData;
    }

    @Override
    public String getPublicKey() {
        return mPublicKey;
    }

    @Override
    public String getHash() {
        return mPublicKeyHash;
    }
}
