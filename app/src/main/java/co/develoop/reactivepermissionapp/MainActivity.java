package co.develoop.reactivepermissionapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import co.develoop.reactivepermission.ReactivePermission;
import co.develoop.reactivepermission.ReactivePermissionResults;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ReactivePermission.Builder(this)
                .setPermission(ACCESS_FINE_LOCATION)
                .setPermission(WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<ReactivePermissionResults>() {

                    @Override
                    public void accept(@NonNull ReactivePermissionResults reactivePermissionResults) throws Exception {
                        if (reactivePermissionResults.hasPermission(ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION GRANTED", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION NOT GRANTED", Toast.LENGTH_LONG).show();
                        }

                        if (reactivePermissionResults.hasPermission(WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE GRANTED", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE NOT GRANTED", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}