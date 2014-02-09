package com.example.hw3;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener2, LocationListener{

	private CameraBridgeViewBase mOpenCvCameraView;
	private static final String TAG = "HW3";
	
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;
	  
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
	    
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
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
	     
	     LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	     boolean enabled = service
	       .isProviderEnabled(LocationManager.GPS_PROVIDER);

	     // check if enabled and if not send user to the GSP settings
	     // Better solution would be to display a dialog and suggesting to 
	     // go to the settings
	     if (!enabled) {
	       Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	       startActivity(intent);
	     } 
	     
	     latituteField = (TextView) findViewById(R.id.TextView02);
	     longitudeField = (TextView) findViewById(R.id.TextView04);
	     
	  // Get the location manager
	     locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	     // Define the criteria how to select the locatioin provider -> use
	     // default
	     Criteria criteria = new Criteria();
	     provider = locationManager.getBestProvider(criteria, false);
	     Location location = locationManager.getLastKnownLocation(provider);

	     // Initialize the location fields
	     if (location != null) {
	       System.out.println("Provider " + provider + " has been selected.");
	       onLocationChanged(location);
	     } else {
	       latituteField.setText("Location not available");
	       longitudeField.setText("Location not available");
	     }
	}

	 @Override
	  public void onLocationChanged(Location location) {
	    int lat = (int) (location.getLatitude());
	    int lng = (int) (location.getLongitude());
	    latituteField.setText(String.valueOf(lat));
	    longitudeField.setText(String.valueOf(lng));
	  }
	 
	public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	     
	     locationManager.removeUpdates(this);
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
	 
	 @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
	  }
	  
	 
}
