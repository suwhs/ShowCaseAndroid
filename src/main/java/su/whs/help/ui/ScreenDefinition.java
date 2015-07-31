package su.whs.help.ui;

/**
 * Created by igor n. boulliev on 15.07.15.
 */
class ScreenDefinition {
    public enum Mode {
        OVERLAY,
        BALOON
    }
    public Mode mode = Mode.OVERLAY;
    public int drawableResId;
    public String title;
    public CharSequence hint;
    public CharSequence details;
    public int viewId;
    public boolean asRow = false;
    public boolean asColumn = false;

    static class PreferenceDefinition extends ScreenDefinition {
        public String key;
    }
}
