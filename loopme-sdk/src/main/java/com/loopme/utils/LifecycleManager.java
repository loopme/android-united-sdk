package com.loopme.utils;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.loopme.telemetry.EventType;
import com.loopme.telemetry.TelemetryContainer;
import com.loopme.telemetry.TelemetryEvent;
import com.loopme.telemetry.TelemetryEventBuilder;
import com.loopme.telemetry.TelemetryEventSender;

class LifecycleManager {

    // dependency or not?
//public LifecycleManager(TelemetryEventBuilder builder, TelemetryEventSender sender){
//    this.builder = builder;
//    this.sender = sender;
//};

    public void registerActivityLifecycleCallbacks(@NonNull Context context) {
        if (context.getApplicationContext() instanceof Application) {
            Application application = (Application) context.getApplicationContext();

            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                private int activityReferences = 0;
                private boolean isActivityChangingConfigurations = false;

                @Override
                public void onActivityStarted(Activity activity) {
                    if (activityReferences == 0 && !isActivityChangingConfigurations) {
                        SessionManager.getInstance().startSession();
                        // when using it -> do we want to do it this way, or the dependency
                        TelemetryEvent event = TelemetryContainer.INSTANCE.telemetryEventBuilder().build(EventType.SESSION_START);
                        TelemetryContainer.INSTANCE.telemetryEventSender().send(event);
                    }
                    activityReferences++;
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    activityReferences--;
                    isActivityChangingConfigurations = activity.isChangingConfigurations();

                    if (activityReferences == 0 && !isActivityChangingConfigurations) {
                        SessionManager.getInstance().endSession();
                    }
                }

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

                @Override
                public void onActivityResumed(Activity activity) {}

                @Override
                public void onActivityPaused(Activity activity) {}

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

                @Override
                public void onActivityDestroyed(Activity activity) {}
            });
        }
    }
}

