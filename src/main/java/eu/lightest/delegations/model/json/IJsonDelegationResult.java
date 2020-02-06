package eu.lightest.delegations.model.json;

import java.util.List;

public interface IJsonDelegationResult {
    void add(JsonDelegationResultEntry entry);
    List<JsonDelegationResultEntry> get();
}
