package pallavgrover.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.Response;
import pallavgrover.popularmovies.model.VideosBean;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView title,releaseDate,rating,plot;
    private Movie movie;
    private boolean isFavorite;
    private RecyclerView recyclerViewReview;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String KEY_RECYCLER_STATE_TRAILER = "recycler_state_trailer";
    private static Bundle mBundleRecyclerViewState;
    private static Bundle mBundleRecyclerViewStateTrailer;
    private RecyclerView recyclerViewTrailer;
    private Parcelable listStateTrailer;
    private Parcelable listState;
    private int lastFirstVisiblePosition;
    private int lastFirstVisiblePositionTrailer;
    private Bundle outState;
    private NestedScrollView mScrollView;
    private int scrollXPosition;
    private int scrollYPosition;
    private List<Response.Reviews> reviewsList;
    private List<VideosBean.Trailer> trailerList;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private int rev;
    private int trail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.movie_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (API_KEY.isEmpty()) {
            Toast.makeText(this, "Please obtain your API KEY from themoviedb.org first!", Toast.LENGTH_LONG).show();
            return;
        }

        if(savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.ARG_MOVIE_DETAILS,
                    getIntent().getParcelableExtra(MovieDetailsFragment.ARG_MOVIE_DETAILS));
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.llFragmentContainer, fragment)
                    .commit();
        }
    }
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        Intent intent = getIntent();
//        movie = intent.getParcelableExtra("movie_id");
//        if (API_KEY.isEmpty()) {
//            Toast.makeText(getApplicationContext(), "Please obtain your API KEY from themoviedb.org first!", Toast.LENGTH_LONG).show();
//            return;
//        }
//        setFavoriteStatus();
//        poster = (ImageView) findViewById(R.id.movie_poster);
////        backDrop = (ImageView) findViewById(R.id.movie_backdrop);
//        title = (TextView) findViewById(R.id.movie_title);
//        rating = (TextView) findViewById(R.id.movie_user_rating);
//        releaseDate = (TextView) findViewById(R.id.movie_release_date);
//        plot = (TextView) findViewById(R.id.movie_overview);
//        recyclerViewReview = (RecyclerView) findViewById(R.id.review_list);
//        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerViewReview.setLayoutManager(linearLayoutManager);
//        recyclerViewTrailer = (RecyclerView) findViewById(R.id.trailer_list);
//        mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
//        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//        recyclerViewTrailer.setLayoutManager(gridLayoutManager);
//        title.setText(movie.getTitle());
//        releaseDate.setText(String.format("%s %s", getString(R.string.release_Date), movie.getReleaseDate()));
//        rating.setText(String.valueOf(String.format("%s %s", getString(R.string.rating), movie.getVoteAverage())));
//        plot.setText(movie.getOverview());
//        reviewsList = (List<Response.Reviews>) (savedInstanceState != null
//                ? savedInstanceState.getParcelableArrayList("review")
//                : new ArrayList<>());
//        trailerList = (List<VideosBean.Trailer>) (savedInstanceState != null
//                ? savedInstanceState.getParcelableArrayList("trailer")
//                : new ArrayList<>());
//
//        ApiInterface apiService =
//                ApiClient.getClient().create(ApiInterface.class);
//
//        if(savedInstanceState != null) {
//            rev = savedInstanceState.getInt("rev");
//            trail = savedInstanceState.getInt("trail");
//        }
//        if(reviewsList.size()>0){
//            ReviewsAdapter adapter = new ReviewsAdapter(getApplicationContext(), reviewsList);
//            recyclerViewReview.setAdapter(adapter);
//            ((LinearLayoutManager) recyclerViewReview.getLayoutManager()).scrollToPosition(rev);
//        }else {
//            Call<Response> call = apiService.getMovieReviews(movie.getId(), API_KEY);
//            call.enqueue(new Callback<Response>() {
//                @Override
//                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                    reviewsList = response.body().getResults();
//                    ReviewsAdapter adapter = new ReviewsAdapter(getApplicationContext(), reviewsList);
//                    recyclerViewReview.setAdapter(adapter);
//                }
//
//                @Override
//                public void onFailure(Call<Response> call, Throwable t) {
//                    // Log error here since request failed
//                    Log.e("", t.toString());
//                }
//            });
//        }
//        if(trailerList.size()>0){
//            TrailerAdapter adapter = new TrailerAdapter(getApplicationContext(), trailerList);
//            recyclerViewTrailer.setAdapter(adapter);
//            ((LinearLayoutManager) recyclerViewTrailer.getLayoutManager()).scrollToPosition(trail);
//        }else {
//            Call<VideosBean> trailer = apiService.getMovieVideos(movie.getId(), API_KEY);
//            trailer.enqueue(new Callback<VideosBean>() {
//
//                @Override
//                public void onResponse(Call<VideosBean> call, retrofit2.Response<VideosBean> response) {
//                    trailerList = response.body().getResults();
//                    TrailerAdapter adapter = new TrailerAdapter(getApplicationContext(), trailerList);
//                    recyclerViewTrailer.setAdapter(adapter);
//                }
//
//                @Override
//                public void onFailure(Call<VideosBean> call, Throwable t) {
//                    // Log error here since request failed
//                    Log.e("", t.toString());
//                }
//            });
//        }
//        if (isFavorite) {
//            try {
//                String filename = String.valueOf(movie.getId());
//                File photofile = new File(getFilesDir(), filename);
//                Bitmap freshBitMap = BitmapFactory.decodeStream(new FileInputStream(photofile));
//                poster.setImageBitmap(freshBitMap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Glide.with(MovieDetailsActivity.this).load(posterUrl+movie.getPosterPath()).into(poster);
//        }
////        Glide.with(MovieDetailsActivity.this).load(posterUrlBig+movie.getPosterPath()).into(backDrop);
//        final ImageView favoriteButton = (ImageView) findViewById(R.id.favorite);
//        final Drawable favoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_orange_24dp);
//        final Drawable nonFavoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp);
//        //check if the current movie is a favorite
//        favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);
//
//        favoriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //toggle favorite boolean
//                isFavorite = !isFavorite;
//                //toggle the drawable
//                favoriteButton.setImageDrawable(isFavorite ? favoriteIcon : nonFavoriteIcon);
//                //toggle db
//                if (isFavorite) {
//                    Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    addMovie(byteArray);
//
//                } else {
//                    removeMovie();
//                }
//            }
//        });
//    }
//
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

    @Override
    protected void onPause() {
        super.onPause();
        // save RecyclerView state
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore RecyclerView state
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("last_visible_review", linearLayoutManager.findLastVisibleItemPosition());
//        outState.putInt("first_visible_review", linearLayoutManager.findFirstVisibleItemPosition());
//        outState.putParcelableArrayList("review", (ArrayList<? extends Parcelable>) reviewsList);
//        outState.putParcelableArrayList("trailer", (ArrayList<? extends Parcelable>) trailerList);
//        lastFirstVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
//        lastFirstVisiblePositionTrailer = gridLayoutManager.findLastVisibleItemPosition();
//        outState.putInt("rev",lastFirstVisiblePosition);
//        outState.putInt("trail",lastFirstVisiblePositionTrailer);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//         rev = savedInstanceState.getInt("rev");
//         trail = savedInstanceState.getInt("trail");
//        ((LinearLayoutManager) recyclerViewReview.getLayoutManager()).scrollToPosition(rev);
//        ((LinearLayoutManager) recyclerViewTrailer.getLayoutManager()).scrollToPosition(trail);
    }
}
