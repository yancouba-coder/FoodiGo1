package com.example.foodigo1;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RETOUR_START_ACTIVITY = 1;
    String photoPath = null;
    ImageView photoFoodieCaptured = null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private File photoFile;
    private String currentPhotoPath;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        photoFoodieCaptured = findViewById(R.id.photoFoodieCaptured);
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(this);
        manager.updatePoints(this);


        // Déclaration de l'objet ActivityResultContract
        ActivityResultContract<Void, Bitmap> takePicture = new ActivityResultContract<Void, Bitmap>() {
            @Override
            public Intent createIntent(Context context, Void input) {
                // Création de l'Intent pour démarrer l'Activity de prise de photo
                return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            }

            @Override
            public Bitmap parseResult(int resultCode, Intent result) {
                // Traitement du résultat de l'Activity de prise de photo
                return (Bitmap) result.getExtras().get("data");
            }
        };

        // Déclaration de l'objet ActivityResultCallback
        ActivityResultCallback<Bitmap> callback = new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                // Traitement du résultat de l'Activity de prise de photo une fois qu'elle a été terminée

                MediaStore.Images.Media.insertImage(getContentResolver(), result, "nom image", "description");
                onPictureTaken(result);
            }
        };
        // Enregistrement du callback pour obtenir le résultat de l'Activity de prise de photo
        ActivityResultLauncher<Void> launcher = registerForActivityResult(takePicture, callback);
        launcher.launch(null);
        //findViewById(R.id.back).setOnClickListener(v -> launcher.launch(null));

     }



    private void onPictureTaken(Bitmap imageBitmap) {

        // Définir la largeur et la hauteur souhaitées pour l'image redimensionnée
        int width = 700;
        int height = 1200;

// Redimensionner l'image
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, true);

        // Affecter l'image à l'objet ImageView
        photoFoodieCaptured.setImageBitmap(scaledBitmap);
        // Enregistrer l'image dans la galerie
        saveImageToGallery(imageBitmap);
    }

    private void saveImageToGallery(Bitmap imageBitmap) {
        // Vérifiez si le répertoire de l'appareil photo existe
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory");
                return;
            }
        }

        // Créez un nom de fichier unique
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";


        try {
            // Créez un fichier temporaire
            photoFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Enregistrez le chemin de la photo
            currentPhotoPath = photoFile.getAbsolutePath();

            // Écrivez l'image dans le fichier
            FileOutputStream out = new FileOutputStream(photoFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ajoutez la photo à la galerie
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        System.out.println("Path de la photto = " + photoFile.getPath());
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
//TODO : modifier ouu effacer les lignes si dessus
    }


    private void listPhotos() {
        // Obtenir le répertoire où sont enregistrées les photos
        File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Vérifier que le répertoire existe
        if (photoDir != null) {
            // Obtenir la liste des fichiers dans le répertoire
            File[] photoFiles = photoDir.listFiles();

            // Vérifier que la liste n'est pas vide
            if (photoFiles != null) {
                // Parcourir la liste des fichiers et afficher leur nom
                for (File file : photoFiles) {
                    if (file.isFile()) {
                        System.out.println(file.getName());
                    }
                }
            }
        }
    }


    /*
    private void takePicture(){
        //On crée un intent pour ouvrir une fentre pour prendre la photo
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //test pour controler que l'intent peut être géré
        if (i.resolveActivity(getPackageManager()) != null){
            //créer un nom de fichier unique
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo"+time,".jpg",photoDir);

                //enregistrer le chemin de la photo
                photoPath = photoFile.getAbsolutePath();

                //création de l'uri
                Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this, PhotoActivity.this.getApplicationContext().getPackageName()+"provider",photoFile);

                //transfert uri vers l'inten poour enregristrement photo dans fichier temporaire
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(i, RETOUR_START_ACTIVITY);
                //registerForActivityResult(i.Star)
                //photo.launch(null);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    /*
    private ActivityResultCallback<Bitmap> callback = new ActivityResultCallback<Bitmap>() {
        @Override
        public void onActivityResult(Bitmap result) {
            Bitmap image = result.copy(Bitmap.Config.ARGB_8888,true);
            photoFoodieCaptured.setImageBitmap(image);
        }
    };*/
/*
    private ActivityResultContract<Void, Bitmap> contract = new ActivityResultContract<Void, Bitmap>(){
        @Override
        public Intent createIntent(Context context, Void input){
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        @Override
        public Bitmap parseResult(int resultCode, Intent result) {
            return (Bitmap) result.getExtras().get("data") ;
        }
    };

    //ActivityResultLauncher<Void> photo = registerForActivityResult(contract, callback);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RETOUR_START_ACTIVITY && resultCode==RESULT_OK){
            Bitmap image = BitmapFactory.decodeFile(photoPath);
            photoFoodieCaptured.setImageBitmap(image);

        }
    }
*/
    private void takePicture() {
        System.out.println("Begin TakePicture");
        // Créez un objet Intent pour lancer l'appareil photo
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Vérifiez que l'appareil photo est disponible
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Créez un fichier pour stocker l'image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Gérez les exceptions ici
            }
            // Continuez seulement si le fichier a été créé correctement
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "$(applicationId).provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Enregistrez le résultat de l'activité de prise de photo
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Traitez le résultat ici
                    }
                }).launch(takePictureIntent);
            }
        }
        System.out.println("End TakePicture");
    }

    private File createImageFile() throws IOException {
        // Créez un nom de fichier unique
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* préfixe */
                ".jpg",         /* suffixe */
                storageDir      /* répertoire */
        );

        // Enregistrez le chemin absolu du fichier image pour utilisation ultérieure
        photoPath = image.getAbsolutePath();
        changeImage(image);
        System.out.println("End createImageFile");
        return image;
    }

    /*
    Affiche l'image prise dans l'imageView
     */
    public void changeImage(File image){
        // Charger l'image depuis le chemin de fichier en utilisant la méthode decodeFile() de BitmapFactory
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

        // Afficher l'image dans l'ImageView
        photoFoodieCaptured.setImageBitmap(bitmap);
        System.out.println("End changeImage");

    }

    @Override
    public void onClick(View view) {
        System.out.println("Begin onClick");
        // onCreate() ajoute le laucher en setOnClickListner
        // ActivityResultLauncher<Void> launcher = registerForActivityResult(takePicture, callback);
        // findViewById(R.id.back).setOnClickListener(v -> launcher.launch(null));

        switch (view.getId()) {
            case (R.id.menu): //menu
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;
            case (R.id.back): //retour
                if (view.getParent() != null) {
                    //on redirige vers l'activité qui appelle
                    finish();
                } else {
                    //si on a perdu le prent on redirige vers l'acceuil
                    startActivity(new Intent(this, MainActivity.class));
                }
                //revenir sur l'activité appelante'
                break;
        }
    }
}