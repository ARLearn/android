package org.celstec.arlearn2.android.dataCollection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.TextInputCollectionActivity;
import org.celstec.arlearn2.android.util.MediaFolders;

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
public class TextInputManager extends DataCollectionManager {


    public TextInputManager(Activity ctx) {
        super(ctx);
        response.setTextType();

    }
    @Override
    public void takeDataSample() {
        Intent textInputIntent = new Intent(ctx, TextInputCollectionActivity.class);

//        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(bitmapFile));
        ctx.startActivityForResult(textInputIntent, TEXT_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String text = data.getExtras().get("textValue").toString();
            response.setValue(text);
            saveResponseForSyncing();
        }
    }
}
