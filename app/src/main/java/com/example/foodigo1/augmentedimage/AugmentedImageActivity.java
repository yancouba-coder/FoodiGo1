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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
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
import com.example.foodigo1.GalleryFoodiesActivity;
import com.example.foodigo1.ManageFoodiesCaptured;
import com.example.foodigo1.TempActivity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
public class AugmentedImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer, CompassService.OnDirectionChangedListener, View.OnClickListener {


  private static final String TAG = AugmentedImageActivity.class.getSimpleName();

  // Rendering. The Renderers are created here, and initialized when the GL surface is created.
  private GLSurfaceView surfaceView;
  private ImageView fitToScanView;
  private RequestManager glideRequestManager;
  private ManageFoodiesCaptured manager;
  private CompassService compassService;

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

  private AugmentedImageActivity act;
  Boolean isOnTheGoodDirection = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    act = this;
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

    userLatitude = getIntent().getExtras().getDouble("userLatitude");
    userLongitude = getIntent().getExtras().getDouble("userLongitude");

    //Foodie positions
    foodieLatitude = getIntent().getExtras().getDouble("foodieLatitude");
    foodieLongitude = getIntent().getExtras().getDouble("foodieLongitude");
    //compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);


    /*Log.e(TAG2,"onCreate");
    textureView = activity.findViewById(R.id.texture);
    if (textureView != null) {
      textureView.setSurfaceTextureListener(textureView.getSurfaceTextureListener());
            *//*
            btnTake = findViewById(R.id.btnTake);
            btnGallery = findViewById(R.id.btnGallery);
            if (btnGallery != null) {
                btnTake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture();
                    }
                });

                if (btnGallery != null) {
                    btnGallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }

             *//*
    }
*/

    //This code is added by myself
    //managerFoodieCaptured = ManageFoodiesCaptured.getInstance(activity);






  }

  //quand le téléphone pointe dans la bonne direction
  private void onTheGoodDirection(String foodieName) {
    Log.e(TAG, "onTheGoodDirection");
    //TODO : vérifie que le tel pointe dans la bonne direction

    /*
    Affiche l'image du foodie en couleur au centre de l'écran
    */
    glideRequestManager
            //manager.getIdOfDrawableFoodie(foodieName) : retourne l'id qui correspond à l'image en couleur du foodie
            .load(manager.getIdOfDrawableFoodie(foodieName))
            //.load(Uri.parse("file:///android_asset/fit_to_scan.png"))
            .into(fitToScanView);

    fitToScanView.setVisibility(View.VISIBLE); //affiche le foodie

    isOnTheGoodDirection = true;
  }

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
/* SERVICE BOUSSOLE **/
    Intent intent = new Intent(this, CompassService.class);
    // conection au service boussole
    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

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
    compassService.notifyThePosition(foodieLatitude, foodieLongitude, userLatitude, userLongitude);

    if (compassService.userIsFrontOfFoodie(foodieLatitude, foodieLongitude, userLatitude, userLongitude)) {
      //compassService.notifyThePosition(foodieLatitude,foodieLongitude,userLatitude,userLongitude);
      onTheGoodDirection(foodieNameOnCapture);
    }


    //fitToScanView.setVisibility(View.VISIBLE);

