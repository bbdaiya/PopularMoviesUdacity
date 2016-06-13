package app.com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by bbdaiya on 1/6/16.
 */
public class TrailerListViewAdapter extends RecyclerView.Adapter<TrailerListViewAdapter.MyHolder> {
    //
//    Context context;
//    int layoutResourceId;
//    ArrayList<String> key = new ArrayList<String>();
//
//    /*
//    *Constructor
//     */
//
//    public TrailerListViewAdapter(Context context, int layoutResourseId, ArrayList key){
//        super(context, layoutResourseId, key);
//        this.context = context;
//        this.layoutResourceId = layoutResourseId;
//        this.key = key;
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
    final String LOG_TAG = TrailerListViewAdapter.class.getSimpleName();
    //        Log.e(LOG_TAG, "I m in getView");
//
//        class ViewHolder{
//            ImageView imageView;
//            TextView textView;
//        }
//        ViewHolder holder = null;
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService
//                (Context.LAYOUT_INFLATER_SERVICE);
//
//        if(convertView==null){
//
//            convertView = inflater.inflate(layoutResourceId, parent, false);
//            holder = new ViewHolder();
//            holder.imageView = (ImageView)convertView.findViewById(R.id.trailer_image_view);
//            holder.textView = (TextView)convertView.findViewById(R.id.trailer_text_view);
//            convertView.setTag(holder);
//        }
//        else{
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//
//        /*
//        * URL for thumbnail of trailer
//         */
//        final String BASE = "http://img.youtube.com/vi/";
//        final String KEY = key.get(position)+"/1.jpg";
//
//        String url = BASE+KEY;
//        Log.v(LOG_TAG, url);
//
//        Picasso.with(context).load(url).into(holder.imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                Log.e(LOG_TAG, "Success");
//            }
//
//            @Override
//            public void onError() {
//                Log.e(LOG_TAG, "Error");
//            }
//        });
//        String[] arr = context.getResources().getStringArray(R.array.trailer_list);
//        holder.textView.setText(arr[position]);
//        return convertView;
//    }
//}

    Context context;

    ArrayList<String> key = new ArrayList<String>();

    /*
    *Constructor
     */

    public TrailerListViewAdapter(Context context,  ArrayList key){
        this.context = context;

        this.key = key;
    }
    @Override
    public int getItemCount() {
        Log.v(LOG_TAG, String.valueOf(key.size()));
        return key.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView imageView;

        MyHolder(View v){
            super(v);
            v.setOnClickListener(this);
            this.imageView = (ImageView)v.findViewById(R.id.trailer_image_view);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+key.get(getAdapterPosition())));
            context.startActivity(intent);
        }
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.trailer_list_view_items, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
         /*
        * URL for thumbnail of trailer
         */
        final String BASE = "http://img.youtube.com/vi/";
        final String KEY = key.get(position)+"/0.jpg";

        String url = BASE+KEY;
        Log.v(LOG_TAG, url);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        Picasso.with(context).load(url).into(holder.imageView, new Callback() {
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


}