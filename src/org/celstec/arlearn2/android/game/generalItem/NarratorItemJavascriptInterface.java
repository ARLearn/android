package org.celstec.arlearn2.android.game.generalItem;

import android.app.Notification;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.AudioResultActivity;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.ScanTag;
import org.celstec.dao.gen.ResponseLocalObject;

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
public class NarratorItemJavascriptInterface {

    private NarratorItem generalItemBean;
    private long runId;

    public NarratorItemJavascriptInterface(NarratorItem generalItemBean, long runId) {
        this.generalItemBean = generalItemBean;
        this.runId = runId;
    }

    public NarratorItemJavascriptInterface(ScanTag generalItemBean, long runId) {
        this.generalItemBean = generalItemBean;
        this.runId = runId;
    }

    @JavascriptInterface
    public void submitAction(String action){
        if (generalItemBean != null) {
            ARL.actions.issueAction(action,
                    runId,
                    generalItemBean.getId(),
                    generalItemBean.getType());
        }
    }


    @JavascriptInterface
    public void hint(String title, String text, String button){
        Intent audioRecording = new Intent(ARL.ctx, HintActivity.class);
        audioRecording.putExtra("title", title);
        audioRecording.putExtra("text", text);
        audioRecording.putExtra("button", button);
        ARL.ctx.startActivity(audioRecording);
    }

    @JavascriptInterface
    public void startChat(String title){
        Intent chatIntent = new Intent(ARL.ctx, ChatActivity.class);
        chatIntent.putExtra("threadName", title);
        chatIntent.putExtra("runId", runId);
        ARL.ctx.startActivity(chatIntent);


    }

}
