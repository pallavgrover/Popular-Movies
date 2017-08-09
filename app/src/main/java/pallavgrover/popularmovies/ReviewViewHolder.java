package pallavgrover.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import pallavgrover.popularmovies.model.Response;


public class ReviewViewHolder extends RecyclerView.ViewHolder {

    TextView review_content;

    public ReviewViewHolder(View v) {
        super(v);
        review_content = (TextView) v.findViewById(R.id.review_content);
    }

    public void bindView(Response.Reviews review) {
        review_content.setText(review.getContent());
    }
}
