package com.bigbawb.spidereater;
 
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import mygame.AndroidManager;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import java.util.logging.Level;
import java.util.logging.LogManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.jme3.app.AndroidHarness;
import com.google.android.gms.ads.*;
 
public class MainActivity extends AndroidHarness{
 
    /*
     * Note that you can ignore the errors displayed in this file,
     * the android project will build regardless.
     * Install the 'Android' plugin under Tools->Plugins->Available Plugins
     * to get error checks and code completion for the Android project files.
     */
    
    private AdView         adView;
    private AndroidManager androidManager;
 
    public MainActivity(){
        // Set the application class to run
        appClass = "mygame.Main";
        // Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
        eglConfigType = ConfigType.BEST;
        // Exit Dialog title & message
        exitDialogTitle = "Exit?";
        exitDialogMessage = "Press Yes";
        // Enable verbose logging
        eglConfigVerboseLogging = false;
        // Choose screen orientation
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        // Enable MouseEvents being generated from TouchEvents (default = true)
        mouseEventsEnabled = true;
        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);
    }
    
  @Override
  public void onCreate(Bundle state) {   
    super.onCreate(state);
    //setContentView(R.layout.main);

    if (app != null) {
      app.getStateManager().attach(new AndroidManager());
      androidManager = app.getStateManager().getState(AndroidManager.class);
      androidManager.setFilePath(getFilesDir().toString());
      }
    
    int androidVersion = android.os.Build.VERSION.SDK_INT;
        
    if (androidVersion > 10) {
    
      adView = new AdView(this);
      adView.setAdSize(AdSize.SMART_BANNER);
      adView.setAdUnitId("ca-app-pub-9434547190848397/5281988264");
      adView.buildLayer();
 
      LinearLayout ll = new LinearLayout(this);
      ll.setGravity(Gravity.TOP);
      ll.addView(adView);
      addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        
      AdRequest adRequest = new AdRequest.Builder().build();

      // Start loading the ad in the background.
      adView.loadAd(adRequest);
      adView.bringToFront();
      adView.requestFocus();
      }
    
    }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    System.runFinalization();
    android.os.Process.killProcess(android.os.Process.myPid());
    }
 
}
