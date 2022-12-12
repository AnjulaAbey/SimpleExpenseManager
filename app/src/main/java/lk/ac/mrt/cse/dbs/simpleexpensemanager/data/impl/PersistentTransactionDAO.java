package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
public class PersistentTransactionDAO implements TransactionDAO {
    //To check the transactions from the account and handle them
    private final DatabaseHandler handler;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
    public PersistentTransactionDAO(DatabaseHandler sqLiteHelper) {
        this.handler = sqLiteHelper;
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase Db = handler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.DATE, date.toString());
        contentValues.put(DatabaseHandler.ACCOUNT_NO, accountNo);
        contentValues.put(DatabaseHandler.EXPENSE_TYPE, expenseType.toString());
        contentValues.put(DatabaseHandler.AMOUNT, amount);

        Db.insert(DatabaseHandler.TRANSACTION_TABLE_NAME, null, contentValues);
        Db.close();
    }
    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();
        String transactionQuery = "SELECT * FROM " + DatabaseHandler.TRANSACTION_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(transactionQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ACCOUNT_NO));
                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.DATE));
                    date = simpleDateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String stringType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.EXPENSE_TYPE));
                ExpenseType type = stringType.equals("EXPENSE") ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.AMOUNT));
                transactions.add(new Transaction(date, accountNo, type, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase Db = handler.getReadableDatabase();
        String[] parameters = {String.valueOf(limit)};
        String transactionQuery = "SELECT * FROM " + DatabaseHandler.TRANSACTION_TABLE_NAME + " ORDER BY " + DatabaseHandler.TRANSACTION_ID + " DESC LIMIT ?";
        Cursor cursor = Db.rawQuery(transactionQuery, parameters);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ACCOUNT_NO));

                Date date = null;
                try {
                    String dateString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.DATE));
                    date = simpleDateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String stringType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.EXPENSE_TYPE));
                ExpenseType type = stringType.equals("EXPENSE") ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.AMOUNT));
                transactions.add(new Transaction(date, accountNo, type, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Db.close();
        return transactions;
    }
}
