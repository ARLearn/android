package org.celstec.arlearn2.android.game.generalItem;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.StyleUtilInterface;
import org.celstec.arlearn2.android.R;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class FeedbackView extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_general_item_feedback);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        StyleUtilInterface styleUtil = DrawableUtil.styleUtil;
        ((GradientDrawable) (findViewById(R.id.continue_button)).getBackground()).setColor(styleUtil.getPrimaryColor());
        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
        shapeDrawable.setColor(styleUtil.getBackgroundDark());

        String feedback =getIntent().getStringExtra("feedback");
        if (feedback != null) {
            ((TextView)this.findViewById(R.id.feedbackText)).setText(feedback);
            this.findViewById(R.id.feedbackText).setVisibility(View.VISIBLE);
            this.findViewById(R.id.feedbackTextWeb).setVisibility(View.GONE);
        }else {
            this.findViewById(R.id.feedbackText).setVisibility(View.GONE);
            this.findViewById(R.id.feedbackTextWeb).setVisibility(View.VISIBLE);
            feedback ="<font color='white'>";
            String feedbackQuestion = null;
            int i = 0;
            do {
                feedbackQuestion =  getIntent().getStringExtra("feedbacka"+i);
                String feedbackItem = getIntent().getStringExtra("feedback"+i);
                i++;
                if (feedbackQuestion !=null) feedback +="<b>"+ feedbackQuestion + "</b><br>";
                if (feedbackItem !=null) feedback += feedbackItem +"<br><br>";
            } while (feedbackQuestion!= null);
            if (getIntent().getBooleanExtra("missing", false)) {
                feedback += "<b>At least one correct answer is missing</b>";
            }
            feedback += "</font>";
            WebView webView = (WebView) this.findViewById(R.id.feedbackTextWeb);
            webView.setBackgroundColor(0x00000000);

            webView.loadDataWithBaseURL("file:///android_res/raw/", feedback, "text/html", "UTF-8", null);

        }

        if (getIntent().getBooleanExtra("correct", true)){
            ((TextView)this.findViewById(R.id.feedbackTitle)).setText("correct");
        } else {
            ((TextView)this.findViewById(R.id.feedbackTitle)).setText("wrong");
        }

        (findViewById(R.id.continue_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackView.this.finish();
            }
        });
    }
}
