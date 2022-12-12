package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
public class DatabaseHandler extends SQLiteOpenHelper {
    //Class to handle the things in the database
    //Like adding,removing and updating the database
    private static final String Db_name="200007G.db";
    private static final int version=1;

    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String ACCOUNT_NO = "Number";
    public static final String BANK_NAME = "bank";
    public static final String HOLDER_NAME= "holder";
    public static final String BALANCE= "balance";

    public static final String TRANSACTION_TABLE_NAME = "transaction_table_name";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expense_type";
    public static final String AMOUNT = "amount";
    public static final String TRANSACTION_ID = "id";
    public DatabaseHandler(@Nullable Context context) {
        super(context, Db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountTableStatement =
                "CREATE TABLE " + ACCOUNT_TABLE_NAME + "(" +
                        ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                        BANK_NAME + " TEXT NOT NULL, " +
                        HOLDER_NAME + " TEXT NOT NULL, " +
                        BALANCE + " REAL NOT NULL)";

        String createTransactionTableStatement = "CREATE TABLE " + TRANSACTION_TABLE_NAME + "(" +
                TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " INTEGER NOT NULL, " +
                ACCOUNT_NO + " TEXT NOT NULL, " +
                EXPENSE_TYPE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                "FOREIGN KEY(" + ACCOUNT_NO + ") REFERENCES " + ACCOUNT_TABLE_NAME + "(" + ACCOUNT_NO + "))";

        sqLiteDatabase.execSQL(createAccountTableStatement);
        sqLiteDatabase.execSQL(createTransactionTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}