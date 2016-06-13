package app.com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    final String LOG = MainActivity.class.getSimpleName();
    private static Context context;
    public static boolean isTablet;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTablet = isTablet(getApplicationContext());
        setContentView(R.layout.activity_main);

        if(!isTablet) {
            Log.v(LOG, "This is a phone");

        }
        else {

            Log.v(LOG, "This is a tablet");

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MainActivity.context = context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_favorites){
            MainActivityFragment.showFavorites = true;
            MainActivityFragment.showFavoritesList();


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(MainActivityFragment.showFavorites){
            MainActivityFragment.showFavorites = false;
            recreate();
        }
        else
            super.onBackPressed();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void ItemClicked(MovieDetails movieDetails) {
        if(!isTablet){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class).putExtra("movies_details", movieDetails);
            startActivity(intent);
        }
        else{
            Bundle bundle = new Bundle();

            bundle.putSerializable("movie_detail",movieDetails);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DetailActivityFragment fragment = DetailActivityFragment.newInstance(bundle);
            fragmentTransaction.add(R.id.detail_activity_fragment, fragment).commit();
        }
    }
}
