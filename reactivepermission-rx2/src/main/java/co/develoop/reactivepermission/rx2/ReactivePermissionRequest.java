package co.develoop.reactivepermission.rx2;

import android.content.pm.PackageManager;

class ReactivePermissionRequest {

    private String mPermission;
    private int mPermissionState;

    ReactivePermissionRequest(String permission, int permissionState) {
        mPermission = permission;
        mPermissionState = permissionState;
    }

    boolean isGranted() {
        return mPermissionState == PackageManager.PERMISSION_GRANTED;
    }

    String getPermission() {
        return mPermission;
    }
}