package pallavgrover.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.MoviesResponse;
import pallavgrover.popularmovies.model.Response;
import pallavgrover.popularmovies.model.VideosBean;
import pallavgrover.popularmovies.retrofit.ApiClient;
import pallavgrover.popularmovies.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;
import static pallavgrover.popularmovies.Util.Constants.posterUrl;
import static pallavgrover.popularmovies.Util.Constants.posterUrlBig;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView poster,backDrop;
    private TextView title,releaseDate,rating,plot;
    private Movie movie;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_movie_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie_id");
        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY from themoviedb.org first!", Toast.LENGTH_LONG).show();
            return;
        }
        setFavoriteStatus();
        poster = (ImageView) findViewById(R.id.movie_poster);
        backDrop = (ImageView) findViewById(R.id.movie_backdrop);
        title = (TextView) findViewById(R.id.movie_title);
        rating = (TextView) findViewById(R.id.movie_user_rating);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        plot = (TextView) findViewById(R.id.movie_overview);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Response> call = apiService.getMovieReviews(movie.getId(),API_KEY);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.review_list);
                ReviewsAdapter adapter = new ReviewsAdapter(getApplicationContext(), response.body().getResults());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });

        Call<VideosBean>  trailer = apiService.getMovieVideos(movie.getId(),API_KEY);
        trailer.enqueue(new Callback<VideosBean>() {

            @Override
            public void onResponse(Call<VideosBean> call, retrofit2.Response<VideosBean> response) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.trailer_list);
                TrailerAdapter adapter = new TrailerAdapter(getApplicationContext(), response.body().getResults());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
            }

            @Override
            public void onFailure(Call<VideosBean> call, Throwable t) {
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });
        if (isFavorite) {
            try {
                String filename = String.valueOf(movie.getId());
                File photofile = new File(getFilesDir(), filename);
                Bitmap freshBitMap = BitmapFactory.decodeStream(new FileInputStream(photofile));
                poster.setImageBitmap(freshBitMap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(MovieDetailsActivity.this).load(posterUrl+movie.getPosterPath()).into(poster);
        }
        Glide.with(MovieDetailsActivity.this).load(posterUrlBig+movie.getPosterPath()).into(backDrop);
        title.setText(movie.getTitle());
        releaseDate.setText(String.format("%s %s", getString(R.string.release_Date), movie.getReleaseDate()));
        rating.setText(String.valueOf(String.format("%s %s", getString(R.string.rating), movie.getVoteAverage())));
        plot.setText(movie.getOverview());
        final ImageView favoriteButton = (ImageView) findViewById(R.id.favorite);
        final Drawable favoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_orange_24dp);
        final Drawable nonFavoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp);
        //check if the current movie is a favorite
        favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle favorite boolean
                isFavorite = !isFavorite;
                //toggle the drawable
                favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);
                //toggle db
                if (isFavorite) {
                    Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    addMovie(byteArray);

                } else {
                    removeMovie();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeMovie() {
        String currentMovieId = String.valueOf(movie.getId());
        String whereClause = FavoritesContract.Favorites.COLUMN_API_ID + " = ?";
        String[] whereArgs = new String[]{currentMovieId};
        int rowsDeleted = this.getContentResolver().delete(FavoritesContract.Favorites.CONTENT_URI, whereClause, whereArgs);

        File photofile = new File(
                getFilesDir(), currentMovieId);
        if (photofile.exists()) {
            photofile.delete();
        }
    }

    private void addMovie(byte[] byteArray) {
        /* Add Movie to ContentProvider */
        ContentValues values = new ContentValues();
        values.put(FavoritesContract.Favorites.COLUMN_TITLE, movie.getTitle());
        values.put(FavoritesContract.Favorites.COLUMN_SYNOPSIS, movie.getOverview());
        values.put(FavoritesContract.Favorites.COLUMN_RATING, movie.getVoteAverage());
        values.put(FavoritesContract.Favorites.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(FavoritesContract.Favorites.COLUMN_API_ID, movie.getId());
        Uri insertedMovieUri = getContentResolver().
                insert(FavoritesContract.Favorites.CONTENT_URI, values);

        /* Write the file to disk */
        String filename = String.valueOf(movie.getId());
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFavoriteStatus() {
        Cursor cursor = getContentResolver().query(
                FavoritesContract.Favorites.CONTENT_URI,
                new String[]{FavoritesContract.Favorites.COLUMN_API_ID},
                FavoritesContract.Favorites.COLUMN_API_ID + " = ? ",
                new String[]{String.valueOf(movie.getId())},
                null);
        if (cursor != null) {
            int cursorCount = cursor.getCount();
            isFavorite = cursorCount > 0 ? true : false;
            cursor.close();
        }
    }

}
