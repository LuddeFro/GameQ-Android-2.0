package io.gameq.android;

import android.content.Context;

/**
 * Created by Ludvig on 14/07/15.
 */
public interface CallbackVersionControl {
    void callback(boolean success, String error, String newestVersion, String link);
}
