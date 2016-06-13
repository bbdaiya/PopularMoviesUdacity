package app.com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.e(DetailActivity.class.getSimpleName(), "in Detail activity");


//        Intent intent = getIntent();
//        if(intent==null){
//            Log.e(DetailActivity.class.getSimpleName(), "intent NULL detail activity");
//
//        }
//        else{
//            Log.e(DetailActivity.class.getSimpleName(), "intent not NULL detail activity");
//
//        }
//        MovieDetails mDetails = (MovieDetails) intent.getSerializableExtra("movies_details");
//        Bundle bundle = new Bundle();
//
//        bundle.putSerializable("movie_detail",mDetails);
//        if(bundle==null){
//            Log.e(DetailActivity.class.getSimpleName(), "bundle NULL detail activity");
//        }
//        else{
//            Log.e(DetailActivity.class.getSimpleName(), "bundle not NULL detail activity");
//
//        }
//        DetailActivityFragment fragment = DetailActivityFragment.newInstance(bundle);
//       // fragment.setArguments(bundle);
        Log.v(DetailActivity.class.getSimpleName(), "set argument done");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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


}

