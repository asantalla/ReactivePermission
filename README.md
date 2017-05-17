# ReactivePermission

ReactivePermission is an Android library to request permissions using RxJava.

| Current Branch | Branch  | Bintray JCenter | Build Status |
|:--------------:|:-------:|:-------:|:------------:|
| | [`RxJava1.x`](https://github.com/asantalla/ReactivePermission/tree/RxJava1.x) | [ ![jcenter](https://api.bintray.com/packages/asantalla/develoop/reactivepermission-rx1/images/download.svg) ](https://bintray.com/asantalla/develoop/reactivepermission-rx1/_latestVersion) | [![Build Status](https://travis-ci.org/asantalla/ReactivePermission.svg?branch=RxJava1.x)](https://travis-ci.org/asantalla/ReactivePermission) |
| :ballot_box_with_check: | [`RxJava2.x`](https://github.com/asantalla/ReactivePermission/tree/RxJava2.x) | [ ![jcenter](https://api.bintray.com/packages/asantalla/develoop/reactivepermission-rx2/images/download.svg) ](https://bintray.com/asantalla/develoop/reactivepermission-rx2/_latestVersion) | [![Build Status](https://travis-ci.org/asantalla/ReactivePermission.svg?branch=RxJava2.x)](https://travis-ci.org/asantalla/ReactivePermission) |

Contents
--------

- [Download](#download)
- [Usage](#usage)
- [License](#license)

Download
--------

First of all, be sure you have the **jcenter repository** included in the `build.gradle` file in the root of your project.

```
repositories {
    jcenter()
}
```

Next add the gradle compile dependency to the `build.gradle` file of your app module.

```
compile 'co.develoop.reactivepermission:reactivepermission-rx2:1.0.0'
```

Usage
-----

We can request permissions using the below code inside a **FragmentActivity**.

```java
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
```

License
-------

Copyright 2017 Adrián Santalla

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.