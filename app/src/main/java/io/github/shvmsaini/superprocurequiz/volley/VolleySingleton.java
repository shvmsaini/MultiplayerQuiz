package io.github.shvmsaini.superprocurequiz.volley;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton volley to populate request into single queue.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private static Context mCtx;
    private static RetryPolicy policy;
    private RequestQueue mRequestQueue;

    /**
     * Private constructor, only initialization from getInstance.
     *
     * @param context parent context
     */
    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Singleton construct design pattern.
     *
     * @param context parent context
     * @return single instance of VolleySingleton
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    /**
     * Get RetryPolicy
     *
     * @return RetryPolicy
     */
    public static RetryPolicy getRetryPolicy() {
        if (policy == null) {
            final int socketTimeout = 30000;
            policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        return policy;
    }

    /**
     * Get current request queue.
     *
     * @return RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add new request depend on type like string, json object, json array request.
     *
     * @param req new request
     * @param <T> request type
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
