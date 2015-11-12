package org.celstec.arlearn2.android.listadapter.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.listadapter.AbstractGeneralItemsVisibilityAdapter;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class GeneralItemVisibilityAdapter extends AbstractGeneralItemsVisibilityAdapter {


    public GeneralItemVisibilityAdapter(Context context, long runId, long gameId) {
        super(context, runId, gameId);
    }

    public GeneralItemVisibilityAdapter(Context context, long runId, long gameId,  boolean messagesOnly) {
        super(context, runId, gameId, messagesOnly);
    }

    @Override
    public View newView(Context context, GeneralItemVisibilityLocalObject item, ViewGroup parent) {
        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View returnView = inflater.inflate(R.layout.game_message_entry, parent, false);
//        returnView.setBackgroundDrawable(DrawableUtil.getGameMessageEntryRead());
//        ((TextView) returnView.findViewById(R.id.messageText)).setTextColor(DrawableUtil.getGameMessageTextRead());
//        (returnView.findViewById(R.id.messageIcon)).setBackgroundDrawable(DrawableUtil.getGameMessageIconBackgroundRead());
        return returnView;

//        if (item.getGeneralItemLocalObject().isRead(runId)) {
//            View returnView = inflater.inflate(R.layout.game_message_entry_read, parent, false);
//            returnView.setBackgroundDrawable(DrawableUtil.getGameMessageEntryRead());
//            ((TextView) returnView.findViewById(R.id.messageText)).setTextColor(DrawableUtil.getGameMessageTextRead());
//            (returnView.findViewById(R.id.messageIcon)).setBackgroundDrawable(DrawableUtil.getGameMessageIconBackgroundRead());
//            return returnView;
//        } else {
//            View returnView = inflater.inflate(R.layout.game_message_entry, parent, false);
//            returnView.setBackgroundDrawable(DrawableUtil.getGameMessageEntry());
//            ((TextView) returnView.findViewById(R.id.messageText)).setTextColor(DrawableUtil.getGameMessageText());
//            (returnView.findViewById(R.id.messageIcon)).setBackgroundDrawable(DrawableUtil.getGameMessageIconBackgroundUnRead());
//            return returnView;
//        }


    }

    private String getDate(Long timeStampStr){

        try{
            DateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    @Override
    public void bindView(View view, Context context, GeneralItemVisibilityLocalObject item) {
//        TextView messageText =(TextView) view.findViewById(R.id.messageText);
//        ImageView messageIcon =  (ImageView) view.findViewById(R.id.messageIcon);
//        if (item.getGeneralItemLocalObject()!=null) {
//            messageText.setText(item.getGeneralItemLocalObject().getTitle());
//            messageIcon.setImageResource(
//                    GeneralItemMapper.mapConstantToDrawable(
//                            GeneralItemMapper.mapBeanToConstant(item.getGeneralItemLocalObject().getGeneralItemBean())
//                    )
//            );
//
//        } else {
//            messageText.setText("message not loaded");
//        }

        if (item.getGeneralItemLocalObject().isRead(runId)) {
            view.findViewById(R.id.messageEntryLinearLayoutRead).setVisibility(View.VISIBLE);
            view.findViewById(R.id.messageEntryLinearLayoutUnRead).setVisibility(View.GONE);
            view.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntryRead());
            ((TextView) view.findViewById(R.id.messageTextRead)).setTextColor(DrawableUtil.getGameMessageTextRead());
            (view.findViewById(R.id.messageIconRead)).setBackgroundDrawable(DrawableUtil.getGameMessageIconBackgroundRead());

            TextView messageText =(TextView) view.findViewById(R.id.messageTextRead);
            ImageView messageIcon =  (ImageView) view.findViewById(R.id.messageIconRead);
            setIconAndMessageText(item, messageText, messageIcon);
        } else {
            view.findViewById(R.id.messageEntryLinearLayoutUnRead).setVisibility(View.VISIBLE);
            view.findViewById(R.id.messageEntryLinearLayoutRead).setVisibility(View.GONE);
            view.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
            ((TextView) view.findViewById(R.id.messageTextUnRead)).setTextColor(DrawableUtil.getGameMessageText());
            (view.findViewById(R.id.messageIconUnRead)).setBackgroundDrawable(DrawableUtil.getGameMessageIconBackgroundUnRead());
            int dip = DrawableUtil.dipToPixels(8);
            (view.findViewById(R.id.messageIconUnRead)).setPadding(dip, dip, dip, dip);
            TextView messageText =(TextView) view.findViewById(R.id.messageTextUnRead);
            ImageView messageIcon =  (ImageView) view.findViewById(R.id.messageIconUnRead);
            setIconAndMessageText(item, messageText, messageIcon);
        }
    }

    private void setIconAndMessageText(GeneralItemVisibilityLocalObject item, TextView messageText, ImageView messageIcon) {
        if (item.getGeneralItemLocalObject()!=null) {
            GeneralItemLocalObject gi = item.getGeneralItemLocalObject();
            messageText.setText(item.getGeneralItemLocalObject().getTitle());
            Drawable icon = GameFileLocalObject.getDrawable(ARL.ctx, gi.getGameId(), "/generalItems/" + gi.getId() + "/icon");
            if (icon == null) {
                messageIcon.setImageResource(
                        GeneralItemMapper.mapConstantToDrawable(
                                GeneralItemMapper.mapBeanToConstant(item.getGeneralItemLocalObject().getGeneralItemBean())
                        )
                );
            } else {
                messageIcon.setImageDrawable(icon);
            }

        } else {
            messageText.setText("message not loaded");
        }
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && lazyList != null) {
            GeneralItemLocalObject item = lazyList.get(position).getGeneralItemLocalObject();
            if (item != null) {
                return item.getId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
