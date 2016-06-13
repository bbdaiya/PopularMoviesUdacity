package app.com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import app.com.example.android.popularmovies.data.MovieContract;

import static android.support.v7.widget.LinearLayoutManager.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {




    boolean isFavorite ;
    int favoriteImage;
    /*
    *ArrayLists
     */
    ArrayList<String> videoArr = new ArrayList<>();
    ArrayList<ReviewDetails> reviewArr = new ArrayList<>();

    /*
    * Adapters
     */
    TrailerListViewAdapter trailerListViewAdapter;
    ReviewsRecyclarViewAdapter reviewsRecyclarViewAdapter;


    /*
    *Recycler Views
     */
    RecyclerView recyclerView;
    RecyclerView rv;

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public static DetailActivityFragment newInstance(Bundle bundle){
        DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(bundle);
        Log.v(DetailActivityFragment.class.getSimpleName(), "argument set");
        return fragment;
    }
    /*
    * Detail Activity
     */

    TextView movie_title, release, rating, overview;
    ImageView poster;
    MovieDetails mDetails;
    ImageView favoriteButton;



    public DetailActivityFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState!=null){
            favoriteImage = savedInstanceState.getInt("FavoriteImage");
            isFavorite = savedInstanceState.getBoolean("isFavorite");
        }

        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        if(!MainActivity.isTablet){
            Intent intent = getActivity().getIntent();
            if(intent==null){
                Log.e(DetailActivity.class.getSimpleName(), "intent NULL detail activity");

            }
            else{
                Log.e(DetailActivity.class.getSimpleName(), "intent not NULL detail activity");

            }
           mDetails = (MovieDetails) intent.getSerializableExtra("movies_details");
        }
        else{
            Bundle bundle = getArguments();
            if(bundle!=null){
                mDetails = (MovieDetails)bundle.getSerializable("movie_detail");
            }
            else{
                Log.e(LOG_TAG,"bundle is NULL");
            }
        }


