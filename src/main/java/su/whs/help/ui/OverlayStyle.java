package su.whs.hole.ui;

/**
 * Created by igor n. boulliev on 15.07.15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

import java.util.Locale;

import su.whs.utils.TextUtils;
import su.whs.watl.samples.R;

/**
 *
 * @author igor n.boulliev copyright (c) 2014 whs.su
 *
 */
public class OverlayStyle {
    private int a_backgroundColor = 0x44a0a0a0;
    private int a_backgroundAlpha = 68;
    private String a_hintFontName = null;
    private float a_hintTextSize = 0f;
    private int a_hintFontColor = Color.WHITE;
    private boolean a_highlightsAllowCircles = true;
    private boolean a_noarrows = false;
    private boolean a_touchToNext = true;
    private int a_arrow_resourceId = 0;
    private int[] a_hintMargins = new int[] { 15, 15, 15, 15 };
    private int a_markerResourceId = -1;
    private int[] a_markerMargins = new int[] { 8, 8, 8, 8 };
    private int[] a_arrowMargins = new int[] { 4, 4, 4, 4 };
    private int a_btnBackground = -1;
    private String a_btnNextText = null;
    private String a_btnLastText = null;
    private String a_btnBackText = null;
    private String a_btnCloseText = null;
    private int a_btnCloseBackground = -1;
    private int a_borderWidth = 5;
    private boolean a_backButtonEnabled = false;
    private boolean a_closeButtonEnabled = false;
    private float a_buttonsTextSize = 22f;
    private int a_buttonsFontColor = Color.WHITE;
    private String a_buttonsFontName = null;
    private float a_highlightRectEdgesBlur = 5;
    private float a_highlightRectCornersRadius = 5f;

    public String toString() {
        return String.format(Locale.getDefault(), ""
                        + "HelpOverlayScreenStyle:\n" + "\tbackground color: %x\n"
                        + "\tbackground alpha: %x\n" + "\thint font name: '%s'\n"
                        + "\thint text size: %f\n" + "\thint font color: %x\n"
                        + "\tallow circles: %s\n" + "\tarrows enabled: %s\n"
                        + "\tbutton next: '%s'\n" + "\tbutton back: '%s'\n",
                a_backgroundColor, a_backgroundAlpha, a_hintFontName,
                a_hintTextSize, a_hintFontColor, a_highlightsAllowCircles,
                !a_noarrows, a_btnNextText, a_btnBackText);

    }

    public OverlayStyle(Context context) {
        initDefaults(context);
    }

    private void initDefaults(Context context) {
        if (a_btnNextText == null) {
            a_btnNextText = context.getString(R.string.whs_s_next);
        }
        if (a_btnLastText == null) {
            a_btnLastText = context.getString(R.string.whs_s_last);
        }
        if (a_hintTextSize==0f) {
            Button b = new Button(context);
            b.setTextAppearance(context,android.R.style.TextAppearance_Small);
            a_hintTextSize = b.getTextSize();
        }
    }

