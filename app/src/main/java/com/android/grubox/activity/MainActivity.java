package com.android.grubox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.grubox.R;
import com.android.grubox.connectionutils.Connection;
import com.android.grubox.connectionutils.URL1;
import com.android.grubox.databaseutils.VendingDatabase;
import com.android.grubox.models.ProductResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    List<ProductResponse> productResponses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        productResponses=new ArrayList<>();
        new ProductInfo(URL1.getProducturl(),new JSONObject()).execute();

    }

    public class ProductInfo extends AsyncTask<Void,Void,String>
    {
        String url;
        JSONObject jsonObject;
        public ProductInfo(String url, JSONObject jsonObject) {
            this.url=url;
            this.jsonObject=jsonObject;
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                JSONArray topArray=new JSONArray(s);
                for(int i=0;i<topArray.length();i++)
                {
                    ProductResponse productResponse=new ProductResponse();
                    JSONObject topObject=topArray.getJSONObject(i);
                    productResponse.setRow(topObject.getInt("Row"));
                    productResponse.setColumn(topObject.getInt("Column"));
                    productResponse.setQuantity(topObject.getInt("QtyafterFilling"));
                    JSONObject Stockid=topObject.getJSONObject("StockID");
                    JSONObject Pid=Stockid.getJSONObject("Pid");
//                    if(Pid.getString("img_Front") != null) {

                    productResponse.getandsetBitmapFromURL(getBaseContext(), Pid.getString("img_Front"));
//                    productResponse.getandsetBitmapFromURL(getBaseContext(), null);
//                    productResponse.getandsetBitmapFromURL(getBaseContext(), "http://www.theartstory.org/images20/works/munch_edward_3.jpg");
//                    }
                    productResponse.setId(Stockid.getInt("id"));
                    productResponse.setB_name(Pid.getString("Bname"));
                    productResponse.setC_name(Pid.getString("Cname"));
                    productResponse.setF_name(Pid.getString("Fname"));
                    productResponse.setMrp(Pid.getInt("MRP"));
                    productResponse.setCatTag(Pid.getString("CatTag"));
                    productResponse.setPercepTag(Pid.getString("PercepTag"));
                    productResponses.add(productResponse);


                }

            new UpdateDatabase().execute();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection connection=new Connection(url,jsonObject,MainActivity.this);
            return connection.connectiontask();
        }

    }

    public class UpdateDatabase extends AsyncTask<Void,Void,Void>
    {



        @Override
        protected Void doInBackground(Void... voids) {
            VendingDatabase vendingDatabase=new VendingDatabase(MainActivity.this);
            try {
                vendingDatabase.open();
                vendingDatabase.createEntryforProduct(productResponses);
                vendingDatabase.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent=new Intent(MainActivity.this,ProductListing.class);
            startActivity(intent);

        }
    }
}
