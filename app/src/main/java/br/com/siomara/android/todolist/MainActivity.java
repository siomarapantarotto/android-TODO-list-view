package br.com.siomara.android.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This app implements a simple ListView with tasks, storing data in an SQLiteDatabase.
 * To understand the app behaviour check Logcat or analyze via the Database Inspector.
 * To open a database in the Database Inspector, do the following:
 * 1) Run the app on a connected emulator or mobile device (API 26 or newer versions);
 * 2) Select View > Tool Windows > App Inspection from the menu bar;
 * 3) Select the Database Inspector tab.
 *
 * @see "https://developer.android.com/studio/inspect/database?hl=pt-br"
 */
public class MainActivity extends AppCompatActivity {

    private EditText edtNewTask;
    private Button btnSave;
    private ListView lstTasks;
    private SQLiteDatabase sqLiteDatabase;

    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            initializeComponents();

            // Drops the entire database. Use this command to rename the database, table and its
            // columns. Start the app once and DON'T FORGET to commented out the command before
            // running the app again. BTW, dropping a DB that doesn't exist will cause an error.
            //Boolean aBoolean = deleteDatabase("appTODOList"); // Comment/Uncomment

            // Creates the database, the table and define its columns.
            sqLiteDatabase = openOrCreateDatabase("appTODOList", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS taskTODO (id INTEGER PRIMARY KEY AUTOINCREMENT, description VARCHAR)");

            // implements button click to save a new task.
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTaskTODO = edtNewTask.getText().toString();
                    saveTask(newTaskTODO);
                }
            });

            // Implements LONG LIST CLICK to delete a task.
            lstTasks.setLongClickable(true);
            lstTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Deletes ", "position: " + position + "\tids: " + ids.get(position) + " - " + items.get(position));
                    removeTask(ids.get(position));
                    getTasks();
                    return true;
                }
            });

            // Retrieves all the tasks from the database.
            getTasks();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // Method to retrieve all the tasks and display them on the list view.
    private void getTasks() {
        try {
            // Sets SQL to retrieve all tasks in descending order by indexer.
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM taskTODO ORDER BY id DESC", null);

            // Retrieves columns indexes.
            int idIndex = cursor.getColumnIndex("id");
            int taskIndex = cursor.getColumnIndex("description");

            // Creates adapter.
            items = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            itemsAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    items);

            lstTasks.setAdapter(itemsAdapter);

            // List all tasks on console for debugging purpose.
            cursor.moveToFirst();
            while (cursor != null) {
                Log.d("Results ", "ID: " + cursor.getString(idIndex) + "\t" + cursor.getString(taskIndex));
                items.add(cursor.getString(taskIndex));
                ids.add(Integer.parseInt(cursor.getString(idIndex)));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to save a new task.
    private void saveTask(String task) {
        try {
            if (task.equals("")) {
                toast(R.string.inform_mandatory_task);
            } else {
                sqLiteDatabase.execSQL("INSERT INTO taskTODO (description) VALUES ('" + task + "')");
                toast(R.string.task_added_successfully);
                getTasks();
                edtNewTask.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // Method to delete task from table and remove it from list.
    private void removeTask(Integer id) {
        try {
            sqLiteDatabase.execSQL("DELETE FROM taskTODO WHERE id ==" + id);
            toast(R.string.task_removed_successfully);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to connect and initialize GUI components.
    public void initializeComponents() {
        edtNewTask = findViewById(R.id.editTextTask);
        btnSave = findViewById(R.id.buttonAdd);
        lstTasks = findViewById(R.id.listViewTask);
    }


    // Method to toast messages.
    private void toast(int stringsXMLMessage) {
        Toast.makeText(MainActivity.this, stringsXMLMessage, Toast.LENGTH_SHORT).show();
    }

}
