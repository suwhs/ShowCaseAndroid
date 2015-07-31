package su.whs.help.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.Preference;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import su.whs.help.utils.OverlaysManager;
import su.whs.showcase.R;

/**
 * Created by igor n. boulliev on 13.07.15.
 */

public class OverlayView extends FrameLayout {
    private static final String TAG="OverlayView";
    private boolean mPostionCalculated = false;
    private Button mNextButton;
    private TextView mHintTextView = null;
    private ScrollView mHintTextContainer = null;
    private ScreenDefinition mScreenDefinition = null;
    private ViewGroup mScopeRoot = null;
    private Bitmap mOverlayBitmap = null;
    private Canvas mOverlayCanvas = null;
    private OverlayStyle mStyle = null;
    private Paint pHole = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ViewGroup mRoot = null;
    private Rect mTargetRect = new Rect();
    private Rect mLocalRect = new Rect();
    private Rect mContentRect = new Rect();
    private boolean mHintMeasureState = true;
    private boolean mReadyToDraw = false;

    private enum Place {
        LEFT, TOP, RIGHT, BOTTOM
    }

    {
        mBorder.setStyle(Paint.Style.STROKE);
        pHole.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        pHole.setColor(Color.TRANSPARENT);
    }

    public OverlayView(Context context) {
        super(context);
        init(context,null,0,0,new OverlayStyle(context));
    }

