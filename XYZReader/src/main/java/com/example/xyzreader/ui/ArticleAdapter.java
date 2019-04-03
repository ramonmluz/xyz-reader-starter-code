package com.example.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private static final String TAG = ArticleAdapter.class.toString();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private Cursor mCursor;

    private Context mContext;


    public ArticleAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
    }
    public ArticleAdapter(){
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
//            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);

        final ViewHolder vh = new ViewHolder(view);

       return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
//        Date publishedDate = parsePublishedDate();
//        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
//
//            holder.subtitleView.setText(Html.fromHtml(
//                    DateUtils.getRelativeTimeSpanString(
//                            publishedDate.getTime(),
//                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                            DateUtils.FORMAT_ABBREV_ALL).toString()
//                            + "<br/>" + " by "
//                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
//        } else {
//            holder.subtitleView.setText(Html.fromHtml(
//                    outputFormat.format(publishedDate)
//                            + "<br/>" + " by "
//                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
//        }

        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(mContext).getImageLoader());

        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

        holder.thumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(position))));
            }
        });
    }

//    private Date parsePublishedDate() {
//        try {
//            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
//            return dateFormat.parse(date);
//        } catch (ParseException ex) {
//            Log.e(TAG, ex.getMessage());
//            Log.i(TAG, "passing today's date");
//            return new Date();
//        }
//    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
