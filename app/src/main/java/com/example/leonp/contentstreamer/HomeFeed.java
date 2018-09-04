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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
import com.example.leonp.contentstreamer.models.ContentPost;
import com.example.leonp.contentstreamer.models.PostsDO;

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

    // constants
    private static final int WRITE_PERMISSION_REQUEST_CODE = 2;

    // widgets
    private ListView listView;
    //private Button btnGetContent;
    private RelativeLayout relProgressBar;
    private View mEmptyView;
    private Button btnMakeDbCall;


    // vars
    private List<PostsDO> mPostList;
    private ContentPostListAdapter mAdapter;
    private Context mContext;
    private List<S3ObjectSummary> mSummaryList;

    // Observables
    private Observable<List<PostsDO>> mPostListObservable = Observable.create(postList -> {
        postList.onNext(callToDB());
    });

    private List<PostsDO> callToDB() {

        String[] projection = {
                "userId",
                "title",
                "author",
                "createdAt",
                "streamType",
                "imagePath"
        };

        DynamoDBMapper mapper = AWSProvider.getDynamoDBMapper(mContext);
        MatrixCursor cursor = new MatrixCursor(projection);

        PostsDO template = new PostsDO();
        template.setUserId("1");

        DynamoDBQueryExpression<PostsDO> queryExpression = new DynamoDBQueryExpression<PostsDO>()
                .withHashKeyValues(template);

        List<PostsDO> dbPostList = mapper.query(PostsDO.class, queryExpression);

        return dbPostList;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        mContext = this;

        //btnGetContent = (Button) findViewById(R.id.btnGetContent);
        btnMakeDbCall = (Button) findViewById(R.id.btnMakeDbCall);
        relProgressBar = (RelativeLayout) findViewById(R.id.relProgressBar);
        relProgressBar.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.listView);
        mEmptyView = findViewById(R.id.emptyView);
        listView.setEmptyView(mEmptyView);

        mPostList = new ArrayList<>();
        mSummaryList = new ArrayList<>();

        mAdapter = new ContentPostListAdapter(mContext, R.layout.layout_postlistitem, mPostList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Clicked on: " + mPostList.get(position).getTitle());
            }
        });

        // initGetContentButton();
        initMakeDbCallButton();

    }

    private void getDBObjects(List<PostsDO> postList) {

        Iterator<PostsDO> iterator = postList.iterator();
//        for (PostsDO tempPost: iterator
//             ) {
//
//        }
        Log.d(TAG, "getDBObjects: Post List size: " + postList.size());
        while (iterator.hasNext()) {

            PostsDO tempPost = iterator.next();
            Log.d(TAG, "onClick: In DB found title: " + tempPost.getTitle());
            Log.d(TAG, "onClick: In DB found author: " + tempPost.getAuthor());
            Log.d(TAG, "onClick: In DB found image path: " + tempPost.getImagePath());
            Log.d(TAG, "onClick: In DB found created at: " + tempPost.getCreatedAt());
            Log.d(TAG, "onClick: In DB found stream type: " + tempPost.getStreamType());

        }

        Log.d(TAG, "getDBObjects: Done with while loop");

    }

    private void initMakeDbCallButton() {

        btnMakeDbCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                mPostList.clear();
                mAdapter.notifyDataSetChanged();
                mEmptyView.setVisibility(View.INVISIBLE);
                relProgressBar.setVisibility(View.VISIBLE);

                CompositeDisposable disposable = new CompositeDisposable();

                Disposable subscribe = mPostListObservable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(postList -> {
                            mPostList.addAll(postList);
                            mAdapter.notifyDataSetChanged();
                            relProgressBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onClick: Finished post list size is: " + mPostList.size());
                            v.setEnabled(true);
                        });

                disposable.add(subscribe);

            }
        });
    }

    private void initGetContentButton() {

//        btnGetContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: Clicked on get content button");
//                GetContentTask task = new GetContentTask();
//                task.execute();
//            }
//        });

    }

    private class GetContentTask extends AsyncTask<String, String, List<ContentPost>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //listView.setEmptyView(null);
            mPostList.clear();
            mAdapter.notifyDataSetChanged();
            mEmptyView.setVisibility(View.INVISIBLE);
            relProgressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "onPreExecute: Post List Size: " + mPostList.size());
        }

        @Override
        protected List<ContentPost> doInBackground(String... strings) {

            List<ContentPost> postList = new ArrayList<>();

            AmazonS3Client client = AWSProvider.getS3Client(mContext);
            ListObjectsRequest request = new ListObjectsRequest()
                    .withBucketName(Constants.s3Bucket)
                    .withPrefix("public/");

            ObjectListing listing = client.listObjects(request);
            List<S3ObjectSummary> summaries = listing.getObjectSummaries();

            for (S3ObjectSummary summary : summaries) {
                ContentPost tempPost = new ContentPost();
                tempPost.setCreatedAt(String.valueOf(summary.getLastModified()));
                tempPost.setFileSize(String.valueOf(summary.getSize()));
                ObjectMetadata metadata = client.getObjectMetadata(Constants.s3Bucket, summary.getKey());
                Map<String, String> map = metadata.getUserMetadata();
                try {
                    String title = map.get(Constants.metaKeyTitle);
                    String author = map.get(Constants.metaKeyAuthor);
                    String streamType = map.get(Constants.metaKeyStreamType);

                    tempPost.setTitle(title);
                    tempPost.setAuthor(author);
                    tempPost.setStreamType(streamType);

                    if (streamType.equals(Constants.metaStreamTypePicture)) {
                        tempPost.setPostBitmap(BitmapFactory.
                                decodeStream(client.getObject(Constants.s3Bucket, summary.getKey()).getObjectContent()));
                    }

                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Exception: " + e.getMessage());
                    e.printStackTrace();
                }

                postList.add(tempPost);
            }

            return postList;
        }


        @Override
        protected void onPostExecute(List<ContentPost> postList) {
            super.onPostExecute(postList);
            Log.d(TAG, "onPostExecute: Finished task.  Post list is: " + postList);
            //mPostList.addAll(postList);
            mAdapter.notifyDataSetChanged();
            relProgressBar.setVisibility(View.INVISIBLE);
            Log.d(TAG, "onPostExecute: Post List Size: " + mPostList.size());
        }
    }

    /**
     *
     * Options Menu
     *
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.goToProfile:
                Log.d(TAG, "onOptionsItemSelected: Clicked to go to profile");
                // TODO: start intent to profile activity
                Intent intent = new Intent(HomeFeed.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.goToLogout:
                Log.d(TAG, "onOptionsItemSelected: Clicked to logout");
                // TODO: logout from the session and return to sign in screen
                myDownloadTask task = new myDownloadTask();
                task.execute();
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: Somehow reached default switch...");
        }

        return true;

    }

    private File downloadPicture(String key) {
        TransferUtility transferUtility = AWSProvider.getTransferUtility(mContext);
        String path = "public/" + key;

        File file = new File("/storage/emulated/0/" + path);
        TransferObserver downloadObserver = transferUtility.download(Constants.s3Bucket, path, file);

        downloadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.d(TAG, "onStateChanged: Transfer state completed");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDoneF = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDoneF;

                Log.d(TAG, "onProgressChanged: ID: " + id + "\n" +
                        " Percent Done: " + percentDone + "\n" +
                        "Total Bytes: " + bytesTotal + "\n" +
                        "Current Bytes: " + bytesCurrent);
            }

            @Override
            public void onError(int id, Exception ex) {

                Log.d(TAG, "onError: Error during transfer: " + ex.getMessage());

                ex.printStackTrace();
            }
        });

        return file;
    }


    private void downloadContent() {

        TransferUtility transferUtility = AWSProvider.getTransferUtility(mContext);
        String path = "public/background.jpg";
        File file = new File("/storage/emulated/0/" + path);
        TransferObserver downloadObserver = transferUtility.download(Constants.s3Bucket, path, file);

        downloadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.d(TAG, "onStateChanged: Transfer state completed");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDoneF = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDoneF;

                Log.d(TAG, "onProgressChanged: ID: " + id + "\n" +
                        " Percent Done: " + percentDone + "\n" +
                        "Total Bytes: " + bytesTotal + "\n" +
                        "Current Bytes: " + bytesCurrent);
            }

            @Override
            public void onError(int id, Exception ex) {

                Log.d(TAG, "onError: Error during transfer: " + ex.getMessage());

                ex.printStackTrace();
            }
        });
    }


    private class myDownloadTask extends AsyncTask<String, String, List<S3ObjectSummary>> {

        @Override
        protected List<S3ObjectSummary> doInBackground(String... lists) {
            return listAllS3Objects();
        }

        @Override
        protected void onPostExecute(List<S3ObjectSummary> s3ObjectSummaries) {
            super.onPostExecute(s3ObjectSummaries);
            // TODO: populate the list view with the above summaries
            mSummaryList = s3ObjectSummaries;
            UpdateContentListTask task = new UpdateContentListTask();
            task.execute();
        }
    }

    private class UpdatePictureTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            for (String key : strings) {
                AmazonS3Client client = AWSProvider.getS3Client(mContext);
                S3Object object = client.getObject(Constants.s3Bucket, key);
                S3ObjectInputStream stream = object.getObjectContent();
                return BitmapFactory.decodeStream(stream);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }

    private class UpdateContentListTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            updateContentList();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateContentList() {
        if (mSummaryList.size() > 0) {
            for (S3ObjectSummary summary : mSummaryList) {

                ContentPost tempPost = new ContentPost();
                tempPost.setFileSize(String.valueOf(summary.getSize()));
                tempPost.setCreatedAt(String.valueOf(summary.getLastModified()));

                String tempKey = summary.getKey();
                ObjectMetadata metadata = AWSProvider
                        .getS3Client(mContext)
                        .getObjectMetadata(Constants.s3Bucket, tempKey);

                try {
                    Map<String, String> myMap = metadata.getUserMetadata();

                    String title = myMap.get("title");
                    String author = myMap.get("author");
                    String streamType = myMap.get("streamtype");
                    tempPost.setTitle(title);
                    tempPost.setAuthor(author);
                    tempPost.setStreamType(streamType);

                    if (streamType.equals("picture")) {
                        UpdatePictureTask task = new UpdatePictureTask();
                        task.execute(tempKey);
                    }

                } catch (Exception e) {
                    Log.d(TAG, "listAllS3Objects: Ran into exception: " + e.getMessage());
                    e.printStackTrace();
                }

                //postList.add(tempPost);
            }
        }
    }

    private List<S3ObjectSummary> listAllS3Objects() {

        AmazonS3Client client = AWSProvider.getS3Client(mContext);
        Log.d(TAG, "listAllS3Objects: Client is: " + client);

        ListObjectsRequest listObjectsRequest =
                    new ListObjectsRequest()
                            .withBucketName(Constants.s3Bucket)
                            .withPrefix("public/");



        //ObjectListing listing = AWSProvider.getS3Client(mContext).listObjects(Constants.s3Bucket);
        ObjectListing listing = AWSProvider.getS3Client(mContext).listObjects(listObjectsRequest);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

        for (S3ObjectSummary summary : summaries) {

            Log.d(TAG, "putS3DataInListview: Found Object: " + summary.getKey() +
                    " with size: " + summary.getSize() +
                    " \n\tFrom Bucket name: " + summary.getBucketName());

            String tempKey = summary.getKey();
            ObjectMetadata metadata = AWSProvider
                    .getS3Client(mContext)
                    .getObjectMetadata(Constants.s3Bucket, tempKey);

            try {
                Map<String, String> myMap = metadata.getUserMetadata();

                String title = myMap.get("title");
                String author = myMap.get("author");
                String streamType = myMap.get("streamtype");

                Log.d(TAG, "listAllS3Objects: Object has metadata title of: " + title +
                        " \n\tAuthor: " + author +
                        " \n\tStream Type: " + streamType);
            } catch (Exception e) {
                Log.d(TAG, "listAllS3Objects: Ran into exception: " + e.getMessage());
                e.printStackTrace();
            }

        }

        return summaries;
    }



    /**
     * REQUESTING PERMISSIONS (consider putting that in its own class especially if more permissions will be needed)
     */

    private void requestWritePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            downloadContent();
        }
    }
}
