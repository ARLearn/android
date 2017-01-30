package org.celstec.arlearn2.android.delegators;

import android.os.Handler;
import org.celstec.arlearn2.android.events.GeneralItemBecameVisibleEvent;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

/**
 * Created by str on 17/03/15.
 */
public class VisibilityHandler {

    private static VisibilityHandler instance;
    private Handler mHandler = new Handler();

    private VisibilityHandler() {

    }

    public static VisibilityHandler getInstance()  {

        if (instance == null) {
            instance = new VisibilityHandler();
        }
        return instance;
    }

    public void scheduleVisibilityEvent(long satisfiedAt, final RunLocalObject run, final GameLocalObject game){
        ARL.time.printTime("satisfiedAt ", satisfiedAt);
        ARL.time.printTime();
        long delay = satisfiedAt -System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("not con we are calculating visibility");
                ARL.generalItemVisibility.calculateVisibility(run.getId(), game.getId());
            }
        }, delay);
    }

    public void scheduleInvisibilityEvent(){

    }

    public void scheduleInVisibilityEvent(long disappearAt, final RunLocalObject run, final GameLocalObject game, final Long itemId) {
        ARL.time.printTime("satisfiedAt ", disappearAt);
        ARL.time.printTime();
        long delay = disappearAt -ARL.time.getServerTime();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GeneralItemEvent newInVisibilityStatementDetected = new GeneralItemEvent(itemId);
                ARL.time.printTime();
                System.out.println("servertime as long " + ARL.time.getServerTime());
                ARL.eventBus.postSticky(newInVisibilityStatementDetected);
//                GeneralItemBecameVisibleEvent event = new GeneralItemBecameVisibleEvent(itemId);
//                ARL.eventBus.postSticky(event);
//                ARL.generalItemVisibility.calculateInVisibility(run.getId(), game.getId());
            }
        }, delay);
    }
}
