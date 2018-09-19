package com.example.leonp.contentstreamer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;


import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFeed extends AppCompatActivity {

    private static final String TAG = "HomeFeed";


    // widgets
    private ViewPager mViewPager;
    private ImageView ivMenu;

    // vars
    private AdapterFragment mFragmentAdapter;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        mViewPager = (ViewPager) findViewById(R.id.container);
        ivMenu = (ImageView) findViewById(R.id.ivMenu);

        mContext = getApplicationContext();

        mFragmentAdapter = new AdapterFragment(getSupportFragmentManager());
        mFragmentAdapter.addFragment(new HomeFeedFragment());

        mViewPager.setAdapter(mFragmentAdapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked on menu icon");
                PopupMenu menu = new PopupMenu(mContext, ivMenu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "onMenuItemClick: Clicked menu item: " + item.getItemId());
                        switch (item.getItemId()) {
                            case R.id.profile:
                                Log.d(TAG, "onMenuItemClick: Clicked to go to profile");
                                Intent intent = new Intent(mContext, ProfileActivity.class);

                                startActivity(intent);


                                break;
                            case R.id.logout:
                                Toast.makeText(mContext, "Clicked Logout", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.about:
                                Toast.makeText(mContext, "Clicked About", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(mContext, "No item clicked...", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                getMenuInflater().inflate(R.menu.menu, menu.getMenu());
                menu.show();
            }
        });
    }


}
