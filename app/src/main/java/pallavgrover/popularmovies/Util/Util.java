package pallavgrover.popularmovies.Util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;

import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;

/**
 * Created by pallav.grover on 23/07/17.
 */

public class Util {

    public static class YoutubeUtility {

        private static final String YOUTUBE_IMAGE_PREFIX = "http://img.youtube.com/vi/";
        private static final String YOUTUBE_IMAGE_POSTFIX = "/0.jpg";
        private static final String YOUTUBE_TRAILER_PREFIX = "https://www.youtube.com/watch?v=";

        /*given a youtube trailer id, generate the thumbnail url */
        public static String getPosterUrlFromTrailerId(String trailerIdString) {
            return YOUTUBE_IMAGE_PREFIX + trailerIdString + YOUTUBE_IMAGE_POSTFIX;
        }

        /*given a youtube trailer id, generate the trailer url */
        public static String getTrailerUrlFromTrailerId(String trailerId) {
            return YOUTUBE_TRAILER_PREFIX + trailerId;
        }
    }
    public static void removeMovie(Context context,Movie movie) {
        String currentMovieId = String.valueOf(movie.getId());
        String whereClause = FavoritesContract.Favorites.COLUMN_API_ID + " = ?";
        String[] whereArgs = new String[]{currentMovieId};
        int rowsDeleted = context.getContentResolver().delete(FavoritesContract.Favorites.CONTENT_URI, whereClause, whereArgs);

        File photofile = new File(
                context.getFilesDir(), currentMovieId);
        if (photofile.exists()) {
            photofile.delete();
        }
    }

    public static void addMovie(byte[] byteArray, Movie movie,Context context) {
        /* Add Movie to ContentProvider */
        ContentValues values = new ContentValues();
        values.put(FavoritesContract.Favorites.COLUMN_TITLE, movie.getTitle());
        values.put(FavoritesContract.Favorites.COLUMN_SYNOPSIS, movie.getOverview());
        values.put(FavoritesContract.Favorites.COLUMN_RATING, movie.getVoteAverage());
        values.put(FavoritesContract.Favorites.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(FavoritesContract.Favorites.COLUMN_API_ID, movie.getId());
        Uri insertedMovieUri = context.getContentResolver().
                insert(FavoritesContract.Favorites.CONTENT_URI, values);

        /* Write the file to disk */
        String filename = String.valueOf(movie.getId());
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
