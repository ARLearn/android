package authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.viewWrappers.GameRowBig;
import org.celstec.arlearn2.beans.account.Account;

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
public class LoginFragment  extends SherlockFragment {

    private static final String TAG = "Login";
    private View googleButton;
    private View facebookButton;
    private View linkedInButton;
    private View twitterButton;
    private View ecoButton;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.login, container, false);
        googleButton = v.findViewById(R.id.googleButton);
        facebookButton = v.findViewById(R.id.facebookButton);
        linkedInButton= v.findViewById(R.id.linkedinButton);
        twitterButton = v.findViewById(R.id.twitterButton);
        ecoButton =  v.findViewById(R.id.ecoButton);
        googleButton.setOnClickListener(new ButtonClickListener(Account.GOOGLE));
        facebookButton.setOnClickListener(new ButtonClickListener(Account.FACEBOOK));
        linkedInButton.setOnClickListener(new ButtonClickListener(Account.LINKEDIN));
        twitterButton.setOnClickListener(new ButtonClickListener(Account.TWITTER));
        ecoButton.setOnClickListener(new ButtonClickListener(Account.ECO));
        return v;
    }

    private class ButtonClickListener implements View.OnClickListener {

        private int providerId;

        public ButtonClickListener(int providerId) {
            this.providerId = providerId;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();
            Fragment frag = null;
            switch (providerId) {
                case Account.GOOGLE:
                    frag = new WebViewGoogle();
                    break;
                case Account.FACEBOOK:
                    frag = new WebViewFacebook();
                    break;
                case Account.TWITTER:
                    frag = new WebViewTwitter();
                    break;
                case Account.LINKEDIN:
                    frag = new WebViewLinkedin();
                    break;
                case Account.ECO:
                    frag = new WebViewEco();
                default:
                    break;
            }
            frag.setArguments(args);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.right_pane, frag).addToBackStack(null).commit();

        }



    }

}
