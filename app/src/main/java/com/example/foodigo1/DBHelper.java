package com.example.foodigo1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "FoodiGO.db";
    public DBHelper(Context context) {
        super(context, "FoodiGO.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(ID INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, prenom TEXT, pseudo TEXT,password TEXT, date_de_naissance TEXT, score INTEGER,listFoodiesCaptured TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(User user){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("nom", user.getNom());
        contentValues.put("prenom", user.getPrenom());
        contentValues.put("pseudo", user.getPseudo());
        contentValues.put("date_de_naissance", user.getDateDenaissance());
        contentValues.put("password", user.getPassWord());
        contentValues.put("score",0);
        contentValues.put("listFoodiesCaptured", "");
        long result = MyDB.insert("users", null, contentValues);
        if(result==-1) return false;
        else
            return true;
    }

    public Boolean checkPseudo(String _pseudo) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where pseudo = ?", new String[]{_pseudo});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public Boolean checkUserPseudoPassword(String _pseudo, String _password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where pseudo = ? and password = ?", new String[] {_pseudo,_password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public void updateScore(String _pseudo, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", newScore); // Mettre à jour la valeur de la colonne score
        String whereClause = "pseudo" + " = ?";
        String[] whereArgs = { String.valueOf(_pseudo) };
        db.update(DBNAME, values, whereClause, whereArgs);
    }
    public void updateListOfFoodieCaptured(String _peudo, String foodieName){

    }
    public User getByPseudo(String pseudo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "nom", "prenom", "pseudo","password", "date_de_naissance" , "score" ,"listFoodiesCaptured" };
        String selection = "pseudo" + " = ?";
        String[] selectionArgs = { String.valueOf(pseudo) };
        Cursor cursor = db.query(DBNAME, columns, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
           /* dataModel = new DataModel();
            dataModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            dataModel.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            dataModel.setScore(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE)));

            */
           // String lisfortfoodie=cursor.getString(cursor.getColumnIndex("listFoodiesCaptured"));
        }
        cursor.close();
        return user;
    }
}