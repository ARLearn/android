package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.internal.qr;
import net.sourceforge.zbar.*;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.qrCodeScanning.CameraPreview;
import org.celstec.arlearn2.android.qrCodeScanning.QRScanner;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.ScanTag;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.w3c.dom.Text;

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
public class ScanTagFeatures extends GeneralItemActivityFeatures implements QRScanner.ScanResultInterface {

    QRScanner qrScanner;
    boolean init = false;
    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.SCAN_TAG);
    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    public ScanTagFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
    }

    public void setMetadata(){
        DrawableUtil drawableUtil = ARL.getDrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);
        TextView titleView = (TextView) this.activity.findViewById(R.id.titleId);
        titleView.setText(generalItemLocalObject.getTitle());
        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        webView.loadDataWithBaseURL("file:///android_res/raw/", ((ScanTag) generalItemBean).getRichText(), "text/html", "UTF-8", null);
        ((GradientDrawable)activity.findViewById(R.id.button).getBackground()).setColor(drawableUtil.styleUtil.getPrimaryColor());

        if (!init) {
            initiateIcon();
            ((TextView)activity.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.findViewById(R.id.scanIntro).setVisibility(View.GONE);
                    activity.findViewById(R.id.cameraPreview).setVisibility(View.VISIBLE);
                    FrameLayout preview = (FrameLayout) activity.findViewById(R.id.cameraPreview);
                    qrScanner = new QRScanner(activity, ScanTagFeatures.this, preview);
                    RelativeLayout relativeLayoutControls = (RelativeLayout) activity.findViewById(R.id.scanOverlay);
                    relativeLayoutControls.bringToFront();
                    init = true;

                }
            });

        }
//        scanText = (TextView)v.findViewById(R.id.scanText);
    }

    @Override
    public void onPauseActivity(){
        if (qrScanner != null) qrScanner.releaseCamera();
    }

    @Override
    public void data(String data) {
        System.out.println("scanned "+data);
        Action action = new Action();
        action.setAction(data);
        action.setRunId(activity.getGameActivityFeatures().getRunId());
        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
        action.setGeneralItemId(generalItemLocalObject.getId());

        ActionsDelegator.getInstance().createAction(action);
        activity.finish();
    }
}
