package com.gaitroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHandler extends SQLiteOpenHelper {
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "gaitroid";
    
    // User table name
    private static final String TABLE_USER = "user";
    
    // User Table Columns names
    private static final String KEY_ID = "userId";
    private static final String KEY_USERNAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_AGE = "age";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CITY = "city";
    private static final String KEY_STREET = "street";
    private static final String KEY_POSTCODE = "postcode";
    private static final String KEY_CREATEDTIME = "created_time";
    
    public UserDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_AGE + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_STREET + " TEXT,"
                + KEY_POSTCODE + " TEXT,"
                + KEY_CREATEDTIME + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }
    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
 
        // Create tables again
        onCreate(db);
    }
    
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new User
    void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
//        values.put(KEY_ID, user.getID()); // id
//        values.put(KEY_USERNAME, user.getUsername()); // userName
//        values.put(KEY_PASSWORD, user.getPassword()); // pw
//        values.put(KEY_FIRSTNAME, user.getFirstname()); // fn
//        values.put(KEY_LASTNAME, user.getLastname()); // ln
//        values.put(KEY_GENDER, user.getGender()); // gender
//        values.put(KEY_EMAIL, user.getEmail()); // email
//        values.put(KEY_PHONE, user.getPhone()); // Phone
//        values.put(KEY_AGE, user.getAge()); // age
//        values.put(KEY_COUNTRY, user.getCountry()); // country
//        values.put(KEY_CITY, user.getCity()); // CITY
//        values.put(KEY_STREET, user.getStreet()); // STREET
//        values.put(KEY_POSTCODE, user.getPostcode()); // PC
//        values.put(KEY_CREATEDTIME, user.getCreateTime()); // CT
// 
//        // Inserting Row
//        db.insert(TABLE_USER, null, values);
        
        String sql =
                "INSERT or REPLACE INTO \""+ TABLE_USER+"\" (\""+KEY_ID+"\", \""+KEY_USERNAME+"\", \""+KEY_PASSWORD+"\", \""+KEY_FIRSTNAME+"\", \""+KEY_LASTNAME+"\", \""+KEY_GENDER+"\", \""+KEY_EMAIL+"\", \""+KEY_PHONE+"\", \""+KEY_AGE+"\", \""+KEY_COUNTRY+"\", \""+KEY_CITY+"\", \""+KEY_STREET+"\",  \""+KEY_POSTCODE+"\",  \""+KEY_CREATEDTIME+"\")"
                + " VALUES(\""+user.getID()+"\", \""+user.getUsername()+"\", \""+user.getPassword()+"\", \""+user.getFirstname()+"\", \""+user.getLastname()+"\", \""+user.getGender()+"\", \""+user.getEmail()+"\", \""+user.getPhone()+"\", \""+user.getAge()+"\", \""+user.getCountry()+"\", \""+user.getCity()+"\", \""+user.getStreet()+"\", \""+user.getPostcode()+"\", \""+user.getCreateTime()+"\")" ;       
                    db.execSQL(sql);
        db.close(); // Closing database connection
    }
    
    // Getting single user
    User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_ID,
        		KEY_USERNAME, KEY_PASSWORD, KEY_FIRSTNAME, KEY_LASTNAME, KEY_GENDER,KEY_EMAIL, KEY_PHONE, KEY_AGE, KEY_COUNTRY, KEY_CITY,KEY_STREET, KEY_POSTCODE, KEY_CREATEDTIME}, KEY_USERNAME + "=?",
                new String[] { String.valueOf(username) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        User user = new User(cursor.getString(0),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8),
                cursor.getString(9), cursor.getString(10),
                cursor.getString(11), cursor.getString(12),
                cursor.getString(13));
        // return user
        return user;
    }
    
    // check is user login in before
    public boolean hasUser(){
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT  * FROM " + TABLE_USER;
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	if(cursor.getCount() > 0){
    		return true;
    	}
    	else
    		return false;
    }
    
    // Updating single user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getID()); // id
        values.put(KEY_USERNAME, user.getUsername()); // userName
        values.put(KEY_PASSWORD, user.getPassword()); // pw
        values.put(KEY_FIRSTNAME, user.getFirstname()); // fn
        values.put(KEY_LASTNAME, user.getLastname()); // ln
        values.put(KEY_GENDER, user.getGender()); // gender
        values.put(KEY_EMAIL, user.getEmail()); // email
        values.put(KEY_PHONE, user.getPhone()); // Phone
        values.put(KEY_AGE, user.getAge()); // age
        values.put(KEY_COUNTRY, user.getCountry()); // country
        values.put(KEY_CITY, user.getCity()); // CITY
        values.put(KEY_STREET, user.getStreet()); // STREET
        values.put(KEY_POSTCODE, user.getPostcode()); // PC
        values.put(KEY_CREATEDTIME, user.getCreateTime()); // CT
 
        // updating row
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
    }
    
    // Deleting single user
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
        db.close();
    }
}
