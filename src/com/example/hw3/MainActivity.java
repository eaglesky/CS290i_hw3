package com.example.hw3;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;


import android.os.Bundle;

import android.app.Activity;

import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends Activity implements CvCameraViewListener2, SensorEventListener{

	private CameraBridgeViewBase mOpenCvCameraView;
	private static final String TAG = "HW3";
	
	private TextView latituteField;
	private TextView longitudeField;
	
	// GPSTracker class
    GPSTracker gps;
	  
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                mOpenCvCameraView.enableView();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	    
	    
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.Hw3CameraView);
	     mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	     mOpenCvCameraView.setCvCameraViewListener(this);
	     mOpenCvCameraView.setZOrderOnTop(true);
	     mOpenCvCameraView.getHolder().setFormat(PixelFormat.TRANSPARENT);
	     
	    
	     latituteField = (TextView) findViewById(R.id.TextView02);
	     longitudeField = (TextView) findViewById(R.id.TextView04);
	     
	 
	     gps = new GPSTracker(this);
	     
         // check if GPS enabled    
         if(gps.canGetLocation()){
              
             double lat = gps.getLatitude();
             double lng = gps.getLongitude();
              
             // \n is for new line
             //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
             latituteField.setText(String.valueOf(lat));
             longitudeField.setText(String.valueOf(lng));
             
         }else{
             // can't get location
             // GPS or Network is not enabled
             // Ask user to enable GPS/network in settings
             gps.showSettingsAlert();
             latituteField.setText("Location not available");
  	         longitudeField.setText("Location not available");
  	       
         }
         
	  
	}

	
	 
	public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	     
	 
	 }

	 public void onDestroy() {
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onCameraViewStarted(int width, int height) {
	 }

	 public void onCameraViewStopped() {
	 }

	 public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
	     return inputFrame.rgba();
	 }
	 
	

	 public void onAccuracyChanged(Sensor sensor,int accuracy){
			
    }
		
    public void onSensorChanged(SensorEvent event){
    	
    }
	  
	 
}
