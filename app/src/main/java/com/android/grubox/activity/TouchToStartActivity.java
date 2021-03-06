package com.android.grubox.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.grubox.R;
import com.android.grubox.adapter.TouchToStartGrid;
import com.android.grubox.fragments.CarouselMain;
import com.android.grubox.fragments.CarouselOffers;

/**
 * Created by Utkarsh Bindal on 12/05/2017.
 */
public class TouchToStartActivity extends AppCompatActivity {
    Button button_touchToStart;
    Button button_show_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_touch_to_start);

        //To get the dimensions of the screen
        Configuration config = this.getResources().getConfiguration();
        int width = config.screenWidthDp;
        int height = config.screenHeightDp;

        Log.v("Dimensions: w:"+ width +"; h: " +height, "dim");

        //Adding the carousel for ads
        if (findViewById(R.id.MainCarousel) != null) {

            if (savedInstanceState != null) {
                return;
            }

            Fragment fragment;
            fragment=new CarouselMain();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.MainCarousel, fragment).commit();
        }

        //Temporary data for hashtag buttons
        final String[] web = {
                "#masala", "#kurkure", "#cocacola", "#healthy", "#snacks", "#mango", "#chips", "#chocolate", "#fanta"

        };

        //Adding the adapter for the gridView
        GridView first_grid;
        TouchToStartGrid adapter = new TouchToStartGrid(TouchToStartActivity.this, web);
        first_grid=(GridView)findViewById(R.id.grid_for_buttons);
        first_grid.setAdapter(adapter);
//        first_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Toast.makeText(TouchToStartActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
//
//            }
//        });

        //To start the menu activity
        //This has to be inside an onClickListener
        //Both buttons perform the same function
        button_touchToStart = (Button) findViewById(R.id.button_start);
        button_show_all = (Button) findViewById(R.id.button_show_all);

        button_touchToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TouchToStartActivity.this,ProductListing.class);
                startActivity(intent);
            }
        });

        button_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TouchToStartActivity.this,ProductListing.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        button_touchToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TouchToStartActivity.this,ProductListing.class);
                startActivity(intent);
            }
        });

        button_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TouchToStartActivity.this,ProductListing.class);
                startActivity(intent);
            }
        });
    }
}
