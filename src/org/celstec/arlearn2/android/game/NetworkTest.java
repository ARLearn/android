package org.celstec.arlearn2.android.game;

import android.os.Handler;
import android.os.Message;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.beans.Info;
import org.celstec.arlearn2.client.GenericClient;
import org.celstec.arlearn2.client.InfoClient;

import java.net.InetAddress;

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
public class NetworkTest {

    public void executeTest() {
        isNetworkAvailable(h,2000);
    }

    public class NetworkResult{
        private boolean result;

        public NetworkResult(boolean result) {
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }

    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;
            @Override
            public void run() {
                System.out.println("running thread");
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("https://streetlearn.appspot.com/js/routes.js");
                        try {
                            new DefaultHttpClient().execute(requestForTest); // can last...
                            responded = true;
                            System.out.println("esponded");
                        }
                        catch (Exception e) {
                            System.out.println("exception");
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        System.out.println("while " + (!responded) + waited+" "+timeout);

                        sleep(100);
                        if(!responded ) {
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e) {} // do nothing
                finally {
                    if (!responded) { handler.sendEmptyMessage(0); }
                    else { handler.sendEmptyMessage(1); }
                }
                System.out.println("out of loop");

            }
        }.start();
    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what != 1) { // code if not connected
                System.out.println("no network");
                ARL.eventBus.post(new NetworkResult(false));

            } else { // code if connected
                System.out.println("online");
                ARL.eventBus.post(new NetworkResult(true));

            }
        }
    };
}
