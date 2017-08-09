package pallavgrover.popularmovies;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pallavgrover.popularmovies.database.FavoritesContract;
import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.Response;
import pallavgrover.popularmovies.model.VideosBean;
import pallavgrover.popularmovies.retrofit.ApiClient;
import pallavgrover.popularmovies.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

import static pallavgrover.popularmovies.Util.Constants.API_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    public static final String ARG_MOVIE_DETAILS = "movie_details";
//    private ImageView poster;
//    private TextView title,releaseDate,rating,plot;
    private Movie movie;
    private boolean isFavorite;
    private RecyclerView recyclerViewMovieDetail;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String KEY_RECYCLER_STATE_TRAILER = "recycler_state_trailer";
    private static Bundle mBundleRecyclerViewState;
    private static Bundle mBundleRecyclerViewStateTrailer;
//    private RecyclerView recyclerViewTrailer;
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
//    private GridLayoutManager gridLayoutManager;
    private int rev;
    private int trail;
    private MovieDetailAdapter adapter;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments().containsKey(ARG_MOVIE_DETAILS)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movie = getArguments().getParcelable(ARG_MOVIE_DETAILS);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_movie_details, container, false);
        setFavoriteStatus();
//        poster = (ImageView) rootView.findViewById(R.id.movie_poster);
//        backDrop = (ImageView) findViewById(R.id.movie_backdrop);
//        title = (TextView) rootView.findViewById(R.id.movie_title);
//        rating = (TextView) rootView.findViewById(R.id.movie_user_rating);
//        releaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
//        plot = (TextView) rootView.findViewById(R.id.movie_overview);
        recyclerViewMovieDetail = (RecyclerView) rootView.findViewById(R.id.movie_detail_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerViewReview.setLayoutManager(linearLayoutManager);
//        recyclerViewTrailer = (RecyclerView) rootView.findViewById(R.id.trailer_list);
//        mScrollView = (NestedScrollView) rootView.findViewById(R.id.scroll_view);
//        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//        title.setText(movie.getTitle());
//        releaseDate.setText(String.format("%s %s", getString(R.string.release_Date), movie.getReleaseDate()));
//        rating.setText(String.valueOf(String.format("%s %s", getString(R.string.rating), movie.getVoteAverage())));
//        plot.setText(movie.getOverview());
        reviewsList = (List<Response.Reviews>) (savedInstanceState != null
                ? savedInstanceState.getParcelableArrayList("review")
                : new ArrayList<>());
        trailerList = (List<VideosBean.Trailer>) (savedInstanceState != null
                ? savedInstanceState.getParcelableArrayList("trailer")
                : new ArrayList<>());

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        recyclerViewMovieDetail.setLayoutManager(linearLayoutManager);
        adapter = new MovieDetailAdapter(getActivity(), reviewsList, trailerList, movie, isFavorite);
        recyclerViewMovieDetail.setAdapter(adapter);

        if(reviewsList.size()>0){

        }else {
            Call<Response> call = apiService.getMovieReviews(movie.getId(), API_KEY);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    reviewsList = response.body().getResults();
                    adapter.updateReviews(reviewsList);
//                    ReviewsAdapter adapter = new ReviewsAdapter(getActivity(), reviewsList);
//                    recyclerViewReview.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("", t.toString());
                }
            });
        }
        if(trailerList.size()>0){
//            TrailerAdapter adapter = new TrailerAdapter(getActivity(), trailerList);
//            recyclerViewTrailer.setAdapter(adapter);
        }else {
            Call<VideosBean> trailer = apiService.getMovieVideos(movie.getId(), API_KEY);
            trailer.enqueue(new Callback<VideosBean>() {

                @Override
                public void onResponse(Call<VideosBean> call, retrofit2.Response<VideosBean> response) {
                    trailerList = response.body().getResults();
                    adapter.updateTrailers(trailerList);

//                    TrailerAdapter adapter = new TrailerAdapter(getActivity(), trailerList);
//                    recyclerViewTrailer.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<VideosBean> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("", t.toString());
                }
            });
        }
//        if (isFavorite) {
//            try {
//                String filename = String.valueOf(movie.getId());
//                File photofile = new File(getActivity().getFilesDir(), filename);
//                Bitmap freshBitMap = BitmapFactory.decodeStream(new FileInputStream(photofile));
//                poster.setImageBitmap(freshBitMap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Glide.with(getActivity()).load(posterUrl+movie.getPosterPath()).into(poster);
//        }
//        Glide.with(MovieDetailsActivity.this).load(posterUrlBig+movie.getPosterPath()).into(backDrop);
//        final ImageView favoriteButton = (ImageView) rootView.findViewById(R.id.favorite);
//        final Drawable favoriteIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_orange_24dp);
//        final Drawable nonFavoriteIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_black_24dp);
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
        return rootView;
    }


    private void setFavoriteStatus() {
        Cursor cursor = getActivity().getContentResolver().query(
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("review", (ArrayList<? extends Parcelable>) reviewsList);
        outState.putParcelableArrayList("trailer", (ArrayList<? extends Parcelable>) trailerList);
    }

}
