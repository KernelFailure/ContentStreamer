package com.example.leonp.contentstreamer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.leonp.contentstreamer.models.ContentPost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFeed extends AppCompatActivity {

    private static final String TAG = "HomeFeed";

    // constants
    private static final int DOWLOAD_REQUEST_CODE = 1;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 2;

    // widgets
    private ListView listView;
    private Button btnGetContent;
    private Button btnDownloadFromS3;

    // vars
    private ArrayList<ContentPost> postList;
    private ContentPostListAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        listView = (ListView) findViewById(R.id.listView);
        btnGetContent = (Button) findViewById(R.id.btnGetContent);
        btnDownloadFromS3 = (Button) findViewById(R.id.btnDownloadFromS3);

        mContext = this;

        View emptyView = findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        postList = new ArrayList<>();
//        mAdapter = new ContentPostListAdapter(this, R.layout.layout_postlistitem, postList);
//        listView.setAdapter(mAdapter);

        initDownloadButton();

        btnGetContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked get content button");

                // TODO: get some content to display here
                ContentPost samplePost1 = new ContentPost("1", "Blade Runner",
                        "2017", "2 Hours");
                ContentPost samplePost2 = new ContentPost("2", "Requiem For a Dream",
                        "2000", "1 Hour 42 min");
                ContentPost samplePost3 = new ContentPost("3", "Sicario",
                        "2015", "121 minutes");
                ContentPost samplePost4 = new ContentPost("4", "Harry Potter Series",
                        "1997", "8 movies");
                ContentPost samplePost5 = new ContentPost("5", "Black Hawk Down",
                        "2001", "2 Hours");
                ContentPost samplePost6 = new ContentPost("6", "Mr. Robot",
                        "2014", "3 Seasons");
                ContentPost samplePost7 = new ContentPost("7", "James Bond Series",
                        "1956", "20 movies");
                ContentPost samplePost8 = new ContentPost("8", "Ghostbusters",
                        "1984", "1 Hour 47 minutes");
                ContentPost samplePost9 = new ContentPost("9", "The Dark Knight",
                        "2008", "2 Hours 32 minutes");

                postList.add(samplePost1);
                postList.add(samplePost2);
                postList.add(samplePost3);
                postList.add(samplePost4);
                postList.add(samplePost5);
                postList.add(samplePost6);
                postList.add(samplePost7);
                postList.add(samplePost8);
                postList.add(samplePost9);

                mAdapter = new ContentPostListAdapter(mContext, R.layout.layout_postlistitem, postList);
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked on: " + postList.get(position).getPostTitle());
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

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
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: Somehow reached default switch...");
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DOWLOAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //beginDownload();
            }
        }
    }

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

    private void downloadContent() {

        TransferUtility transferUtility = AWSProvider.getTransferUtility(mContext);

        String path = "public/beer.png";
        //String path = "public/example-image.png";
        //File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + path);
        File file = new File("/storage/emulated/0/headshot.png");
        Log.d(TAG, "beginDownload: Downloading with key: " + path +
                " and file path: " + file.getAbsolutePath());
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

        // second Transfer technique
        List<TransferObserver> observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
        TransferListener listener = new ContentDownloadListener();
        int counter = 0;
        for (TransferObserver observer : observers) {
            counter++;
            Log.d(TAG, "onClick: Observer count: " + counter);


            // Sets listeners to in progress transfers
            if (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState())) {
                observer.setTransferListener(listener);
            }

        }

    }

    private void initDownloadButton() {
        btnDownloadFromS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Download from S3 Button");

                //startActivityForResult();
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestWritePermission();
                } else {

                    downloadContent();

                }
            }
        });
    }

    private class ContentDownloadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onError(int id, Exception ex) {

        }
    }
}
