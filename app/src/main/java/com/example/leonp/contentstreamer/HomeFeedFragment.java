package com.example.leonp.contentstreamer;

import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.example.leonp.contentstreamer.models.ContentPost;
import com.example.leonp.contentstreamer.models.Posts2DO;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFeedFragment extends Fragment {

    private static final String TAG = "HomeFeedFragment";

    // widgets
    private RecyclerView mRecyclerView;
    private RelativeLayout mProgressbar;
    private RelativeLayout mRelTwo;
    private Button btnReloadData;

    // vars
    private Context mContext;
    private List<ContentPost> mPostList;
    private ContentPostListAdapter mPostAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_homefeed, container, false);

        mContext = getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mProgressbar = (RelativeLayout) view.findViewById(R.id.rel1);
        mProgressbar.setVisibility(View.INVISIBLE);
        mRelTwo = (RelativeLayout) view.findViewById(R.id.rel2);
        btnReloadData = (Button) view.findViewById(R.id.btnReloadData);

        btnReloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Relaod Data button");

                v.setEnabled(false);
                callDownloadFromDBObservable(v);

            }
        });

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        mPostList = new ArrayList<>();
        mPostAdapter = new ContentPostListAdapter(mContext, R.layout.layout_postlistitem, mPostList);
        mAdapter = new RecyclerViewAdapter(mContext, mPostList);
        mRecyclerView.setAdapter(mAdapter);

        //callDownloadFromDBObservable(null);

        return view;

    }

    /* ---------------  OBSERVABLES ---------------------- **/

    private Observable<List<ContentPost>> mPostListObservable =
            Observable.create(postList -> postList.onNext(downloadFromDatabase()));

    private void callDownloadFromDBObservable(View v) {
        mProgressbar.setVisibility(View.VISIBLE);
        mRelTwo.setVisibility(View.INVISIBLE);
        Log.d(TAG, "callDownloadFromDBObservable: Progress bar is: " + mProgressbar.getVisibility());
        CompositeDisposable disposable = new CompositeDisposable();
        Disposable subscribe = mPostListObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .subscribe(postList -> {
                    TimeUnit.SECONDS.sleep(1);
                    mPostList.addAll(postList);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onClick: Finished download subscription");
                    try {
                        v.setEnabled(true);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "callDownloadFromDBObservable: NullPointerException: " + e.getMessage());
                        e.printStackTrace();
                    }
                    mProgressbar.setVisibility(View.INVISIBLE);
                    mRelTwo.setVisibility(View.VISIBLE);
                    Log.d(TAG, "callDownloadFromDBObservable: Progress bar is: " + mProgressbar.getVisibility());
                });

        disposable.add(subscribe);
    }

    private List<ContentPost> downloadFromDatabase() {

//        String[] projection = {
//                "userId",
//                "postId",
//                "title",
//                "author",
//                "createdAt",
//                "streamType",
//                "imagePath"
//        };

        DynamoDBMapper mapper = AWSProvider.getDynamoDBMapper(mContext);
        //MatrixCursor cursor = new MatrixCursor(projection);

        Posts2DO template = new Posts2DO();
        template.setUserId(AWSProvider.getIdentityManager(mContext).getCachedUserID());

        DynamoDBQueryExpression expression = new DynamoDBQueryExpression<Posts2DO>()
                .withHashKeyValues(template);
        List<Posts2DO> postList = null;
        List<ContentPost> fullPostList = null;
        try {
            postList = mapper.query(Posts2DO.class, expression);
            fullPostList = new ArrayList<>();

            for (Posts2DO post : postList) {
                ContentPost tempPost = new ContentPost();
                String path = "public/" + post.getImagePath();
                Log.d(TAG, "downloadFromDatabase: Trying with path: " + path);
                AmazonS3Client client = AWSProvider.getS3Client(mContext);
                Bitmap bitmap;
                try {
                    S3Object object = client.getObject(Constants.s3Bucket, path);
                    bitmap = BitmapFactory.decodeStream(object.getObjectContent());
                } catch (Exception e) {
                    Log.e(TAG, "downloadFromDatabase: Exception: " + e.getMessage());
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error);
                }

                tempPost.setPostBitmap(bitmap);
                tempPost.setAuthor(post.getAuthor());
                tempPost.setTitle(post.getTitle());
                tempPost.setCreatedAt(post.getCreatedAt());
                tempPost.setFileSize("100kb");
                tempPost.setStreamType(post.getStreamType());
                fullPostList.add(tempPost);
            }
        } catch (Exception e) {
            Log.e(TAG, "downloadFromDatabase: Exception: " + e.getMessage());
            e.printStackTrace();
        }



        Log.d(TAG, "downloadFromDatabase: Post List size: " + fullPostList.size());
        return fullPostList;
    }
}
