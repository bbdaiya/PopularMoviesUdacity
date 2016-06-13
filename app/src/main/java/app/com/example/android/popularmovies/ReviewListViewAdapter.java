package app.com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bbdaiya on 2/6/16.
 */
public class ReviewListViewAdapter extends ArrayAdapter<ReviewDetails>{

    Context context;
    int layoutResourceId;
    ArrayList<ReviewDetails> data = new ArrayList<ReviewDetails>();

    /*
    *Constructor
     */

    public ReviewListViewAdapter(Context context, int layoutResourceId, ArrayList data){
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    final String LOG_TAG = ReviewListViewAdapter.class.getSimpleName();


        class ViewHolder{
           TextView authorTextView;
            TextView contentTextView;
        }
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null){
            Log.e(LOG_TAG, "convertView null");
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.authorTextView = (TextView) convertView.findViewById(R.id.author_text_view);
            holder.contentTextView = (TextView)convertView.findViewById(R.id.content_text_view);
            convertView.setTag(holder);
        }
        else{
            Log.e(LOG_TAG, "convertView not null");
            holder = (ViewHolder) convertView.getTag();
        }
        Log.v(LOG_TAG, "Position: "+String.valueOf(position));
        Log.v(LOG_TAG, String.valueOf(getCount()));
        holder.authorTextView.setText(data.get(position).getAuthor());
        holder.contentTextView.setText(data.get(position).getContent());
        return convertView;
    }

}

