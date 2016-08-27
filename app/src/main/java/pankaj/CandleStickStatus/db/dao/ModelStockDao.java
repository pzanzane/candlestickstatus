package pankaj.CandleStickStatus.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import pankaj.CandleStickStatus.db.Models.ModelStock;

/**
 * Created by pankaj on 8/25/16.
 */
public class ModelStockDao extends BaseDAO<ModelStock> {


    public static final String symbol="symbol";
    public static final String date="date";
    public static final String open="open";
    public static final String high="high";
    public static final String low="low";
    public static final String close="close";

    public static String TABLE_NAME = "StockPrice";

    public static String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME + " ("
            +ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + symbol+" TEXT,"
            + date+" TEXT,"
            +open+" REAL,"
            +close+" REAL,"
            +high+" REAL,"
            +low+" REAL"
            +");";
    /**
     * Instantiates a new base dao.
     *
     * @param context the context
     * @param db
     */
    public ModelStockDao(Context context, SQLiteDatabase db) {
        super(context, db);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getPrimaryColumnName() {
        return ID;
    }

    @Override
    public ModelStock fromCursor(Cursor c) {

        ModelStock modelDemo = new ModelStock();

        modelDemo.setSymbol(c.getString(c.getColumnIndex(symbol)));
        modelDemo.setDate(c.getString(c.getColumnIndex(date)));
        modelDemo.setOpen(c.getDouble(c.getColumnIndex(open)));
        modelDemo.setClose(c.getDouble(c.getColumnIndex(close)));
        modelDemo.setHigh(c.getDouble(c.getColumnIndex(high)));
        modelDemo.setLow(c.getDouble(c.getColumnIndex(low)));


        return modelDemo;
    }

    @Override
    public ContentValues values(ModelStock modelStockPrice) {

        ContentValues values = new ContentValues();
        values.put(symbol, modelStockPrice.getSymbol());
        values.put(date, modelStockPrice.getDate());
        values.put(open, modelStockPrice.getOpen());
        values.put(close, modelStockPrice.getClose());
        values.put(high, modelStockPrice.getHigh());
        values.put(low, modelStockPrice.getLow());


        return values;
    }
}
