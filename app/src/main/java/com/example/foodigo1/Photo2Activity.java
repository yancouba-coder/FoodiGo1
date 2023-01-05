package com.example.foodigo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Photo2Activity extends AppCompatActivity {




        private TextureView textureView;
        private Surface overlayImage;
        private CameraDevice cameraDevice;
        private ImageReader imageReader;
        CameraManager cameraManager;
        List<Surface> outputSurfaces;
        private CameraCaptureSession cameraCaptureSession;
    // Créer la demande de capture
    CaptureRequest.Builder requestBuilder = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_photo2);

            // Récupérer l'ImageView qui sert d'overlay
            final View overlayImage = findViewById(R.id.overlay_image);

            // Récupérer le bouton pour prendre une photo
            Button takePictureButton = findViewById(R.id.take_picture_button);
            takePictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Prendre une photo
                    takePicture();
                }
            });

            // Récupérer le bouton pour démarrer/arrêter la caméra
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button toggleCameraButton = findViewById(R.id.toggle_camera_button);
            toggleCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Démarrer/arrêter la caméra
                    if (cameraDevice == null) {
                        startCamera();
                        toggleCameraButton.setText(R.string.stop_camera);
                    } else {
                        stopCamera();
                        toggleCameraButton.setText(R.string.start_camera);
                    }
                }
            });

            // Récupérer le gestionnaire de caméra
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);



            // Créer un bitmap à partir de l'overlay
            Bitmap bitmap = drawableToBitmap(getDrawable(R.drawable.mangue));
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            // Créer un canvas vide qui sera utilisé pour dessiner l'overlay
            //Canvas canvas = new Canvas(bitmap);

            // Dessiner l'overlay sur le bitmap
            overlayImage.draw(canvas);


            overlayImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Obtenir les dimensions de l'overlay
                    int width = overlayImage.getWidth();
                    int height = overlayImage.getHeight();
                    Log.d("La taille de l'image est ", width + ", " + height);
                    ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
                    imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = reader.acquireLatestImage();
                            // Traiter l'image ici
                            image.close();
                        }
                    }, null);
                    // Enlever l'écouteur
                    overlayImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });




            // Connecter la surface à l'ImageReader
            //imageReader.setSurface(overlayImage);

            // Créer une surface à partir du bitmap
            //Surface surface = new Surface(new SurfaceTexture(bitmap));
            // Récupérer l'image de surimpression à partir de l'ImageView
            //Bitmap overlayBitmap = Bitmap.createBitmap(getDrawable(R.drawable.mangue));

            // Dessiner l'image de surimpression sur le Canvas
            //canvas.drawBitmap(bitmap, 0, 0, null);


            // Ajouter la surface à la liste des surfaces de sortie
            //outputSurfaces.add(surface);

            // Ajouter la surface à la requête de capture
            //requestBuilder.addTarget(surface);



        }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

        @Override
        protected void onResume() {
            super.onResume();

            // Démarrer la caméra lorsque l'application est mise en premier plan
            startCamera();
        }

        @Override
        protected void onPause() {
            super.onPause();

            // Arrêter la caméra lorsque l'application est mise en arrière-plan
            stopCamera();
        }

        private void startCamera() {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Demande la permission de caméra à l'utilisateur
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
                        10);
            }
            // Obtenir une instance de CameraManager
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            try {
                // Obtenir le nom de la caméra à utiliser
                String cameraId = cameraManager.getCameraIdList()[0];

                // Obtenir les caractéristiques de la caméra
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                // Définir les dimensions de la vue de prévisualisation
                Size previewSize = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(SurfaceTexture.class)[0];

                // Configurer la session de capture
                //SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                //surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                //Surface previewSurface = new Surface(surfaceTexture);

                // Créer un reader d'image pour récupérer l'image finale
                imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(),
                        ImageFormat.JPEG, 2);

                // Configurer la liste des surfaces de sortie
                // Créer la liste des surfaces de sortie
                outputSurfaces = Arrays.asList(imageReader.getSurface());

                // Créer la session de capture

                // Créer la session de capture
                try {
                    Log.d("outputSurfaces", outputSurfaces.toString());
                    cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // Enregistrer la session de capture
                            cameraCaptureSession = session;

                            // Démarrer la capture
                            try {
                                cameraCaptureSession.setRepeatingRequest(requestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // Traitement en cas d'échec de la configuration de la session de capture
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        try {
                            cameraDevice = camera;
                            camera.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {


                                    //Voici comment déclarer les paramètres de la requête de capture :
                                    try {
                                        requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                    requestBuilder.addTarget(overlayImage);
                                    requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                    requestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);

                                    // Démarrer la capture
                                    try {
                                        session.capture(requestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                                            @Override
                                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                // Récupérer l'image finale
                                                Image image = imageReader.acquireNextImage();
                                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                                byte[] bytes = new byte[buffer.capacity()];
                                                buffer.get(bytes);

                                                // Enregistrer l'image dans un fichier
                                                FileOutputStream outputStream = null;
                                                try {
                                                    outputStream = new FileOutputStream(new File(getExternalFilesDir(null), "photo.jpg"));
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    outputStream.write(bytes);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    outputStream.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                // Libérer les ressources
                                                image.close();
                                                session.close();
                                                camera.close();

                                                Toast.makeText(Photo2Activity.this, "Photo enregistrée !", Toast.LENGTH_SHORT).show();
                                            }
                                        }, null);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                    // Traitement en cas d'échec de la configuration de la session
                                }
                            }, null);
                        } catch (CameraAccessException e) {
                            // Traitement en cas d'exception
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        // Traitement en cas de déconnexion de la caméra
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        // Traitement en cas d'erreur de la caméra
                    }
                }, null);
            } catch (CameraAccessException e) {
                // Traitement en cas d'exception
            }
            // Créer la session de capture
            try {
                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        // Enregistrer la session de capture
                        cameraCaptureSession = session;

                        // Démarrer la capture
                        try {
                            cameraCaptureSession.setRepeatingRequest(requestBuilder.build(), null, null);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        // Traitement en cas d'échec de la configuration de la session de capture
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        private void stopCamera() {
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }

        }

        private void takePicture() {
            try {
                // Créer la requête de capture
                CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.addTarget(imageReader.getSurface());

                // Configurer les paramètres de la requête
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                // Préparer le gestionnaire de fichier pour enregistrer l'image
                File outputFile = new File(getExternalFilesDir(null), "image.jpg");
                OutputStream outputStream = new FileOutputStream(outputFile);
                imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        // Récupérer l'image
                        Image image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        // Écrire l'image dans le fichier
                        try {
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // Fermer l'image et le fichier
                            image.close();
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, null);

                // Envoyer la requête de capture et attendre la réponse
                CaptureRequest captureRequest = captureRequestBuilder.build();
                cameraCaptureSession.capture(captureRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }









}








