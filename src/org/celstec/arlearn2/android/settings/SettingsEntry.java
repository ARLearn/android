package org.celstec.arlearn2.android.settings;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.util.DrawableUtil;

/**
 * Created by str on 28/01/15.
 */
public abstract class SettingsEntry {

    public static final int BUTTON = 1;
    public static final int STRING = 2;

    private String name;
    private int type;
    private Activity activity;

    protected TextView button;

    public SettingsEntry(String name, int type, Activity activity) {
        this.name = name;
        this.type = type;
        this.activity = activity;
    }

    public View inflate(LayoutInflater inflater, View v) {
        View view = inflater.inflate(R.layout.settings_entry, (ViewGroup) v, false);
        ((TextView)view.findViewById(R.id.text)).setText(name);
        button = ((TextView) view.findViewById(R.id.buttonSettings));
        button.setText(name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
//                StateListDrawable stateListDrawable = new StateListDrawable();
//                stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(R.color.settingsBlue));
//                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(R.color.settingsBluePressed));
//
//
//                button.setBackgroundDrawable(new ColorDrawable(R.color.settingsBlue));
            }
        });

        button.setEnabled(buttonState());

        return view;
    }

    protected abstract void click();

    protected boolean buttonState() {
        return true;
    }
}
