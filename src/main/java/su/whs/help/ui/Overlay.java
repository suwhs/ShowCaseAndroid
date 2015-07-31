package su.whs.help.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import su.whs.help.utils.OverlaysManager;
import su.whs.showcase.R;

/**
 * Created by igor n. boulliev on 11.07.15.
 */
public class Overlay extends View implements IOverlay {
    private static final String TAG = "Overlay";

    private int mXmlResourceId = -1;
    private boolean mIsContentDescriptionMode = false;
    private OverlayStyle mStyle = null;
    private OverlayView mOverlay = null;
    private List<ScreenDefinition> mScreens = new ArrayList<ScreenDefinition>();
    private int mCurrentScreen = 0;
    private AttributeSet mAttributes;

    public Overlay(Context context) {
        super(context);
    }

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public Overlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Overlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mAttributes = attrs;
        if (!isInEditMode()) {
            setVisibility(View.INVISIBLE);
            getRootView().getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            onViewsCreated();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            else
                                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
            );
        }

        boolean isXmlMode = false;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.whsHelpOverlay);
        if (ta.hasValue(R.styleable.whsHelpOverlay_overlayScreens)) {
            TypedValue tv = new TypedValue();
            // check for xml-description resource
            if (ta.getValue(R.styleable.whsHelpOverlay_overlayScreens, tv)) {
                if (tv.type == TypedValue.TYPE_REFERENCE
                        || tv.type == TypedValue.TYPE_STRING) {
                    mXmlResourceId = tv.resourceId;
                    isXmlMode = true;
                }
            }
        }

        if (ta.hasValue(R.styleable.whsHelpOverlay_autoStart)) {
            switch (ta.getInt(R.styleable.whsHelpOverlay_autoStart, 0)) {
                case 0: // once
                    break;
                case 1: // version
                    break;
                default: // disabled
                    break;
            }
        }

        /*
        if (ta.hasValue(R.styleable.whsHelpOverlay_startOnClick)) {
            mOnClickStartId = ta.getResourceId(
                    R.styleable.whsHelpOverlay_startOnClick, -1);
        }
        */

        if (!isXmlMode) {
            mIsContentDescriptionMode = true;
        }

        mStyle = new OverlayStyle(getContext(), attrs);
        Log.d(TAG, "Style: " + mStyle.toString());
        ta.recycle();
    }

    private void onViewsCreated() {
        /* all views created */
        OverlaysManager.getInstance(getContext()).register(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScreens.clear();
        OverlaysManager.getInstance(getContext()).unregister(this);
    }

    @Override
    public Parcelable onSaveInstanceState() {

        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    /**
     * method called by OverlaysManager
     */

    public void start() {
        if (mIsContentDescriptionMode) {
            startContentDescriptionMode();
        } else {
            startXml();
        }
    }

    /* */

    private void startContentDescriptionMode() {
        initOverlayView(); // disable interaction until screens actualy starts
        ViewParent parent = getParent();
        // scan parent's child views and collect pairs [view:contentDescription]
        // build screens
        // start display sequence
        if (parent instanceof ViewGroup) {
            ViewGroup p = (ViewGroup)parent;
            setupContentDescriptionMode(p);
        } else {
            Log.e(TAG, "parent are not ViewGroup");
            throw new IllegalStateException();
        }
        next();

    }

    private void next() {
        if (mCurrentScreen<mScreens.size()) {
            mOverlay.setScreenDefinition((ViewGroup)getParent(),mScreens.get(mCurrentScreen));
            mCurrentScreen++;
        } else {
            finishOverlayView();
        }
        updateControlsState();
    }

    private void updateControlsState() {
        mOverlay.setNextButtonIsLast(!(mCurrentScreen < mScreens.size()));
    }

    private void setupContentDescriptionMode(ViewGroup container) {
        List<View> buf = new ArrayList<View>();

        for (int i=0; i<container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof IOverlay && child != this) // if container already have IOverlay - stop processing
                return;
            buf.add(child);
        }
        List<ViewGroup> groups = new ArrayList<ViewGroup>();
        for (int i=0; i<buf.size(); i++) {
            View child = buf.get(i);
            if (child instanceof ViewGroup) {
                groups.add((ViewGroup) child);
            } else if (child instanceof AdapterView) {
                setupContentDescriptionMode((AdapterView)child);
            } else {
                CharSequence contentDescription = child.getContentDescription();
                if (contentDescription==null || contentDescription.length()<1)
                    continue;
                ScreenDefinition sd = new ScreenDefinition();
                sd.viewId = child.getId();
                if (sd.viewId<0)
                    continue;
                sd.hint = contentDescription;
                mScreens.add(sd);
            }
        }
        for (int i=0; i<groups.size(); i++) {
            ViewGroup group = groups.get(0);
        }
    }

    private void setupContentDescriptionMode(AdapterView adapterView) {
        Log.e(TAG, "adapterView support not implemented yet");
    }

    private void startXml() {
        initOverlayView(); // disable interaction until screens actualy starts
        // use Builder to build screens for parent's scope
        // start display sequence
    }

    private void initOverlayView() {
        if (mOverlay!=null) {
            mOverlay.reset();
            mOverlay.bringToFront();
            mOverlay.setVisibility(View.VISIBLE);
            mOverlay.requestFocus();
            return;
        }
        View root = getRootView();
        if (root!=null && root instanceof ViewGroup) {
            ViewGroup container = (ViewGroup)root;
            mOverlay = new OverlayView(getContext(),mAttributes);
            mOverlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(mOverlay);
            mOverlay.bringToFront();
            mOverlay.requestFocus();
        }
    }

    private void finishOverlayView() {
        if (mOverlay!=null) {
            View root = getRootView();
            if (root!=null && root instanceof ViewGroup) {
                ViewGroup container = (ViewGroup)root;
                container.removeView(mOverlay);
                mOverlay = null;
                OverlaysManager.getInstance(getContext()).onFinished(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishOverlayView();
    }

}
