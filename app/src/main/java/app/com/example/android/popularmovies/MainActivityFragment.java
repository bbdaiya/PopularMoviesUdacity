package app.com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.Inflater;

import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.data.MovieDbHelper;
import app.com.example.android.popularmovies.data.MovieProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    static Context context;

    final static String LOG = MainActivityFragment.class.getSimpleName();
    boolean isTablet = MainActivity.isTablet;
    public static boolean showFavorites = false;
    static GridViewAdapter gridViewAdapter;
    static GridView gridView;
    public MainActivityFragment() {
    }

    @Override
    public void onStart() {

        super.onStart();
        if(!showFavorites){
            updateMovies();
        }
        else{
            showFavoritesList();
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        inflater.inflate(R.menu.menu_main, menu);
//          }
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        if(id==R.id.action_favorites){
//            showFavorites = true;
//           showFavoritesList();
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<MovieDetails> mArr = new ArrayList<MovieDetails>();
        gridViewAdapter = new GridViewAdapter(getContext(), R.layout.grid_view_items, mArr);
        gridView = (GridView) rootview.findViewById(R.id.grid_view);
        int orientation=this.getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(4);
        }
        Log.v(LOG, String.valueOf(showFavorites));

        if(!showFavorites){
            updateMovies();
            gridView.setAdapter(gridViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((Callback)getActivity()).ItemClicked(mArr.get(position));
                }
            });
        }
        else{
            showFavoritesList();
        }


        return rootview;
    }


    public interface Callback{
        void ItemClicked(MovieDetails movieDetails);
    }
    public void updateMovies(){
        FetchMovieContent fetchMovieContent;


            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String order = pref.getString(getString(R.string.sort_key), getString(R.string.sort_default));
            fetchMovieContent = new FetchMovieContent(order);
            fetchMovieContent.execute();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i(LOG, "Attached");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG, "Detached");
    }

    public static void showFavoritesList(){

        ArrayList<MovieDetails> movieArr = new ArrayList<MovieDetails>();
        Log.v(LOG, String.valueOf(MovieContract.FavoriteEntry.CONTENT_URI));
        Cursor cursor;
        if(context!=null){
            cursor = context.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null,null,null,null);
            Log.v(LOG,"Count:"+String.valueOf(cursor.getCount()));
            if(cursor.getCount()==0){
                Toast toast = Toast.makeText(context, "Favorite List is Empty", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                if(cursor.moveToFirst()){
                    do{
                        MovieDetails movieDetails = new MovieDetails(
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)),
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER)),
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS)),
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATINGS)),
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
                                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))
                        );
                        movieArr.add(movieDetails);
                        String data = cursor.getString(cursor.getColumnIndex("movie_title"));
                        Log.v(LOG, "ID:"+data);

                    }while (cursor.moveToNext());
                }
                else{
                    Log.v(LOG, "Cursor null");
                }
            }

            cursor.close();
            final ArrayList<MovieDetails> mArr = new ArrayList<MovieDetails>();
            gridViewAdapter = new GridViewAdapter(context, R.layout.grid_view_items, mArr);
            gridView.setAdapter(gridViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, DetailActivity.class).putExtra("movies_details", mArr.get(position));
                    context.startActivity(intent);
                }
            });


            if(movieArr!=null){
                gridViewAdapter.clear();
                for(MovieDetails md : movieArr){
                    gridViewAdapter.add(md);
                }
            }
        }
        else if(context==null){
            Log.e(LOG, "getContext null");
        }

    }



    class FetchMovieContent extends AsyncTask<Void, Void, ArrayList<MovieDetails>>{

         final String LOG = FetchMovieContent.class.getSimpleName();
        Vector<ContentValues> cVVector;
        String sort;
        FetchMovieContent(String sort){
            this.sort = sort;
        }
        FetchMovieContent(){

        }
        @Override
        protected ArrayList<MovieDetails> doInBackground(Void... params) {

            //for connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJson = null;    //will contain movies as json string

            if(!showFavorites){
                try {
                    final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    final String SORT_ORDER = sort;
                    final String APPID_PARAM = "api_key";

                    Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendPath(SORT_ORDER)
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

                    movieJson = buffer.toString();
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
            }
            try {
                return getMovieData(movieJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<MovieDetails> getMovieData(String movieJson) throws JSONException {


                final String RESULTS = "results";
                final String ORIGINAL_TITLE = "original_title";
                final String POSTER_PATH = "poster_path";
                final String OVERVIEW = "overview";
                final String VOTE_AVERAGE = "vote_average";
                final String RELEASE_DATE = "release_date";
                final String MOVIE_ID = "id";


                JSONObject movies = new JSONObject(movieJson);
                JSONArray resultsArr = movies.getJSONArray(RESULTS);

                ArrayList<MovieDetails> movieArr = new ArrayList<MovieDetails>();
                cVVector = new Vector<ContentValues>(resultsArr.length());
                for(int i = 0; i < resultsArr.length(); i++){
                    JSONObject eachMovie = resultsArr.getJSONObject(i);
                    MovieDetails mDetails = new MovieDetails(
                            eachMovie.getString(ORIGINAL_TITLE),
                            eachMovie.getString(POSTER_PATH),
                            eachMovie.getString(OVERVIEW),
                            eachMovie.getString(VOTE_AVERAGE),
                            eachMovie.getString(RELEASE_DATE),
                            eachMovie.getString(MOVIE_ID)
                    );

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mDetails.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mDetails.getOriginal_title());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, mDetails.getPoster_path());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, mDetails.getOverview());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATINGS, mDetails.getVote_average());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mDetails.getRelease_date());
                    cVVector.add(contentValues);
                    Log.v(LOG,mDetails.getPoster_path());
                    movieArr.add(mDetails);

                }
                if(cVVector.size()>0){

                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }


            //Show Favorites

            return movieArr;


        }

        @Override
        protected void onPostExecute(ArrayList<MovieDetails> movieDetailses) {
            if(movieDetailses!=null){
                gridViewAdapter.clear();
                for(MovieDetails md : movieDetailses){
                    gridViewAdapter.add(md);
                }
            }
            super.onPostExecute(movieDetailses);
        }
    }




}

