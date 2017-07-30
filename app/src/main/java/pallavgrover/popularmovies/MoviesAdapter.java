package pallavgrover.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import okhttp3.internal.Util;
import pallavgrover.popularmovies.Util.Constants;
import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private int rowLayout;
    private Context context;
    private boolean showingFavorites;


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        ImageView image;


        public MovieViewHolder(View v) {
            super(v);
            movieTitle = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.image);
        }
    }

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context,boolean isFavorites) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
        this.showingFavorites = isFavorites;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        holder.movieTitle.setText(movies.get(position).getTitle());
        setFavoriteStatus(position);
        if (showingFavorites) {
            try {
                /*The file is named the same as the api id */
                String filename = String.valueOf(movies.get(position).getId());
                File photofile = new File(context.getFilesDir(), filename);
                Log.i("pallav", "getting saved photo data");
                Bitmap freshBitMap = BitmapFactory.decodeStream(new FileInputStream(photofile));
                holder.image.setImageBitmap(freshBitMap);
                holder.movieTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_orange_24dp, 0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(context).load(Constants.posterUrl + movies.get(position).getPosterPath()).into(holder.image);
            holder.movieTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_black_24dp, 0);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(context,MovieDetailsActivity.class);
                i.putExtra("movie_id",movies.get(position));
                context.startActivity(i);
            }
        });

        holder.movieTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingFavorites = !showingFavorites;
                //toggle the drawable
                if (showingFavorites) {
                    Bitmap bitmap = ((BitmapDrawable) holder.image.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    pallavgrover.popularmovies.Util.Util.addMovie(byteArray,movies.get(position),context);
                    holder.movieTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_orange_24dp, 0);

                } else {
                    pallavgrover.popularmovies.Util.Util.removeMovie(context,movies.get(position));
                    holder.movieTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_black_24dp, 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private void setFavoriteStatus(int position) {
        Cursor cursor = context.getContentResolver().query(
                FavoritesContract.Favorites.CONTENT_URI,
                new String[]{FavoritesContract.Favorites.COLUMN_API_ID},
                FavoritesContract.Favorites.COLUMN_API_ID + " = ? ",
                new String[]{String.valueOf(movies.get(position).getId())},
                null);
        if (cursor != null) {
            int cursorCount = cursor.getCount();
            showingFavorites = cursorCount > 0 ? true : false;
            cursor.close();
        }
    }
}