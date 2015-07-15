package io.gameq.android;

import android.content.Context;

/**
 * Created by Ludvig on 14/07/15.
 */
public interface CallbackGetStatus {
    void callback(boolean success, String error, int status, int game, long acceptBefore);
}
