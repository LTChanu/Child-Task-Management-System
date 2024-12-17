package com.chanu.childtask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTask extends AppCompatActivity {

    DatabaseReference Data;
    TextInputLayout inName, inDescription;
    TextInputEditText etTaskDateTime;
    String category="Study", key="0";
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        etTaskDateTime = findViewById(R.id.etTaskDateTime);
        inName = findViewById(R.id.inTaskName);
        inDescription = findViewById(R.id.inTaskDescription);
        radioGroup = findViewById(R.id.inTaskCategory);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                category = radioButton.getText().toString();
            }
        });
    }

    public void setDateTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a DatePickerDialog to pick the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    // The selected date is returned in the parameters
                    // Handle the selected date
                    //String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    String keyDate = String.format(Locale.getDefault(), "%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay);

                    // Create a TimePickerDialog to pick the time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddTask.this,
                            (view2, selectedHour, selectedMinute) -> {
                                // The selected time is returned in the parameters
                                // Handle the selected time
                                //String selectedTime = selectedHour + ":" + selectedMinute;
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                String keyTime = String.format(Locale.getDefault(), "%02d%02d", selectedHour, selectedMinute);

                                // Combine the selected date and time
                                String selectedDateTime = selectedDate + " " + selectedTime;
                                key = keyDate+keyTime;

                                // Set the selected date and time in the EditText
                                etTaskDateTime.setText(selectedDateTime);
                            }, hour, minute, true);

                    // Show the TimePickerDialog
                    timePickerDialog.show();
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    public void addTask(View view) {
        common.startLoading(this,"Adding");
        String name = Objects.requireNonNull(inName.getEditText()).getText().toString();
        String description = Objects.requireNonNull(inDescription.getEditText()).getText().toString();

        if (name.equals("") || category.equals("") || key.equals("0")){
            common.stopLoading();
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_LONG).show();
        }else {
            //String key = String.valueOf(year) +String.valueOf(month+1)+String.valueOf(day) + String.valueOf(hour) + String.valueOf(minute);

            Data.child("user").child(common.rDBEmail).child("TaskList").child(key).child("name").setValue(name);
            Data.child("user").child(common.rDBEmail).child("TaskList").child(key).child("description").setValue(description);
            Data.child("user").child(common.rDBEmail).child("TaskList").child(key).child("category").setValue(category);
            Data.child("user").child(common.rDBEmail).child("TaskList").child(key).child("time").setValue(key).addOnSuccessListener(unused -> {
                inName.getEditText().setText("");
                inDescription.getEditText().setText("");
                etTaskDateTime.setText("");
                key="0";
                common.stopLoading();
                Toast.makeText(AddTask.this, "Successfully Add Task", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                common.stopLoading();
                Toast.makeText(AddTask.this, "Database Error", Toast.LENGTH_LONG).show();
            });
        }
    }
}