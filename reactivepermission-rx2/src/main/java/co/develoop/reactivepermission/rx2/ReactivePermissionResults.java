package co.develoop.reactivepermission.rx2;

import java.util.HashMap;
import java.util.Map;

public class ReactivePermissionResults {

    private Map<String, Boolean> results;

    ReactivePermissionResults() {
        results = new HashMap<>();
    }

    void add(String permission, Boolean result) {
        results.put(permission, result);
    }

    public Boolean hasPermission(String permission) {
        return results.get(permission);
    }
}