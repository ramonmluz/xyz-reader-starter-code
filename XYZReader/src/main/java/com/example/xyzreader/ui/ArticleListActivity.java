package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ArticleListActivity.class.toString();

    public static final String SAVED_RECYCLER_VIEW_ID = "SAVED_RECYCLER_VIEW_ID";

    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private boolean mIsRefreshing = false;

    private ArticleAdapter adapter;

    private Cursor cursor;

    private BroadcastReceiver mRefreshingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        initBroadcastReceiver();

        getLoaderManager().initLoader(0, null, this);

//        if (savedInstanceState == null) {
//            onRefresh();
//        }
    }

    private void initRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);

        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Parcelable listStatePosition = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_RECYCLER_VIEW_ID, listStatePosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Parcelable listStatePosition = savedInstanceState.getParcelable(SAVED_RECYCLER_VIEW_ID);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(listStatePosition);
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void initBroadcastReceiver() {
        mRefreshingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                    mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
                    updateRefreshingUI();
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getBaseContext(), UpdaterService.class));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    private void registerReceiver() {
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }


    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mRefreshingReceiver);
    }


    private void updateRefreshingUI() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        this.cursor = cursor;

        adapter = new ArticleAdapter(this.cursor, this);

        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.getAdapter().notifyDataSetChanged();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}


