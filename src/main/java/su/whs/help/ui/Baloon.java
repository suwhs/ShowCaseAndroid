package su.whs.help.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * Created by igor n. boulliev on 31.07.15.
 */
public class Baloon extends FrameLayout {
    private Rect mTargetRect = new Rect();

    public Baloon(Context context) {
        super(context);
        init(context,null,0,0);
    }

    public Baloon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0,0);
    }

    public Baloon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Baloon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr,defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void startJolting() {
        Animation shake = AnimationUtils.loadAnimation(getContext(),getAnimationResource());
        startAnimation(shake);
    }

    protected int getAnimationResource() {
        return su.whs.showcase.R.anim.shake;
    }
}
