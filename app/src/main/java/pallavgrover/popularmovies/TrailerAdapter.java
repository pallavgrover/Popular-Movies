package pallavgrover.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import java.util.List;

import pallavgrover.popularmovies.Util.Util;
import pallavgrover.popularmovies.model.VideosBean;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private List<VideosBean.Trailer> mTrailerIds;
    private Context mContext;

    public TrailerAdapter(Context context, List<VideosBean.Trailer> trailers) {
        mContext = context;
        mTrailerIds = trailers;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View trailerImageView = inflater.inflate(R.layout.item_trailer, parent, false);
        ViewHolder viewHolder = new ViewHolder(trailerImageView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String trailerIdString = mTrailerIds.get(position).getKey();
        ImageButton imageButton = holder.imageButton;
        Glide.with(getContext())
                .load(Util.YoutubeUtility.getPosterUrlFromTrailerId(trailerIdString))
                .into(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Util.YoutubeUtility.getTrailerUrlFromTrailerId(trailerIdString))));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailerIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            imageButton = (ImageButton) itemView.findViewById(R.id.thumbnail_item);
        }
    }
}