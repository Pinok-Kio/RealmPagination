package com.serega.roomandpagingtest;

import android.app.Application;
import io.realm.Realm;

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 03.06.18.
 */
public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
