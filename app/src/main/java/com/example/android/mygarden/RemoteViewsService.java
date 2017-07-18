package com.example.android.mygarden;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

public class RemoteViewsService implements android.widget.RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor mCursor;

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri PLANT_URI = PlantContract.PlantEntry.CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME);

    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        else {
            return mCursor.getCount();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

            mCursor.moveToPosition(position);
            int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
            long plantId = mCursor.getLong(idIndex);
            long timeNow = System.currentTimeMillis();
            long wateredAt = mCursor.getLong(waterTimeIndex);
            long createdAt = mCursor.getLong(createTimeIndex);
            int plantType = mCursor.getInt(plantTypeIndex);

        RemoteViews views = new RemoteViews(mContext.getPackageName(),R.layout.plant_widget);

        int imgID = PlantUtils.getPlantImageRes(mContext, (timeNow - createdAt), (timeNow - wateredAt), plantType);

        views.setImageViewResource(R.id.widget_plant_image, imgID);

        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
