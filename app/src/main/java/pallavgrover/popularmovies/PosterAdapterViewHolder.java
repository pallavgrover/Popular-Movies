package pallavgrover.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import pallavgrover.popularmovies.Util.Constants;
import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;

/**
 * Created by harsh on 09/08/2017.
 */

public class PosterAdapterViewHolder extends RecyclerView.ViewHolder {

    private TextView title,releaseDate,rating,plot;
    private ImageView favoriteButton, poster;
    private Context context;
    boolean isFavorite;
    public PosterAdapterViewHolder(View v, Context context, boolean isFavorite) {
        super(v);
        this.context = context;
        this.isFavorite = isFavorite;
        title = (TextView) v.findViewById(R.id.movie_title);
        rating = (TextView) v.findViewById(R.id.movie_user_rating);
        releaseDate = (TextView) v.findViewById(R.id.movie_release_date);
        plot = (TextView) v.findViewById(R.id.movie_overview);
        poster = (ImageView) v.findViewById(R.id.movie_poster);
        favoriteButton = (ImageView) v.findViewById(R.id.favorite);
    }

    public void bindView(final Movie movie) {
        final Drawable favoriteIcon = ContextCompat.getDrawable(context, R.drawable.ic_favorite_orange_24dp);
        final Drawable nonFavoriteIcon = ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp);
        //check if the current movie is a favorite
        favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);

        title.setText(movie.getTitle());
        releaseDate.setText(String.format("%s %s", context.getString(R.string.release_Date), movie.getReleaseDate()));
        rating.setText(String.valueOf(String.format("%s %s", context.getString(R.string.rating), movie.getVoteAverage())));
        plot.setText(movie.getOverview());

        if (isFavorite) {
            try {
                String filename = String.valueOf(movie.getId());
                File photofile = new File(context.getFilesDir(), filename);
                Bitmap freshBitMap = BitmapFactory.decodeStream(new FileInputStream(photofile));
                poster.setImageBitmap(freshBitMap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(context).load(Constants.posterUrl+movie.getPosterPath()).into(poster);
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle favorite boolean
                isFavorite = !isFavorite;
                //toggle the drawable
                favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);
                //toggle db
                //// TODO: 09/08/2017 add call back to activity
                if (isFavorite) {
                    Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    addMovie(byteArray,movie);

                } else {
                    removeMovie(movie);
                }
            }
        });
    }

    private void removeMovie(Movie movie) {
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

    private void addMovie(byte[] byteArray,Movie movie) {
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
