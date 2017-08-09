package pallavgrover.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import pallavgrover.popularmovies.Util.Util;

/**
 * Created by harsh on 09/08/2017.
 */


public class TrailerViewHolder extends RecyclerView.ViewHolder {

    View col1, col2;
    Context context;

    public TrailerViewHolder(View v, Context context) {
        super(v);
        this.context =context;
        col1 = itemView.findViewById(R.id.col_1);
        col2 = itemView.findViewById(R.id.col_2);
    }

    public void bindView(MovieDetailAdapter.TrailerRow trailer) {
        bindViewCol(0, trailer);
        bindViewCol(1, trailer);
    }

    private View getColViewFromPosition(int i) {
        switch (i) {
            case 0:
                return col1;
            case 1:
                return col2;
        }
        return null;
    }

    private void bindViewCol(int position, MovieDetailAdapter.TrailerRow row) {
        final View col = getColViewFromPosition(position);
        if (position < row.trailers.size()) {
            col.setVisibility(View.VISIBLE);
            final String trailerIdString = row.trailers.get(position).getKey();
            ImageView trailerImageView = (ImageView) col.findViewById(R.id.thumbnail_item);
            Glide.with(context)
                    .load(Util.YoutubeUtility.getPosterUrlFromTrailerId(trailerIdString))
                    .into(trailerImageView);
        }else{
            col.setVisibility(View.INVISIBLE);
        }
    }
}
