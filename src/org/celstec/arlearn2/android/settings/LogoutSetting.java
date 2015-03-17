package org.celstec.arlearn2.android.settings;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.util.DrawableUtil;

/**
 * Created by str on 28/01/15.
 */
public class LogoutSetting extends SettingsEntry {


    public LogoutSetting(Activity activity) {
        super(activity.getString(R.string.logout), BUTTON, activity);

    }

    protected void click() {
        ARL.accounts.disAuthenticate();
        button.setEnabled(false);

    }

    protected  boolean buttonState(){
        return ARL.accounts.isAuthenticated();
    }

}
