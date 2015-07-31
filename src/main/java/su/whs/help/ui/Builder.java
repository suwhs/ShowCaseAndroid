package su.whs.help.ui;

/**
 * Created by igor n. boulliev on 15.07.15.
 */
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class Builder {
    private static final String TAG = "Builder";
    private List<ScreenDefinition> mDefinitions = new ArrayList<ScreenDefinition>();
    private String text = null;
    private Context context;

    public Builder(Context context,int xmlResourceId) throws XmlPullParserException,
            IOException {
        this.context = context;
        Resources res = context.getResources();
        XmlPullParser parser = res.getXml(xmlResourceId);
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                handleStartTag(parser, res);
            } else if (parser.getEventType() == XmlPullParser.END_TAG) {
                handleEndTag(parser, res);
            } else if (parser.getEventType() == XmlPullParser.TEXT) {
                handleText(parser, res);
            }
            parser.next();
        }
    }

    public List<ScreenDefinition> get() {
        return mDefinitions;
    }

    private void handleStartTag(XmlPullParser parser, Resources res) {
        if ("screen".equals(parser.getName())) {
            beginScreen(parser, res);
        }
    }

    private void handleEndTag(XmlPullParser parser, Resources res) {
        if ("screen".equals(parser.getName())) {
            commitScreen();
        } else if ("hint".equals(parser.getName())) {
            processHint(parser, res);
        } else if ("details".equals(parser.getName())) {
            processDetails(parser, res);
        }
    }

    private void handleText(XmlPullParser parser, Resources res) {
        text = parser.getText();
        if (text != null)
            text = AndroidUtils.resolveString(context, res, text);
    }

    private ScreenDefinition currentScreenDefition = null;

    private void beginScreen(XmlPullParser parser, Resources res) {
        if (currentScreenDefition != null)
            throw new IllegalStateException("inner screen tag not allowed");
        // attributes:
        // id
        currentScreenDefition = new ScreenDefinition();
        String _id = parser.getAttributeValue(null, "id");
        if (_id != null) {
            currentScreenDefition.viewId = AndroidUtils.resolveId(
                    context, res, _id);
            Log.v(TAG, "id attribute='" + currentScreenDefition.viewId
                    + "'");
        }
        // hint
        String _hint = parser.getAttributeValue(null, "hint");
        if (_hint != null) {
            currentScreenDefition.hint = AndroidUtils.resolveString(
                    context, res, _hint);
            Log.v(TAG, "hint attribute='" + currentScreenDefition.hint
                    + "'");
        }
        // details
        String _details = parser.getAttributeValue(null, "details");
        if (_details != null) {
            currentScreenDefition.details = AndroidUtils.resolveString(
                    context, res, _details);
            Log.v(TAG, "details attribute='"
                    + currentScreenDefition.details + "'");
        }

    }

    private void commitScreen() {
        mDefinitions.add(currentScreenDefition);
        currentScreenDefition = null;
    }

    protected void processHint(XmlPullParser parser, Resources res) {
        if (text != null)
            currentScreenDefition.hint = text;
        text = null;
    }

    protected void processDetails(XmlPullParser parser, Resources res) {
        if (text != null)
            currentScreenDefition.details = text;
        text = null;
    }

}