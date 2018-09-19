package com.example.leonp.contentstreamer;

import android.content.Context;
import android.database.MatrixCursor;
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

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.example.leonp.contentstreamer.models.Posts2DO;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFeedFragment extends Fragment {

    private static final String TAG = "HomeFeedFragment";

    // widgets
    private RecyclerView mRecyclerView;
    //private ListView mListView;
    private Button btnReloadData;

    // vars
    private Context mContext;
    private List<Posts2DO> mPostList;
    private ContentPostListAdapter mPostAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;


    // Observable
    private Observable<List<Posts2DO>> mPostListObservable = Observable.create(postList -> {
        postList.onNext(downloadFromDatabase());
    });

    private List<Posts2DO> downloadFromDatabase() {

        String[] projection = {
                "userId",
                "postId",
                "title",
                "author",
                "createdAt",
                "streamType",
                "imagePath"
        };

        DynamoDBMapper mapper = AWSProvider.getDynamoDBMapper(mContext);
        MatrixCursor cursor = new MatrixCursor(projection);

        Posts2DO template = new Posts2DO();
        template.setUserId(AWSProvider.getIdentityManager(mContext).getCachedUserID());

        DynamoDBQueryExpression expression = new DynamoDBQueryExpression<Posts2DO>()
                .withHashKeyValues(template);

        List<Posts2DO> postList = mapper.query(Posts2DO.class, expression);
        Log.d(TAG, "downloadFromDatabase: Post List size: " + postList.size());
        return postList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_homefeed, container, false);

        mContext = getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //mListView = (ListView) view.findViewById(R.id.listView);
        btnReloadData = (Button) view.findViewById(R.id.btnReloadData);

        btnReloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Relaod Data button");

                v.setEnabled(false);
                CompositeDisposable disposable = new CompositeDisposable();
                Disposable subscribe = mPostListObservable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(postList -> {
                            mPostList.addAll(postList);
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onClick: Finished download subscription");
                            v.setEnabled(true);
                        });

                disposable.add(subscribe);

            }
        });

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        mPostList = new ArrayList<>();
        initFakeData();
        mPostAdapter = new ContentPostListAdapter(mContext, R.layout.layout_postlistitem, mPostList);
        mAdapter = new RecyclerViewAdapter(mContext, mPostList);
        mRecyclerView.setAdapter(mAdapter);



        return view;

    }

    private void initFakeData() {

//        Posts2DO post1 = new Posts2DO();
//        post1.setStreamType("audio");
//        post1.setTitle("One");
//        mPostList.add(post1);
//        Posts2DO post2 = new Posts2DO();
//        post2.setStreamType("audio");
//        post2.setTitle("Two");
//        mPostList.add(post2);
//        Posts2DO post3 = new Posts2DO();
//        post3.setStreamType("audio");
//        post3.setTitle("Three");
//        mPostList.add(post3);
//        PostsDO post4 = new PostsDO();
//        post4.setStreamType("audio");
//        post4.setTitle("Four");
//        mPostList.add(post4);
//        PostsDO post5 = new PostsDO();
//        post5.setStreamType("audio");
//        post5.setTitle("Five");
//        mPostList.add(post5);
//        PostsDO post6 = new PostsDO();
//        post6.setStreamType("audio");
//        post6.setTitle("Six");
//        mPostList.add(post6);
//        PostsDO post7 = new PostsDO();
//        post7.setStreamType("audio");
//        post7.setTitle("Seven");
//        mPostList.add(post7);
//        PostsDO post8 = new PostsDO();
//        post8.setStreamType("audio");
//        post8.setTitle("Eight");
//        mPostList.add(post8);
//        PostsDO post9 = new PostsDO();
//        post9.setStreamType("audio");
//        post9.setTitle("Nine");
//        mPostList.add(post9);
//        PostsDO post10 = new PostsDO();
//        post10.setStreamType("audio");
//        post10.setTitle("Ten");
//        mPostList.add(post10);
//
//        //mAdapter.notifyDataSetChanged();
//
//        Log.d(TAG, "initFakeData: Loaded data. List size: " + mPostList.size());


    }
}
