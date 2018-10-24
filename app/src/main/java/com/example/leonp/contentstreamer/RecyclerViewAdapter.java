package com.example.leonp.contentstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.bumptech.glide.Glide;
import com.example.leonp.contentstreamer.models.ContentPost;
import com.example.leonp.contentstreamer.models.PostPicture;
import com.example.leonp.contentstreamer.models.Posts2DO;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private List<ContentPost> mPostList;


    public RecyclerViewAdapter(Context mContext, List<ContentPost> mPostList) {
        this.mContext = mContext;
        this.mPostList = mPostList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvAuthor;
        TextView tvCreatedAt;
        TextView tvFileSize;
        ImageView ivStreamType;
        ProgressBar pictureProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvFileSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            ivStreamType = (ImageView) itemView.findViewById(R.id.ivStreamType);
            pictureProgressBar = (ProgressBar) itemView.findViewById(R.id.pictureProgressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_postlistitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        holder.tvTitle.setText(mPostList.get(position).getTitle());
        holder.tvAuthor.setText(mPostList.get(position).getAuthor());
        holder.tvCreatedAt.setText(mPostList.get(position).getCreatedAt());
        Glide.with(mContext).load(mPostList.get(position).getPostBitmap()).into(holder.ivStreamType);
        //holder.pictureProgressBar.setVisibility(View.VISIBLE);

        // Observables
//        Observable<PostPicture> mPostPictureObservable = Observable.create(postPicture -> {
//
//            PostPicture post = new PostPicture();
//            String imagePath = mPostList.get(position).getImagePath();
//
//            try {
//                AmazonS3Client client = AWSProvider.getS3Client(mContext);
//                String bucketName = Constants.s3Bucket;
//                String key = "public/batman.jpg";
//                Log.d(TAG, "onBindViewHolder: Trying to get S3 object with: " + bucketName + " : " + key);
//                S3Object object = client.getObject(bucketName, key);
//                Log.d(TAG, "onBindViewHolder: Got S3 Object: " + object.getKey());
//
//
//                Map<String, String> myMap = object.getObjectMetadata().getUserMetadata();
//                client.getObjectMetadata(Constants.s3Bucket, imagePath)
//                        .getContentLength();
//                String fileSize = String.valueOf(object.getObjectMetadata().getContentLength());
//
//                Bitmap bitmap = BitmapFactory.decodeStream(object.getObjectContent());
//                post.setBitmap(bitmap);
//                post.setFileSize(fileSize);
//
//                Log.d(TAG, "onBindViewHolder: Got file size: " + fileSize + " from: " + imagePath);
//            } catch (Exception e) {
//                Log.e(TAG, "onBindViewHolder: Error setting post picture: " + e.getMessage());
//                e.printStackTrace();
//                post.setBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_error));
//                post.setFileSize("");
//            }
//
//            postPicture.onNext(post);
//        });
//
//        CompositeDisposable disposable = new CompositeDisposable();
//        Disposable subscribe = mPostPictureObservable.observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(postPicture -> {
//                    holder.pictureProgressBar.setVisibility(View.INVISIBLE);
//                    holder.tvFileSize.setText(postPicture.getFileSize());
//                    holder.ivStreamType.setImageBitmap(postPicture.getBitmap());
//                });
//        disposable.add(subscribe);

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }
}
