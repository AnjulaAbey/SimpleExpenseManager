package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
public class PersistentExpenseManager extends ExpenseManager{
    public PersistentExpenseManager(Context context) {
        setup(context);
    }
    @Override
    public void setup(Context context) {
        DatabaseHandler handler = new DatabaseHandler(context);
        TransactionDAO ptDAO = new PersistentTransactionDAO(handler);
        setTransactionsDAO(ptDAO);
        AccountDAO paDAO = new PersistentAccountDAO(handler);
        setAccountsDAO(paDAO);
    }
}