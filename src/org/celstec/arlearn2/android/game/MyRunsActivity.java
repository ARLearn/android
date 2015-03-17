package org.celstec.arlearn2.android.game;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.RunsLazyListAdapter;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

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
public class MyRunsActivity extends ListActivity implements ListItemClickInterface<RunLocalObject> {
    private RunsLazyListAdapter adapter;
    private Long gameId;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(GameLocalObject.class.getName(), gameId);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_list);
        gameId = getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new RunsLazyListAdapter(this, gameId);
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
    }

    @Override
    public void onListItemClick(View v, int position, RunLocalObject run) {
        GameMessages.startActivity(this, run.getGameLocalObject().getId(), run.getId());
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, RunLocalObject object) {
        return false;
    }

    public static void startActivity(Context ctx, long gameId){
        Intent gameIntent = new Intent(ctx, MyRunsActivity.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
        ctx.startActivity(gameIntent);
    }
}
