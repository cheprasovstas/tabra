package com.showcase.tabra;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.showcase.tabra.ui.login.LoginActivity;
import com.showcase.tabra.ui.product.ProductListActivity;
import com.showcase.tabra.utils.Util;


public class MainEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_empty);

        Intent activityIntent;

        // go straight to main if a token is stored
        if (Util.getToken(this) != null) {
            activityIntent = new Intent(this, ProductListActivity.class);
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }

        startActivity(activityIntent);
        finish();


//        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
//        if (pref.getBoolean(LOGIN_KEY, false)) {
//            //has login
//            startActivity(new Intent(this, MainActivity.class));
//            //must finish this activity (the login activity will not be shown when click back in main activity)
//            finish();
//        }
//        else {
//            // Mark login
//            pref.edit().putBoolean(LOGIN_KEY, true).apply();
//
//            // Do something
//        }


    }
}