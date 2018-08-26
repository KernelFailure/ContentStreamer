package com.example.leonp.contentstreamer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leonp.contentstreamer.models.ContentPost;

import java.util.List;

public class ContentPostListAdapter extends ArrayAdapter<ContentPost> {

    private static final String TAG = "ContentPostListAdapter";

    // vars
    private LayoutInflater mInflator;
    private Context mContext;
    private int mResource;

    public ContentPostListAdapter(@NonNull Context context, int resource, @NonNull List<ContentPost> objects) {
        super(context, resource, objects);

        mContext = context;
        mResource = resource;
        mInflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    static class ViewHolder{
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvCreatedAt;
        TextView tvFileSize;
        ImageView ivStreamType;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflator.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
            holder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tvFileSize);
            holder.ivStreamType = (ImageView) convertView.findViewById(R.id.ivStreamType);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTitle.setText(getItem(position).getTitle());
        holder.tvAuthor.setText(getItem(position).getAuthor());
        holder.tvCreatedAt.setText(getItem(position).getCreatedAt());
        holder.tvFileSize.setText(getItem(position).getFileSize());

        int imgRes = R.drawable.ic_error;
        String streamType = getItem(position).getStreamType();

        switch (streamType) {
            case "sound":
                imgRes = R.drawable.ic_music;
                break;
            case "picture":
                imgRes = R.drawable.ic_image;
                break;
            default:
                Log.d(TAG, "getView: Couldn't resolve stream type: " + streamType);
                break;
        }

        holder.ivStreamType.setImageResource(imgRes);

        return convertView;
    }
}
