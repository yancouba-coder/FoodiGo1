package com.example.foodigo1;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TempActivity extends AppCompatActivity {

    private static final String TAG="AndroidCameraApi";
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
    protected CameraDevice cameraDevice;
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
    private CameraDevice.StateCallback stateCallback;
    private String foodieName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        this.foodieName = getIntent().getExtras().getString("foodieName");
        textureView = findViewById(R.id.texture);
        //if (textureView != null) {
            textureView.setSurfaceTextureListener(textureView.getSurfaceTextureListener());

            //getIntent().getExtras().getSerializable("view");
            btnTake = findViewById(R.id.btnTake);
            //btnGallery = findViewById(R.id.btnGallery);


            //if (btnGallery != null) {
                btnTake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture();
                    }
                });



            //}

        //}
        stateCallback= new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                Log.e(TAG, "onOpende");
                cameraDevice=camera;
                createCameraPreview();

                if (cameraDevice != null) {
                    //btnTake.performClick();
                    //takePicture();
                    Log.e(TAG,"cameraDevice ok : takePicture a été appelé");
                    //TempActivity.this.finish();
                }
                Log.e(TAG,"stateCallback : onOpened");

            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                cameraDevice.close();

            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                cameraDevice.close();
                cameraDevice=null;

            }
        };

        if (cameraDevice != null) {
            //btnTake.performClick();
            //takePicture();
            //this.finish();
        }

    }

    TextureView.SurfaceTextureListener textureListener= new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera();
            Log.d(TAG, "surface = " + surface + ", width = "  + width+ ", height = " + height );
            if (cameraDevice != null) {
                //btnTake.performClick();
//takePicture();
                //TempActivity.this.finish();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            if (cameraDevice != null) {
                //btnTake.performClick();
//takePicture();
                //TempActivity.this.finish();
            }
            Log.e(TAG,"onSurfaceTextureSizeChanged");

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            if (cameraDevice != null) {
                //btnTake.performClick();
//takePicture();
                //TempActivity.this.finish();
            }
            Log.e(TAG,"onSurfaceTextureUpdated");

        }
    };


    protected void startBackgroundThred(){
        mBackgroundThred= new HandlerThread("Camera Background");
        mBackgroundThred.start();
        mBackgroundHandler=new Handler(mBackgroundThred.getLooper());
        if (cameraDevice != null) {
            //btnTake.performClick();
            //takePicture();
            //TempActivity.this.finish();
        }
        Log.e(TAG,"startBackgroundThred" + cameraDevice);


    }
    protected void stopBackgroundThread(){
        if (mBackgroundThred != null) {
            mBackgroundThred.quitSafely();
            try{
                mBackgroundThred.join();
                mBackgroundThred=null;
                mBackgroundHandler=null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        //android.graphics.SurfaceTexture@fc5693a, width = 0, height = 1004
        //textureListener.onSurfaceTextureAvailable((SurfaceTexture) textureView.getSurfaceTextureListener(),0,1004);
        if (cameraDevice != null) {
            //btnTake.performClick();
            //takePicture();
            //TempActivity.this.finish();
        }
        Log.e(TAG,"onStart");

    }

    protected void takePicture(){
        if(cameraDevice==null){
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        if(!isExternalStorageAvaibleForRW() ||isExternalStorageReadOnly()){
            btnTake.setEnabled(false);
        }
        if(isStoragePermissionGranted()){
            CameraManager manager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try{
                CameraCharacteristics characteristics= manager.getCameraCharacteristics(cameraDevice.getId());
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
                //Orientation
                int rotation= getWindowManager().getDefaultDisplay().getRotation();
                // Configurez la capture de l'image en fonction de la rotation de l'appareil
                Log.d(TAG, "rotation : " + rotation + ", Surface.ROTATION_0 = " + Surface.ROTATION_0 + ", Surface.ROTATION_90 : "+ Surface.ROTATION_90);
                /*if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
                } else {
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);
                }*/
                file=null;
                folder=new File(folderName);
                String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFilename= "IMG_" + timeStamp+".jpg";
                file=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"/"+imageFilename);

                if(!folder.exists()){
                    folder.mkdirs();
                }
                ImageReader.OnImageAvailableListener readerListener= new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
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
                        sendBroadcast(mediaScanIntent);

                        SharedPreferences sharedPref = TempActivity.this.getSharedPreferences(String.valueOf("path"), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("path", file.getPath());
                        editor.apply();
                        ManageFoodiesCaptured managerFoodieaptured = ManageFoodiesCaptured.getInstance(TempActivity.this);

                        managerFoodieaptured.newFoodieCaptured(foodieName,file.getPath());
                        finish();
                        startActivity(new Intent(TempActivity.this, ShowTotemActivity.class));


                    }
                };
                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
                final CameraCaptureSession.CaptureCallback captureListener=new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                        if (cameraDevice != null) {
                            //btnTake.performClick();
                            //takePicture();
                            //TempActivity.this.finish();
                        }
                        Log.e(TAG,"onCaptureStarted");
                    }

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        //Toast.makeText(TempActivity.this,"Saved"+file,Toast.LENGTH_LONG).show();
                        Log.d(TAG,""+file);

                        createCameraPreview();
                        if (cameraDevice != null) {
                            //btnTake.performClick();
                            //takePicture();
                            //TempActivity.this.finish();
                        }
                        Log.e(TAG,"onCaptureCompleted");
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
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true;

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return false;
            }
        }
        else{
            return true;
        }
    }
    protected void createCameraPreview(){
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
                    Toast.makeText(TempActivity.this,"Configuration change", Toast.LENGTH_SHORT).show();

                }

            } , null);


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cameraDevice != null) {
            //btnTake.performClick();
//takePicture();
            //TempActivity.this.finish();
        }
        Log.e(TAG,"createCameraPreview");

    }
    private void openCamera(){
        CameraManager manager=(CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG,"Is camera open");
        try{
            cameraID=manager.getCameraIdList()[0];
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map= characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map!=null;
            imageDimension=map.getOutputSizes(SurfaceTexture.class)[0];
            //add permission
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(TempActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
            }
            manager.openCamera(cameraID,stateCallback,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"openCamera X" );
        if (cameraDevice != null) {
            //takePicture();
            //TempActivity.this.finish();
        }
        Log.e(TAG,"openCamer bis " + cameraDevice);

    }
    protected void updatePreview(){
        if(null==cameraDevice){
            Log.e(TAG,"UpdatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"updatePreview :: btnTake.performClick(); takePicture();");
        if (cameraDevice != null) {
            //btnTake.performClick();
//takePicture();
            //this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        if(requestCode==REQUEST_CAMERA_PERMISSION){
            if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                Toast.makeText(TempActivity.this,"Sorry !!!, you cant use this app without granting Camera",Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }


    @Override
    protected void onResume(){
        super.onResume();
        Log.e(TAG,"OnResume" + cameraDevice);
        startBackgroundThred();
        if(textureView.isAvailable()){
            openCamera();
        }
        else{
            textureView.setSurfaceTextureListener(textureListener);
        }


    }
    @Override
    protected void onPause(){
        Log.e(TAG,"Onpause");
        stopBackgroundThread();
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.e(TAG,"onStop");
        stopBackgroundThread();
        super.onStop();

    }
    @Override
    protected void onDestroy() {
        Log.e(TAG,"onDestroy");
        stopBackgroundThread();
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e(TAG,"onRestart");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.e(TAG,"onPostCreate");
    }


}