    public OverlayView(Context context, OverlayStyle style) {
        super(context);
        init(context,null,0,0,style);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0,0, new OverlayStyle(context, attrs));
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0, new OverlayStyle(context,attrs));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes, new OverlayStyle(context, attrs));
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, OverlayStyle style) {
        if (context instanceof Activity) {
            mRoot = (ViewGroup) ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        } else {
            Log.e(TAG, "context are not instance of activity");
        }
        mStyle = style;
        mBorder.setColor(mStyle.getHintFontColor()); // TODO: create attribute BorderColor
        mBorder.setStrokeWidth(style.getBorderWidth());
        pHole.setMaskFilter(new BlurMaskFilter(style.getHighlightRectEdgesBlur(), BlurMaskFilter.Blur.NORMAL));
        mHintTextView = new TextView(context,attrs);

        mHintTextView.setTextColor(Color.WHITE);
        Typeface tf = mStyle.getHintTypeface(context);
        mHintTextView.setTypeface(tf);
        mHintTextContainer = new ScrollView(context,attrs);
        mHintTextView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mHintTextContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHintTextContainer.addView(mHintTextView);
        addView(mHintTextContainer);
        mNextButton = new Button(getContext());
        mNextButton.setBackgroundResource(R.drawable.help_overlay_button_bg);
        mNextButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mNextButton);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        requestFocus();
    }

    public void reset() {
        mPostionCalculated = false;
        mHintMeasureState = true;
        mReadyToDraw = false;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (!mPostionCalculated) {
            calculateViewsPositions();
            if (!mReadyToDraw) return;
        }
        if (mOverlayCanvas == null) {
            if (mOverlayBitmap!=null) {
                throw new IllegalStateException("bitmap was not propertly cleaned");
            }
            mOverlayBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_4444);
            mOverlayCanvas = new Canvas(mOverlayBitmap);
        }
        drawBackground(mOverlayCanvas);
        super.dispatchDraw(mOverlayCanvas);
        canvas.drawBitmap(mOverlayBitmap, 0, 0, null);
    }

    public void setScreenDefinition(ViewGroup scope, ScreenDefinition screenDefinition) {
        reset();
        mScreenDefinition = screenDefinition;
        mScopeRoot = scope;
        requestLayout();
    }

    private void drawBackground(Canvas canvas) {
        mOverlayBitmap.eraseColor(mStyle.getBackgroundColor());
        mOverlayCanvas.drawRect(mTargetRect, pHole);
    }
    private View lookupPreferenceItemByKey(String key) {
        return lookupPreferenceItemByKey(key,mScopeRoot);
    }

    private View lookupPreferenceItemByKey(String key, ViewGroup container) {
        for (int i=0; i<container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof AdapterView) {
                // we found settings listview ?
                View r = lookupPreferenceItemByKey(key, (AdapterView) child);
                if (r!=null) return r;
            }
        }
        return null;
    }

    private View lookupPreferenceItemByKey(String key, AdapterView adapterView) {
        for (int i=0; i < adapterView.getChildCount(); i++) {
            View v = adapterView.getChildAt(i);
            int pos = adapterView.getPositionForView(v);
            if (pos!=AdapterView.INVALID_POSITION) {
                Object obj = adapterView.getItemAtPosition(pos);
                if (obj instanceof Preference) {
                    Preference pref = (Preference)obj;
                    if (key.equals(pref.getKey())) return v;
                }
            }
        }
        return null;
    }

    private void calculateViewsPositions() {
        // take a rect of view
        // move hint text first
        // move hint title next
        View target;
        if (mScreenDefinition.viewId < 1 && mScreenDefinition instanceof ScreenDefinition.PreferenceDefinition) {
            target = lookupPreferenceItemByKey(((ScreenDefinition.PreferenceDefinition) mScreenDefinition).key);
            if (target!=null)
                mScreenDefinition.viewId = target.getId();
            else {
                Log.v(TAG,"no key found :(");
            }
        } else
            target = mScopeRoot.findViewById(mScreenDefinition.viewId);
        int[] locationInScreen = new int[2];
        if (target!=null) {
            target.getLocationOnScreen(locationInScreen);
            mTargetRect.set(locationInScreen[0], locationInScreen[1], 0, 0);
            this.getLocationOnScreen(locationInScreen);
            mTargetRect.left -= locationInScreen[0];
            mTargetRect.top -= locationInScreen[1];
            mTargetRect.right = mTargetRect.left + target.getWidth();
            mTargetRect.bottom = mTargetRect.top + target.getHeight();
        }

        /* lookup most square */
        /*
            _ 1 2 3
            a
            b  x
            c
         */

        int _2a = getWidth() * mTargetRect.top;
        int _1b = getHeight() * mTargetRect.left;
        int _3b = getHeight() * (getWidth() - mTargetRect.right);
        int _2c = getWidth() * (getHeight() - mTargetRect.bottom);

        if (_2a>_2c) {

        }

        TextPaint p = new TextPaint();
        p.setTextSize(mStyle.getHintTextSize());
        String m = "MMMMMMMMMMMMMMMM";
        float tm = p.measureText(m, 0, m.length());
        Rect hintRect = new Rect();
        hintRect.left = mStyle.getHintMargins()[0];
        hintRect.top = mTargetRect.bottom+mStyle.getHintMargins()[0];
        hintRect.right = getWidth()-mStyle.getHintMargins()[2];
        hintRect.bottom = getHeight()-mStyle.getHintMargins()[3];
        int hintHeight = 0;

        if (mHintMeasureState) {
            mHintTextView.setText(mScreenDefinition.hint);
            mHintMeasureState = false;
            mReadyToDraw = false;
        }  else if (!mReadyToDraw) {
            hintHeight = mHintTextView.getHeight();
            Log.d(TAG, "measured hint height: " + hintHeight);
            mReadyToDraw = true;
        }
        Rect nextButtonRect = new Rect();
        nextButtonRect.bottom = mContentRect.bottom - mStyle.getHintMargins()[3];
        nextButtonRect.right = mContentRect.right - mStyle.getHintMargins()[2];
        nextButtonRect.top = mContentRect.bottom-mNextButton.getHeight() - mStyle.getHintMargins()[3];
        nextButtonRect.left = mContentRect.right-mNextButton.getWidth() - mStyle.getHintMargins()[2];
        if (nextButtonRect.top<mTargetRect.bottom) {
            case_target_at_bottom(hintRect,nextButtonRect);
        } else if (mContentRect.bottom - mTargetRect.bottom < mTargetRect.top - mContentRect.top) {
            case_hint_above_target(hintRect);
        }

        move(mHintTextContainer, hintRect);
        move(mNextButton,nextButtonRect);
        synchronized (OverlayView.this) {
            if (mReadyToDraw) {
                mPostionCalculated = true;
            }
        }
    }

    private void case_target_at_bottom(Rect hint, Rect next) {
        next.bottom = mTargetRect.top - mStyle.getHintMargins()[3];
        next.top = mTargetRect.top-mNextButton.getHeight() - mStyle.getHintMargins()[3];
        hint.bottom = next.top - mStyle.getHintMargins()[3];
        hint.top = hint.bottom - mHintTextView.getHeight();
    }

    private void case_target_at_top(Rect hint, Rect next) {

    }

    private void case_hint_above_target(Rect hint) {

    }



    private void move(View v, Rect r) {
        Log.d(TAG, "move " + v + " to " + r);
        FrameLayout.LayoutParams lp = (LayoutParams) v.getLayoutParams();
        lp.setMargins(r.left, r.top, getWidth() - r.right, getHeight() - r.bottom);
        v.setLayoutParams(lp);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof View) {
            View content = ((View)parent).findViewById(android.R.id.content);
            content.getGlobalVisibleRect(mContentRect);
        } else {

        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOverlayCanvas!=null) {
            mOverlayCanvas = null;
        }
        if (mOverlayBitmap!=null && !mOverlayBitmap.isRecycled()) {
            mOverlayBitmap.recycle();
            mOverlayBitmap = null;
        }
    }

    public void setNextButtonIsLast(boolean last) {
        String text;
        if (last)
            text = mStyle.getLastButtonText();

        else
            text = mStyle.getNextButtonText();
        mNextButton.setText(text);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getAction()==KeyEvent.ACTION_DOWN) {

            } else if (event.getAction()==KeyEvent.ACTION_UP) {
                OverlaysManager.getInstance(getContext()).onBackPressed();
            }
            return true;
        }
        return super.onKeyPreIme(keyCode,event);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed || mHintMeasureState) {
            mPostionCalculated = false;
            invalidate();
        }
    }
}
