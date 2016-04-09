package net.ariapura.sunshine;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.ariapura.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String DETAIL_URI = "DETAIL_URI";
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_LOCATION_SETTING = 9;
    static final int COL_WEATHER_CONDITION_ID = 10;
    static final int COL_COORD_LAT = 11;
    static final int COL_COORD_LONG = 12;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String SUNSHINE_HASH_TAG = " #SunshineApp";
    private static final int DETAIL_FORECAST_LOADER_ID = 0;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };
    ShareActionProvider mShareActionProvider;
    String mForecastString;

    TextView mDayTv;
    TextView mDateTv;
    TextView mHighTv;
    TextView mLowTv;
    TextView mHumidityTv;
    TextView mWindTv;
    TextView mPressureTv;
    TextView mForecastTextView;
    ImageView mImage;
    Uri mUri;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        // bind views
        mDayTv = (TextView)rootView.findViewById(R.id.detail_day);
        mDateTv = (TextView)rootView.findViewById(R.id.detail_date);
        mHighTv = (TextView)rootView.findViewById(R.id.detail_hi);
        mLowTv = (TextView)rootView.findViewById(R.id.detail_low);
        mHumidityTv = (TextView)rootView.findViewById(R.id.detail_humidity);
        mWindTv = (TextView)rootView.findViewById(R.id.detail_wind);
        mPressureTv = (TextView)rootView.findViewById(R.id.detail_pressure);
        mForecastTextView = (TextView)rootView.findViewById(R.id.detail_forecast);
        mImage = (ImageView) rootView.findViewById(R.id.detail_icon);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem itemShare = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(itemShare);

        if (mForecastString != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.e(LOG_TAG, "mShareActionProvider is null");
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + SUNSHINE_HASH_TAG);
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_FORECAST_LOADER_ID, getArguments(), this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Context context = getContext();
            long timemillis = data.getLong(COL_WEATHER_DATE);
            String date = Utility.formatDate(timemillis);
            String location = data.getString(COL_LOCATION_SETTING);
            String desc = data.getString(COL_WEATHER_DESC);

            mForecastString = date + " " + location + " " + desc;
            mForecastTextView.setText(desc);
            mDayTv.setText(Utility.getDayName(context, timemillis));
            mDateTv.setText(date);
            mHighTv.setText(Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MAX_TEMP), Utility.isMetric(context)));
            mLowTv.setText(Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MIN_TEMP), Utility.isMetric(context)));
            mHumidityTv.setText(String.format(context.getString(R.string.format_humidity), data.getFloat(COL_WEATHER_HUMIDITY)));
            mWindTv.setText(Utility.getFormattedWind(context, data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES)));
            mPressureTv.setText(String.format(context.getString(R.string.format_pressure), data.getFloat(COL_WEATHER_PRESSURE)));
            mImage.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID)));

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastTextView.setText("");
    }

    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_FORECAST_LOADER_ID, null, this);
        }
    }
}
