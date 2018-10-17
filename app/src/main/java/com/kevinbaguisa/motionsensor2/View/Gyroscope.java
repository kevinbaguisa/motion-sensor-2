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

public class Gyroscope extends AppCompatActivity {

    TextView tvTimer, tvX_AxisValG, tvY_AxisValG, tvZ_AxisValG, tvXval, tvYval, tvZval;
    Button btnStartTimer;
    EditText etCountDownNum;
    SensorManager sm;
    Sensor gyroSensor;
    ArrayList<Float> xALG, yALG, zALG;
    ListView LVXG, LVYG, LVZG;
    float gXval, gYval, gZval;
    DecimalFormat df;
    float x1,x2,y1,y2;
    ClientInstance client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        tvXval = (TextView) findViewById(R.id.valXG);
        tvYval = (TextView) findViewById(R.id.valYG);
        tvZval = (TextView) findViewById(R.id.valZG);

        tvTimer = (TextView) findViewById(R.id.timerTV);
        btnStartTimer = (Button) findViewById(R.id.startTimerBtnG);
        etCountDownNum = (EditText) findViewById(R.id.countDownNumETG);

        tvX_AxisValG = (TextView) findViewById(R.id.xAxisValuesTVG);
        tvY_AxisValG = (TextView) findViewById(R.id.yAxisValuesTVG);
        tvZ_AxisValG = (TextView) findViewById(R.id.zAxisValuesTVG);

        LVXG = (ListView) findViewById(R.id.lvXG);
        LVYG = (ListView) findViewById(R.id.lvYG);
        LVZG = (ListView) findViewById(R.id.lvZG);

        xALG = new ArrayList<>();
        yALG = new ArrayList<>();
        zALG = new ArrayList<>();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        df = new DecimalFormat("00.00");

        SensorEventListener gyroSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gXval = event.values[0];
                gYval = event.values[1];
                gZval = event.values[2];

                if(gXval<0){
                    gXval *= -1.0f;
                }
                if(gYval<0){
                    gYval *= -1.0f;
                }
                if(gZval<0){
                    gZval *= -1.0f;
                }

                tvXval.setText("x-axis real-time: " + df.format(gXval));
                tvYval.setText("y-axis real-time: " + df.format(gYval));
                tvZval.setText("z-axis real-time: " + df.format(gZval));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }; sm.registerListener(gyroSensorListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

        btnStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((xALG!=null) || (yALG!=null) || (zALG!=null))
                {
                    xALG.clear();
                    yALG.clear();
                    zALG.clear();
                }
                startTimer();
                tvX_AxisValG.setText("x-axis average: 00.00");
                tvY_AxisValG.setText("y-axis average: 00.00");
                tvZ_AxisValG.setText("z-axis average: 00.00");
            }
        });
    }

    public void startTimer(){
        final int numOfSec = Integer.parseInt(etCountDownNum.getText().toString()) * 1000;

        new CountDownTimer(numOfSec, 1000){
            @Override
            public void onTick(long l) {
                etCountDownNum.setText("" + l / 1000);

                xALG.add(gXval);
                yALG.add(gYval);
                zALG.add(gZval);
            }

            @Override
            public void onFinish() {
                float sumXG = 0.0f;
                float sumYG = 0.0f;
                float sumZG = 0.0f;

                for (float i : xALG){
                    sumXG += i;
                }

                for (float i : yALG){
                    sumYG += i;
                }

                for (float i : zALG){
                    sumZG += i;
                }

                float sizeXALG = xALG.size();
                float sizeYALG = yALG.size();
                float sizeZALG = zALG.size();

                float avgXG = sumXG/sizeXALG;
                float avgYG = sumYG/sizeYALG;
                float avgZG = sumZG/sizeZALG;

                tvX_AxisValG.setText("x-axis average: " + df.format(avgXG));
                tvY_AxisValG.setText("y-axis average: " + df.format(avgYG));
                tvZ_AxisValG.setText("z-axis average: " + df.format(avgZG));

                etCountDownNum.setText("");
                etCountDownNum.setHint("done!");

                ArrayAdapter<Float> arrayAdapter0 = new ArrayAdapter<Float>(Gyroscope.this, android.R.layout.simple_list_item_1, xALG);
                LVXG.setAdapter(arrayAdapter0);

                ArrayAdapter<Float> arrayAdapter1 = new ArrayAdapter<Float>(Gyroscope.this, android.R.layout.simple_list_item_1, yALG);
                LVYG.setAdapter(arrayAdapter1);

                ArrayAdapter<Float> arrayAdapter2 = new ArrayAdapter<Float>(Gyroscope.this, android.R.layout.simple_list_item_1, zALG);
                LVZG.setAdapter(arrayAdapter2);

                Call<ResponseBody> call = client.getInstance().getAPI().postGyroAvg(avgXG, avgYG, avgZG);
                call.enqueue(new Callback<ResponseBody>(){
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(Gyroscope.this, "Successfully added averages", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Gyroscope.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(Gyroscope.this, Accelerometer.class);
                    startActivity(intent);
                    x1=x2=y1=y2=0.0f;
                }else if (x1 > x2){
                    Intent intent = new Intent(Gyroscope.this, Accelerometer.class);
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
