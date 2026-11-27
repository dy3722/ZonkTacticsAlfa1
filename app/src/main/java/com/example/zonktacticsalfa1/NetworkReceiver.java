package com.example.zonktacticsalfa1;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NetworkReceiver {
    private static boolean isListening = false;
    private static ConnectivityManager.NetworkCallback networkCallback;
    private static Boolean lastConnectionState = null;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static int startedActivities = 0;
    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks;

    public static boolean isListening() {
        return isListening;
    }

    public static void startListening(Context context) {
        if (isListening) return;

        isListening = true;
        lastConnectionState = null;
        if (startedActivities == 0) {
            startedActivities = 1;
        }

        Context appContext = context.getApplicationContext();

        if (lifecycleCallbacks == null && appContext instanceof Application) {
            lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    startedActivities++;
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    startedActivities = Math.max(0, startedActivities - 1);
                    if (startedActivities == 0 && isListening) {
                        stopListening(activity);
                    }
                }

                @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
                @Override public void onActivityResumed(@NonNull Activity activity) {}
                @Override public void onActivityPaused(@NonNull Activity activity) {}
                @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
                @Override public void onActivityDestroyed(@NonNull Activity activity) {}
            };
            ((Application) appContext).registerActivityLifecycleCallbacks(lifecycleCallbacks);
        }

        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            lastConnectionState = (activeNetwork != null);

            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    if (lastConnectionState != null && !lastConnectionState) {
                        showToast(appContext, "חיבור האינטרנט חזר!");
                    }
                    lastConnectionState = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    if (lastConnectionState != null && lastConnectionState) {
                        showToast(appContext, "חיבור האינטרנט אבד!");
                    }
                    lastConnectionState = false;
                }
            };

            cm.registerNetworkCallback(request, networkCallback);
        }
    }

    public static void stopListening(Context context) {
        if (!isListening) return;

        isListening = false;
        lastConnectionState = null;

        Context appContext = context.getApplicationContext();

        if (networkCallback != null) {
            ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                try {
                    cm.unregisterNetworkCallback(networkCallback);
                } catch (Exception ignored) {}
            }
            networkCallback = null;
        }
    }

    private static void showToast(Context context, String message) {
        if (startedActivities > 0) {
            mainHandler.post(() -> Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show());
        }
    }
}
