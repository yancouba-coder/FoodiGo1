/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.foodigo1.augmentedimage;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.foodigo1.CompassService;
import com.example.foodigo1.ManageFoodiesCaptured;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.foodigo1.R;
import com.example.foodigo1.augmentedimage.rendering.AugmentedImageRenderer;
import com.example.foodigo1.common.helpers.CameraPermissionHelper;
import com.example.foodigo1.common.helpers.DisplayRotationHelper;
import com.example.foodigo1.common.helpers.FullScreenHelper;
import com.example.foodigo1.common.helpers.SnackbarHelper;
import com.example.foodigo1.common.helpers.TrackingStateHelper;
import com.example.foodigo1.common.rendering.BackgroundRenderer;

/**
 * This app extends the HelloAR Java app to include image tracking functionality.
 *
 * <p>In this example, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * AugmentedImage.getTrackingMethod() and render only when the tracking method equals to
 * FULL_TRACKING. See details in <a
 * href="https://developers.google.com/ar/develop/java/augmented-images/">Recognize and Augment
 * Images</a>.
 */
public class AugmentedImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer, CompassService.OnDirectionChangedListener{



  private static final String TAG = AugmentedImageActivity.class.getSimpleName();

  // Rendering. The Renderers are created here, and initialized when the GL surface is created.
  private GLSurfaceView surfaceView;
  private ImageView fitToScanView;
  private RequestManager glideRequestManager;
  private ManageFoodiesCaptured manager;
  private CompassService compassService ;

  private boolean installRequested;

