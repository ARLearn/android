package org.celstec.arlearn2.android.delegators;

import android.os.Handler;
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
        System.out.println("satisfiedAt "+satisfiedAt + " - " + (System.currentTimeMillis()-satisfiedAt));
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
}
