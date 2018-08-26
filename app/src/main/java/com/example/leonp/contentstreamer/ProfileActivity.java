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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.bumptech.glide.Glide;

public class ProfileActivity extends Activity {

    private static final String TAG = "ProfileActivity";

    // widgets
    private TextView tvPlaceholder;
    private Button btnGetPicture;
    private ImageView ivProfilePicture;
    private RelativeLayout relProgressBar;

    // vars
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = this;

        tvPlaceholder = (TextView) findViewById(R.id.tvPlaceholder);
        btnGetPicture = (Button) findViewById(R.id.btnGetPicture);
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);
        relProgressBar = (RelativeLayout) findViewById(R.id.relProgressBar);

        relProgressBar.setVisibility(View.INVISIBLE);

        initGetPictureButton();
    }

    private void initGetPictureButton() {

        btnGetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "initGetPictureButton: Clicked get picture button");

                GetPictureTask task = new GetPictureTask();
                task.execute();
            }
        });
    }

    private class GetPictureTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvPlaceholder.setVisibility(View.INVISIBLE);
            relProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {


            AmazonS3Client client = AWSProvider.getS3Client(mContext);
            S3Object object = client.getObject(Constants.s3Bucket, "public/hikers_watch_icon.jpg");
            S3ObjectInputStream stream = object.getObjectContent();

            return BitmapFactory.decodeStream(stream);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            relProgressBar.setVisibility(View.INVISIBLE);
            if (bitmap != null) {
                ivProfilePicture.setImageBitmap(bitmap);
                return;
            }
            tvPlaceholder.setVisibility(View.VISIBLE);
            Log.d(TAG, "onPostExecute: Bitmap was null");
        }
    }
}
