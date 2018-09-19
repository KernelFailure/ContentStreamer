package com.example.leonp.contentstreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.DefaultSignInResultHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.IdentityProvider;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInActivity;

public class AuthenticatorActivity extends Activity {

    private static final String TAG = "AuthenticatorActivity";

    // vars
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        mContext = this;

        final IdentityManager identityManager = AWSProvider.getIdentityManager(mContext);
        Log.d(TAG, "onCreate: Attempting to log in with ID manager: " + identityManager);
        identityManager.login(mContext, new DefaultSignInResultHandler() {



            @Override
            public void onSuccess(Activity callingActivity, IdentityProvider provider) {
                Toast.makeText(callingActivity,
                        "Logged in with: " + identityManager.getCachedUserID(),
                        Toast.LENGTH_SHORT).show();
                // to to home feed after successful login
                Intent intent = new Intent(callingActivity, HomeFeed.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                callingActivity.startActivity(intent);
                callingActivity.finish();

            }

            @Override
            public boolean onCancel(Activity callingActivity) {
                Toast.makeText(callingActivity, "Login Failed", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        AuthUIConfiguration configuration = new AuthUIConfiguration.Builder()
                .userPools(true)
                .logoResId(R.drawable.networking) // Change the logo
                //.backgroundColor((int) Long.parseLong("324A5E", 16))
                .build();
        SignInActivity.startSignInActivity(mContext, configuration);
        AuthenticatorActivity.this.finish();
    }
}
