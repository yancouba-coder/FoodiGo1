package com.example.foodigo1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.foodigo1.augmentedimage.AugmentedImageActivity;


public class CompassService extends Service implements SensorEventListener {
    private static final String TAG = "CompassService";

    // Le gestionnaire de capteurs pour accéder à la boussole
    private SensorManager sensorManager;
    // Le capteur de type TYPE_ORIENTATION pour obtenir les données d'orientation
    private Sensor orientationSensor;

    // L'orientation actuelle du téléphone (N, S, E, W)
    private String orientation = "N";

    // Instance de CompassBinder pour lier le service à l'activité
    private final IBinder binder = new CompassBinder();
    Double foodieLatitude;
    Double foodieLongitude;
    Double userLatitude;
    Double userLongitude;

    public void notifyThePosition(double foodieLatitude, double foodieLongitude, double userLatitude, double userLongitude) {
        Log.d(TAG, "notifyThePosition" );
        this.foodieLatitude = foodieLatitude;
        this.foodieLongitude = foodieLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    // Classe interne pour lier le service à l'activité
    public class CompassBinder extends Binder {
        public CompassService getService() {
            return CompassService.this;
        }
    }
    String orientationPhone;
    String directionToGetTheFoodie;

    @Override

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        // Obtenir le gestionnaire de capteurs et le capteur de type TYPE_ROTATION_VECTOR
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (orientationSensor == null) {
            Log.e("CompassService.onCreate()","Le capteur TYPE_ORIENTATION n'est pas disponible");

        } else {
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("CompassService.onCreate()","Le capteur TYPE_ORIENTATION est bel et bien disponible");
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "********** onStartCommand");
        // Enregistrer un écouteur pour recevoir les mises à jour du capteur
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // Désenregistrer l'écouteur du capteur
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("CompassService ; onBind ");
        System.out.println("CompassService ; orientationPhone " + orientationPhone);
        System.out.println("CompassService ; directionToGetTheFoodie " + directionToGetTheFoodie);

        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            // Vérifiez si l'événement provient du capteur d'orientation
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                // Récupérez la valeur de l'azimut (orientation en degrés)
                float azimuth = event.values[0];

                // Convertissez l'azimut en orientation N S E W
                String orientation = "";
                if (azimuth >= 337.5 || azimuth < 22.5) {
                    orientation = "N";
                } else if (azimuth >= 22.5 && azimuth < 67.5) {
                    orientation = "NE";
                } else if (azimuth >= 67.5 && azimuth < 112.5) {
                    orientation = "E";
                } else if (azimuth >= 112.5 && azimuth < 157.5) {
                    orientation = "SE";
                } else if (azimuth >= 157.5 && azimuth < 202.5) {
                    orientation = "S";
                } else if (azimuth >= 202.5 && azimuth < 247.5) {
                    orientation = "SW";
                } else if (azimuth >= 247.5 && azimuth < 292.5) {
                    orientation = "W";
                } else if (azimuth >= 292.5 && azimuth < 337.5) {
                    orientation = "NW";
                }
                orientationPhone = orientation;
                Log.e("Juste before the if : ", orientationPhone + directionToGetTheFoodie + orientation);
                //Log.d(TAG, String.valueOf(foodieLatitude + foodieLongitude + userLatitude + userLongitude));
                if (directionToGetTheFoodie ==null &&
                        foodieLatitude != null &&
                        foodieLongitude != null &&
                        userLatitude != null &&
                        userLongitude != null  ){
                    userIsFrontOfFoodie(foodieLatitude,foodieLongitude,userLatitude,userLongitude);
                }
                Log.e("Juste after the null if : ", orientationPhone + directionToGetTheFoodie + orientation);
                //setOnDirectionChangedListener(AugmentedImageActivity.class);
                System.out.println(mListener);
                if (orientationPhone == directionToGetTheFoodie && mListener != null) {
                    mListener.onDirectionChanged(directionToGetTheFoodie);
                    Log.e("In the if : orientationPhone == orientation ", orientationPhone + directionToGetTheFoodie + orientation);

                }

                // Affichez ou utilisez l'orientation N S E W
                Log.d(TAG, "Orientation: " + orientation);
            }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("CompassService ; onAccuracyChanged");
        // Ce callback n'est pas utilisé dans ce exemple
    }

    // Méthode pour obtenir l'orientation actuelle du téléphone (N, S, E, W)
    public String getOrientation() {
        //System.out.println("CompassService ; getOrientation");
        Log.d("CompassService.getOrientation",orientation);
        return orientation;
    }

    /*
    Retrourne une direction qui correspond à la direction que doit suivre le joueur pour aller vers le fooodie.
     */
    public String getDirectionOfFoodie(double latitudeTarget, double longitudeTarget, double latitudeUser, double longitudeUser) {
        // Calcule la différence entre les coordonnées de la cible et de l'utilisateur
        // On converti les coordonnées en un vecteur qui a pour origine l'utilisateur et qui se dirige vers la cible.
        // vectorAngleInDegrees renvoie la direction du vecteur dans l'espace en degré. Cette direction nous permet de diviser
        // les directions en 8 troncons de 45° N,NE,E,SE,S,SW,W,NW et de convertir laa direction du vecteur en orientation
        // Un angle de 0 correspond au plein est, 180 oou -180 au plein ouest, 90 au plein nord et -90 au plein sud
        double latitudeDiff = latitudeTarget - latitudeUser;
        //Log.d("latitudeDiff", String.valueOf(latitudeDiff));
        double longitudeDiff = longitudeTarget - longitudeUser;
        //Log.d("longitudeDiff", String.valueOf(longitudeDiff));
        String direction;
        double vectorAngle = Math.atan2(latitudeDiff, longitudeDiff);
        //Log.d("vectorAngle", String.valueOf(vectorAngle));

        double vectorAngleInDegrees = Math.toDegrees(vectorAngle);
        Log.d("vectorAngleInDegrees", String.valueOf(vectorAngleInDegrees));

        if ( -22.5 <= vectorAngleInDegrees && vectorAngleInDegrees < 22.5){
            direction = "E";
        } else if ( 22.5 <= vectorAngleInDegrees && vectorAngleInDegrees < 67.5){
            direction = "NE";
        }else if ( 67.5 <= vectorAngleInDegrees && vectorAngleInDegrees < 112.5){
            direction = "E";
        }else if ( 112.5 <= vectorAngleInDegrees && vectorAngleInDegrees < 157.5){
            direction = "NW";
        }else if ( 157.5 <= vectorAngleInDegrees && vectorAngleInDegrees < 180){
            direction = "W";
        }else if ( -157.5 >= vectorAngleInDegrees && vectorAngleInDegrees > -180){
            direction = "W";
        }else if ( -112.5 >= vectorAngleInDegrees && vectorAngleInDegrees > -157.5){
            direction = "SW";
        }else if ( -67.5 >= vectorAngleInDegrees && vectorAngleInDegrees > -112.5){
            direction = "S";
        }else if (-22.5 >= vectorAngleInDegrees && vectorAngleInDegrees > -67.5){
            direction = "SE";
        }else{
            Log.e("CompassService.getOrientation()", "les coordonnées saisies n'ont pas permis d'indiquer la direction du vecteur. Target: " + latitudeTarget + "," + longitudeTarget + "User: "+ latitudeUser + "," + longitudeTarget);
            return null;
        }
        Log.d("CompassService.getOrientation() : direction = ", direction);
        return direction ;

        /*
        double tolerance = 0.0001;
        // Determine la direction en fonction des différences.
        if (latitudeDiff > 0 && longitudeDiff < tolerance) {
            return "N";
        }  else if (latitudeDiff < 0 && longitudeDiff < -tolerance) {
            return "S";
        } else if (longitudeDiff > 0 && latitudeDiff < tolerance) {
            return "E";
        } else if (longitudeDiff < 0 && latitudeDiff < -tolerance) {
            return "W";
        } else if (latitudeDiff > 0 && longitudeDiff > 0) {
            return "NE";
        } else if (latitudeDiff > 0 && longitudeDiff < 0) {
            return "NW";
        }else if (latitudeDiff < 0 && longitudeDiff > 0) {
            return "SE";
        } else if (latitudeDiff < 0 && longitudeDiff < 0) {
            return "SW";
        } else {
            return "";
        }
        */
    }

    /*
    Le téléphone de l'utilisateur est orienté vers le foodie
     */
    public Boolean userIsFrontOfFoodie(double foodieLatitude, double foodieLongitude, double userLatitude, double userLongitude){
        orientationPhone = getOrientation();


        directionToGetTheFoodie = getDirectionOfFoodie(foodieLatitude,foodieLongitude,userLatitude,userLongitude);

        Log.e("orientationPhone ",orientationPhone);
        Log.e("direction ",directionToGetTheFoodie);
        if (orientationPhone == directionToGetTheFoodie){

            return true;
        }else{
            return false;
        }
    }

    public interface OnDirectionChangedListener {
        void onDirectionChanged(String direction);
    }
    private OnDirectionChangedListener mListener;

    public void setOnDirectionChangedListener(OnDirectionChangedListener listener) {
        mListener = listener;
        Log.e(TAG, "setOnDirectionChangedListener" + mListener);
    }


}


