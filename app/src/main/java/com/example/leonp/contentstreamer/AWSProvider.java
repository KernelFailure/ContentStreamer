package com.example.leonp.contentstreamer;

import android.content.Context;
import android.util.Log;
import android.webkit.DownloadListener;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.List;

public class AWSProvider {

    private static final String TAG = "AWSProvider";

    // vars
    private Context context;


    private AWSProvider(Context context) {
        this.context = context;
        AWSConfiguration configuration = new AWSConfiguration(context);

        //AmazonS3Client client = null;
    }

    private static CognitoCachingCredentialsProvider getCognitoCredentials(Context context) {

        Log.d(TAG, "getCognitoCredentials: Start getting Cognito Credentials");

        //AWSMobileClient.getInstance().initialize(context).execute();

        CognitoCachingCredentialsProvider credProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                Constants.cognitoPoolId,
                Regions.fromName(Constants.cognitoRegion)
        );

        Log.d(TAG, "getCognitoCredentials: Result is: " + credProvider);

        return credProvider;
    }

    public static AmazonS3Client getS3Client(Context context) {

        Log.d(TAG, "getS3Client: Start Get S3 client");

        AmazonS3Client client = new AmazonS3Client(getCognitoCredentials(context));

        Log.d(TAG, "getS3Client: Result is: " + client);

        return client;

    }

    public static TransferUtility getTransferUtility(Context context) {

        Log.d(TAG, "getTransferUtility: Start get Transfer utility");

        AWSConfiguration config = AWSMobileClient.getInstance().getConfiguration();

        TransferUtility transferUtility = TransferUtility.builder()
                .context(context.getApplicationContext())
                .awsConfiguration(config)
                .s3Client(getS3Client(context))
                .defaultBucket(Constants.s3Bucket)
                .build();

        Log.d(TAG, "getTransferUtility: Result is: " + transferUtility);

        return transferUtility;
    }


    public static IdentityManager getIdentityManager(Context context) {

        IdentityManager identityManager = new IdentityManager(context, getAWSConfiguration(context));
        IdentityManager.setDefaultIdentityManager(identityManager);
        identityManager.addSignInProvider(CognitoUserPoolsSignInProvider.class);

        return IdentityManager.getDefaultIdentityManager();

    }

    private static AWSConfiguration getAWSConfiguration(Context context) {
        return new AWSConfiguration(context);
    }

    public static DynamoDBMapper getDynamoDBMapper(Context context) {

        AWSCredentialsProvider provider = IdentityManager.getDefaultIdentityManager().getCredentialsProvider();
        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(provider);
        DynamoDBMapper dbMapper = DynamoDBMapper.builder()
                .awsConfiguration(getAWSConfiguration(context))
                .dynamoDBClient(dbClient)
                .build();

        return dbMapper;

    }

}
