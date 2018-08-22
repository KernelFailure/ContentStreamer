package com.example.leonp.contentstreamer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.leonp.contentstreamer.models.ContentPost;

import java.util.List;

public class ContentPostListAdapter extends ArrayAdapter<ContentPost> {

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
        TextView tvPostId;
        TextView tvCreatedAt;
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
            holder.tvPostId = (TextView) convertView.findViewById(R.id.tvPostId);
            holder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        ContentPost tempPost = getItem(position);
//        String title = tempPost.getPostTitle();
//        String

        holder.tvTitle.setText(getItem(position).getPostTitle());
        holder.tvPostId.setText(getItem(position).getPostId());
        holder.tvCreatedAt.setText(getItem(position).getCreatedAt());

        return convertView;
    }
}
