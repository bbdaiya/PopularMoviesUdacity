package app.com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bbdaiya on 3/6/16.
 */
public class ReviewsRecyclarViewAdapter extends RecyclerView.Adapter<ReviewsRecyclarViewAdapter.MyHolder> {
    final String LOG = ReviewsRecyclarViewAdapter.class.getSimpleName();

    ArrayList<ReviewDetails> reviewDetailses = new ArrayList<>();
    Context context;
    int layoutResourceId;

    public ReviewsRecyclarViewAdapter(ArrayList<ReviewDetails> reviewDetailses, Context context, int layoutResourceId) {
        this.reviewDetailses = reviewDetailses;
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView author;
        TextView content;

        public MyHolder(View itemView) {
            super(itemView);
            this.author = (TextView)itemView.findViewById(R.id.author_text_view);
            this.content = (TextView)itemView.findViewById(R.id.content_text_view);
        }
    }

    @Override
    public int getItemCount() {
        Log.v(LOG, String.valueOf(reviewDetailses.size()));
        return reviewDetailses.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.author.setText(reviewDetailses.get(position).getAuthor());
        holder.content.setText(reviewDetailses.get(position).getContent());
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutResourceId, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;

    }
}
