package com.example.hw3;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.example.hw3.R;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;

import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements CvCameraViewListener2, SensorEventListener
, OnSeekBarChangeListener{

	private CameraBridgeViewBase mOpenCvCameraView;
	private static final String TAG = "HW3";
	
	private TextView latituteField;
	private TextView longitudeField;
	
	private SeekBar sb;
	// GPSTracker class
    GPSTracker gps;
	  

    private SensorManager mSensorManager; 
    private Sensor mAccelerometer; 
    private Sensor mGyroscope;
    
    private int imageType = 0;  //0: RGB, 1: Grayscale, 2: Binary, 3: Intensity-normalized
                                //4: Gamma-corrected
    
    private Mat lut;
    
    private void setLut(double gamma)
    {
    	 for( int i = 0; i < 256; i++ )  
    	 {  
    	        double data = Math.pow((double)(i/255.0), gamma) * 255.0;  
    	        lut.put(0, i, data);
    	 } 
    }
    
    private void setBarTextInvisible()
    {
    	sb.setVisibility(SeekBar.INVISIBLE);
        ((TextView)findViewById(R.id.sbNum)).setVisibility(TextView.INVISIBLE);
        ((TextView)findViewById(R.id.sbValName)).setVisibility(TextView.INVISIBLE);
    }
    
    private void setBarTextVisible()
    {
    	sb.setVisibility(SeekBar.VISIBLE);
        ((TextView)findViewById(R.id.sbNum)).setVisibility(TextView.VISIBLE);
        ((TextView)findViewById(R.id.sbValName)).setVisibility(TextView.VISIBLE);
    }
    
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                mOpenCvCameraView.enableView();
	                
	                openCvInit();
	                
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	private void openCvInit()
	{
		lut = new Mat(1, 256, CvType.CV_8U);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	   //         WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		sb = (SeekBar)findViewById(R.id.seekBar1);
		sb.setVisibility(SeekBar.INVISIBLE);

		mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.Hw3CameraView);
	     mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	     mOpenCvCameraView.setCvCameraViewListener(this);
	     mOpenCvCameraView.setZOrderOnTop(true);
	     mOpenCvCameraView.getHolder().setFormat(PixelFormat.TRANSPARENT);
	     
	     mOpenCvCameraView.setMaxFrameSize(500, 900);
	    
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
         
         
      
         mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
         mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	  
         mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
         mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
         
       
	}

	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
	    
	}
	 
	public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	     
	 
	     mSensorManager.unregisterListener(this);
	 }

	 public void onDestroy() {
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main, menu);
		//return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_rgb:
	            imageType = 0;
	            Toast.makeText(getApplicationContext(), "RGB image", Toast.LENGTH_SHORT).show();
	            setBarTextInvisible();
	            return true;
	        case R.id.action_gray:
	            imageType = 1;
	            Toast.makeText(getApplicationContext(), "Grayscale image", Toast.LENGTH_SHORT).show();
	            setBarTextInvisible();
	            return true;
	        case R.id.action_binary:
	        	imageType = 2;
	        	Toast.makeText(getApplicationContext(), "Binary image", Toast.LENGTH_SHORT).show();
	        	setBarTextVisible();
	        	sb.setMax(255);
	        	sb.setProgress(127);
	        	sb.setOnSeekBarChangeListener(this);
	        	((TextView)findViewById(R.id.sbNum)).setText(Integer.toString(127));
	        	((TextView)findViewById(R.id.sbValName)).setText("Threshold");
	        	return true;
	        case R.id.action_rgbNorm:
	        	imageType = 3;
	        	Toast.makeText(getApplicationContext(), "Intensity-normalized image", Toast.LENGTH_SHORT).show();
	        	setBarTextInvisible();
	        	return true;
	        case R.id.action_gamma:
	        	imageType = 4;
	        	Toast.makeText(getApplicationContext(), "Gamma-corrected image", Toast.LENGTH_SHORT).show();
	        	setBarTextVisible();
	        	((TextView)findViewById(R.id.sbValName)).setText("Gamma");
	        	sb.setMax(100);
	        	sb.setProgress(10);
	        	sb.setOnSeekBarChangeListener(this);
	        	((TextView)findViewById(R.id.sbNum)).setText(Integer.toString(1));
	        	setLut(1);
	        	return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onCameraViewStarted(int width, int height) {
	 }

	 public void onCameraViewStopped() {
	 }

	 public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		 Mat ret = null;
		 switch (imageType) {
		 case 0: //rgb
			 ret = inputFrame.rgba();
		
			 break;
		 case 1: //grayscale
			 ret = inputFrame.gray();
			 
			 break;
		 case 2: //Binary
			 Mat matGray = inputFrame.gray();
			 ret = matGray;
			 
			 TextView tv = (TextView)findViewById(R.id.sbNum);
			 int thresh = Integer.parseInt(tv.getText().toString());
			// Log.v(TAG, "Thresh: " + thresh);
			 
			 Imgproc.threshold(matGray, ret, thresh, 255, Imgproc.THRESH_BINARY);
			 break;
		 
		 case 3: //Intensity-normalized
			 ret = inputFrame.rgba();
			 
			 int channelNum = ret.channels();
			 int rowNum = (int) ret.rows();
		     int colNum = (int) ret.cols();
		     //Log.v(TAG, rowNum + ", " + colNum);
		     int sum = 255;
		    // int x = 0;
		    // int y = 0;
		     for (int x = 0; x < rowNum; x++)
		    	 for (int y = 0; y < colNum; y++)
		    	 {
		    		 double[] rgb = ret.get(x, y);
		    		 double total = rgb[0] + rgb[1] + rgb[2];
		    				    		 
		    		 //Log.v(TAG, rgba);
		    		 
		    		// double[] rgbn = new double[channelNum];
		    		 
		    		 rgb[0] = rgb[0]/total*(double)sum;
		    		 rgb[1] = rgb[1]/total*(double)sum;
		    		 rgb[2] = rgb[2]/total*(double)sum;
		    		 
		    		 
		    		 ret.put(x, y, rgb);
		    		 
		    		 
		    	 }
		    
		    // Log.v(TAG, rgbn[0] + ", " + rgbn[1] + ", " + rgbn[2])
			 break;
			 
		 case 4: //Gamma-corrected
			 Mat rgba = inputFrame.rgba();
			 ret = rgba;
			 Core.LUT(rgba, lut, ret);
			 break;
	     default:
	    	 ret = inputFrame.rgba();
	    	 break;
		 }
	     return ret;
	 }
	 
	
     @Override
	 public void onAccuracyChanged(Sensor sensor,int accuracy){
			
    }
		
     @Override
    public void onSensorChanged(SensorEvent event){
    	
    	 
    	 if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
	    	 float x = event.values[0];
	    	 float y = event.values[1];
	    	 float z = event.values[2];
	
	
	    	 ((TextView)findViewById(R.id.AccX)).setText(Float.toString(x));
	
	    	 ((TextView)findViewById(R.id.AccY)).setText(Float.toString(y));
	
	    	 ((TextView)findViewById(R.id.AccZ)).setText(Float.toString(z));
    	 } else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
    		 float x = event.values[0];
	    	 float y = event.values[1];
	    	 float z = event.values[2];
	    	 
	    	 ((TextView)findViewById(R.id.GyrX)).setText(Float.toString(x));
	    		
	    	 ((TextView)findViewById(R.id.GyrY)).setText(Float.toString(y));
	
	    	 ((TextView)findViewById(R.id.GyrZ)).setText(Float.toString(z));
    	 }

    	 
    }
     
     @Override
 	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
 		TextView tv = (TextView)findViewById(R.id.sbNum);
 		
 		if (imageType == 2)
 			tv.setText(Integer.toString(progress));	
 		else if (imageType == 4) {
 			double dGamma = (double)progress/10.0;
 			setLut(dGamma);
 			tv.setText(String.valueOf(dGamma));	
 		}
 	}

 	@Override
 	public void onStartTrackingTouch(SeekBar seekBar) {
 		// TODO Auto-generated method stub
 		
 	}

 	@Override
 	public void onStopTrackingTouch(SeekBar seekBar) {
 		// TODO Auto-generated method stub
 		
 	}
	  
	 
}
