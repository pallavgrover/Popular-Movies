package pallavgrover.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.MoviesResponse;
import pallavgrover.popularmovies.retrofit.ApiClient;
import pallavgrover.popularmovies.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;
import static pallavgrover.popularmovies.Util.Constants.OFFLINE_SET;
import static pallavgrover.popularmovies.Util.Constants.POPULAR_SET;
import static pallavgrover.popularmovies.Util.Constants.TOP_RATED_SET;

/**
 * Created by pallav.grover on 17/06/17.
 */

public class MoviesActivity extends AppCompatActivity{


    private List<Movie> result;
    private MoviesAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public static final String BUNDLE_KEY_FAVORITES = "faves";
    public static final String BUNDLE_KEY_MOVIE_LIST = "movie list key";
    private  int pageNumber;
    private int currentMovie;
    private List<Movie> mMovieList;
    private TextView imageView;
    private Parcelable listState;
    private int lastFirstVisiblePosition;
    private boolean mUsingOfflineData;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY from themoviedb.org first!", Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.addOnScrollListener(new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchMoreMovies(adapter);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        result = (List<Movie>) (savedInstanceState != null
                        ? savedInstanceState.getParcelableArrayList("movie list key")
                        : new ArrayList<>());

        currentMovie = (savedInstanceState != null
                ? savedInstanceState.getInt("key")
                : 0);
        pageNumber = (savedInstanceState != null
                ? savedInstanceState.getInt("page")
                : 0);
        if(currentMovie != OFFLINE_SET) {
            if (result.size() > 0) {
                adapter = new MoviesAdapter(result, R.layout.list_item, MoviesActivity.this);
                recyclerView.setAdapter(adapter);
            } else {
                getMostPopular();

            }
        }else{
            getOfflineData();
            result = null;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentMovie == TOP_RATED_SET) {
            MenuItem item = menu.findItem(R.id.top_rated);
            item.setChecked(true);
        }else if(currentMovie == OFFLINE_SET){
            MenuItem item = menu.findItem(R.id.favorites);
            item.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_rated:
                getTopRated();
                item.setChecked(true);
                break;
            case R.id.most_popular:
                getMostPopular();
                item.setChecked(true);
                break;
            case R.id.favorites:
                getOfflineData();
                item.setChecked(true);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTopRated(){
        pageNumber =1;
        currentMovie = TOP_RATED_SET;
        mUsingOfflineData = false;
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                int statusCode = response.code();
                result = response.body().getResults();
                adapter = new MoviesAdapter(response.body().getResults(), R.layout.list_item, MoviesActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });
    }

    public void getMostPopular(){
        pageNumber = 1;
        currentMovie = POPULAR_SET;
        mUsingOfflineData = false;
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                int statusCode = response.code();
                result = response.body().getResults();
                adapter = new MoviesAdapter(response.body().getResults(), R.layout.list_item, MoviesActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });
    }

    private void fetchMoreMovies(final MoviesAdapter moviesAdapter) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MoviesResponse> call;
        switch (currentMovie) {
            case POPULAR_SET:
                call = apiService.getMorePopular(API_KEY,++pageNumber);
                break;
            case TOP_RATED_SET:
                call = apiService.getMoreTopRated(API_KEY,++pageNumber);
                break;
            default:
                call = null;
                // could just limit queries to like 20 movies and then do a fetchMoreMovies
                break;
        }
        if (call != null) {
            updateMovieList(moviesAdapter, call);
        }
    }

    /*
    Enqueue a Call<> and use Response to add Movies to the List and notify Adapter
     */
    private void updateMovieList(final MoviesAdapter moviesAdapter, Call<MoviesResponse> call) {
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                MoviesResponse obj = response.body();
                List<Movie> moreMovieList;
                moreMovieList = obj.getResults();
                int curSize = adapter.getItemCount();
                result.addAll(moreMovieList);
                moviesAdapter.notifyItemRangeChanged(curSize, result.size() - 1);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
            }
        });
    }
    private void getOfflineData() {
        currentMovie = OFFLINE_SET;
        mUsingOfflineData = true;

        Cursor cursor = getContentResolver().query(FavoritesContract.Favorites.CONTENT_URI,
                new String[]{FavoritesContract.Favorites.COLUMN_API_ID,
                        FavoritesContract.Favorites.COLUMN_TITLE,
                        FavoritesContract.Favorites.COLUMN_RATING,
                        FavoritesContract.Favorites.COLUMN_SYNOPSIS,
                        FavoritesContract.Favorites.COLUMN_RELEASE_DATE},
                null,
                null,
                null);

        /*use the results from the cursor to make a movie list and update ui */
        // TODO: ultimately will switch to Realm and this won't be a thing
        mMovieList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int idColumnIndex = cursor.getColumnIndex(FavoritesContract.Favorites.COLUMN_API_ID);
                int titleColumnIndex = cursor.getColumnIndex(FavoritesContract.Favorites.COLUMN_TITLE);
                int voteColumnIndex = cursor.getColumnIndex(FavoritesContract.Favorites.COLUMN_RATING);
                int overviewColumnIndex = cursor.getColumnIndex(FavoritesContract.Favorites.COLUMN_SYNOPSIS);
                int releaseDateColumnIndex = cursor.getColumnIndex(FavoritesContract.Favorites.COLUMN_RELEASE_DATE);

                int id = cursor.getInt(idColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String vote_average_string = cursor.getString(voteColumnIndex);
                String overview = cursor.getString(overviewColumnIndex);
                String release_date = cursor.getString(releaseDateColumnIndex);

                double vote_average = Double.valueOf(vote_average_string);
                /*the poster will be set by the adapter, so pass null*/
                mMovieList.add(new Movie(title, release_date, vote_average, overview,id));
            }
            cursor.close();
            adapter = new MoviesAdapter(mMovieList, R.layout.list_item, MoviesActivity.this,true);
            recyclerView.setAdapter(adapter);
        } else {
            // TODO: in event that user has no favorites, maybe prompt them
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movie list key", (ArrayList<? extends Parcelable>) result);
        outState.putBoolean(BUNDLE_KEY_FAVORITES, mUsingOfflineData);
        outState.putInt("key",currentMovie);
        outState.putInt("page",pageNumber);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
