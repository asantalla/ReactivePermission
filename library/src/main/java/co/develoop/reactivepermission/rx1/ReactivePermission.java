package co.develoop.reactivepermission.rx1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ReactivePermission extends Fragment {

    private final static String FRAGMENT_TAG_NAME = "REACTIVE_PERMISSION";
    private final static int REQUEST_CODE = 9999;

    private BehaviorSubject<ReactivePermissionResults> behaviorSubject;
    private CompositeSubscription compositeSubscription;

    private List<String> mPermissions;
    private Boolean isRequesting = false;

    public ReactivePermission() {
        behaviorSubject = BehaviorSubject.create();
        compositeSubscription = new CompositeSubscription();
    }

    public static ReactivePermission newInstance() {
        return new ReactivePermission();
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
        compositeSubscription.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            broadcast(getPermissionsResults(permissions, grantResults));
            isRequesting = false;
        }
    }

    public Boolean hasSubscriptions() {
        return compositeSubscription.hasSubscriptions();
    }

    private void subscribe(List<String> permissions, Action1<ReactivePermissionResults> onResults) {
        mPermissions = permissions;
        compositeSubscription.clear();
        compositeSubscription.add(behaviorSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onResults));
    }

    private void requestPermissions() {
        if (compositeSubscription.hasSubscriptions()) {
            if (!mPermissions.isEmpty()) {
                String[] permissions = mPermissions.toArray(new String[mPermissions.size()]);

                if (android.os.Build.VERSION.SDK_INT >= 23 && permissions.length > 0) {
                    if (!isRequesting) {
                        FragmentCompat.requestPermissions(this, mPermissions.toArray(new String[mPermissions.size()]), REQUEST_CODE);
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
        behaviorSubject.onNext(reactivePermissionResults);
    }

    public static class Builder {
        private FragmentActivity mFragmentActivity;
        private List<String> mPermissions;

        public Builder(FragmentActivity fragmentActivity) {
            mFragmentActivity = fragmentActivity;
            mPermissions = new ArrayList<>();
        }

        public Builder setPermission(String permission) {
            mPermissions.add(permission);
            return this;
        }

        public void subscribe(Action1<ReactivePermissionResults> onResults) {
            FragmentManager fragmentManager = mFragmentActivity.getFragmentManager();
            ReactivePermission reactivePermission = (ReactivePermission) fragmentManager.findFragmentByTag(FRAGMENT_TAG_NAME);

            if (reactivePermission == null) {
                reactivePermission = ReactivePermission.newInstance();
                reactivePermission.subscribe(mPermissions, onResults);
                fragmentManager.beginTransaction().add(reactivePermission, FRAGMENT_TAG_NAME).commit();
            } else {
                if (!reactivePermission.hasSubscriptions()) {
                    reactivePermission.subscribe(mPermissions, onResults);
                    reactivePermission.requestPermissions();
                }
            }
        }
    }
}