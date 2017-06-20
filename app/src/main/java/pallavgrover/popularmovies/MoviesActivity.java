package pallavgrover.popularmovies;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pallavgrover.popularmovies.Util.Constants;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.MoviesResponse;
import pallavgrover.popularmovies.retrofit.ApiClient;
import pallavgrover.popularmovies.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;
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
    private  int pageNumber;
    private int currentMovieSet;

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
        getMostPopular();
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTopRated(){
        pageNumber =1;
        currentMovieSet = TOP_RATED_SET;
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
        currentMovieSet = POPULAR_SET;
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
        switch (currentMovieSet) {
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


}
