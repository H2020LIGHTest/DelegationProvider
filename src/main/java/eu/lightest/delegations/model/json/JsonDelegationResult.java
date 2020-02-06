package eu.lightest.delegations.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JsonDelegationResult implements IJsonDelegationResult {

    @SerializedName("result")
    private List<JsonDelegationResultEntry> mResults = new ArrayList<>();

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        for( JsonDelegationResultEntry result : mResults ) {
            builder.append( result );
        }
        builder.append("}");

        return builder.toString();
    }

    @Override
    public void add(JsonDelegationResultEntry entry) {
        mResults.add(entry);
    }

    @Override
    public List<JsonDelegationResultEntry> get() {
        return mResults;
    }
}