/*
    Log.e(TAG2,"OnResume");
    startBackgroundThred();
    if(textureView.isAvailable()){
      openCamera();
    }
    else{
      textureView.setSurfaceTextureListener(textureListener);
    }
*/


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
/*
    Log.e(TAG2,"Onpause");
    stopBackgroundThread();*/
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
/*
    Log.e(TAG2, "onRequestPermissionsResult");
    if(requestCode==REQUEST_CAMERA_PERMISSION){
      if(results[0]==PackageManager.PERMISSION_DENIED){
        Toast.makeText(activity,"Sorry !!!, you cant use this app without granting Camera",Toast.LENGTH_SHORT).show();
        activity.finish();
      }
    }*/

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

  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.d(TAG, "onServiceConnected");
      CompassService.CompassBinder binder = (CompassService.CompassBinder) service;
      compassService = binder.getService();
      compassService.notifyThePosition(foodieLatitude, foodieLongitude, userLatitude, userLongitude);

      compassService.setOnDirectionChangedListener(act);
      act.compassService = compassService;
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
    System.out.println("Augmented  onResume: ");

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
      Log.d(TAG, "Permission CAMERA : granted ");
    }


  }

  @Override
  protected void onStart() {
    System.out.println("AugmentedImageActivity onStart: ");

    /* SERVICE BOUSSOLE **/
    Intent intent = new Intent(this, CompassService.class);
    // conection au service boussole
    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    compassService = new CompassService();

    /* SERVICE LOCALISATION **/
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

    /* SERVICE BOUSSOLE **/
    if (serviceBound) {
      //deconnection du service boussole
      unbindService(serviceConnection);
      serviceBound = false;
    }

    /* SERVICE LOCALISATION **/
    // Délier le service de l'activité
       /* if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        */
  }


  @Override
  public void onClick(View v) {
    Intent i = null;
    switch (v.getId()) {
      case R.id.close:
        //i = new Intent(this, GalleryFoodiesActivity.class);
        finish();
        break;
      case R.id.takePicture:
        //TODO : gérer l'enregistrement de la photo
        //buttonScreenshot(v); //image du foodie et du bouton sur fond noir
        //takeScrenshotv2(); // tout noir
        //takeScreenshot@();//image du foodie sur fond noir
        //takePicture();
        //callTackPictureInBackground();
        //screenshoot();
        if (isOnTheGoodDirection){
          i = new Intent(this, TempActivity.class);
          i.putExtra("foodieName",foodieNameOnCapture);
        }else{
          Toast.makeText(this, "Dirige ton téléphone vers le foodie. Tourne sur toi même." , Toast.LENGTH_LONG).show();
        }

        //i = new Intent(this, GalleryFoodiesActivity.class);
      default:
        break;
    }
    if (i != null) {
      this.finish();
      startActivity(i);
      finish();

    }


  }



  /**************************************************************************************************************************
   ***************************************************** Prendre une photo **************************************************
   *************************************************************************************************************************/

  /*




    private static final String TAG2="AndroidCameraApi";
    private Button btnTake;
    private Button btnGallery;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS= new SparseIntArray();
    static {
      ORIENTATIONS.append(Surface.ROTATION_0,90);
      ORIENTATIONS.append(Surface.ROTATION_90,0);
      ORIENTATIONS.append(Surface.ROTATION_180,270);
      ORIENTATIONS.append(Surface.ROTATION_270,180);

    }
    private String cameraID;
    private CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSession;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private File folder;
    private String folderName="MyPhotoDir";
    private static final int REQUEST_CAMERA_PERMISSION=200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThred;
    private Activity activity = this;
    private String foodieName;
    private String photoPath;
    private static com.example.foodigo1.augmentedimage.TakePictureInBackground instance;
    private ManageFoodiesCaptured managerFoodieCaptured;


*/
  /*    private TakePictureInBackground(Activity activity, String foodieName){
      this.activity = activity;
      this.foodieName = foodieName;
      Log.e(TAG2,"privateeConstructor");
      onCreateTakePictureInBackground();
    }

    public static com.example.foodigo1.augmentedimage.TakePictureInBackground getInstance(Activity activity, String foodieName){
      Log.e(TAG2,"getInstance");
      if (instance == null) {
        instance = new com.example.foodigo1.augmentedimage.TakePictureInBackground( activity,  foodieName);

      }
      //onResumeTakePictureInBackground();
      return instance;

    }*/
  /*



  private CameraDevice.StateCallback  stateCallback= new CameraDevice.StateCallback() {

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
      Log.e(TAG2, "onOpende");
      cameraDevice=camera;
      createCameraPreview();

    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
      cameraDevice.close();

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
      Log.e(TAG2,"on error");
      cameraDevice.close();
      cameraDevice=null;

    }
  };

    TextureView.SurfaceTextureListener textureListener= new TextureView.SurfaceTextureListener() {
      @Override
      public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        openCamera();
        Log.e(TAG2,"onSurfaceTextureAvailable");

      }

      @Override
      public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

      }

      @Override
      public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
      }

      @Override
      public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

      }
    };


    private void startBackgroundThred(){
      mBackgroundThred= new HandlerThread("Camera Background");
      mBackgroundThred.start();
      mBackgroundHandler=new Handler(mBackgroundThred.getLooper());


    }
    private void stopBackgroundThread(){
      mBackgroundThred.quitSafely();
      try{
        mBackgroundThred.join();
        mBackgroundThred=null;
        mBackgroundHandler=null;

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }


    protected void takePicture(){
      Log.e(TAG2,"takePicture");

      if(cameraDevice==null){
        Log.e(TAG2, "cameraDevice is null");
        return;
      }
      if(!isExternalStorageAvaibleForRW() ||isExternalStorageReadOnly()){
        btnTake.setEnabled(false);
      }
      if(isStoragePermissionGranted()){
        Log.e(TAG2,"takePicture : isStoragePermissionGranted");
        CameraManager cameraManager= (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try{
          Log.e(TAG2,"takePicture : try");

          CameraCharacteristics characteristics= cameraManager.getCameraCharacteristics(cameraDevice.getId());
          Size[] jpegSizes=null;
          if(characteristics!=null){
            jpegSizes=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
          }
          int width=640;
          int height=480;
          if(jpegSizes!=null && jpegSizes.length>0){
            width=jpegSizes[0].getWidth();
            height=jpegSizes[0].getHeight();
          }
          ImageReader reader= ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
          List<Surface> outputSurfaces= new ArrayList<>(2);
          outputSurfaces.add(reader.getSurface());
          outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
          final CaptureRequest.Builder captureBuilder= cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
          captureBuilder.addTarget(reader.getSurface());
          captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
          Log.e(TAG2,"takePicture : after captureBuilder");

          //Orientation
          int rotation= activity.getWindowManager().getDefaultDisplay().getRotation();
          // Configurez la capture de l'image en fonction de la rotation de l'appareil
          if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
          } else {
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);
          }
          file=null;
          folder=new File(folderName);
          String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
          String imageFilename= "IMG_" + timeStamp+".jpg";
          file=new File(activity.getExternalFilesDir(folderName),"/"+imageFilename);
          if(!folder.exists()){
            folder.mkdirs();
          }
          ImageReader.OnImageAvailableListener readerListener= new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
              Log.e(TAG2,"takePicture : onImageAvailable");

              Image image= null;
              try {
                image=reader.acquireLatestImage();

                ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                byte[] bytes= new byte[buffer.capacity()];
                buffer.get(bytes);
                save(bytes);

              }
              catch (FileNotFoundException e){
                e.printStackTrace();
              }
              catch (IOException e){
                e.printStackTrace();
              }
              finally {
                if(image!=null){
                  image.close();
                }

              }


            }
            private void save(byte[] bytes)throws IOException{
              Log.e(TAG2,"save");

              OutputStream output=null;
              try{
                output=new FileOutputStream(file);
                output.write(bytes);
              }finally {
                if(null!=output){
                  output.close();
                }
              }
              Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
              Uri contentUri = Uri.fromFile(file);
              System.out.println("Path de la photo = " + file.getPath());
              mediaScanIntent.setData(contentUri);
              activity.sendBroadcast(mediaScanIntent);
              photoPath = file.getPath();
              SharedPreferences sharedPref = activity.getSharedPreferences(String.valueOf(R.string.nameOfAbsolutePathPreference), Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = sharedPref.edit();
              editor.putString(foodieName, photoPath);

              editor.apply();
              managerFoodieCaptured.newFoodieCaptured(foodieName, photoPath);


            }
          };
          reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
          final CameraCaptureSession.CaptureCallback captureListener=new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
              super.onCaptureStarted(session, request, timestamp, frameNumber);
              Toast.makeText(activity, "La photo a été enregistrée", Toast.LENGTH_LONG).show();
              Log.e(TAG2,"onCaptureStarted");

            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
              super.onCaptureCompleted(session, request, result);
              Toast.makeText(activity,"Saved"+file,Toast.LENGTH_LONG).show();
              Log.d(TAG2,""+file);
              createCameraPreview();
              Log.e(TAG2,"onCaptureCompleted");

            }
          };
          cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
              try {
                session.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
              } catch (CameraAccessException e) {
                e.printStackTrace();
              }

            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
          }, mBackgroundHandler);


        } catch (CameraAccessException e) {
          e.printStackTrace();
        }

      }
    }




    private static boolean isExternalStorageReadOnly(){
      String extStorageState = Environment.getExternalStorageState();
      if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)){
        return true;
      }
      return false;
    }
    private boolean isExternalStorageAvaibleForRW(){

      String extStorageState=Environment.getExternalStorageState();
      if(extStorageState.equals(Environment.MEDIA_MOUNTED)){
        return true;
      }
      return false;

    }
    private boolean isStoragePermissionGranted(){
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
        if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
          return true;

        }
        else{
          ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
          return false;
        }
      }
      else{
        return true;
      }
    }
    protected void createCameraPreview(){
      Log.e(TAG2,"createCameraPreview");

      try {
        SurfaceTexture texture= textureView.getSurfaceTexture();
        assert  texture !=null;

        texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
        Surface surface= new Surface(texture);
        captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);
        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
          @Override
          public void onConfigured(@NonNull CameraCaptureSession session) {
            if(null==cameraDevice){
              return;
            }
            //when the session is ready
            cameraCaptureSession=session;
            updatePreview();

          }

          @Override
          public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(activity,"Configuration change", Toast.LENGTH_SHORT).show();

          }

        } , null);


      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    private void openCamera(){
      CameraManager cameraManager=(CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
      Log.e(TAG2,"Is camera open");
      try{
        cameraID=cameraManager.getCameraIdList()[0];
        CameraCharacteristics characteristics=cameraManager.getCameraCharacteristics(cameraID);
        StreamConfigurationMap map= characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert map!=null;
        imageDimension=map.getOutputSizes(SurfaceTexture.class)[0];
        //add permission
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
          ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
        }
        cameraManager.openCamera(cameraID,stateCallback,null);
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
      Log.e(TAG2,"openCamera X" );
    }
    protected void updatePreview(){
      if(null==cameraDevice){
        Log.e(TAG2,"UpdatePreview error, return");
      }
      captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
      try {
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
    }






*/
  /*
    private void takePicture() {
      // Créez un objet Intent pour lancer l'application de capture d'écran du système
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Créez un fichier dans lequel enregistrer l'image capturée
      File imageFile = File.createTempFile("screenshot", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
      // Définissez le fichier créé comme destination de l'image capturée
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
      // Appelez l'application de capture d'écran du système
      startActivityForResult(intent, REQUEST_CODE_SCREENSHOT);

      if (requestCode == REQUEST_CODE_SCREENSHOT && resultCode == RESULT_OK) {
        // Récupérez le chemin de l'image capturée
        Uri imageUri = data.getData();
        String imagePath = imageUri.getPath();
        // Vous pouvez maintenant accéder à l'image en utilisant le chemin stocké dans la variable imagePath
        // ...
      }
    }
  */
  private void screenshoot() {
    Date date = new Date();
    CharSequence now = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
    getIntent().getData();
    String filename = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + now + ".jpg";
    Log.d("chemin : " , filename);
    View root = getWindow().getDecorView();
    root.setDrawingCacheEnabled(true);
    Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
    root.setDrawingCacheEnabled(false);

    File file = new File(filename);
    file.getParentFile().mkdirs();

    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
      fileOutputStream.flush();
      fileOutputStream.close();

      Uri uri = Uri.fromFile(file);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(uri, "image/*");
      startActivity(intent);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void takeScrenshotv2() {
    GLSurfaceView surfaceView = (GLSurfaceView) findViewById(R.id.surfaceview);
    //ImageView imageView = (ImageView) findViewById(R.id.image_view_fit_to_scan);

// Activer le cache de dessin pour la GLSurfaceView et l'ImageView
    surfaceView.setDrawingCacheEnabled(true);
    //imageView.setDrawingCacheEnabled(true);

// Dessiner la GLSurfaceView et l'ImageView dans le cache de dessin
    surfaceView.buildDrawingCache();
    //imageView.buildDrawingCache();

// Récupérer les instances de Bitmap du cache de dessin de la GLSurfaceView et de l'ImageView
    Bitmap surfaceViewBitmap = surfaceView.getDrawingCache();
    //Bitmap imageViewBitmap = imageView.getDrawingCache();

// Créer une nouvelle instance de Bitmap pour stocker l'image de la superposition de la GLSurfaceView et de l'ImageView
    //Bitmap mergedBitmap = Bitmap.createBitmap(surfaceViewBitmap.getWidth(), surfaceViewBitmap.getHeight(), surfaceViewBitmap.getConfig());

// Créer un nouvel objet Canvas à partir de la Bitmap fusionnée
    //Canvas canvas = new Canvas(mergedBitmap);

// Dessiner les Bitmap de la GLSurfaceView et de l'ImageView dans l'objet Canvas
    //canvas.setBitmap(surfaceViewBitmap);
    //canvas.setBitmap(imageViewBitmap);
    //canvas.drawBitmap(surfaceViewBitmap, 0, 0, null);
    //canvas.drawBitmap(imageViewBitmap, 0, 0, null);
    //Bitmap image = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
    //canvas.setBitmap(image);
// Enregistrer l'image fusionnée dans un fichier
    //saveImageInPhoneGallery(image);
    saveImageInPhoneGallery(surfaceViewBitmap);


// TODO : à tester
  }

  public void buttonScreenshot(View view) {
    View view1 = view.getRootView();
//        View view1 = getWindow().getDecorView().getRootView();

    Bitmap bitmap = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    view1.draw(canvas);
    File fileScreenshot = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            Calendar.getInstance().getTime() + ".jpg");
    String path = fileScreenshot.getAbsolutePath();
    manager.newFoodieCaptured(foodieNameOnCapture, path);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(fileScreenshot);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
      fileOutputStream.flush();
      fileOutputStream.close();
      Log.d("buttonScreenshot", "succes");
    } catch (Exception e) {
      Log.e("buttonScreenshot", "erroor");

    }
  }


  public void takeScreenshotv3() {
    GLSurfaceView surfaceView = (GLSurfaceView) findViewById(R.id.surfaceview);

// Obtenir la session de capture de contenu de la GLSurfaceView
    //ContentCaptureSession captureSession = surfaceView.getContentCaptureSession();
    //public static final int SCREEN_CAPTURE_INTENT = 8;

// Créer une nouvelle instance de CaptureRequest
    //captureSession.cap
    //CaptureRequest request = captureSession.createCaptureRequest(SCREEN_CAPTURE_INTENT);

// Déclencher l'enregistrement de la capture d'écran
    //captureSession.capture(request, null, null);

  }

  public void callTackPictureInBackground(){
    //takePicture();
  }

  private void takeScreenshot() {

    Date now = new Date();
    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

    try {
      // image naming and path  to include sd card  appending name you choose for file
      String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

      // create bitmap screen capture
      View v1 = getWindow().getDecorView().getRootView();
      v1.setDrawingCacheEnabled(true);
      Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

      v1.setDrawingCacheEnabled(false);

      File imageFile = new File(mPath);

      // Enregistrer l'image dans la galerie
      saveImageInPhoneGallery(bitmap);

      FileOutputStream outputStream = new FileOutputStream(imageFile);
      int quality = 100;
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
      outputStream.flush();
      outputStream.close();

      //openScreenshot(imageFile);
    } catch (Throwable e) {
      // Several error may come out with file handling or DOM
      e.printStackTrace();
    }
  }

  private void openScreenshot(File imageFile) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(imageFile);
    intent.setDataAndType(uri, "image/*");
    startActivity(intent);
  }

  public void saveImageInPhoneGallery(Bitmap bitmap) {
    String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "overlay" + imageFileName, "description");
    String path = getContentResolver() + "/" + imageFileName;
    manager.newFoodieCaptured(foodieNameOnCapture, path);
    Toast.makeText(this, "SAave as : " + path, Toast.LENGTH_LONG).show();

  }

}











