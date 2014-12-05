package org.celstec.arlearn2.android.dataCollection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
//import org.celstec.arlearn2.android.views.StyleUtil;

import java.io.File;


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
public class AudioInputManager extends DataCollectionManager {


    public AudioInputManager(Activity ctx) {
        super(ctx);
        response.setAudioType();

    }
    @Override
    public void takeDataSample(Class className) {
        Intent textInputIntent = new Intent(ctx, className);
//        textInputIntent.putExtra(StyleUtil.class.getName(), getTheme());
//        Intent textInputIntent = new Intent(ctx, AudioCollectionActivity.class);
        ctx.startActivityForResult(textInputIntent, AUDIO_RESULT);
    }

    @Override
    public void takeDataSample(Class className, String message) {
        Intent textInputIntent = new Intent(ctx, className);
        textInputIntent.putExtra("message", message);

        ctx.startActivityForResult(textInputIntent, AUDIO_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getExtras().get("filePath").toString();
            File audioFile = new File(filePath);
            response.setUriAsString(Uri.fromFile(audioFile).toString());
//            response.setValue(text);
            saveResponseForSyncing();
        }
    }
}

