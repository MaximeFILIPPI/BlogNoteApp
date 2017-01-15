package com.maxfilippi.myblognote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Max on 1/12/17.
 *
 * Native launch screen class for android
 *
 */

public class LaunchActivity extends SuperActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intentMapsActivity = new Intent(LaunchActivity.this, HomeListActivity.class);
        startActivity(intentMapsActivity);
        finish();
    }


}
