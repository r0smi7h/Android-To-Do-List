package com.rohan.todotodayii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected DBHelper mDBHelper; // instance of DBHelper
    private List<ToDo_Item> list;
    private MyAdapter adapt;
    private EditText myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TASK 1: LAUNCH THE LAYOUT REPRESENTING THE MAIN ACTIVITY
        setContentView(R.layout.activity_main);

        // TASK 2: ESTABLISH REFERENCES TO THE UI
        //      ELEMENTS LOCATED ON THE LAYOUT
        myTask = (EditText) findViewById(R.id.editText1);

        // TASK 3: SET UP THE DATABASE
        mDBHelper = new DBHelper(this);
        /*
        list = mDBHelper.getAllTasks();
        adapt = new MyAdapter(this, R.layout.todo_item, list);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
        */
    }

    // load the list var with a list of ToDo_Item objects/records
    @Override
    protected void onResume(){
        super.onResume();

        //Step 1: Get the data source
        //========================================================================
        // load the list var with a list of ToDo_Item objects/records from the database
        list = mDBHelper.getAllTasks();

        //PerformGetAllRecords myTasks = new PerformGetAllRecords();
        adapt = new MyAdapter(this, R.layout.todo_item, list);
        //myTasks.execute();
        //myTasks.onPostExecute();

        // Step3: Bind the Adapter to the ListView
        //==========================================================
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);

    }

    private class PerformGetAllRecords extends AsyncTask< ToDo_Item, Integer,  ToDo_Item>{



        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "Accessing Database...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ToDo_Item doInBackground(ToDo_Item... params){

            //String s = myTask.getText().toString();

            // BUILD A NEW TASK ITEM AND ADD IT TO THE DATABASE
            //ToDo_Item task = new ToDo_Item(s, 0);
            ToDo_Item task = params[0];
            mDBHelper.addToDoItem(task); //background task

            return task;
        }

        @Override
        protected void onPostExecute(ToDo_Item result){
            super.onPostExecute(result);
            // UPDATE THE UI
            // ADD THE TASK & SET A NOTIFICATION OF CHANGES
            // BIND THE ADAPTER TO THE LISTVIEW
            adapt.add(result);
            adapt.notifyDataSetChanged();

            // CLEAR OUT THE TASK EDITVIEW
            myTask.setText("");

            Toast.makeText(getApplicationContext(), "Database Access Completed", Toast.LENGTH_SHORT).show();

        }

    }

    //BUTTON CLICK EVENT FOR ADDING A TODO TASK
    public void addTaskNow(View view) {

        String s = myTask.getText().toString();


        if (s.isEmpty()) {
            Toast.makeText(getApplicationContext(), "A TODO task must be entered.", Toast.LENGTH_SHORT).show();
        }

        else {

            ToDo_Item task = new ToDo_Item(s, 0);

            PerformGetAllRecords myRecords = new  PerformGetAllRecords();
            myRecords.execute(task);

        }

    }

    //BUTTON CLICK EVENT FOR DELETING ALL TODO TASKS
    public void clearTasks(View view) {
        mDBHelper.clearAll(list);
        adapt.notifyDataSetChanged();
    }

    // create a view holder to hold all the views needed for 1 todo item
    static class ViewHolder{
        //need one field/member variable for each widget/view in the todo item view (todo_item.xml)
        private CheckBox IsDoneCheckbox;

        // 1. add a delete button here
        private Button DeleteTaskBtn;
    }


    //******************* ADAPTER ******************************
    private class MyAdapter extends ArrayAdapter<ToDo_Item> {
        Context context;
        List<ToDo_Item> taskList = new ArrayList<ToDo_Item>();

        public MyAdapter(Context c, int rId, List<ToDo_Item> objects) {
            super(c, rId, objects);
            taskList = objects;
            context = c;
        }

        //******************* TODO TASK ITEM VIEW ******************************
        /**
         * THIS METHOD DEFINES THE TODO ITEM THAT WILL BE PLACED
         * INSIDE THE LIST VIEW.
         *
         * THE CHECKBOX STATE IS THE IS_DONE STATUS OF THE TODO TASK
         * AND THE CHECKBOX TEXT IS THE TODO_ITEM TASK DESCRIPTION.
         */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //CheckBox isDoneChBx = null;

            ViewHolder Holder = new ViewHolder();

            // if we need a new view then convertView is null
            // so we need to build a new view from scratch
            if (convertView == null) {
                // get an inflater(needed to inflate/create a new view of todo_item.xml)
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // use the inflater to inflate/create a new view of todo_item.xml
                // now convertView holds a view for one todo item without any data (yet)
                convertView = inflater.inflate(R.layout.todo_item, parent, false);

                // now we will set up and configure the view item

                // we need to add a click event handler to the checkbox
                // get a reference to the checkbox from findViewById
                Holder.IsDoneCheckbox = (CheckBox) convertView.findViewById(R.id.chkStatus);

                // now use the reference to set up the checkbox (just add a click listener)
                Holder.IsDoneCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // onClick gives us the view that was clicked
                        // so get a reference to it (the checkbox)
                        CheckBox cb = (CheckBox) view;

                        // update the database with the current state
                        // by pulling the todo item from the tag (todo holds the id key from the database)
                        ToDo_Item changeTask = (ToDo_Item) cb.getTag();

                        // set the todo item checked state (0 or 1) from the checkbox checked state (true or false)
                        changeTask.setIs_done(cb.isChecked() == true ? 1 : 0);

                        // update the database with this todo object
                        mDBHelper.updateTask(changeTask);
                    }
                });


                // 2. add code to find button and put in ViewHolder
                Holder.DeleteTaskBtn = (Button) convertView.findViewById(R.id.btnDelete);

                // 3. set onclick listener for delete button
                // (will have to do the code to call the DBHelper delete method for deleting individual tasks)
                // similar to line 169.. will have to pass in a task to delete instead of update
                Holder.DeleteTaskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button btn = (Button) v;
                        ToDo_Item currentRecord = (ToDo_Item) btn.getTag();
                        mDBHelper.deleteTask(currentRecord);
                        adapt.remove(currentRecord);
                        adapt.notifyDataSetChanged();
                    }
                });


                // store the checkbox in the tag on the view (todo_item.xml)
                // so we can pull quickly later when needed, without using findViewById()
                convertView.setTag(Holder);
            }

            // if we are reusing a view, then convertView holds a view of todo_item.xml
            else {
                // no need to call findViewById(), because the checkbox is in the tag
                // so pull it out the tag (which returns an object) and cast it to a checkbox
                // isDoneChkBx = (CheckBox) convertView.getTag();
                Holder = (ViewHolder) convertView.getTag();
            }

            // now we have our checkbox and a convertView (todo_item.xml)
            // now load/update with data

            // get the current data item requested by getView call
            ToDo_Item current = taskList.get(position);

            //put/display the data in the view
            //isDoneChBx.setText(current.getDescription()); // set the description in the checkbox
            //isDoneChBx.setChecked(current.getIs_done() == 1 ? true : false); // set the checked state

            Holder.IsDoneCheckbox.setText(current.getDescription()); // set the description in the checkbox
            Holder.IsDoneCheckbox.setChecked(current.getIs_done() == 1 ? true : false); // set the checked state

            // now add the todo object to the checkbox tag
            // so the data object (todo item) held by the checkbox is also in the tag
            Holder.IsDoneCheckbox.setTag(current);

            // now add the todo object to the Delete Button tag
            Holder.DeleteTaskBtn.setTag(current);

            // finally return this finished view
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
