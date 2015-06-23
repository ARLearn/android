package org.celstec.arlearn2.android.events;

import android.app.Activity;
import android.content.Intent;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.notification.NotificationAction;
import org.celstec.dao.gen.GeneralItemLocalObject;

/**
 * Created by str on 17/03/15.
 */
public class GeneralItemBecameVisibleEvent {
    private long generalItemId;
    private boolean showStroken;
    private boolean autoLaunch = false;

    public GeneralItemBecameVisibleEvent(long generalItemId) {
        this.generalItemId = generalItemId;
    }

    public long getGeneralItemId() {
        return generalItemId;
    }

    public void setGeneralItemId(long generalItemId) {
        this.generalItemId = generalItemId;
    }

    public boolean isShowStroken() {
        return showStroken;
    }

    public void setShowStroken(boolean showStroken) {
        this.showStroken = showStroken;
    }

    public boolean isAutoLaunch() {
        return autoLaunch;
    }

    public void setAutoLaunch(boolean autoLaunch) {
        this.autoLaunch = autoLaunch;
    }

    public void processEvent( final GameActivityFeatures gameActivityFeatures, final Activity activity, final Activity closeActivity){
        ARL.eventBus.removeStickyEvent(this);
        System.out.println("LOG onEventMainThread "+System.currentTimeMillis());
        if (isAutoLaunch()) {
            Intent intent = new Intent(activity, GeneralItemActivity.class);
            gameActivityFeatures.addMetadataToIntent(intent);
            intent.putExtra(GeneralItemLocalObject.class.getName(), getGeneralItemId());
            activity.startActivity(intent);
            if (closeActivity != null) {
                closeActivity.finish();
            }
        } else
        if (isShowStroken()) {
            gameActivityFeatures.showStrokenNotification(new NotificationAction() {
                @Override
                public void onOpen() {

                    Intent intent = new Intent(activity, GeneralItemActivity.class);
                    gameActivityFeatures.addMetadataToIntent(intent);
                    intent.putExtra(GeneralItemLocalObject.class.getName(), getGeneralItemId());
                    activity.startActivity(intent);
                    if (closeActivity != null) {
                        closeActivity.finish();
                    }
                }
            });
        }
    }
}
