package com.insequence.newyorktimessearch.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.insequence.newyorktimessearch.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paulyang on 10/22/16.
 */
public class FilterActivity extends AppCompatActivity implements OnItemSelectedListener{

    CheckBox ch1,ch2,ch3;
    boolean checkArts = false;
    boolean checkFashion = false;
    boolean checkSports = false;
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private String spinnerResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // code for date picker
        Button button = (Button) findViewById(R.id.dateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        // date picker: http://www.tutorialspoint.com/android/android_datepicker_control.htm
        dateView = (TextView) findViewById(R.id.dateView);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);

        // code for spinner
        // spinner https://www.tutorialspoint.com/android/android_spinner_control.htm
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Newest");
        categories.add("Oldest");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // https://www.tutorialspoint.com/android/android_checkbox_control.htm
        // code for checkboxes
        ch1=(CheckBox)findViewById(R.id.checkBoxArts);
        ch2=(CheckBox)findViewById(R.id.checkBoxFashion);
        ch3=(CheckBox)findViewById(R.id.checkBoxSports);

        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkArts = isChecked;
                System.out.println(isChecked);
                // Toast.makeText(, "arts: " + isChecked);
            }
        });

        ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkFashion = isChecked;
                System.out.println(isChecked);
            }
        });

        ch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkSports = isChecked;
                System.out.println(isChecked);
            }
        });

        // save button
        // Button saveButton = (Button) findViewById(R.id.buttonSave);
    }

    // http://stackoverflow.com/questions/5588804/android-button-setonclicklistener-design-help
    // click handler for save button
    public void saveClickHandler(View target) {
        // Do stuff
        Intent data = new Intent();
        data.putExtra("checkArts", checkArts);
        data.putExtra("checkFashion", checkFashion);
        data.putExtra("checkSports", checkSports);
        data.putExtra("spinnerResult", spinnerResult);
        data.putExtra("year", year);
        data.putExtra("month", month);
        data.putExtra("day", day);
        setResult(RESULT_OK, data);
        finish();
    }

    // getting spinner data back
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        spinnerResult = item;
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            Log.d("Debug", "start datepicker with month: " + month);
            return new DatePickerDialog(this, myDateListener, year, month-1, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2 + 1;
            day = arg3;
            System.out.println("mm/dd/yyyy: " + month + "" + day + "" + year);
            showDate(year, month, day);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
