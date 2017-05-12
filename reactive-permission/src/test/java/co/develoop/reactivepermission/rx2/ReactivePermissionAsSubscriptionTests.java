package co.develoop.reactivepermission.rx2;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import io.reactivex.observers.TestObserver;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
public class ReactivePermissionAsSubscriptionTests {

    private static final int REQUEST_CODE = 9999;
    private static final String REACTIVE_PERMISSION_TAG = "REACTIVE_PERMISSION";

    private FragmentActivity mFragmentActivity;

    private TestObserver<ReactivePermissionResults> mTestObserver;

    @Before
    public void setup() {
        ActivityController<FragmentActivity> activityController = Robolectric.buildActivity(FragmentActivity.class);
        mFragmentActivity = spy(activityController.setup().get());

        new ReactivePermission.Builder(mFragmentActivity)
                .setPermission(WRITE_EXTERNAL_STORAGE)
                .subscribe(null);

        ReactivePermission reactivePermissionFragment = spy((ReactivePermission) mFragmentActivity.getFragmentManager().findFragmentByTag(REACTIVE_PERMISSION_TAG));

        mTestObserver = reactivePermissionFragment.test();
    }

    @Test
    public void askForWritePermissionAndGrantIt() throws Exception {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_GRANTED});

        Assert.assertTrue(mTestObserver.values().get(0).hasPermission(WRITE_EXTERNAL_STORAGE));
    }

    @Test
    public void askForWritePermissionAndDenyIt() throws Exception {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_DENIED});

        Assert.assertFalse(mTestObserver.values().get(0).hasPermission(WRITE_EXTERNAL_STORAGE));
    }

    @Test
    public void askForWriteAndCameraPermissionsAndGrantAll() throws Exception {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED});

        Assert.assertTrue(mTestObserver.values().get(0).hasPermission(WRITE_EXTERNAL_STORAGE));
        Assert.assertTrue(mTestObserver.values().get(0).hasPermission(CAMERA));
    }

    @Test
    public void askForWriteAndCameraPermissionsAndDenyAll() throws Exception {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, new int[]{PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_DENIED});

        Assert.assertFalse(mTestObserver.values().get(0).hasPermission(WRITE_EXTERNAL_STORAGE));
        Assert.assertFalse(mTestObserver.values().get(0).hasPermission(CAMERA));
    }

    @Test
    public void askForWriteAndCameraPermissionsAndGrantWritePermissionAndDenyCameraPermission() throws Exception {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED});

        Assert.assertTrue(mTestObserver.values().get(0).hasPermission(WRITE_EXTERNAL_STORAGE));
        Assert.assertFalse(mTestObserver.values().get(0).hasPermission(CAMERA));
    }

    private void requestPermissions(String[] permissions, int[] grantResults) {
        ReactivePermission reactivePermissionFragment = spy((ReactivePermission) mFragmentActivity.getFragmentManager().findFragmentByTag(REACTIVE_PERMISSION_TAG));
        reactivePermissionFragment.onRequestPermissionsResult(REQUEST_CODE, permissions, grantResults);
    }
}