    public OverlayStyle(Context context, AttributeSet attrs) { // TypedArray attributes //) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.whsHelpOverlay);
        // TODO: 'backgroundColor' attribute - tested
        if (attributes.hasValue(R.styleable.whsHelpOverlay_backgroundColor)) {
            a_backgroundColor = attributes.getColor(
                    R.styleable.whsHelpOverlay_backgroundColor, 0x440a0a0a);
        }

        // TODO: 'backgroundAlpha' attribute
        if (attributes.hasValue(R.styleable.whsHelpOverlay_backgroundAlpha)) {
            a_backgroundAlpha = attributes.getInt(
                    R.styleable.whsHelpOverlay_backgroundAlpha, 0x44);
        }

        // TODO: 'hintFontColor' attribute - tested
        if (attributes.hasValue(R.styleable.whsHelpOverlay_hintFontColor)) {
            a_hintFontColor = attributes.getColor(
                    R.styleable.whsHelpOverlay_hintFontColor, Color.WHITE);
        }
        // TODO: 'hintFontName' attribute - testeed
        if (attributes.hasValue(R.styleable.whsHelpOverlay_hintFontName)) {
            a_hintFontName = attributes
                    .getString(R.styleable.whsHelpOverlay_hintFontName);
        }
        // TODO: 'hintTextSize' attribute - tested
        if (attributes.hasValue(R.styleable.whsHelpOverlay_hintTextSize)) {
            a_hintTextSize = attributes.getDimensionPixelSize(
                    R.styleable.whsHelpOverlay_hintTextSize, 18);
        }
        // TODO: 'highlits' attribute
        if (attributes.hasValue(R.styleable.whsHelpOverlay_highlights)) {
            int value = attributes.getInt(
                    R.styleable.whsHelpOverlay_highlights, 0);
            a_highlightsAllowCircles = value != 0;
        }
        // TODO: 'noarrows' attribute
        if (attributes.hasValue(R.styleable.whsHelpOverlay_noarrow)) {
            a_noarrows = attributes.getBoolean(
                    R.styleable.whsHelpOverlay_noarrow, false);
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_buttonsBackground)) {
            a_btnBackground = attributes.getResourceId(
                    R.styleable.whsHelpOverlay_buttonsBackground, -1);
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_nextButtonText)) {
            a_btnNextText = attributes
                    .getString(R.styleable.whsHelpOverlay_nextButtonText);
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_lastButtonText)) {
            a_btnLastText = attributes.getString(R.styleable.whsHelpOverlay_lastButtonText);
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_enableBackButton)) {
            a_backButtonEnabled = attributes.getBoolean(
                    R.styleable.whsHelpOverlay_enableBackButton, false);
            if (attributes.hasValue(R.styleable.whsHelpOverlay_backButtonText)
                    && a_backButtonEnabled) {
                a_btnBackText = attributes
                        .getString(R.styleable.whsHelpOverlay_backButtonText);
            }
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_enableCloseButton)) {
            a_closeButtonEnabled = attributes.getBoolean(
                    R.styleable.whsHelpOverlay_enableCloseButton, false);
            if (attributes
                    .hasValue(R.styleable.whsHelpOverlay_closeButtonBackground)
                    && a_closeButtonEnabled) {
                a_btnCloseBackground = attributes.getResourceId(
                        R.styleable.whsHelpOverlay_closeButtonBackground, -1);
            }
            if (attributes.hasValue(R.styleable.whsHelpOverlay_closeButtonText)
                    && a_closeButtonEnabled) {
                a_btnCloseText = attributes
                        .getString(R.styleable.whsHelpOverlay_closeButtonText);
            }
        }

        if (attributes.hasValue(R.styleable.whsHelpOverlay_buttonsFontName)) {
            a_buttonsFontName = attributes.getString(R.styleable.whsHelpOverlay_buttonsFontName);
        }

        a_buttonsFontColor = attributes.getColor(R.styleable.whsHelpOverlay_buttonsFontColor, Color.WHITE);
        a_buttonsTextSize = attributes.getDimensionPixelSize(R.styleable.whsHelpOverlay_buttonsFontSize, 22);
        a_highlightRectCornersRadius = attributes.getFloat(R.styleable.whsHelpOverlay_highlightRectCornerRadius, 5f);
        a_highlightRectEdgesBlur = attributes.getFloat(R.styleable.whsHelpOverlay_highlightRectEdgesBlur, 5f);

        a_touchToNext = attributes.getBoolean(
                R.styleable.whsHelpOverlay_touchToNext, true);


        if (!a_noarrows) {
            a_arrow_resourceId = attributes.getResourceId(
                    R.styleable.whsHelpOverlay_arrow,
                    R.drawable.ic_arrow_4left_top);
        }

        fillMargins(attributes, R.styleable.whsHelpOverlay_hintMargins,
                R.styleable.whsHelpOverlay_hintMarginLeft,
                R.styleable.whsHelpOverlay_hintMarginTop,
                R.styleable.whsHelpOverlay_hintMarginRight,
                R.styleable.whsHelpOverlay_hintMarginBottom, a_hintMargins);
        // 'hintMarginBottom' attributes
        // TODO: 'marker' attribute
        if (attributes.hasValue(R.styleable.whsHelpOverlay_markerDrawable)) {
            a_markerResourceId = attributes.getResourceId(
                    R.styleable.whsHelpOverlay_markerDrawable, -1);
        }
        // TODO: 'markerMargins' attribute
        // TODO: 'markerMarginLeft', 'markerMarginTop', 'markerMarginBottom',
        // 'markerMarginRight' attributes
        fillMargins(attributes, R.styleable.whsHelpOverlay_markerMargins,
                R.styleable.whsHelpOverlay_markerMarginLeft,
                R.styleable.whsHelpOverlay_markerMarginTop,
                R.styleable.whsHelpOverlay_markerMarginRight,
                R.styleable.whsHelpOverlay_markerMarginBottom, a_markerMargins);

        // arrow margins
        fillMargins(attributes, R.styleable.whsHelpOverlay_arrowMargins,
                R.styleable.whsHelpOverlay_arrowMarginLeft,
                R.styleable.whsHelpOverlay_arrowMarginTop,
                R.styleable.whsHelpOverlay_arrowMarginRight,
                R.styleable.whsHelpOverlay_arrowMarginBottom, a_arrowMargins);

        // borderWidth
        if (attributes.hasValue(R.styleable.whsHelpOverlay_borderWidth)) {
            a_borderWidth = attributes.getDimensionPixelSize(
                    R.styleable.whsHelpOverlay_borderWidth, 5);
        }
        attributes.recycle();
        initDefaults(context);
    }

    private void fillMargins(TypedArray ta, int common, int l, int t, int r,
                             int b, int[] margins) {
        if (ta.hasValue(common)) {
            int value = ta.getDimensionPixelSize(common, 3);
            margins[0] = value;
            margins[1] = value;
            margins[2] = value;
            margins[3] = value;
        }
        if (ta.hasValue(l))
            margins[0] = ta.getDimensionPixelSize(l, margins[0]);
        if (ta.hasValue(t))
            margins[1] = ta.getDimensionPixelSize(t, margins[1]);
        if (ta.hasValue(r))
            margins[2] = ta.getDimensionPixelSize(r, margins[2]);
        if (ta.hasValue(b))
            margins[3] = ta.getDimensionPixelSize(b, margins[3]);
    }

    public int getBackgroundColor() {
        return (a_backgroundColor & 0x00ffffff) + a_backgroundAlpha << 24;
    }

    public String getHintFontName() {
        return a_hintFontName;
    }

    public float getHintTextSize() {
        return a_hintTextSize;
    }

    public int getHintFontColor() {
        return a_hintFontColor;
    }

    public boolean isSquaresOnly() {
        return !a_highlightsAllowCircles;
    }

    public boolean isArrowsEnabled() {
        return !a_noarrows;
    }

    public int getArrowResourceId() {
        return a_arrow_resourceId;
    }

    public int[] getHintMargins() {
        return a_hintMargins;
    }

    public int getMarkerResourceId() {
        return a_markerResourceId;
    }

    public int[] getMarkerMargins() {
        return a_markerMargins;
    }

    public int[] getArrowMargins() {
        return a_arrowMargins;
    }

    public TextPaint getHintTextPaint() {
        TextPaint result = new TextPaint();
        result.setTextSize(a_hintTextSize);
        result.setColor(a_hintFontColor);
        return result;
    }

    public boolean backButtonEnabled() {
        return a_backButtonEnabled;
    }

    public boolean closeButtonEnabled() {
        return a_closeButtonEnabled;
    }

    public String getNextButtonText() {
        return a_btnNextText;
    }

    public String getLastButtonText() {
        return a_btnLastText;
    }

    public String getBackButtonText() {
        return a_btnBackText;
    }

    public String getCloseButtonText() {
        return a_btnCloseText;
    }

    public int getCloseButtonBackground() {
        return a_btnCloseBackground;
    }

    public int getButtonsBackground() {
        return a_btnBackground;
    }

    public Typeface getHintTypeface(Context context) {
        if (a_hintFontName != null)
            return TextUtils.loadFont(context, a_hintFontName);
        return Typeface.DEFAULT;
    }

    public boolean isTouchToNext() {
        return a_touchToNext;
    }

    // TODO: document attribute
    public int getBorderWidth() {
        return a_borderWidth;
    }

    public float getButtonsTextSize() { return a_buttonsTextSize; }
    public int getButtonsFontColor() { return a_buttonsFontColor; }
    public Typeface getButtonsTypeface(Context context) {
        if (a_buttonsFontName!=null)
            return TextUtils.loadFont(context, a_buttonsFontName);
        return Typeface.DEFAULT;
    }

    public float getHighlightRectEdgesBlur() { return a_highlightRectEdgesBlur; }
    public float getHighlightRectCornersRadius() { return a_highlightRectCornersRadius; }
}