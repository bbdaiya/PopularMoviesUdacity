package app.com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by bbdaiya on 6/6/16.
 */
public class MovieProvider extends ContentProvider {

    final String LOG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder movieQueryBuilder = new SQLiteQueryBuilder();

    static {
        movieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder favoriteQueryBuilder = new SQLiteQueryBuilder();

    static {
        favoriteQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                MovieContract.FavoriteEntry.TABLE_NAME + " ON " + MovieContract.MovieEntry.TABLE_NAME + "." +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + MovieContract.FavoriteEntry.TABLE_NAME + "." +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
    }


    private MovieDbHelper movieDbHelper;

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE = 102;
    static final int FAVORITE_WITH_ID = 103;

    static UriMatcher buildUriMatcher() {

        UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITE);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);

        return sUriMatcher;

    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);


        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                retCursor = movieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVORITE: {
                Log.v(LOG,"Query Favorites");
                retCursor = favoriteQueryBuilder.query(movieDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MOVIE_WITH_ID: {
                retCursor = getMovieFromId(uri, projection, sortOrder);
                break;
            }
            case FAVORITE_WITH_ID: {
                retCursor = getFavoriteFromId(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }




    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildMovieUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int returnRow;

        if(selection==null) selection="1";
        switch (sUriMatcher.match(uri)){
            case MOVIES: {
                returnRow = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE: {
                returnRow = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID:{
                long id = MovieContract.FavoriteEntry.getFavoriteIdFromUri(uri);
                String selection1 = MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";
                String[] selectionArgs1 = new String[]{Long.toString(id)};
                returnRow = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection1, selectionArgs1);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(returnRow!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return returnRow;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int returnRow;

        if(selection==null) selection="1";
        switch (sUriMatcher.match(uri)){
            case MOVIES: {
                returnRow = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FAVORITE: {
                returnRow = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(returnRow!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return returnRow;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value: values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if(_id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);

        }


    }

    public Cursor getMovieFromId(Uri uri, String[] projections, String sortOrder) {

        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        String[] selectionArgs = new String[]{Long.toString(movieId)};
        return movieQueryBuilder.query(movieDbHelper.getReadableDatabase(), projections, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor getFavoriteFromId(Uri uri, String[] projections, String sortOrder) {
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        String selection = MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{Long.toString(movieId)};
        return favoriteQueryBuilder.query(movieDbHelper.getReadableDatabase(), projections, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