  private Session session;
  private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
  private DisplayRotationHelper displayRotationHelper;
  private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);

  private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
  private final AugmentedImageRenderer augmentedImageRenderer = new AugmentedImageRenderer();

  private boolean shouldConfigureSession = false;

  // Augmented image configuration and rendering.
  // Load a single image (true) or a pre-generated image database (false).
  private final boolean useSingleImage = false;
  // Augmented image and its associated center pose anchor, keyed by index of the augmented image in
  // the
  // database.
  private final Map<Integer, Pair<AugmentedImage, Anchor>> augmentedImageMap = new HashMap<>();
  String foodieNameOnCapture;

  double userLatitude;
  double userLongitude;
  //Foodie positions
  double foodieLatitude;
  double foodieLongitude;
  public double getUserLatitude() {
    return userLatitude;
  }

  public double getUserLongitude() {
    return userLongitude;
  }

  public double getFoodieLatitude() {
    return foodieLatitude;
  }


  public double getFoodieLongitude() {
    return foodieLongitude;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_photo_api_arcore);
    surfaceView = findViewById(R.id.surfaceview);
    displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

    // Set up renderer.
    surfaceView.setPreserveEGLContextOnPause(true);
    surfaceView.setEGLContextClientVersion(2);
    surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
    surfaceView.setRenderer(this);
    surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    surfaceView.setWillNotDraw(false);

    fitToScanView = findViewById(R.id.image_view_fit_to_scan);
    glideRequestManager = Glide.with(this);

    manager = ManageFoodiesCaptured.getInstance(this);
    foodieNameOnCapture = getIntent().getExtras().getString("foodieName");

    // déplacé dans le onResume()
    //onTheGoodDirection(foodieNameOnCapture);



    installRequested = false;

     userLatitude=getIntent().getExtras().getDouble("userLatitude");
     userLongitude=getIntent().getExtras().getDouble("userLongitude");

    //Foodie positions
     foodieLatitude=getIntent().getExtras().getDouble("foodieLatitude");
     foodieLongitude=getIntent().getExtras().getDouble("foodieLongitude");
    //compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);

  }

  //quand le téléphone pointe dans la bonne direction
  private void onTheGoodDirection(String foodieName){
    Log.e(TAG,"onTheGoodDirection");
    //TODO : vérifie que le tel pointe dans la bonne direction

    // ce qui a été pasé en paramètre
    /*
    appPhoto.putExtra("userLatitude",userLatitude);
    appPhoto.putExtra("userLongitude",userLongitude);

    appPhoto.putExtra("foodieLatitude",foodieLatitude);
    appPhoto.putExtra("foodieLongitude",foodieLongitude);

    appPhoto.putExtra("foodieName", foodieName);
*/


    /*
    Affiche l'image du foodie en couleur au centre de l'écran
    */
    glideRequestManager
            //manager.getIdOfDrawableFoodie(foodieName) : retourne l'id qui correspond à l'image en couleur du foodie
            .load(manager.getIdOfDrawableFoodie(foodieName))
            //.load(Uri.parse("file:///android_asset/fit_to_scan.png"))
            .into(fitToScanView);

    fitToScanView.setVisibility(View.VISIBLE); //affiche le foodie

    //TODO : enregistrer la photo avec le foodie et récup le path
    String path = null;

    manager.newFoodieCaptured(foodieName, path);
  };

  @Override
  protected void onDestroy() {
    if (session != null) {
      // Explicitly close ARCore Session to release native resources.
      // Review the API reference for important considerations before calling close() in apps with
      // more complicated lifecycle requirements:
      // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
      session.close();
      session = null;
    }

    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    compassService = new CompassService();
    compassService.setOnDirectionChangedListener(this);
    onResumeBis();

    if (session == null) {
      Exception exception = null;
      String message = null;
      try {
        switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
          case INSTALL_REQUESTED:
            installRequested = true;
            return;
          case INSTALLED:
            break;
        }

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
          CameraPermissionHelper.requestCameraPermission(this);
          return;
        }

        session = new Session(/* context = */ this);
      } catch (UnavailableArcoreNotInstalledException
          | UnavailableUserDeclinedInstallationException e) {
        message = "Please install ARCore";
        exception = e;
      } catch (UnavailableApkTooOldException e) {
        message = "Please update ARCore";
        exception = e;
      } catch (UnavailableSdkTooOldException e) {
        message = "Please update this app";
        exception = e;
      } catch (Exception e) {
        message = "This device does not support AR";
        exception = e;
      }

      if (message != null) {
        messageSnackbarHelper.showError(this, message);
        Log.e(TAG, "Exception creating session", exception);
        return;
      }

      shouldConfigureSession = true;
    }

    if (shouldConfigureSession) {
      configureSession();
      shouldConfigureSession = false;
    }

    // Note that order matters - see the note in onPause(), the reverse applies here.
    try {
      session.resume();
    } catch (CameraNotAvailableException e) {
      messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
      session = null;
      return;
    }
    surfaceView.onResume();
    displayRotationHelper.onResume();

    //si le téléphone pointe vers le foodie renvoie true
    compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);

    if (compassService.userIsFrontOfFoodie(foodieLatitude,foodieLongitude,userLatitude,userLongitude)){
      //compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);
      onTheGoodDirection(foodieNameOnCapture);
    }


    //fitToScanView.setVisibility(View.VISIBLE);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (session != null) {
      // Note that the order matters - GLSurfaceView is paused first so that it does not try
      // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
      // still call session.update() and get a SessionPausedException.
      displayRotationHelper.onPause();
      surfaceView.onPause();
      session.pause();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
    super.onRequestPermissionsResult(requestCode, permissions, results);
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      Toast.makeText(
              this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG)
          .show();
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this);
      }
      finish();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

    // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
    try {
      // Create the texture and pass it to ARCore session to be filled during update().
      backgroundRenderer.createOnGlThread(/*context=*/ this);
      augmentedImageRenderer.createOnGlThread(/*context=*/ this);
    } catch (IOException e) {
      Log.e(TAG, "Failed to read an asset file", e);
    }
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    displayRotationHelper.onSurfaceChanged(width, height);
    GLES20.glViewport(0, 0, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear screen to notify driver it should not load any pixels from previous frame.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if (session == null) {
      return;
    }
    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session);

    try {
      session.setCameraTextureName(backgroundRenderer.getTextureId());

      // Obtain the current frame from ARSession. When the configuration is set to
      // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
      // camera framerate.
      Frame frame = session.update();
      Camera camera = frame.getCamera();

      // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
      trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

      // If frame is ready, render camera preview image to the GL surface.
      backgroundRenderer.draw(frame);

      // Get projection matrix.
      float[] projmtx = new float[16];
      camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

      // Get camera matrix and draw.
      float[] viewmtx = new float[16];
      camera.getViewMatrix(viewmtx, 0);

      // Compute lighting from average intensity of the image.
      final float[] colorCorrectionRgba = new float[4];
      frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

      // Visualize augmented images.
      drawAugmentedImages(frame, projmtx, viewmtx, colorCorrectionRgba);
    } catch (Throwable t) {
      // Avoid crashing the application due to unhandled exceptions.
      Log.e(TAG, "Exception on the OpenGL thread", t);
    }
  }

  private void configureSession() {
    Config config = new Config(session);
    config.setFocusMode(Config.FocusMode.AUTO);

    session.configure(config);
  }

  private void drawAugmentedImages(
      Frame frame, float[] projmtx, float[] viewmtx, float[] colorCorrectionRgba) {
    Collection<AugmentedImage> updatedAugmentedImages =
        frame.getUpdatedTrackables(AugmentedImage.class);

    // Iterate to update augmentedImageMap, remove elements we cannot draw.
    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      switch (augmentedImage.getTrackingState()) {
        case PAUSED:
          // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
          // but not yet tracked.
          String text = String.format("Detected Image %d", augmentedImage.getIndex());
          messageSnackbarHelper.showMessage(this, text);
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          this.runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  fitToScanView.setVisibility(View.GONE);
                }
              });

          // Create a new anchor for newly found images.
          if (!augmentedImageMap.containsKey(augmentedImage.getIndex())) {
            Anchor centerPoseAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
            augmentedImageMap.put(
                augmentedImage.getIndex(), Pair.create(augmentedImage, centerPoseAnchor));
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage.getIndex());
          break;

        default:
          break;
      }
    }

    // Draw all images in augmentedImageMap
    for (Pair<AugmentedImage, Anchor> pair : augmentedImageMap.values()) {
      AugmentedImage augmentedImage = pair.first;
      Anchor centerAnchor = augmentedImageMap.get(augmentedImage.getIndex()).second;
      switch (augmentedImage.getTrackingState()) {
        case TRACKING:
          augmentedImageRenderer.draw(
              viewmtx, projmtx, augmentedImage, centerAnchor, colorCorrectionRgba);
          break;
        default:
          break;
      }
    }
  }


  @Override
  public void onDirectionChanged(String direction) {
    Log.e("************* direction = ", direction);
    //position du joueur

    //compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);
    onTheGoodDirection(foodieNameOnCapture);
  }



  /********************************************************************************************
   ************************************* SERVICE BOUSSOLE *************************************
   ********************************************************************************************/

  private boolean serviceBound = false;

  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      CompassService.CompassBinder binder = (CompassService.CompassBinder) service;
      compassService = binder.getService();
      compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);
      serviceBound = true;
      Log.d("onServiceConnected", String.valueOf(serviceBound));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      serviceBound = false;
      Log.d("onServiceDisconnected", String.valueOf(serviceBound));
    }
  };


  private void onResumeBis() {
    super.onResume();
    System.out.println("MainActivity onResume: ");

    // Afficher l'orientation du téléphone dans la console à chaque mise à jour
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      Log.e(TAG, "Permission not granted  ACCESS_FINE_LOCATION ");
      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                298091);

        // LOCATION_PERMISSION_REQUEST_CODE is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    } else {
      // Permission has already been granted
    }


    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
      // Permission is not granted
      // Request permission
      Log.e(TAG, "Permission not granted  CAMERA ");
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.CAMERA},
              11);
    } else {
      Log.d(TAG, "Permission CAMERA : granted " );
    }


  }

  @Override
  protected void onStart() {
    System.out.println("AugmentedImageActivity onStart: ");

    /*** SERVICE BOUSSOLE **/
    Intent intent = new Intent(this, CompassService.class);
    // conection au service boussole
    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    /** SERVICE LOCALISATION **/
    super.onStart();
    // Vérifier si le service est lié
       /* if (mBound) {
            // Appeler la méthode getCurrentLocation() sur l'instance du service
            double[] location = mLocationService.getCurrentLocation();
            if (location != null) {
                // Utiliser la latitude et la longitude ici
            }
        }

        */

  }

  @Override
  protected void onStop() {

    System.out.println("MainActivity onStop: ");
    super.onStop();

    /** SERVICE BOUSSOLE **/
    if (serviceBound) {
      //deconnection du service boussole
      unbindService(serviceConnection);
      serviceBound = false;
    }

    /** SERVICE LOCALISATION **/
    // Délier le service de l'activité
       /* if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        */
  }





}
