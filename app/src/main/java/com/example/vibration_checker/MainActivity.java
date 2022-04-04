package com.example.vibration_checker;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private final static String FILE_NAME = "content.txt";
    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor sensorAxel;
    private ArrayList<float[]> axels = new ArrayList<>();

    private TextView dataADXL;
    private TextView textHz;
    private ProgressBar progressBar;
    private ToggleButton toggleButton_up;
    private ToggleButton toggleButton3_true;

    private Button button_start;
    private Button button_go;
    private Button button_stop;

    private boolean is_up = true;
    private boolean is_true = true;

    long time = 0;
    long count = 0;
    boolean start_mes = false;
    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataADXL = findViewById(R.id.text_axel);
        textHz = findViewById(R.id.textHz);
        progressBar = findViewById(R.id.progressBar2);
        toggleButton_up = findViewById(R.id.toggleButton_up);
        toggleButton3_true = findViewById(R.id.toggleButton3_true);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorAxel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        time = System.currentTimeMillis();


        button_start = findViewById(R.id.button_start);
        button_go = findViewById(R.id.button_go);
        button_stop = findViewById(R.id.button_stop);

        toggleButton_up.setText("Едем вверх");
        toggleButton3_true.setText("Правдивые");

        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_mes = true;
                action = "go";
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_mes = true;
                action = "start";
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_mes = true;
                action = "stop";
            }
        });

        toggleButton_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!toggleButton_up.isChecked()){
                    toggleButton_up.setText("Едем вверх");
                    is_up = true;
                }
                else{
                    toggleButton_up.setText("Едем вниз");
                    is_up = false;
                }
            }
        });

        toggleButton3_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!toggleButton3_true.isChecked()){
                    toggleButton3_true.setText("Правдивые");
                    is_true = true;
                }
                else{
                    toggleButton3_true.setText("Ложные");
                    is_true = false;
                }
            }
        });

        System.out.println(Environment.getExternalStoragePublicDirectory("Download"));
        File directory = new File(Environment.getExternalStoragePublicDirectory("Download"),"Elevator");
        if(!directory.exists() && !directory.isDirectory())
        {
            // create empty directory
            if (directory.mkdirs())
            {
                Log.i("CreateDir","App dir created");
            }
            else
            {
                Log.w("CreateDir","Unable to create app dir!");
            }
        }
        else
        {
            Log.i("CreateDir","App dir already exists");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorAxel, SensorManager.SENSOR_DELAY_GAME);
        time = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener, sensorAxel);
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    dataADXL.setText(event.values[0]+" "+event.values[1]+" "+event.values[2]);
                    if (start_mes) {
                        //System.out.println(event.values[0]+" "+event.values[1]+" "+event.values[2]);
                        axels.add(new float[]{event.values[0],event.values[1],event.values[2]});
                        count++;
                        progressBar.setProgress(axels.size());

                        if (axels.size() >= 125) {
                            saveText(axels,action+"_"+String.valueOf(is_up)+"_"+String.valueOf(is_true)+".txt");
                            axels = new ArrayList<>();
                            start_mes = false;
                            button_start.setEnabled(true);
                            button_go.setEnabled(true);
                            button_stop.setEnabled(true);
                        }
                        else{
                            button_start.setEnabled(false);
                            button_go.setEnabled(false);
                            button_stop.setEnabled(false);
                        }

                        if (System.currentTimeMillis() - time > 1000) {
                            System.out.println("mes in sec " + count);
                            textHz.setText("mes in sec " + count);
                            count = 0;
                            time = System.currentTimeMillis();
                        }


                    }
                break;
            }

        }

    };

    public void saveText(ArrayList<float[]> axell, String filename){

        try {

            String text = "";

            for (int i = 0; i < axell.size(); i++) {
                text += axell.get(i)[0] + ";" + axell.get(i)[1] + ";" + axell.get(i)[2] + "\n";
            }
//                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//                fos.write(text.getBytes());
//                fos.close();
            File file = new File(Environment.getExternalStoragePublicDirectory("Download"), "Elevator/"+filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write(text.getBytes());
        } catch (IOException ex) {

        }
    }

}