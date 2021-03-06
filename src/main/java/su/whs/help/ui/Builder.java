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
        } else if ("baloon".equals(parser.getName())) {
            beginBaloon(parser, res);
        }
    }

    private void handleEndTag(XmlPullParser parser, Resources res) {
        if ("screen".equals(parser.getName())) {
            commitScreen();
        } else if ("hint".equals(parser.getName())) {
            processHint(parser, res);
        } else if ("details".equals(parser.getName())) {
            processDetails(parser, res);
        } else if ("baloon".equals(parser.getName())) {
            commitBaloon();
        }
    }

    private void handleText(XmlPullParser parser, Resources res) {
        text = parser.getText();
        if (text != null)
            text = AndroidUtils.resolveString(context, res, text);
    }

    private ScreenDefinition currentScreenDefition = null;
    private boolean preferenceMode = false;

    private void beginScreen(XmlPullParser parser, Resources res) {
        if (currentScreenDefition != null)
            throw new IllegalStateException("inner screen tag not allowed");
        // attributes:
        // id

        String _id = parser.getAttributeValue(null, "id");
        if (_id != null) {
            currentScreenDefition = new ScreenDefinition();
            currentScreenDefition.viewId = AndroidUtils.resolveId(
                    context, res, _id);
            Log.v(TAG, "id attribute='" + currentScreenDefition.viewId
                    + "'");
        } else {
            _id = parser.getAttributeValue(null,"key");
            if (_id!=null) {
                ScreenDefinition.PreferenceDefinition pd = new ScreenDefinition.PreferenceDefinition();
                pd.key = _id;
                currentScreenDefition = pd;
            }
        }
        readHintAndDetails(parser,res);
    }

    private void readHintAndDetails(XmlPullParser parser, Resources res) {
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
        preferenceMode = false;
        currentScreenDefition = null;
    }

    private void beginBaloon(XmlPullParser parser, Resources res) {
        if (currentScreenDefition != null)
            throw new IllegalStateException("inner screen tag not allowed");
        // attributes:
        // id

        String _id = parser.getAttributeValue(null, "id");
        if (_id==null) {
            Log.d(TAG,"try to lookup key attribute");
            String _key = parser.getAttributeValue(null,"key");
            if (_key!=null) {
                ScreenDefinition.PreferenceDefinition pd = new ScreenDefinition.PreferenceDefinition();
                pd.key = _key;
                currentScreenDefition = pd;
            } else {
                Log.e(TAG,"definition has no id or key");
                return;
            }
        } else {
            currentScreenDefition = new ScreenDefinition();
            currentScreenDefition.viewId = AndroidUtils.resolveId(
                    context, res, _id);
        }
        currentScreenDefition.mode = ScreenDefinition.Mode.BALOON;
        readHintAndDetails(parser,res);
    }

    private void commitBaloon() {
        mDefinitions.add(currentScreenDefition);
        preferenceMode = false;
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