//        Log.v(LOG_TAG, mDetails.getOriginal_title());

        /*
        *Views in Detail Activity
         */
        movie_title = (TextView)rootview.findViewById(R.id.movie_title_text_view);
        release = (TextView)rootview.findViewById(R.id.release_date_text_view);
        rating = (TextView)rootview.findViewById(R.id.rating_text_view);
        overview = (TextView)rootview.findViewById(R.id.overview_text_view);
        poster = (ImageView)rootview.findViewById(R.id.poster_image_view);

        isFavorite = mDetails.isFavorite();
        Log.v(LOG_TAG, "Favorite: "+String.valueOf(isFavorite));
        /*
        *Set Views of Detail Activity
         */

        movie_title.setText(mDetails.getOriginal_title());
        String temp[] = mDetails.getRelease_date().split("-");
        release.setText(temp[0]);
        rating.setText(mDetails.getVote_average()+"/10");
        overview.setText(mDetails.getOverview());
        String path = mDetails.getPoster_path();
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w342";
        final String IMAGE_URL = path;
        Log.v(LOG_TAG, "Image Url" + IMAGE_URL);
        String url = IMAGE_BASE+IMAGE_SIZE+IMAGE_URL;
        Picasso.with(getActivity()).load(url).into(poster);


        /*
        *
        * TRAILER
        *
         */

        FetchVideoContent fetchVideoContent = new FetchVideoContent();
        fetchVideoContent.execute();
        getActivity().setContentView(R.layout.trailer_list_view_items);

        rv = (RecyclerView)rootview.findViewById(R.id.trailer_recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);




        /*
        *
        * REVIEWS
        *
        *
         */

        FetchReviewContent fetchReviewContent = new FetchReviewContent();
        fetchReviewContent.execute(mDetails.getId());

        recyclerView = (RecyclerView)rootview.findViewById(R.id.review_list_view);
        LinearLayoutManager lManager
                = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(lManager);




        /*
        *
        * FAVORITES
        *
         */
        favoriteButton = (ImageView)rootview.findViewById(R.id.favorite_button);

        if(mDetails.isFavorite()){
            favoriteImage = R.drawable.favorite_true;
        }
        else{
            favoriteImage = R.drawable.favorite_false;
        }
        favoriteButton.setImageResource(favoriteImage);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){//False
                    favoriteImage = R.drawable.favorite_true;
                    favoriteButton.setImageResource(favoriteImage);
                    isFavorite = true;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Added to Favorite", Toast.LENGTH_SHORT);
                    toast.show();
                    addToFavorites(Long.parseLong(mDetails.getId()));
                    Log.v(LOG_TAG, "Original ID:"+mDetails.getId());
                }
                else{
                    favoriteImage = R.drawable.favorite_false;

                    favoriteButton.setImageResource(favoriteImage);
                    isFavorite = false;
                    mDetails.setFavorite(false);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Removed from Favorite", Toast.LENGTH_SHORT);
                    toast.show();
                    getActivity().getApplicationContext().getContentResolver().delete(MovieContract.FavoriteEntry.buildMovieUri(Long.parseLong(mDetails.getId())),null,null);
                }
            }
        });
        Log.v(LOG_TAG, "OnCreateView: "+String.valueOf(isFavorite));
        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FavoriteImage", favoriteImage);
        outState.putBoolean("isFavorite", isFavorite);
    }


    public void addToFavorites(long movieId){
        mDetails.setFavorite(true);
        Log.v(LOG_TAG, "In addToFavorites");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
        Log.v(LOG_TAG, "Content Value size: "+String.valueOf(contentValues.size()));
        getActivity().getApplicationContext().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null, null,null,null);
        Log.v(LOG_TAG,"Count:"+String.valueOf(cursor.getCount()));
        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("movie_title"));
                Log.v(LOG_TAG, "ID:"+data);

            }while (cursor.moveToNext());
        }
        else{
            Log.v(LOG_TAG, "Cursor null");
        }
        cursor.close();
    }




    class FetchVideoContent extends AsyncTask<Void, Void, ArrayList<String>> {
        final String LOG = FetchVideoContent.class.getSimpleName();
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String videoJson = null;    //will contain movies as json string

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String MOVIE_ID = mDetails.getId();
                final String VIDEO_KEYWORD = "videos";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(MOVIE_ID)
                        .appendPath(VIDEO_KEYWORD)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = null;
                try {
                    url = new URL(builtUri.toString());
                    Log.v(LOG, url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                //Read Input Stream
                InputStream is = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(is==null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line=reader.readLine())!=null){
                    buffer.append(line+"\n");           //for making debugging easy we add newline
                }
                if(buffer.length()==0){
                    //Stream is empty
                    return null;
                }

                videoJson = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getVideosData(videoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        public ArrayList<String> getVideosData(String videoJson) throws JSONException {
            final String RESULTS = "results";
            final String KEY = "key";

            JSONObject videos = new JSONObject(videoJson);
            JSONArray resultsArr = videos.getJSONArray(RESULTS);


            for(int i = 0; i < resultsArr.length(); i++){
                JSONObject eachVideo = resultsArr.getJSONObject(i);
                Log.i(LOG, eachVideo.getString(KEY));
                videoArr.add(eachVideo.getString(KEY));


            }

            return videoArr;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            if(strings!=null){
                trailerListViewAdapter = new TrailerListViewAdapter(getActivity(), strings);
                rv.setAdapter(trailerListViewAdapter);
            }
            super.onPostExecute(strings);
        }
    }

    class FetchReviewContent extends AsyncTask<String, Void, ArrayList<ReviewDetails>> {


        final String LOG = FetchReviewContent.class.getSimpleName();

        @Override
        protected ArrayList<ReviewDetails> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJson = null;    //will contain movies as json string

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String MOVIE_ID = params[0];
                final String REVIEW_KEYWORD = "reviews";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(MOVIE_ID)
                        .appendPath(REVIEW_KEYWORD)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = null;
                try {
                    url = new URL(builtUri.toString());
                    Log.v(LOG, url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                //Read Input Stream
                InputStream is = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (is == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");           //for making debugging easy we add newline
                }
                if (buffer.length() == 0) {
                    //Stream is empty
                    return null;
                }

                reviewJson = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getReviewsData(reviewJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        public ArrayList<ReviewDetails> getReviewsData(String reviewJson) throws JSONException {
            final String RESULTS = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";

            JSONObject reviews = new JSONObject(reviewJson);
            JSONArray resultsArr = reviews.getJSONArray(RESULTS);


            for (int i = 0; i < resultsArr.length(); i++) {
                JSONObject eachReview = resultsArr.getJSONObject(i);
                Log.i(LOG, eachReview.getString(AUTHOR));
                ReviewDetails reviewDetails = new ReviewDetails();
                reviewDetails.setAuthor(eachReview.getString(AUTHOR));
                reviewDetails.setContent(eachReview.getString(CONTENT).trim());
                reviewArr.add(reviewDetails);


            }

            return reviewArr;
        }



    protected void onPostExecute(ArrayList<ReviewDetails> rd) {

        if(rd!=null){
            reviewsRecyclarViewAdapter = new ReviewsRecyclarViewAdapter(reviewArr,getActivity().getApplicationContext(), R.layout.review_list_view_items);
            recyclerView.setAdapter(reviewsRecyclarViewAdapter);

        }
        super.onPostExecute(rd);
    }
    }


}
