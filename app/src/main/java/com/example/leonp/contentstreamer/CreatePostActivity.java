package com.example.leonp.contentstreamer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";

    // constants
    private final static int MEMORY_PICK_REQUEST_CODE = 1;

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
    private Observable<String> mUploadObservable = Observable.create(temp -> {
        Log.d(TAG, "Post title: " + mPostTitle + "\nPost Extension: " + mImageExtension);
        AmazonS3Client client = AWSProvider.getS3Client(mContext);
        //PutObjectRequest request = new PutObjectRequest();
        client.putObject(Constants.s3Bucket + "/public",
                mPostTitle + "." + "jpg",
                mInputStream,
                mObjectMetadata
        );}
    );

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

        ivContentThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked to add picture");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, MEMORY_PICK_REQUEST_CODE);
            }
        });

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Log.d(TAG, "onClick: Clicked to submit post");
                // first we have to make sure all necessary fields are filled out

                mPostTitle = etInputTitle.getText().toString();
                mPostDescription = etDescription.getText().toString();
                if (mPostTitle.equals("")) {
                    Toast.makeText(mContext, "Enter a Title", Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                    return;
                }

                if (mPostDescription.equals("")) {
                    Toast.makeText(mContext, "Enter a Description", Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                    return;
                }

                if (mPostImageUri == null) {
                    Toast.makeText(mContext, "Add an image", Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                    return;
                }

                // TODO: make sure your post has a picture and add it to S3 to get the pic link
                //AmazonS3Client client = AWSProvider.getS3Client(mContext);
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
                    Disposable subscribe = mUploadObservable.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(temp -> {
                                v.setEnabled(true);
                                rel1.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "onClick: Finished subscription");
                            });
                    disposable.add(subscribe);
                }catch (FileNotFoundException e) {
                    Log.e(TAG, "onClick: FileNotFoundException: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "onClick: IOException: " + e.getMessage());
                    e.printStackTrace();
                }catch (Exception e) {
                    Log.e(TAG, "onClick: Exception: " + e.getMessage());
                    e.printStackTrace();
                }
                //v.setEnabled(true);
            }
        });

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
            Glide.with(this).load(mPostImageUri).into(ivContentThumbnail);
        } else {
            Toast.makeText(this, "Failed to add picture", Toast.LENGTH_SHORT).show();
        }
    }
}
