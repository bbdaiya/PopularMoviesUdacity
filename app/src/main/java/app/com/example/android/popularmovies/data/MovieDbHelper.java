package app.com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.style.TtsSpan;
import android.util.Log;

import app.com.example.android.popularmovies.MovieDetails;

/**
 * Created by bbdaiya on 6/6/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    final String LOG = MovieDbHelper.class.getSimpleName();
    //Version
    private static final int DATABASE_VERSION = 8;

    public static final String DATABASE_NAME = "movie.db";

    /*
    *
    * Constructor
    *
     */
    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        /*
        *
        * Create Table to store movies
        *
         */

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS+" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_USER_RATINGS+" REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE+ " TEXT NOT NULL, UNIQUE (" +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+") ON CONFLICT REPLACE);";
        Log.v(LOG,SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);


        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY ("+ MovieContract.FavoriteEntry.COLUMN_MOVIE_ID+") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME+" ("+ MovieContract.MovieEntry._ID+"), UNIQUE (" + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID+") ON CONFLICT REPLACE);";
        Log.v(LOG, SQL_CREATE_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
