package org.celstec.arlearn2.android.qrCodeScanning;

import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.GameLocalObject;

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
public abstract class BarcodeAnalyzer {
//    public  class BarcodeAnalyzer {


        public void analyze(String data) {
            if (data.contains("gameId/")) {
                try {
                    data = data.substring(data.lastIndexOf("gameId/") + 7);
                    if (data.contains("/")) {
                        data = data.substring(0, data.lastIndexOf("/"));
                    }
                    long gameId = Long.parseLong(data);
//                    System.out.println(gameId);
                    Game gameBean = ARL.games.asyncGameBean(gameId);
                    if (gameBean !=null) scannedGame(gameBean);
                } catch (NumberFormatException e) {

                }
            }
//        long gameId = 110l;

//
    }

    public abstract void scannedGame(Game game);

    public abstract void scannedRun(long runId);

    public abstract void scannedLoginToken(String loginToken);

//    public static void main(String[] args) {
//        String data = "http://streetlearn.appspot.com/gameId/123";
//        new BarcodeAnalyzer().analyze(data);
//        data = "http://streetlearn.appspot.com/gameId/12345/test";
//        new BarcodeAnalyzer().analyze(data);
//    }
}
