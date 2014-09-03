package org.celstec.arlearn2.android.dataCollection;

import android.app.Activity;
import android.content.Intent;
import org.celstec.arlearn2.android.dataCollection.activities.TextInputCollectionActivity;
import org.celstec.arlearn2.android.dataCollection.activities.ValueInputCollectionActivity;

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
public class ValueInputManager extends DataCollectionManager {


    public ValueInputManager(Activity ctx) {
        super(ctx);
        response.setValueType();

    }
    @Override
    public void takeDataSample(Class className) {
        Intent textInputIntent = new Intent(ctx, className);
//        Intent textInputIntent = new Intent(ctx, ValueInputCollectionActivity.class);
//        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(bitmapFile));
        ctx.startActivityForResult(textInputIntent, VALUE_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Double text = data.getExtras().getDouble("value");
            response.setNumberValue(text);
            saveResponseForSyncing();
        }
    }
}
