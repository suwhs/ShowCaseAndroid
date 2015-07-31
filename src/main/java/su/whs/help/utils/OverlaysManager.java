package su.whs.hole.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import su.whs.hole.ui.IOverlay;
import su.whs.watl.samples.R;

/**
 * Created by igor n. boulliev on 11.07.15.
 */

public class OverlaysManager {
    private static final String TAG="OverlaysManager";
    private static HashMap<View,OverlaysManager> mInstances = new HashMap<View,OverlaysManager>();
    private boolean mStarted = false;
    private List<IOverlay> mQueued = new ArrayList<IOverlay>();
    private View mRootView = null;
    private int mCurrent = 0;

    public static synchronized OverlaysManager getInstance(Context context) {
        if (context instanceof Activity) {
            View root = ((Activity)context).getWindow().getDecorView();
            if (!mInstances.containsKey(root)) {
                OverlaysManager om =new OverlaysManager(context,root);
                root.setTag(R.string.overlaysManagerTag,om);
                mInstances.put(root,om);
            }
            return mInstances.get(root);
        }
        throw new IllegalArgumentException(""+context+" are not Activity");
    }

    private static synchronized void remove(Context context) {
        if (context instanceof Activity) {
            View root = ((Activity)context).getWindow().getDecorView();
            if (mInstances.containsKey(root))
                mInstances.remove(root);
            return;
        }
        throw new IllegalArgumentException(""+context+" are not Activity");
    }

    private Context mContext = null;
    private List<IOverlay> mViews = new ArrayList<IOverlay>();

    private OverlaysManager(Context context, View root) {
        mContext = context;
        mRootView = root;
    }

    public void register(IOverlay v) {
        if (!mViews.contains(v))
            mViews.add(v);
    }

    public void unregister(View v) {
        if (mViews.contains(v))
            mViews.remove(v);
        if (mViews.size()<1) {
            OverlaysManager.remove(v.getContext());
        }
    }

    /**
     *
     * @param v - IOverlay instance ()
     * @return true if start allowed, else - returns false (for using queue(View v))
     */

    public boolean start(IOverlay v) {
        Log.e(TAG, "start: " + v);
        if (mStarted)
            return false;
        v.start();
        mStarted = true;
        return true;
    }

    public boolean queue(IOverlay v) {
        mQueued.add(v);
        return true;
    }

    public void stop(View v) {
        if (mQueued.size()>0) {
            IOverlay next = mQueued.remove(0);
            next.start();
        } else {
            mStarted = false;
        }
    }

    public void start() {
        mCurrent = 0;
        next();
    }

    public void next() {
        if (mViews.size()>mCurrent) {
            IOverlay v = mViews.get(mCurrent);
            start(v);
        }
    }

    public void onBackPressed() {
        Log.e(TAG,"onBackPressed()");
        if (mCurrent<mViews.size()) {
            mViews.get(mCurrent).onBackPressed();
        }
    }

    public void onFinished(IOverlay v) {
        mStarted = false;
    }
}
