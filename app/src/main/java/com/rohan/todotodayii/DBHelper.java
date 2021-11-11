package com.rohan.todotodayii;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// we must make our own instance of the SQLiteOpenHelper (subclass)
public class DBHelper extends SQLiteOpenHelper {
    //TASK 1: DEFINE THE DATABASE AND TABLE

    // this is just a design pattern - here we
    // create constants that represents the database schema
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "toDo_Today";
    private static final String DATABASE_TABLE = "toDo_Items";


    //TASK 2: DEFINE THE COLUMN NAMES FOR THE TABLE
    private static final String KEY_TASK_ID = "_id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IS_DONE = "is_done";

    // set up any other member variables / fields / properties that we may want
    private int taskCount; // hold the number of the to do items in the database

    // here is the required Constructor
    public DBHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // required for SQLite
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         ** OnCreate fires if the database does not exist after DBHelper constructor is called
         ** here we are creating a single table in the database
         ** notice there is no Create Database statement here - the OS passes this method
         ** a database instance
         */

        // simple SQL statement in a string
        String table = "CREATE TABLE " + DATABASE_TABLE + "("
                + KEY_TASK_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DESCRIPTION + " TEXT, "
                + KEY_IS_DONE + " INTEGER" + ")";

        // execute the simple SQL statement with the database.execSQL(SQLString)
        db.execSQL(table);
    }

    // delete existing table and create new table
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // we put whatever code we need here to convert the old version of the database to the new
        // preserving data, adding or changing tables, etc.

        // DROP OLDER TABLE IF EXISTS
        // (simple example - delete and recreate the table instead of preserving data)
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

        // CREATE TABLE AGAIN
        onCreate(database);
    }

    //=====================================================
    //Now: All other methods are up to the developer (us)
    // we can make up any methods we need to do whatever data operations we need (CRUD)
    //=====================================================

    //********** DATABASE OPERATIONS:  ADD, EDIT, DELETE
    // Adding new task
    public void addToDoItem(ToDo_Item task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //ADD KEY-VALUE PAIR INFORMATION FOR THE TASK DESCRIPTION
        values.put(KEY_DESCRIPTION, task.getDescription()); // task name

        //ADD KEY-VALUE PAIR INFORMATION FOR
        //IS_DONE VALUE: 0 = NOT DONE, 1 = IS DONE
        values.put(KEY_IS_DONE, task.getIs_done());

        // INSERT THE ROW IN THE TABLE
        db.insert(DATABASE_TABLE, null, values);
        taskCount++;

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    public List<ToDo_Item> getAllTasks() {

        //GET ALL THE TASK ITEMS ON THE LIST
        List<ToDo_Item> todoList = new ArrayList<ToDo_Item>();

        //SELECT ALL QUERY FROM THE TABLE
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // LOOP THROUGH THE TODO TASKS
        if (cursor.moveToFirst()) {
            do {
                ToDo_Item task = new ToDo_Item();
                task.setId(cursor.getInt(0));
                task.setDescription(cursor.getString(1));
                task.setIs_done(cursor.getInt(2));
                todoList.add(task);
            } while (cursor.moveToNext());
        }

        // RETURN THE LIST OF TASKS FROM THE TABLE
        return todoList;
    }

    public void clearAll(List<ToDo_Item> list) {
        //GET ALL THE LIST TASK ITEMS AND CLEAR THEM
        list.clear();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, null, new String[]{});
        db.close();
    }

    public void updateTask(ToDo_Item task) {
        // updating row
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_IS_DONE, task.getIs_done());
        db.update(DATABASE_TABLE, values, KEY_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // Add method to delete a task
    // method will accept a task and call db.delete
    public void deleteTask(ToDo_Item task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_TASK_ID + "= ?", new String[]{String.valueOf(task.getId())});
        //taskCount --;
        db.close();
    }

}
