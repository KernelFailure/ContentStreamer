package com.example.leonp.contentstreamer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.bumptech.glide.Glide;
import com.example.leonp.contentstreamer.models.Posts2DO;

import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends Activity {

    private static final String TAG = "ProfileActivity";

    // widgets
    private Button btnInsertDummyData;

    // vars
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = this;

        btnInsertDummyData = (Button) findViewById(R.id.btnInsertDummyData);

        btnInsertDummyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked on insert dummy data");
                v.setEnabled(false);

                // TODO: make this button instead do an api call using retrofit to AWS API gateway

                AWSConfiguration configuration = new AWSConfiguration(mContext);
//                AWSAppSyncClient client = AWSAppSyncClient
//                        .builder()
//                        .context(mContext)
//                        .awsConfiguration(configuration)
//                        .build();


                Observable<Void> observable = Observable.create(item -> insertIntoDb());
                Disposable subscribe = observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(item -> v.setEnabled(true));
                CompositeDisposable disposable = new CompositeDisposable();
                disposable.add(subscribe);


            }
        });

    }

    private void insertIntoDb() {
        DynamoDBMapper mapper = AWSProvider.getDynamoDBMapper(mContext);
        Posts2DO post = new Posts2DO();
        post.setUserId(AWSProvider.getIdentityManager(mContext).getCachedUserID());
        post.setTitle("My First Dummy Post");
        post.setAuthor("Harry Potter");
        post.setCreatedAt("Today");
        post.setImagePath("fakePath");
        post.setPostId("1234");
        post.setStreamType("audio");
        mapper.save(post);
    }


}
