package com.wix.reactnativenotifications.core.notifications;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.wix.reactnativenotifications.core.JsIOHelper;

import java.util.List;

import static com.wix.reactnativenotifications.Defs.NOTIFICATION_RECEIVED_EVENT_NAME;

public class RemoteNotification {

    Context context;
    private final NotificationProps mNotificationProps;
    private final JsIOHelper mJsIOHelper;

    protected RemoteNotification(NotificationProps notificationProps, JsIOHelper jsIOHelper) {
        mNotificationProps = notificationProps;
        mJsIOHelper = jsIOHelper;
    }

    public RemoteNotification(Context context, NotificationProps notificationProps) {
        this(notificationProps, new JsIOHelper(context));
        this.context=context;
    }

    public void onReceived() {
        if(shouldShowNotification(context)) {
            final NotificationProps localNotificationProps = NotificationProps.fromBundle(context, mNotificationProps.getData());
            final ILocalNotification notification = LocalNotification.get(context, localNotificationProps);
            notification.post((int) System.nanoTime());
        }
//        sendReceivedEvent();
    }

    static boolean shouldShowNotification(Context context) {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;

        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        // app is in foreground, but if screen is locked show notification anyway
        return km.inKeyguardRestrictedInputMode();
    }

    private void sendReceivedEvent() {
        mJsIOHelper.sendEventToJS(NOTIFICATION_RECEIVED_EVENT_NAME, mNotificationProps.asBundle());
    }
}
