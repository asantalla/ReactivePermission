package co.develoop.reactivepermission.rx1;

import java.util.HashMap;
import java.util.Map;

public class ReactivePermissionResults {

    private Map<String, Boolean> results;

    public ReactivePermissionResults() {
        results = new HashMap<>();
    }

    protected void add(String permission, Boolean result) {
        results.put(permission, result);
    }

    public Boolean hasPermission(String permission) {
        return results.get(permission);
    }
}