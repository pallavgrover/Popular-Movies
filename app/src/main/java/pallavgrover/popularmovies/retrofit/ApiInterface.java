package pallavgrover.popularmovies.retrofit;

import pallavgrover.popularmovies.Util.Constants;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
 
 
public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);
 
    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getMorePopular(@Query("api_key") String apiKey,@Query("page") int pagenumber);

    @GET("movie/top_rated" )
    Call<MoviesResponse> getMoreTopRated(@Query("api_key") String apiKey,@Query("page") int pagenumber);
}
