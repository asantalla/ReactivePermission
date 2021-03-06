package co.develoop.reactivepermission.rx2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class ReactivePermission extends Fragment {

    private final static String FRAGMENT_TAG_NAME = "REACTIVE_PERMISSION";
    private final static int REQUEST_CODE = 9999;

    private BehaviorSubject<ReactivePermissionResults> behaviorSubject;
    private CompositeDisposable compositeDisposable;

    private List<ReactivePermissionRequest> mPermissions;
    private Boolean isRequesting = false;

    public ReactivePermission() {
        behaviorSubject = BehaviorSubject.create();
        compositeDisposable = new CompositeDisposable();
    }

    public static ReactivePermission newInstance() {
        return new ReactivePermission();
    }

    public TestObserver<ReactivePermissionResults> test() {
        return behaviorSubject.test();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestPermissions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            broadcast(getPermissionsResults(permissions, grantResults));
            isRequesting = false;
        }
    }

    public void clear() {
        compositeDisposable.clear();
    }

    private Boolean hasSubscriptions() {
        return compositeDisposable.size() > 0;
    }

    private void subscribe(List<ReactivePermissionRequest> permissions, Consumer<ReactivePermissionResults> onResults) {
        if (onResults != null) {
            mPermissions = permissions;
            compositeDisposable.add(behaviorSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onResults));
        }
    }

    private void requestPermissions() {
        if (hasSubscriptions()) {
            if (!mPermissions.isEmpty()) {
                String[] permissions = new String[mPermissions.size()];

                for (int i = 0; i < mPermissions.size(); i++) {
                    permissions[i] = mPermissions.get(i).getPermission();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions.length > 0) {
                    if (!isRequesting) {
                        FragmentCompat.requestPermissions(this, permissions, REQUEST_CODE);
                        isRequesting = true;
                    }
                } else {
                    broadcast(getPermissionsResults(permissions, null));
                }
            } else {
                broadcast(null);
            }
        }
    }

    private ReactivePermissionResults getPermissionsResults(@NonNull String[] permissions, int[] grantResults) {
        ReactivePermissionResults reactivePermissionResults = new ReactivePermissionResults();

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults != null ? grantResults[i] : PackageManager.PERMISSION_GRANTED;

            reactivePermissionResults.add(permission, grantResult == PackageManager.PERMISSION_GRANTED);
        }

        return reactivePermissionResults;
    }

    private void broadcast(ReactivePermissionResults reactivePermissionResults) {
        if (hasSubscriptions()) {
            behaviorSubject.onNext(reactivePermissionResults);
        }
    }

    public static class Builder {
        private FragmentActivity mFragmentActivity;
        private List<ReactivePermissionRequest> mPermissions;
        private Boolean isAllGranted = true;

        public Builder(FragmentActivity fragmentActivity) {
            mFragmentActivity = fragmentActivity;
            mPermissions = new ArrayList<>();
        }

        public Builder addPermission(String permission) {
            ReactivePermissionRequest reactivePermissionRequest = new ReactivePermissionRequest(permission, ContextCompat.checkSelfPermission(mFragmentActivity, permission));

            if (!reactivePermissionRequest.isGranted()) {
                isAllGranted = false;
            }

            mPermissions.add(reactivePermissionRequest);

            return this;
        }

        public void subscribe(Consumer<ReactivePermissionResults> onResults) {
            if (!isAllGranted) {
                FragmentManager fragmentManager = mFragmentActivity.getFragmentManager();
                ReactivePermission reactivePermission = (ReactivePermission) fragmentManager.findFragmentByTag(FRAGMENT_TAG_NAME);

                if (reactivePermission == null) {
                    reactivePermission = ReactivePermission.newInstance();
                    reactivePermission.subscribe(mPermissions, onResults);
                    fragmentManager.beginTransaction().add(reactivePermission, FRAGMENT_TAG_NAME).commit();
                } else {
                    reactivePermission.clear();
                    reactivePermission.subscribe(mPermissions, onResults);
                    reactivePermission.requestPermissions();
                }
            } else {
                ReactivePermissionResults reactivePermissionResults = new ReactivePermissionResults();

                for (ReactivePermissionRequest reactivePermissionRequest : mPermissions) {
                    reactivePermissionResults.add(reactivePermissionRequest.getPermission(), reactivePermissionRequest.isGranted());
                }

                try {
                    onResults.accept(reactivePermissionResults);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}