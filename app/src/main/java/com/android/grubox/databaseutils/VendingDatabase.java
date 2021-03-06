 package com.android.grubox.databaseutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.grubox.models.ProductResponse;


import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

 /**
 * Created by my hp on 1/16/2016.
 */
public class VendingDatabase {

    public static final String KEY_ROWID="_id";
    public static final String KEY_SUBID="product_id";
    public static final String KEY_SID="stock_id";
    public static final String KEY_NAME="product_name";
    public static final String KEY_UNITS="product_units";
    public static final String KEY_PRICE="price";
     public static final String KEY_CARTIMAGE="image";

     public static final String KEY_PID="product_id";
     //public static final String KEY_SID="stock_id";
     public static final String KEY_IMAGE="image";
     public static final String KEY_BNAME="b_name";
     public static final String KEY_FNAME="f_name";
     public static final String KEY_CNAME="c_name";
     public static final String KEY_MRP="mrp";
     public static final String KEY_CATTAG="cattag";
     public static final String KEY_PERCEPTAG="perceptag";
     public static final String KEY_CATID="category_id";
     public static final String KEY_ROW="row";
     public static final String KEY_COL="col";

    private static final String DATABASE_NAME="Gruboxdb";
    private static final String DATABASE_TABLE="carttable";
    private static final String DATABASE_TABLEPRODUCT="product_table";
    private static final int DATABASE_VERSION=1;

    private Dbhelper ourhelper;
    private final Context ourcontext;
    private SQLiteDatabase ourdatabase;
    public  DbBitmapUtility DbBitmapUtilityObj = new DbBitmapUtility();

    public void createentryforcart(ProductResponse productResponse) {

        String[] columns=new String[] {KEY_ROWID,KEY_SUBID};

        //Removed functionality for quantity upgrade
//         

        ContentValues cv=new ContentValues();
        cv.put(KEY_SUBID,productResponse.getPId());
        cv.put(KEY_NAME,productResponse.getB_name()+" "+productResponse.getF_name());
        cv.put(KEY_PRICE,productResponse.getMrp());
        cv.put(KEY_UNITS,productResponse.getQuantity_cart());
        cv.put(KEY_SID, productResponse.getId());
        cv.put(KEY_IMAGE, DbBitmapUtilityObj.getBytes(productResponse.getImage()));
        ourdatabase.insert(DATABASE_TABLE,null,cv);

    }

     public int getRowandCol(int product_id)
     {

         String[] columns=new String[] {KEY_ROWID,KEY_COL,KEY_ROW,KEY_UNITS};

         Cursor c=ourdatabase.query(DATABASE_TABLEPRODUCT, columns,KEY_PID+"="+product_id, null, null, null, null);
         int row=c.getColumnIndex(KEY_ROW);
         int col=c.getColumnIndex(KEY_COL);
         int quantity = c.getColumnIndex(KEY_UNITS);
         int max_quantity =0;
         int max_row=0;
         int max_col=0;

         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
             if(max_quantity < c.getInt(quantity) ) {
                 max_quantity = c.getInt(quantity);
                 max_row = c.getInt(row);
                 max_col= c.getInt(col);
             }
         }

