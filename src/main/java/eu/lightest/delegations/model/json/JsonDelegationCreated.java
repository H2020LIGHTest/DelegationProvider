package eu.lightest.delegations.model.json;

import com.google.gson.annotations.SerializedName;

public class JsonDelegationCreated implements IJsonDelegationCreated {

    @SerializedName("id")
    protected Integer mId;

    public JsonDelegationCreated(Integer id) {
        mId = id;
    }

    @Override
    public Integer getId() {
        return mId;
    }
}
