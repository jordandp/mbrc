package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import android.view.ViewConfiguration;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.util.NotificationService;
import com.kelsos.mbrc.util.RemoteBroadcastReceiver;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import java.lang.reflect.Field;

public class RemoteApplication extends Application {

    private static final Logger logger = LoggerManager.getLogger();

    public void onCreate() {
        super.onCreate();
        RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION,
                Modules.override(RoboGuice.newDefaultRoboModule(this))
                        .with(new RemoteModule()));
        final RoboInjector injector = RoboGuice.getInjector(this);

        startService(new Intent(this, Controller.class));

        //Initialization of the background service
        injector.getInstance(MainDataModel.class);

        injector.getInstance(SocketService.class);
        injector.getInstance(RemoteBroadcastReceiver.class);
        injector.getInstance(NotificationService.class);
        injector.getInstance(SyncHandler.class);

        //HACK: Force overflow code courtesy of Timo Ohr http://stackoverflow.com/a/11438245
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                logger.i("force overflow hack");
            }
        }
    }

}