package world.develop.nomroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Miguel on 22/11/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NomRoute_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String USERS_TABLE_NAME = "users_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NICKNAME";
    private static final String COL_3 = "PASSWORD";
    private static final String COL_4 = "EMAIL";
    private static final String COL_5 = "NUMBER_PHOTOS";
    private static final String COL_6 = "NUMBER_TRACKS";
    private static final String COL_7 = "NUMBER_ORDERS";

    private static final String TRACK_TABLE_NAME = "track_table";
    private static final String TRACK_COL_1 = "TRACK_ID";
    private static final String TRACK_COL_2 = "DESCRICAO";
    private static final String TRACK_COL_3 = "TRACK_NUMBER";
    private static final String TRACK_COL_4 = "USER_ID";
    private static final String TRACK_COL_5 = "TRACKED";
    private static final String TRACK_COL_6 = "USER_TRACKED";
    private static final String TRACK_COL_7 = "SAVED_ROUTE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        createUserTable(db);
        createTrackTable(db);

        if (getAllDataTrack().getCount() == 0) {
            insertTrackData(db, "Pizza com cenas", 123456);
            insertTrackData(db, "Hamburguer com batatas", 654321);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createUserTable(sqLiteDatabase);
        createTrackTable(sqLiteDatabase);

        insertTrackData(sqLiteDatabase, "Pizza de galinha e natas", 123456);
        insertTrackData(sqLiteDatabase, "McMenu Royal Bacon", 654321);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRACK_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void createUserTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + USERS_TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY , "
                + COL_2 + " TEXT, " + COL_3 + " TEXT, " + COL_4 + " TEXT, " + COL_5 + " INTEGER, " +
                COL_6 + " INTEGER, " + COL_7 + " INTEGER)");
    }

    public void createTrackTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TRACK_TABLE_NAME + " (" + TRACK_COL_1 + " INTEGER PRIMARY KEY, " +
                TRACK_COL_2 + " TEXT, " + TRACK_COL_3 + " INTEGER, " + TRACK_COL_4 + " INTEGER, "
                + TRACK_COL_5 + " INTEGER, " + TRACK_COL_6 + " INTEGER, " + TRACK_COL_7 +
                " INTEGER,  FOREIGN KEY (" + TRACK_COL_4 + ") REFERENCES "
                + USERS_TABLE_NAME + "(" + COL_1 + "))");
    }

    public boolean insertData(SQLiteDatabase db, String nickname, String password, String email,
                              int numberOfPhotos, int numberOfTracks, int numberOfOrders) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, nickname);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, email);
        contentValues.put(COL_5, numberOfPhotos);
        contentValues.put(COL_6, numberOfTracks);
        contentValues.put(COL_7, numberOfOrders);
        long result = db.insert(USERS_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;

        return true;
    }

    /**
     * Inserir uma encomenda na tabela de track
     *
     * @param descricao   descricacao da encomenda
     * @param trackNumber numero da encomenda
     * @return true se adicionou, false se nao adicionou
     */
    public boolean insertTrackData(SQLiteDatabase db, String descricao, int trackNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_COL_2, descricao);
        contentValues.put(TRACK_COL_3, trackNumber);
        contentValues.put(TRACK_COL_5, 0);

        long result = db.insert(TRACK_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;

        return true;

    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + USERS_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllDataTrack() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TRACK_TABLE_NAME, null);
        return res;
    }

    public Cursor getUsername(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.query(USERS_TABLE_NAME,
                new String[]{COL_2, COL_3},
                COL_2 + "=?" + " AND " + COL_3 + "=?",
                new String[]{username, password},
                null, null, null);

        return res;
    }

    public boolean updatePhotos(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numero = numberOfPhotos(username);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, numero + 1);

        db.update(USERS_TABLE_NAME, contentValues, "NICKNAME = ?", new String[]{username});

        return true;
    }

    public int numberOfPhotos(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor fotos = db.rawQuery("SELECT NUMBER_PHOTOS FROM users_table WHERE NICKNAME = ?", new String[]{username});
        fotos.moveToNext();

        int numero = fotos.getInt(fotos.getColumnIndex(COL_5));

        return numero;
    }

    public String getEmail(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor email = db.rawQuery("SELECT " + COL_4 + " FROM " + USERS_TABLE_NAME + " WHERE " + COL_2 +
                " = ?", new String[]{username});
        email.moveToNext();

        String user_email = email.getString(email.getColumnIndex(COL_4));

        return user_email;
    }

    public boolean getCodigo(String codigo, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor track_code = db.rawQuery("SELECT " + TRACK_COL_3 + ", " + TRACK_COL_5 + " FROM " + TRACK_TABLE_NAME +
                " WHERE " + TRACK_COL_3 + " =?", new String[]{codigo});

        track_code.moveToNext();


        if (track_code.getCount() == 1) {
            ContentValues contentValues = new ContentValues();
            ContentValues userValues = new ContentValues();

            int numero = numberOfTracks(codigo);
            int userTracked = userTracked(codigo);

            if (numero == 0) {
                contentValues.put(TRACK_COL_5, numero + 1);
                db.update(TRACK_TABLE_NAME, contentValues, TRACK_COL_3 + " = ?", new String[]{codigo});
            }

            if (username != null && username != "") {
                int id = getUserId(username);
                int userTracks = userNumberTracks(username);

                contentValues.put(TRACK_COL_4, id);
                db.update(TRACK_TABLE_NAME, contentValues, TRACK_COL_3 + " = ?", new String[]{codigo});

                if (userTracked == 0) {
                    contentValues.put(TRACK_COL_6, 1);
                    db.update(TRACK_TABLE_NAME, contentValues, TRACK_COL_3 + " = ?", new String[]{codigo});

                    userValues.put(COL_6, userTracks + 1);
                    db.update(USERS_TABLE_NAME, userValues, COL_2 + " =?", new String[]{username});
                }
            }

            return true;

        } else
            return false;
    }

    /**
     * Vai buscar o valor de track de um encomenda da tabela de track
     *
     * @param codigo
     * @return
     */
    public int numberOfTracks(String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor tracked = db.rawQuery("SELECT " + TRACK_COL_5 + " FROM " + TRACK_TABLE_NAME +
                " WHERE " + TRACK_COL_3 + " =?", new String[]{codigo});

        tracked.moveToNext();

        int numero = tracked.getInt(tracked.getColumnIndex(TRACK_COL_5));

        return numero;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id = db.rawQuery("SELECT " + COL_1 + " FROM " + USERS_TABLE_NAME + " WHERE " + COL_2 + " =?", new String[]{username});

        id.moveToNext();

        int userId = id.getInt(id.getColumnIndex(COL_1));

        return userId;
    }

    /**
     * Vai buscar se na tabela de tracks o utilizador já foi registado que fez track ou não
     *
     * @param codigo
     * @return
     */
    public int userTracked(String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor tracked = db.rawQuery("SELECT " + TRACK_COL_6 + " FROM " + TRACK_TABLE_NAME +
                " WHERE " + TRACK_COL_3 + " =?", new String[]{codigo});

        tracked.moveToNext();

        int numero = tracked.getInt(tracked.getColumnIndex(TRACK_COL_6));

        return numero;
    }

    /**
     * Vai buscar o numero de tracks que o utilizador tem atualmente
     *
     * @param username
     * @return
     */
    public int userNumberTracks(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor tracks = db.rawQuery("SELECT " + COL_6 + " FROM " + USERS_TABLE_NAME +
                " WHERE " + COL_2 + " =?", new String[]{username});
        tracks.moveToNext();

        int numero = tracks.getInt(tracks.getColumnIndex(COL_6));

        return numero;
    }

    public boolean saveRoute(String codigo, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        ContentValues userValues = new ContentValues();

        Cursor saved = db.rawQuery("SELECT " + TRACK_COL_7 + " FROM " + TRACK_TABLE_NAME +
                " WHERE " + TRACK_COL_3 + " =?", new String[]{codigo});

        saved.moveToNext();
        int numberOfSaved = saved.getInt(saved.getColumnIndex(TRACK_COL_7));
        int user_saves = getUserSavedRoutes(username);

        if (numberOfSaved == 0) {

            content.put(TRACK_COL_7, 1);
            db.update(TRACK_TABLE_NAME, content, TRACK_COL_3 + " =?", new String[]{codigo});

            userValues.put(COL_7, user_saves + 1);
            db.update(USERS_TABLE_NAME, userValues, COL_2 + " =?", new String[]{username});

            return true;
        }

        return false;

    }

    public int getUserSavedRoutes(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor saved = db.rawQuery("SELECT " + COL_7 + " FROM " + USERS_TABLE_NAME +
                " WHERE " + COL_2 + " =?", new String[]{username});

        saved.moveToNext();
        int numberOfSaved = saved.getInt(saved.getColumnIndex(COL_7));

        return numberOfSaved;
    }

    public void changeMail(String userMail, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues userValues = new ContentValues();

        userValues.put(COL_4, userMail);
        db.update(USERS_TABLE_NAME, userValues, COL_2 + " =?", new String[]{username});

    }

    public void changePassword(String password, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues userValues = new ContentValues();

        userValues.put(COL_3, password);
        db.update(USERS_TABLE_NAME, userValues, COL_2 + " =?", new String[]{username});

    }

    public Cursor getUserTracks(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        int userId = getUserId(username);
        Cursor orders = db.rawQuery("SELECT * FROM " + TRACK_TABLE_NAME +
                " WHERE " + TRACK_COL_4 + " =?", new String[]{String.valueOf(userId)});

        return orders;
    }
}

