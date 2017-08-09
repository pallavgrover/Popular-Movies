package pallavgrover.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pallavgrover.popularmovies.model.Movie;
import pallavgrover.popularmovies.model.Response;
import pallavgrover.popularmovies.model.VideosBean;

/**
 * An adapter to put Reviews into a RecyclerView
 */
public class MovieDetailAdapter extends RecyclerView.Adapter {
    private static final String LOG_TAG = "ReviewsAdapter";
    private List<Response.Reviews> mReviews;
    private Context mContext;
    private List<VideosBean.Trailer> mTrailers;
    private Movie movie;
    public static final int TYPE_POSTER = 0x1f;
    public static final int TYPE_HEADER = 0x2f;
    public static final int TYPE_TRAILER = 0x3f;
    public static final int TYPE_REVIEW = 0x4f;
    private boolean isfavorite;

    private ArrayList<CustomData> mItems = new ArrayList<>();

    public MovieDetailAdapter(Context context, List<Response.Reviews> reviews, List<VideosBean.Trailer> trailers, Movie movie, boolean isfavorite){
        Log.i(LOG_TAG, "constructed");
        mContext = context;
        mReviews = reviews;
        mTrailers = trailers;
        this.movie = movie;
        this.isfavorite = isfavorite;
        populateCustomDataSet();
    }

    public void updateReviews(List<Response.Reviews> reviews){
        mReviews = reviews;
        populateCustomDataSet();
        notifyDataSetChanged(); //// TODO: 09/08/2017 instead of notifying whole adapter just notify the position for review
    }

    public void updateTrailers(List<VideosBean.Trailer> trailers){
        mTrailers = trailers;
        populateCustomDataSet();
        notifyDataSetChanged(); //// TODO: 09/08/2017 instead of notifying whole adapter just notify the position for trailers
    }

    private void populateCustomDataSet() {
        mItems.clear();

        // add poster data
        CustomData posterData = new CustomData(TYPE_POSTER, movie);
        mItems.add(posterData);

        // add title
        CustomData titleData = new CustomData(TYPE_HEADER, getContext().getString(R.string.trailers));
        mItems.add(titleData);

        // add trailers row
        addTrailers();

        if(mReviews!=null && mReviews.size()>0) {

            // add title
            CustomData reviewTitleData = new CustomData(TYPE_HEADER, getContext().getString(R.string.reviews));
            mItems.add(reviewTitleData);

            // add reviews
            for (Response.Reviews review : mReviews) {
                mItems.add(new CustomData(TYPE_REVIEW, review));
            }
        }
    }

    private void addTrailers() {
        int i = 0;
        while (i < mTrailers.size()) {
            TrailerRow row = new TrailerRow();
            if (addTrailerToRow(row, i)) {
                i++;
            } else {
                mItems.add(new CustomData(TYPE_TRAILER, row));
                break;
            }
            if (addTrailerToRow(row, i)) {
                i++;
            } else {
                mItems.add(new CustomData(TYPE_TRAILER, row));
                break;
            }
            mItems.add(new CustomData(TYPE_TRAILER, row));
        }
    }

    private boolean addTrailerToRow(TrailerRow trailerRow, int position) {
        if (position < mTrailers.size()) {
            trailerRow.trailers.add(mTrailers.get(position));
            return true;
        }
        return false;
    }

    public static class TrailerRow {
        public ArrayList<VideosBean.Trailer> trailers = new ArrayList<>();

        public TrailerRow() {
        }
    }

    public static class CustomData {
        public int type;
        public Object data;

        CustomData(int type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case TYPE_POSTER:
                view = LayoutInflater.from(context).inflate(R.layout.poster_layout, parent, false);
                viewHolder =  new PosterAdapterViewHolder(view, context, isfavorite);
                break;
            case TYPE_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.title_layout, parent, false);
                viewHolder = new TitleViewHolder(view);
                break;
            case TYPE_TRAILER:
                view = LayoutInflater.from(context).inflate(R.layout.layout_row_trailer, parent, false);
                viewHolder = new TrailerViewHolder(view, context);
                break;
            case TYPE_REVIEW:
                view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
                viewHolder = new ReviewViewHolder(view);
                break;
        }
        return viewHolder;

//        View reviewView = inflater.inflate(R.layout.item_review, parent, false);
//        ReviewsAdapter.ViewHolder viewHolder = new ViewHolder(reviewView);
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CustomData customData = mItems.get(position);
        switch (customData.type) {
            case TYPE_POSTER:
                ((PosterAdapterViewHolder) holder).bindView((Movie) customData.data);
                break;
            case TYPE_HEADER:
                ((TitleViewHolder) holder).bindView((String) customData.data);
                break;
            case TYPE_TRAILER:
                ((TrailerViewHolder) holder).bindView((TrailerRow) customData.data);
                break;
            case TYPE_REVIEW:
                ((ReviewViewHolder) holder).bindView((Response.Reviews) customData.data);
                break;
        }
    }

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        String reviewString = mReviews.get(position).getContent();
//
//        TextView textView = holder.reviewTextView;
//        textView.setText(reviewString);
//    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder{
//
//        public TextView reviewTextView;
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            reviewTextView = (TextView) itemView.findViewById(R.id.review_content);
//        }
//    }
}