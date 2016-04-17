package app.com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    GridViewAdapter gridViewAdapter;
    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        updateMovies();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<MovieDetails> mArr = new ArrayList<MovieDetails>();
        gridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_view_items, mArr);
        GridView gridView = (GridView) rootview.findViewById(R.id.grid_view);

        updateMovies();
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movies_details", mArr.get(position));
                startActivity(intent);
            }
        });

        return rootview;
    }
    public void updateMovies(){
        FetchMovieContent fetchMovieContent = new FetchMovieContent();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String order = pref.getString(getString(R.string.sort_key), getString(R.string.sort_default));
        fetchMovieContent.execute(order);
    }

    class FetchMovieContent extends AsyncTask<String, Void, ArrayList<MovieDetails>>{

         final String LOG = FetchMovieContent.class.getSimpleName();

        @Override
        protected ArrayList<MovieDetails> doInBackground(String... params) {

            //for connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJson = null;    //will contain movies as json string

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String SORT_ORDER = params[0];
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


            JSONObject movies = new JSONObject(movieJson);
            JSONArray resultsArr = movies.getJSONArray(RESULTS);

            ArrayList<MovieDetails> movieArr = new ArrayList<MovieDetails>();

            for(int i = 0; i < resultsArr.length(); i++){
                JSONObject eachMovie = resultsArr.getJSONObject(i);
                MovieDetails mDetails = new MovieDetails(
                        eachMovie.getString(ORIGINAL_TITLE),
                        eachMovie.getString(POSTER_PATH),
                        eachMovie.getString(OVERVIEW),
                        eachMovie.getString(VOTE_AVERAGE),
                        eachMovie.getString(RELEASE_DATE)
                );
                Log.v(LOG,mDetails.getPoster_path());
                movieArr.add(mDetails);

            }

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

