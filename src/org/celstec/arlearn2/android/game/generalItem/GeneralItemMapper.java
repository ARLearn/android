package org.celstec.arlearn2.android.game.generalItem;

import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.itemTypes.*;
import org.celstec.arlearn2.beans.generalItem.*;

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
public class GeneralItemMapper {
    
    public final static int NARRATOR_ITEM = 0;
    public final static int VIDEO_OBJECT = 1;
    public final static int AUDIO_OBJECT = 2;
    public final static int SCAN_TAG = 3;
    public final static int MULTI_CHOICE = 4;
    public final static int SINGLE_CHOICE = 5;
    public final static int MULTI_CHOICE_IMAGE = 6;
    public final static int SINGLE_CHOICE_IMAGE = 7;
    public final static int YOUTUBE = 8;
    public final static int PURE_AUDIO = 9;
    public final static int SORT_QUESTION = 10;

    public static int mapBeanToConstant (GeneralItem giBean) {
        if (giBean instanceof SortQuestion) {
            return SORT_QUESTION;
        } else if (giBean instanceof SingleChoiceImageTest) {
            return SINGLE_CHOICE_IMAGE;
        } else if (giBean instanceof MultipleChoiceImageTest) {
            return MULTI_CHOICE_IMAGE;
        } else if (giBean instanceof SingleChoiceTest) {
            return SINGLE_CHOICE;
        } else if (giBean instanceof MultipleChoiceTest) {
            return MULTI_CHOICE;
        } else if (giBean instanceof PureAudio) {
            return PURE_AUDIO;
        } else if (giBean instanceof AudioObject) {
            return AUDIO_OBJECT;
        } else if (giBean instanceof ScanTag) {
            return SCAN_TAG;
        } else if (giBean instanceof VideoObject) {
            return VIDEO_OBJECT;
        } else  if (giBean instanceof NarratorItem) {
            return NARRATOR_ITEM;
        }
        return 0;
    }

    public static int mapConstantToDrawable(int constant) {
        int result = R.drawable.game_data_collection_audio;
        switch (constant){
            case GeneralItemMapper.NARRATOR_ITEM:
                result =R.drawable.game_general_item_type_narrator;
                break;
            case GeneralItemMapper.SCAN_TAG:
                result =R.drawable.game_general_item_type_scan;
                break;
            case GeneralItemMapper.SINGLE_CHOICE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.MULTI_CHOICE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.SINGLE_CHOICE_IMAGE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.MULTI_CHOICE_IMAGE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.SORT_QUESTION:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.AUDIO_OBJECT:
                result =R.drawable.game_general_item_type_audio;
                break;
            case GeneralItemMapper.PURE_AUDIO:
                result =R.drawable.game_general_item_type_audio;
                break;
            case GeneralItemMapper.VIDEO_OBJECT:
                result =R.drawable.game_general_item_type_video;
                break;
            case GeneralItemMapper.YOUTUBE:
                result =R.drawable.game_general_item_type_youtube;
                break;
        }
        return result;
    }

    public static int mapConstantToDrawableMarker(int constant) {
        int result = R.drawable.game_data_collection_audio;
        switch (constant){
            case GeneralItemMapper.NARRATOR_ITEM:
                result =R.drawable.game_general_item_type_narrator;
                break;
            case GeneralItemMapper.SCAN_TAG:
                result =R.drawable.game_general_item_type_scan;
                break;
            case GeneralItemMapper.SINGLE_CHOICE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.MULTI_CHOICE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.SINGLE_CHOICE_IMAGE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.MULTI_CHOICE_IMAGE:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.SORT_QUESTION:
                result =R.drawable.game_general_item_type_mc;
                break;
            case GeneralItemMapper.AUDIO_OBJECT:
                result =R.drawable.game_general_item_marker_audio;
                break;
            case GeneralItemMapper.PURE_AUDIO:
                result =R.drawable.game_general_item_marker_audio;
                break;
            case GeneralItemMapper.VIDEO_OBJECT:
                result =R.drawable.game_general_item_type_video;
                break;
            case GeneralItemMapper.YOUTUBE:
                result =R.drawable.game_general_item_type_youtube;
                break;
        }
        return result;
    }
}
