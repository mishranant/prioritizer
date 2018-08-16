package com.darrroxtar.prioritizer;

import android.content.Intent;
//import android.databinding.DataBindingUtil;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.Toast;

//import com.darrroxtar.prioritizer.databinding.ActivityAddTaskBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTask extends AppCompatActivity {
    private static final int nTasks = 6;
    private int mYear, mMonth, mDay;
    private TextView[] deadlineTextView = new TextView[nTasks];
    private int[] resIDDeadline = {R.id.deadline1, R.id.deadline2, R.id.deadline3, R.id.deadline4, R.id.deadline5, R.id.deadline6};
    private Spinner[] workloadSpinner = new Spinner[nTasks];
    private int[] resIDWorkload = {R.id.workload1, R.id.workload2, R.id.workload3, R.id.workload4, R.id.workload5, R.id.workload6};
    private TextView[] taskNameTextView = new TextView[nTasks];
    private int[] resIDtaskName = {R.id.task1, R.id.task2, R.id.task3, R.id.task4, R.id.task5, R.id.task6};
    private Task[] tasklist = new Task[nTasks];

    //private Button deadlineBtn;
    //private ViewGroup layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //ActivityAddTaskBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task);
        for (int i = 0; i < nTasks; i++) {
            deadlineTextView[i] = findViewById(resIDDeadline[i]);
            workloadSpinner[i] = findViewById(resIDWorkload[i]);
            taskNameTextView[i] = findViewById(resIDtaskName[i]);
            deadlineTextView[i].setOnTouchListener(callShowCalendar);
        }

    }

    public void showCalendar(final TextView view) {
        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String monthprefix = "", dayprefix = "";
                if (month < 10) monthprefix = "0";
                if (day < 10) dayprefix = "0";
                String date = dayprefix + day + "/" + monthprefix + month + "/" + year;
                view.setText(date);
            }
        }, mYear, mMonth, mDay);
        dialogDatePicker.show();
    }

    View.OnTouchListener callShowCalendar = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                showCalendar((TextView) v);
                return true;
            }
            return false;
        }
    };

    public void fetchTaskDetails(View view) {
        //String tag= "fetch task";
        for (int i = 0; i < nTasks; i++) {
            if (deadlineTextView[i].getText() != null) {
                tasklist[i] = new Task(taskNameTextView[i].getText().toString(), priorityFinder(workloadSpinner[i].getSelectedItem().toString(), deadlineTextView[i].getText().toString()));
            }
        }
        buildMaxHeap();
        /*try {
            FileOutputStream fos = openFileOutput("Prioritizer", MODE_APPEND);
            fos.write(tasklist[0].name.getBytes());
            fos.close();
            Toast.makeText(getApplicationContext(), "Data Saved!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        /*for (int i = 0; i < nTasks; ++i){
            tasklist[];
        }*/
        Intent intent = new Intent(com.darrroxtar.prioritizer.AddTask.this, Home.class);
        startActivity(intent);
    }

    private float priorityFinder(String workload, String deadline) {
        float millis = 0;
        try {
            millis = new SimpleDateFormat("dd/MM/yyyy").parse(deadline).getTime() / 1000;
            millis -= (new Date()).getTime() / 1000;
            return (float) (Float.parseFloat(workload) * 10E5 / millis);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void buildMaxHeap() {
        for (int i = 1; i < nTasks; i++) {
            heapifyUp(i);
//            Log.d("testheap"+ i, tasklist[i].name);
        }
    }

    private void heapifyUp(int index) {
        if (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (tasklist[index].priority > tasklist[parentIndex].priority) {
                Task temp = tasklist[index];
                tasklist[index] = tasklist[parentIndex];
                tasklist[parentIndex] = temp;
                heapifyUp(parentIndex);
            }
        }
    }
}
