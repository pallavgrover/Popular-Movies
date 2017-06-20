package pallavgrover.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.MoviesResponse;
import pallavgrover.popularmovies.retrofit.ApiClient;
import pallavgrover.popularmovies.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;
import static pallavgrover.popularmovies.Util.Constants.posterUrl;
import static pallavgrover.popularmovies.Util.Constants.posterUrlBig;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView poster,backDrop;
    private TextView title,releaseDate,rating,plot;

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
        int value = intent.getIntExtra("movie_id",0);
        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY from themoviedb.org first!", Toast.LENGTH_LONG).show();
            return;
        }
        poster = (ImageView) findViewById(R.id.movie_poster);
        backDrop = (ImageView) findViewById(R.id.movie_backdrop);
        title = (TextView) findViewById(R.id.movie_title);
        rating = (TextView) findViewById(R.id.movie_user_rating);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        plot = (TextView) findViewById(R.id.movie_overview);
        if(value !=0){
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<Movie> call = apiService.getMovieDetails(value,API_KEY);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    int statusCode = response.code();
                    Glide.with(MovieDetailsActivity.this).load(posterUrl+response.body().getPosterPath()).into(poster);
                    Glide.with(MovieDetailsActivity.this).load(posterUrlBig+response.body().getPosterPath()).into(backDrop);
                    title.setText(response.body().getTitle());
                    releaseDate.setText(String.format("%s %s", getString(R.string.release_Date), response.body().getReleaseDate()));
                    rating.setText(String.valueOf(String.format("%s %s", getString(R.string.rating), response.body().getVoteAverage())));
                    plot.setText(response.body().getOverview());
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("", t.toString());
                }
            });
        }
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
}
