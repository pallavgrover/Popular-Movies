package pallavgrover.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import pallavgrover.popularmovies.model.Response;

/**
 * An adapter to put Reviews into a RecyclerView
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private static final String LOG_TAG = "ReviewsAdapter";
    private List<Response.Reviews> mReviews;
    private Context mContext;

    public ReviewsAdapter(Context context, List<Response.Reviews> reviews){
        Log.i(LOG_TAG, "constructed");
        mContext = context;
        mReviews = reviews;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reviewView = inflater.inflate(R.layout.item_review, parent, false);
        ReviewsAdapter.ViewHolder viewHolder = new ViewHolder(reviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String reviewString = mReviews.get(position).getContent();

        TextView textView = holder.reviewTextView;
        textView.setText(reviewString);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView reviewTextView;
        public ViewHolder(View itemView) {
            super(itemView);

            reviewTextView = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}