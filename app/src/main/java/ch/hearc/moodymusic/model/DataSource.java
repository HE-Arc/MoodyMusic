package ch.hearc.moodymusic.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public abstract class DataSource {

    protected SQLiteDatabase mDatabase;
    private DatabaseHandler mDatabaseHandler;
    protected Context mContext;

    public DataSource(Context context) {
        mDatabaseHandler = new DatabaseHandler(context);
        mContext = context;
    }

    public SQLiteDatabase open() throws SQLException {
        mDatabase = mDatabaseHandler.getWritableDatabase();
        return mDatabase;
    }

    public void close() {
        mDatabaseHandler.close();
    }

    public SQLiteDatabase getDb() {
        return mDatabase;
    }
}
