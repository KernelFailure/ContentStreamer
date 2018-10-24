package com.example.leonp.contentstreamer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bumptech.glide.Glide;
import com.example.leonp.contentstreamer.models.Posts2DO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";

    // constants
    private final static int MEMORY_PICK_REQUEST_CODE = 1;
    private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;

    // widgets
    private EditText etInputTitle;
    private ImageView ivContentThumbnail;
    private EditText etDescription;
    private Button btnSubmitPost;
    private RelativeLayout rel1;

    // vars
    private Context mContext;
    private Uri mPostImageUri;
    private String mImagePath;
    private String mImageExtension;
    private String mPostTitle;
    private String mPostDescription;
    private InputStream mInputStream;
    private ObjectMetadata mObjectMetadata;
    private Uri fileUri;

    // Observable
    private Observable<String> mUploadToS3Observable = Observable.create(temp -> {
        temp.onNext("fff");
        postToS3();
        }
    );

    private Observable<String> mUploadToDynamoObservable = Observable.create(post -> {
        post.onNext("fff");
        postToDynamoDB();
    });




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        mContext = this;

        etInputTitle = (EditText) findViewById(R.id.etInputTitle);
        ivContentThumbnail = (ImageView) findViewById(R.id.ivContentThumbnail);
        etDescription = (EditText) findViewById(R.id.etDescription);
        btnSubmitPost = (Button) findViewById(R.id.btnSubmitPost);
        rel1 = (RelativeLayout) findViewById(R.id.rel1);
        
        // Set progress bar invisible 
        rel1.setVisibility(View.INVISIBLE);

        setThumbnailClickListener();
        setSubmitButtonClickListener();

    }

    private void setThumbnailClickListener() {
        ivContentThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked to add picture");

                if (!checkForReadStoragePermission()) {
                    Log.d(TAG, "onClick: Read permissions weren't granted.  Requesting now");
                    askForReadStoragePermission();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, MEMORY_PICK_REQUEST_CODE);
            }
        });
    }


    private void setSubmitButtonClickListener() {

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                Log.d(TAG, "onClick: Clicked to submit post");

                mPostTitle = etInputTitle.getText().toString();
                mPostDescription = etDescription.getText().toString();

                if (!allFieldsFilledOut(mPostTitle, mPostDescription)) {
                    v.setEnabled(true);
                    return;
                }

                try {
                    mInputStream = getContentResolver().openInputStream(mPostImageUri);
                    mImagePath = mPostImageUri.getPath();
                    mImageExtension = getContentResolver().getType(mPostImageUri);
                    //mImageExtension = mImagePath.substring(mImagePath.lastIndexOf(".") + 1);
                    Log.d(TAG, "onClick: Path: " + mImagePath + "\nExtension: " + mImageExtension);
                    mObjectMetadata = new ObjectMetadata();
                    mObjectMetadata.setContentLength(mInputStream.available());
//                    client.putObject(Constants.s3Bucket, postTitle, inputStream, metadata);

                    // show progress bar
                    rel1.setVisibility(View.VISIBLE);

                    CompositeDisposable disposable = new CompositeDisposable();



                    Disposable subscribe1 = mUploadToDynamoObservable
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.single())
                            .subscribe();
                    disposable.add(subscribe1);

                    Disposable subscribe = mUploadToS3Observable.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.single())
                            .subscribe(temp -> {
                                cleanupUI(v);
                            });
                    disposable.add(subscribe);


                }catch (FileNotFoundException e) {
                    Log.e(TAG, "onClick: FileNotFoundException: " + e.getMessage());
                    e.printStackTrace();
                    v.setEnabled(true);
                } catch (IOException e) {
                    Log.e(TAG, "onClick: IOException: " + e.getMessage());
                    e.printStackTrace();
                    v.setEnabled(true);
                }catch (Exception e) {
                    Log.e(TAG, "onClick: Exception: " + e.getMessage());
                    e.printStackTrace();
                    v.setEnabled(true);
                }
            }
        });

    }

    private void cleanupUI(View view) {
        Log.d(TAG, "cleanupUI: Cleaning up UI");
        try {
            TimeUnit.SECONDS.sleep(2);
            view.setEnabled(true);
            rel1.setVisibility(View.INVISIBLE);
            etInputTitle.setText("");
            etDescription.setText("");
            ivContentThumbnail.setImageResource(R.drawable.ic_add);
            Toast.makeText(mContext, "Success!!", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Log.e(TAG, "cleanupUI: InterruptedException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String postToDynamoDB() {
        try {
            Log.d(TAG, "postToDynamoDB: Current Thread: " + Thread.currentThread().getName());
            Log.d(TAG, "postToDynamoDB: Is signed in: " + AWSProvider.getIdentityManager(mContext).isUserSignedIn());
            String key = mPostTitle + "." + "jpg";
            DynamoDBMapper mapper = AWSProvider.getDynamoDBMapper(mContext);
            Posts2DO post = new Posts2DO();
            IdentityHandler handler = new IdentityHandler() {
                String id = null;
                @Override
                public void onIdentityId(String identityId) {
                    id = identityId;
                }

                @Override
                public void handleError(Exception exception) {
                    id = "temp";
                }

            };
            Boolean isSignedIn = AWSProvider.getIdentityManager(mContext).isUserSignedIn();
            Log.d(TAG, "postToDynamoDB: Signed in is: " + isSignedIn);
            post.setUserId("flake");
            post.setTitle(mPostTitle);
            post.setAuthor("Spider-Man");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            post.setCreatedAt(timeStamp);
            post.setImagePath(key);
//        post.setImagePath("fakePath");
            post.setPostId("7");
            //hello
            post.setStreamType("picture");
            Log.d(TAG, "Final Post: " + post);
            Log.d(TAG, "postToS3: DynamoDB mapper: " + mapper);
            Log.d(TAG, "postToS3: UserID: ");
            mapper.save(post);
        } catch (Exception e) {
            Log.e(TAG, "postToDynamoDB: Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return "another string";
    }

    private String postToS3() {
        Log.d(TAG, "postToDynamoDB: Current Thread: " + Thread.currentThread().getName());
        Log.d(TAG, "postToS3: Is signed in: " + AWSProvider.getIdentityManager(mContext).isUserSignedIn());
        Log.d(TAG, "Post title: " + mPostTitle + "\nPost Extension: " + mImageExtension);
        AmazonS3Client client = AWSProvider.getS3Client(mContext);
        //PutObjectRequest request = new PutObjectRequest();
        String key = mPostTitle + "." + "jpg";
        String bucketName = Constants.s3Bucket + "/public";
        client.putObject(bucketName,
                key,
                mInputStream,
                mObjectMetadata);

        return "String";
    }

    private boolean allFieldsFilledOut(String title, String description) {
        if (mPostTitle.equals("") || title.equals("")) {
            Toast.makeText(mContext, "Enter a Title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mPostDescription.equals("") || description.equals("")) {
            Toast.makeText(mContext, "Enter a Description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mPostImageUri == null) {
            Toast.makeText(mContext, "Add an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkForReadStoragePermission() {
        if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkForReadStoragePermission: Permissions for read storage are already granted");
            return true;
        } else {
            Log.d(TAG, "checkForReadStoragePermission: Permissions for read storage were NOT granted");
            return false;
        }
    }

    private void askForReadStoragePermission() {
        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: Data: " + data.toString());
        if (requestCode == MEMORY_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Log.d(TAG, "onActivityResult: Got Bitmap: " + bitmap.toString());
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
            mPostImageUri = data.getData();
            Log.d(TAG, "onActivityResult: Post Image URI: " + mPostImageUri.toString());
            Glide.with(this).load(mPostImageUri).into(ivContentThumbnail);
        } else {
            Toast.makeText(this, "Failed to add picture", Toast.LENGTH_SHORT).show();
        }
    }
}