         return max_row*10 + max_col;
     }

    public List<ProductResponse> getCartData() {
        String[] columns=new String[] {KEY_ROWID,KEY_NAME,KEY_SUBID,KEY_PRICE,KEY_UNITS,KEY_CARTIMAGE,KEY_SID};

        Cursor c=ourdatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        int sub_id=c.getColumnIndex(KEY_SUBID);
        int sub_name=c.getColumnIndex(KEY_NAME);
        int price=c.getColumnIndex(KEY_PRICE);
        int row_id=c.getColumnIndex(KEY_ROWID);
        int units=c.getColumnIndex(KEY_UNITS);
        int image = c.getColumnIndex(KEY_CARTIMAGE);
        int stock_id = c.getColumnIndex(KEY_SID);


        List<ProductResponse> databaseModels=new ArrayList<>();
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){

            ProductResponse databaseModel=new ProductResponse();
            databaseModel.setPId(c.getInt(sub_id));
            databaseModel.setId(c.getInt(stock_id));
            databaseModel.setF_name(c.getString(sub_name));
            databaseModel.setMrp(c.getInt(price));
            databaseModel.setQuantity_cart(c.getInt(units));
            databaseModel.setRow_id(c.getInt(row_id));
            databaseModel.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
            databaseModels.add(databaseModel);

        }

        c.close();
        return databaseModels;
    }



    public void updateEntryQuantity(int l, int quantity) {

        ContentValues cv=new ContentValues();
        cv.put(KEY_UNITS,quantity);

        ourdatabase.update(DATABASE_TABLE,cv,KEY_ROWID+"="+l,null);
    }

    public void deleteEntryforcart(int l) {
        ourdatabase.delete(DATABASE_TABLE,KEY_ROWID+"="+l,null);
    }

     public void createEntryforProduct(List<ProductResponse> productResponses) {

         ourdatabase.delete(DATABASE_TABLEPRODUCT,null,null);

         for(ProductResponse productResponse:productResponses)
         {
            // List<Integer> categories=Utility.getCategory(productResponse.getCatTag(),productResponse.getPercepTag());

//             for(Integer integer:categories) {

                 ContentValues cv = new ContentValues();
                 cv.put(KEY_PID, productResponse.getPId());
                 cv.put(KEY_SID, productResponse.getId());
                 cv.put(KEY_BNAME, productResponse.getB_name());
                 cv.put(KEY_CNAME, productResponse.getC_name());
                 cv.put(KEY_FNAME, productResponse.getF_name());
                 cv.put(KEY_IMAGE,  DbBitmapUtilityObj.getBytes(productResponse.getImage()));
                 cv.put(KEY_CATTAG, productResponse.getCatTag());
                 cv.put(KEY_PERCEPTAG, productResponse.getPercepTag());
                 cv.put(KEY_UNITS, productResponse.getQuantity());
                 cv.put(KEY_ROW, productResponse.getRow());
                 cv.put(KEY_COL, productResponse.getColumn());
                 cv.put(KEY_CATID,0);
                 cv.put(KEY_MRP, productResponse.getMrp());



                 ourdatabase.insert(DATABASE_TABLEPRODUCT, null, cv);

//             }
         }

     }

     public List<ProductResponse> getProductsList()
     {
         String[] columns=new String[] {KEY_ROWID,KEY_CATID,KEY_SID,KEY_COL,KEY_ROW,KEY_MRP,KEY_PERCEPTAG,KEY_BNAME,KEY_CATTAG,KEY_CNAME,KEY_FNAME,KEY_IMAGE,KEY_PID,KEY_UNITS};

         Cursor c=ourdatabase.query(DATABASE_TABLEPRODUCT, columns, null, null, null, null, null);

         int pid=c.getColumnIndex(KEY_PID);
         int id=c.getColumnIndex(KEY_SID);
         int row_id=c.getColumnIndex(KEY_ROWID);
         int image=c.getColumnIndex(KEY_IMAGE);
         int b_name=c.getColumnIndex(KEY_BNAME);
         int f_name=c.getColumnIndex(KEY_FNAME);
         int c_name=c.getColumnIndex(KEY_CNAME);
         int mrp=c.getColumnIndex(KEY_MRP);
         int catTag=c.getColumnIndex(KEY_CATTAG);
         int percepTag=c.getColumnIndex(KEY_PERCEPTAG);
         int quantity=c.getColumnIndex(KEY_UNITS);
         int row=c.getColumnIndex(KEY_ROW);
         int column=c.getColumnIndex(KEY_PID);

         List<ProductResponse> productResponses=new ArrayList<>();

         int number =1;
         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
//             Log.d("number", Integer.toString(number++));
//             Log.d("bytes: ", new String(c.getBlob(image)));
             ProductResponse productResponse=new ProductResponse();
             productResponse.setCatTag(c.getString(catTag));
             productResponse.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
             productResponse.setQuantity(c.getInt(quantity));
             productResponse.setColumn(c.getInt(column));
             productResponse.setMrp(c.getInt(mrp));
             productResponse.setB_name(c.getString(b_name));
             productResponse.setC_name(c.getString(c_name));
             productResponse.setF_name(c.getString(f_name));
             productResponse.setPId(c.getInt(pid));
             productResponse.setId(c.getInt(id));
             productResponse.setPercepTag(c.getString(percepTag));
             productResponse.setRow(c.getInt(row));
             productResponse.setRow_id(c.getInt(row_id));


             productResponses.add(productResponse);
         }
         c.close();
         return productResponses;

     }


     public List<ProductResponse> getProductsList(int cat_id)
     {
         String[] columns=new String[] {KEY_ROWID,KEY_CATID,KEY_SID,KEY_COL,KEY_ROW,KEY_MRP,KEY_PERCEPTAG,KEY_BNAME,KEY_CATTAG,KEY_CNAME,KEY_FNAME,KEY_IMAGE,KEY_PID,KEY_UNITS};

        // Cursor c=ourdatabase.query(DATABASE_TABLEPRODUCT, columns, null, null, null, null, null);
         Cursor c=ourdatabase.query(DATABASE_TABLEPRODUCT,columns,KEY_CATID+"="+cat_id,null,null,null,null,null);

         int pid=c.getColumnIndex(KEY_PID);
         int id=c.getColumnIndex(KEY_SID);
         int row_id=c.getColumnIndex(KEY_ROWID);
         int image=c.getColumnIndex(KEY_IMAGE);
         int b_name=c.getColumnIndex(KEY_BNAME);
         int f_name=c.getColumnIndex(KEY_FNAME);
         int c_name=c.getColumnIndex(KEY_CNAME);
         int mrp=c.getColumnIndex(KEY_MRP);
         int catTag=c.getColumnIndex(KEY_CATTAG);
         int percepTag=c.getColumnIndex(KEY_PERCEPTAG);
         int quantity=c.getColumnIndex(KEY_UNITS);
         int row=c.getColumnIndex(KEY_ROW);
         int column=c.getColumnIndex(KEY_PID);

         List<ProductResponse> productResponses=new ArrayList<>();

         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
             ProductResponse productResponse=new ProductResponse();
             productResponse.setCatTag(c.getString(catTag));
             productResponse.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
             productResponse.setQuantity(c.getInt(quantity));
             productResponse.setColumn(c.getInt(column));
             productResponse.setMrp(c.getInt(mrp));
             productResponse.setB_name(c.getString(b_name));
             productResponse.setC_name(c.getString(c_name));
             productResponse.setF_name(c.getString(f_name));
             productResponse.setId(c.getInt(id));
             productResponse.setPId(c.getInt(pid));
             productResponse.setPercepTag(c.getString(percepTag));
             productResponse.setRow(c.getInt(row));
             productResponse.setRow_id(c.getInt(row_id));


             productResponses.add(productResponse);
         }
         c.close();
         return productResponses;

     }

     public int getTotalSum() {

         Cursor cur = ourdatabase.rawQuery("SELECT SUM("+KEY_PRICE+"*"+KEY_UNITS+") AS total_sum FROM "+DATABASE_TABLE, null);

         int total_sum=cur.getColumnIndex("total_sum");
         if(cur.moveToFirst())
         {
             return cur.getInt(total_sum);
         }
//         String[] columns=new String[] {KEY_ROWID,KEY_SUBID};
//
//         Cursor c=ourdatabase.query(DATABASE_TABLE, columns,KEY_SUBID+"="+productResponse.getId(), null, null, null, null);
//         int sub_id=c.getColumnIndex(KEY_SUBID);
//         int row_id=c.getColumnIndex(KEY_ROWID);
//         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
//         {
//             updateEntryQuantity(c.getInt(row_id),productResponse.getQuantity_cart()+1);
//             return;
//         }
        return 0;

     }

     public List<ProductResponse> getTagBrand(String name) {

         Cursor c = ourdatabase.rawQuery("SELECT * FROM "+ DATABASE_TABLEPRODUCT + " WHERE "+ KEY_BNAME +" LIKE  \'%" +name+"%\'", null);
         int pid=c.getColumnIndex(KEY_PID);
         int id=c.getColumnIndex(KEY_SID);
         int row_id=c.getColumnIndex(KEY_ROWID);
         int image=c.getColumnIndex(KEY_IMAGE);
         int b_name=c.getColumnIndex(KEY_BNAME);
         int f_name=c.getColumnIndex(KEY_FNAME);
         int c_name=c.getColumnIndex(KEY_CNAME);
         int mrp=c.getColumnIndex(KEY_MRP);
         int catTag=c.getColumnIndex(KEY_CATTAG);
         int percepTag=c.getColumnIndex(KEY_PERCEPTAG);
         int quantity=c.getColumnIndex(KEY_UNITS);
         int row=c.getColumnIndex(KEY_ROW);
         int column=c.getColumnIndex(KEY_PID);

         List<ProductResponse> productResponses=new ArrayList<>();

         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
             ProductResponse productResponse=new ProductResponse();
             productResponse.setCatTag(c.getString(catTag));
             productResponse.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
             productResponse.setQuantity(c.getInt(quantity));
             productResponse.setColumn(c.getInt(column));
             productResponse.setMrp(c.getInt(mrp));
             productResponse.setB_name(c.getString(b_name));
             productResponse.setC_name(c.getString(c_name));
             productResponse.setF_name(c.getString(f_name));
             productResponse.setPId(c.getInt(pid));
             productResponse.setId(c.getInt(id));
             productResponse.setPercepTag(c.getString(percepTag));
             productResponse.setRow(c.getInt(row));
             productResponse.setRow_id(c.getInt(row_id));


             productResponses.add(productResponse);
         }
         c.close();
         return productResponses;

     }

     public List<ProductResponse> getTagPrice(int lowrate, int highrate) {
         Cursor c = ourdatabase.rawQuery("SELECT * FROM "+ DATABASE_TABLEPRODUCT + " WHERE "+ KEY_MRP +" BETWEEN " +lowrate+" AND "+highrate, null);
         int pid=c.getColumnIndex(KEY_PID);
         int id=c.getColumnIndex(KEY_SID);
         int row_id=c.getColumnIndex(KEY_ROWID);
         int image=c.getColumnIndex(KEY_IMAGE);
         int b_name=c.getColumnIndex(KEY_BNAME);
         int f_name=c.getColumnIndex(KEY_FNAME);
         int c_name=c.getColumnIndex(KEY_CNAME);
         int mrp=c.getColumnIndex(KEY_MRP);
         int catTag=c.getColumnIndex(KEY_CATTAG);
         int percepTag=c.getColumnIndex(KEY_PERCEPTAG);
         int quantity=c.getColumnIndex(KEY_UNITS);
         int row=c.getColumnIndex(KEY_ROW);
         int column=c.getColumnIndex(KEY_PID);

         List<ProductResponse> productResponses=new ArrayList<>();

         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
             ProductResponse productResponse=new ProductResponse();
             productResponse.setCatTag(c.getString(catTag));
             productResponse.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
             productResponse.setQuantity(c.getInt(quantity));
             productResponse.setColumn(c.getInt(column));
             productResponse.setMrp(c.getInt(mrp));
             productResponse.setB_name(c.getString(b_name));
             productResponse.setC_name(c.getString(c_name));
             productResponse.setF_name(c.getString(f_name));
             productResponse.setPId(c.getInt(pid));
             productResponse.setId(c.getInt(id));
             productResponse.setPercepTag(c.getString(percepTag));
             productResponse.setRow(c.getInt(row));
             productResponse.setRow_id(c.getInt(row_id));


             productResponses.add(productResponse);
         }
         c.close();
         return productResponses;
     }

     public List<ProductResponse> getTagFlavour(String name) {

         Cursor c = ourdatabase.rawQuery("SELECT * FROM "+ DATABASE_TABLEPRODUCT + " WHERE "+ KEY_FNAME +" LIKE  \'%" +name+"%\'", null);
         int pid=c.getColumnIndex(KEY_PID);
         int id=c.getColumnIndex(KEY_SID);
         int row_id=c.getColumnIndex(KEY_ROWID);
         int image=c.getColumnIndex(KEY_IMAGE);
         int b_name=c.getColumnIndex(KEY_BNAME);
         int f_name=c.getColumnIndex(KEY_FNAME);
         int c_name=c.getColumnIndex(KEY_CNAME);
         int mrp=c.getColumnIndex(KEY_MRP);
         int catTag=c.getColumnIndex(KEY_CATTAG);
         int percepTag=c.getColumnIndex(KEY_PERCEPTAG);
         int quantity=c.getColumnIndex(KEY_UNITS);
         int row=c.getColumnIndex(KEY_ROW);
         int column=c.getColumnIndex(KEY_PID);

         List<ProductResponse> productResponses=new ArrayList<>();

         for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
         {
             ProductResponse productResponse=new ProductResponse();
             productResponse.setCatTag(c.getString(catTag));
             productResponse.setImage(DbBitmapUtilityObj.getImage(c.getBlob(image)));
             productResponse.setQuantity(c.getInt(quantity));
             productResponse.setColumn(c.getInt(column));
             productResponse.setMrp(c.getInt(mrp));
             productResponse.setB_name(c.getString(b_name));
             productResponse.setC_name(c.getString(c_name));
             productResponse.setF_name(c.getString(f_name));
             productResponse.setPId(c.getInt(pid));
             productResponse.setId(c.getInt(id));
             productResponse.setPercepTag(c.getString(percepTag));
             productResponse.setRow(c.getInt(row));
             productResponse.setRow_id(c.getInt(row_id));


             productResponses.add(productResponse);
         }
         c.close();
         return productResponses;


     }

     public void DeleteCart() {
//         ourhelper=new Dbhelper(ourcontext);
//         ourdatabase=ourhelper.getWritableDatabase();
         //On cencel button cart items are removed
         ourdatabase.execSQL("DELETE FROM "+ DATABASE_TABLE);

     }

     public class DbBitmapUtility {

         // convert from bitmap to byte array
         public  byte[] getBytes(Bitmap bitmap) {
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
             return stream.toByteArray();
         }

         // convert from byte array to bitmap
         public  Bitmap getImage(byte[] image) {
             return BitmapFactory.decodeByteArray(image, 0, image.length);
         }
     }

     private static class Dbhelper extends SQLiteOpenHelper {
        public Dbhelper(Context context) {
            super(context,DATABASE_NAME, null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT NOT NULL, " + KEY_SUBID + " INTEGER NOT NULL, " + KEY_SID + " INTEGER NOT NULL, " + KEY_UNITS + " INTEGER, " + KEY_CARTIMAGE + " BLOB, " + KEY_PRICE + " INTEGER NOT NULL);");

            //Check here if the table is created properly
            Log.v("checkdb", "Inside onCreate of Vending Database");


            db.execSQL("CREATE TABLE "+DATABASE_TABLEPRODUCT+" ("+KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    KEY_BNAME+" TEXT NOT NULL, "+KEY_CNAME+" TEXT NOT NULL, "+KEY_FNAME+" TEXT NOT NULL, "+KEY_IMAGE+" BLOB, "+KEY_CATTAG+" TEXT, "+KEY_PERCEPTAG+" TEXT, "+KEY_PID+" INTEGER NOT NULL, "+KEY_SID+" INTEGER NOT NULL, "+KEY_UNITS+" INTEGER, "+KEY_CATID+" INTEGER, "+KEY_ROW+" INTEGER, "+KEY_COL+" INTEGER, "+KEY_MRP+" INTEGER NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
            onCreate(db);
        }
    }

    //Testing start

     public boolean isTableExists(String tableName) {
         //if(openDb) {
             if(ourdatabase == null || !ourdatabase.isOpen()) {
                 ourdatabase = ourhelper.getReadableDatabase();
             }

             if(!ourdatabase.isReadOnly()) {
                 ourdatabase.close();
                 ourdatabase = ourhelper.getReadableDatabase();
             }
         //}

         Cursor cursor = ourdatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
         if(cursor!=null) {
             if(cursor.getCount()>0) {
                 cursor.close();
                 return true;
             }
             cursor.close();
         }
         return false;
     }

     //Testing end

    //Constructers
    public VendingDatabase(Context c){
        ourcontext=c;
    }

    public VendingDatabase open()throws SQLException{
        ourhelper=new Dbhelper(ourcontext);
        ourdatabase=ourhelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourhelper.close();
    }
}
