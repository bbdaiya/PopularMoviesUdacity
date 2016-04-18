package app.com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bbdaiya on 7/4/16.
 */
public class GridViewAdapter extends ArrayAdapter<MovieDetails> {
    static final String LOG_TAG = GridViewAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    ArrayList<MovieDetails> data = new ArrayList<MovieDetails>();

    public GridViewAdapter(Context context, int layoutResourseId, ArrayList data){
        super(context, layoutResourseId, data);
        this.context = context;
        this.layoutResourceId = layoutResourseId;
        this.data = data;
    }
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)

    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, parent, false);
        if(convertView==null){
            imageView = new ImageView(context);
            convertView = imageView;
        }
        else{
            imageView = (ImageView) convertView;
        }

        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w342";
        final String IMAGE_URL = data.get(position).getPoster_path();
        Log.v(LOG_TAG, "Image Url"+IMAGE_URL);
        String url = IMAGE_BASE+IMAGE_SIZE+IMAGE_URL;
        Log.v(LOG_TAG, "url1"+url);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if(!isAirplaneModeOn(context)){
            int orientation=((Activity) getContext()).getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT){
                Picasso.with(context).load(url).resize(width/2, 0).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG, "Success");
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Error");
                    }
                });
            }
            else{

                Picasso.with(context).load(url).resize(width/4,0).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(LOG_TAG, "Success");
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Error");
                    }
                });
            }
            ;
        }

        return imageView;
    }
}