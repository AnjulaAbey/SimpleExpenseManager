package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
public class PersistentAccountDAO implements AccountDAO {
    //Class to handle the things done between the accounts and execute them
    private DatabaseHandler handler;
    public PersistentAccountDAO(DatabaseHandler databaseHandler){
        this.handler = databaseHandler;
    }
    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumberList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();

        String gettingAcountNumber = "SELECT " + handler.ACCOUNT_NO + " FROM " + handler.ACCOUNT_TABLE_NAME;
        Cursor output = sqLiteDatabase.rawQuery(gettingAcountNumber, null);

        if (output.moveToFirst()){
            do{
                accountNumberList.add(output.getString(0));
            } while (output.moveToNext());
        }

        sqLiteDatabase.close();
        output.close();
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountsLst = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHandler.ACCOUNT_TABLE_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ACCOUNT_NO));
                String bankName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.BANK_NAME));
                String accountHolder = cursor.getString(cursor.getColumnIndex(DatabaseHandler.HOLDER_NAME));
                double balance = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.BALANCE));

                accountsLst.add(new Account(accountNo, bankName, accountHolder, balance));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return accountsLst;    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String[] parameters = {accountNo};
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHandler.ACCOUNT_TABLE_NAME + " WHERE accountNo = ?";

        Cursor cursor = sqLiteDatabase.rawQuery(query, parameters);

        if (!cursor.moveToFirst()) {
            String message = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(message);
        }
        String bankName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.BANK_NAME));
        String accountHolder = cursor.getString(cursor.getColumnIndex(DatabaseHandler.HOLDER_NAME));
        double balance = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.BALANCE));

        Account account = new Account(accountNo, bankName, accountHolder, balance);

        cursor.close();
        sqLiteDatabase.close();

        return account;    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.ACCOUNT_NO, account.getAccountNo());
        values.put(DatabaseHandler.BANK_NAME, account.getBankName());
        values.put(DatabaseHandler.HOLDER_NAME, account.getAccountHolderName());
        values.put(DatabaseHandler.BALANCE, account.getBalance());

        sqLiteDatabase.insert(DatabaseHandler.ACCOUNT_TABLE_NAME, null, values);
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();
        String[] parameters = {accountNo};
        String whereClause = DatabaseHandler.ACCOUNT_NO + " = ?";
        int affectedRows = sqLiteDatabase.delete(DatabaseHandler.ACCOUNT_TABLE_NAME, whereClause, parameters);

        if (affectedRows == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        sqLiteDatabase.close();
    }
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String[] parameters = {accountNo};
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHandler.BALANCE + " FROM " + DatabaseHandler.ACCOUNT_TABLE_NAME + " WHERE " + DatabaseHandler.ACCOUNT_NO + "= ?";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, parameters);
        if (!cursor.moveToFirst()) {
            String message = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(message);
        }
        double currentBalance = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.BALANCE));
        cursor.close();
        switch (expenseType) {
            case EXPENSE:
                currentBalance -= amount;
                break;
            case INCOME:
                currentBalance += amount;
                break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.BALANCE, currentBalance);

        String whereClause = DatabaseHandler.ACCOUNT_NO + " = ?";
        sqLiteDatabase.update(DatabaseHandler.ACCOUNT_TABLE_NAME, contentValues, whereClause, parameters);
        sqLiteDatabase.close();
    }
}
