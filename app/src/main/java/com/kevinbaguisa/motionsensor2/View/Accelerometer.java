package com.kevinbaguisa.motionsensor2.View;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevinbaguisa.motionsensor2.Controller.ClientInstance;
import com.kevinbaguisa.motionsensor2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Accelerometer extends AppCompatActivity {

    TextView tvTimer, tvX_AxisValA, tvY_AxisValA, tvZ_AxisValA, tvXval, tvYval, tvZval;
    Button btnStartTimer;
    EditText etCountDownNum;
    SensorManager sm;
    Sensor acceleSensor;
    ArrayList<Float> xALA, yALA, zALA;
    ListView LVXA, LVYA, LVZA;
    float aXval, aYval, aZval;
    DecimalFormat df;
    float x1,x2,y1,y2;
    ClientInstance client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        acceleSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tvXval = (TextView) findViewById(R.id.valXA);
        tvYval = (TextView) findViewById(R.id.valYA);
        tvZval = (TextView) findViewById(R.id.valZA);

        tvTimer = (TextView) findViewById(R.id.timerTV);
        btnStartTimer = (Button) findViewById(R.id.startTimerBtn);
        etCountDownNum = (EditText) findViewById(R.id.countDownNumET);

        tvX_AxisValA = (TextView) findViewById(R.id.xAxisValuesTV);
        tvY_AxisValA = (TextView) findViewById(R.id.yAxisValuesTV);
        tvZ_AxisValA = (TextView) findViewById(R.id.zAxisValuesTV);

        LVXA = (ListView) findViewById(R.id.lvXA);
        LVYA = (ListView) findViewById(R.id.lvYA);
        LVZA = (ListView) findViewById(R.id.lvZA);

        xALA = new ArrayList<>();
        yALA = new ArrayList<>();
        zALA = new ArrayList<>();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        df = new DecimalFormat("00.00");

        SensorEventListener acceleSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                aXval = event.values[0];
                aYval = event.values[1];
                aZval = event.values[2];

                if(aXval<0){
                    aXval *= -1.0f;
                }
                if(aYval<0){
                    aYval *= -1.0f;
                }
                if(aZval<0){
                    aZval *= -1.0f;
                }

                tvXval.setText("x-axis real-time: " + df.format(aXval));
                tvYval.setText("y-axis real-time: " + df.format(aYval));
                tvZval.setText("z-axis real-time: " + df.format(aZval));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }; sm.registerListener(acceleSensorListener, acceleSensor, SensorManager.SENSOR_DELAY_NORMAL);

        btnStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((xALA!=null) || (yALA!=null) || (zALA!=null))
                {
                    xALA.clear();
                    yALA.clear();
                    zALA.clear();
                }
                startTimer();
                tvX_AxisValA.setText("x-axis average: 00.00");
                tvY_AxisValA.setText("y-axis average: 00.00");
                tvZ_AxisValA.setText("z-axis average: 00.00");
            }
        });
    }

    public void startTimer(){
        final int numOfSec = Integer.parseInt(etCountDownNum.getText().toString()) * 1000;

        new CountDownTimer(numOfSec, 1000){
            @Override
            public void onTick(long l) {
                etCountDownNum.setText("" + l / 1000);

                xALA.add(aXval);
                yALA.add(aYval);
                zALA.add(aZval);
            }

            @Override
            public void onFinish() {
                float sumXA = 0.0f;
                float sumYA = 0.0f;
                float sumZA = 0.0f;

                for (float i : xALA){
                    sumXA += i;
                }

                for (float i : yALA){
                    sumYA += i;
                }

                for (float i : zALA){
                    sumZA += i;
                }

                float sizeXALA = xALA.size();
                float sizeYALA = yALA.size();
                float sizeZALA = zALA.size();

                float avgXA = sumXA/sizeXALA;
                float avgYA = sumYA/sizeYALA;
                float avgZA = sumZA/sizeZALA;

                tvX_AxisValA.setText("x-axis average: " + df.format(avgXA));
                tvY_AxisValA.setText("y-axis average: " + df.format(avgYA));
                tvZ_AxisValA.setText("z-axis average: " + df.format(avgZA));

                etCountDownNum.setText("");
                etCountDownNum.setHint("done!");

                ArrayAdapter<Float> arrayAdapter = new ArrayAdapter<Float>(Accelerometer.this, android.R.layout.simple_list_item_1, xALA);
                LVXA.setAdapter(arrayAdapter);

                ArrayAdapter<Float> arrayAdapter1 = new ArrayAdapter<Float>(Accelerometer.this, android.R.layout.simple_list_item_1, yALA);
                LVYA.setAdapter(arrayAdapter1);

                ArrayAdapter<Float> arrayAdapter2 = new ArrayAdapter<Float>(Accelerometer.this, android.R.layout.simple_list_item_1, zALA);
                LVZA.setAdapter(arrayAdapter2);

                Call<ResponseBody> call = client.getInstance().getAPI().postAcceAvg(avgXA, avgYA, avgZA);
                call.enqueue(new Callback<ResponseBody>(){
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(Accelerometer.this, "Successfully added averages", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Accelerometer.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", t.toString());
                    }
                });
            }
        }.start();
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                    Intent intent = new Intent(Accelerometer.this, Gyroscope.class);
                    startActivity(intent);
                    x1=x2=y1=y2=0.0f;
                }else if (x1 > x2){
                    Intent intent = new Intent(Accelerometer.this, Gyroscope.class);
                    startActivity(intent);
                    x1=x2=y1=y2=0.0f;
                }else{
                    x1=x2=y1=y2=0.0f;
                }
                break;
        }
        return false;
    }
}
