package com.example.leonp.contentstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.bumptech.glide.Glide;
import com.example.leonp.contentstreamer.models.ContentPost;
import com.example.leonp.contentstreamer.models.Posts2DO;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        ProgressBar pictureProgressBar;
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
            holder.pictureProgressBar = (ProgressBar) convertView.findViewById(R.id.pictureProgressBar);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTitle.setText(getItem(position).getTitle());
        holder.tvAuthor.setText(getItem(position).getAuthor());
        holder.tvCreatedAt.setText(getItem(position).getCreatedAt());
        holder.pictureProgressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(getItem(position).getPostBitmap()).into(holder.ivStreamType);

        //holder.ivStreamType.setImageResource(R.drawable.ic_error);
        String streamType = getItem(position).getStreamType();

        switch (streamType) {
            case "sound":

                holder.ivStreamType.setImageResource(R.drawable.ic_music);
                break;
            case "picture":

                try {

                    Log.d(TAG, "getView: Trying to set picture");

//                    Observable<Bitmap> s3ObjectObservable = Observable.create(emittedBitmap -> {
//
//                        S3Object object = AWSProvider.getS3Client(mContext)
//                                .getObject(Constants.s3Bucket, getItem(position).getImagePath());
//                        ObjectMetadata metadata = object.getObjectMetadata();
//                        Map<String, String> userMetaData = metadata.getUserMetadata();
////                        userMetaData.get)
//                        //AWSProvider.getS3Client(mContext).get
//                        Bitmap bitmap = BitmapFactory.decodeStream(object.getObjectContent());
//
//                        emittedBitmap.onNext(bitmap);
//                    });
//
//                    Disposable subscribe = s3ObjectObservable.observeOn(AndroidSchedulers.mainThread())
//                            .subscribeOn(Schedulers.io())
//                            .subscribe(emittedBitmap -> {
//                                Log.d(TAG, "getView: Trying set bitmap: " + emittedBitmap.toString());
//                                holder.ivStreamType.setImageBitmap(emittedBitmap);
//                                holder.pictureProgressBar.setVisibility(View.INVISIBLE);
//                });

//                    Log.d(TAG, "getView: Finished subscribing: " + subscribe.toString());
//
//                    CompositeDisposable disposable = new CompositeDisposable();
//                    disposable.add(subscribe);

//                    S3Object object = AWSProvider.getS3Client(mContext).getObject(
//                            Constants.s3Bucket, getItem(position).getImagePath());
//                    holder.ivStreamType.setImageBitmap(BitmapFactory.decodeStream(object.getObjectContent()));
                } catch (NullPointerException e) {
                    Log.e(TAG, "getView: NullPointerException: " + e.getMessage());
                    e.printStackTrace();
                    holder.ivStreamType.setImageResource(R.drawable.ic_image);
                }
                break;
            default:
                Log.d(TAG, "getView: Couldn't resolve stream type: " + streamType);
                break;
        }


        return convertView;
    }
}
