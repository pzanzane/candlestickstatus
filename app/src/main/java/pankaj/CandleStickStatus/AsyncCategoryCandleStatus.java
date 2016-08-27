package pankaj.CandleStickStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pankaj.CandleStickStatus.db.DbConfiguration;
import pankaj.CandleStickStatus.db.DbHelper;
import pankaj.CandleStickStatus.db.Models.ModelCategory;
import pankaj.CandleStickStatus.db.Models.ModelStock;
import pankaj.CandleStickStatus.db.Models.ModelStockCategory;
import pankaj.CandleStickStatus.db.dao.ModelStockDao;
import pankaj.CandleStickStatus.helpers.AsyncTaskArrayCompletionListener;
import pankaj.CandleStickStatus.helpers.HTTPHelper;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;

/**
 * Created by pankaj on 8/16/16.
 */
public class AsyncCategoryCandleStatus extends AsyncTask<Void, Void, Void> {

    private AsyncTaskArrayCompletionListener<ModelStockCategory> mCompleteListener = null;
    private ArrayList<ModelStockCategory> stockList = null;
    private  List<ModelCategory> listCategories = null;

    private String startDate = null, endDate = null;
    private Context mContext = null;

    public AsyncCategoryCandleStatus(Context context,
                                     AsyncTaskArrayCompletionListener completeListener, List<ModelCategory> listCategories,
                                     Date startDate, Date endDate) {
        mContext = context;
        mCompleteListener = completeListener;
        this.listCategories = listCategories;


        this.startDate = UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, startDate);
        this.endDate = UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, endDate);

        Log.d("WASTE", "startDate: " + startDate + " || endDate" + endDate);
    }

    @Override
    protected Void doInBackground(Void... params) {

            stockList = new ArrayList<>();
            for (int i = 0; i < listCategories.size(); i++) {

                ModelStockCategory modelStockPriceWithCategory = new ModelStockCategory();
                modelStockPriceWithCategory.setStrCategory(listCategories.get(i).getStrCategory());

                String strStockPriceList = getListToCommaSeperatedValues(listCategories.get(i).getListStocks());
                Log.d("WASTE","stockPrice::"+strStockPriceList);

                if (startDate.equalsIgnoreCase(endDate)) {
                    modelStockPriceWithCategory.setListStockPrices(Arrays.asList(sameDay(strStockPriceList)));
                } else {
                    modelStockPriceWithCategory.setListStockPrices(Arrays.asList(differentDays(strStockPriceList)));
                }

                stockList.add(modelStockPriceWithCategory);
            }



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (stockList != null) {
            mCompleteListener.onTaskComplete(stockList);
        } else {
            mCompleteListener.onException(new Exception("No Data"));
        }

    }

    private ModelStock[] sameDay(String strStockPriceList) {

        ModelStock[] models = null;
        HTTPHelper.ResponseObject responseObject = null;
        String url = "https://query.yahooapis.com/v1/public/yql?";


        ContentValues values = new ContentValues();
        values.put("q", "select Symbol,Date,Open,High,Low,Close from yahoo.finance.historicaldata where symbol IN("
                           + strStockPriceList + ")  and startDate = \"" + startDate + "\" and endDate = \"" + startDate + "\"");
        values.put("format", "json");
        values.put("env", "store://datatables.org/alltableswithkeys");

        HTTPHelper helper = new HTTPHelper(HTTPHelper.CONTENT_TYPE_JSON);
        try {
            responseObject = helper.executeHttpGet(url, null, values);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (responseObject.getStatusCode() == 200) {
            try {
                JSONObject jsonParent = new JSONObject(responseObject.getStrResponse());
                JSONObject jsonQuery = jsonParent.getJSONObject(Constants.QUERY);
                JSONArray jsonQuotes = jsonParent.getJSONObject(Constants.QUERY).getJSONObject(Constants.RESULTS).getJSONArray(Constants.QUOTE);


                models = new ModelStock[jsonQuotes.length()];

                for (int i = 0; i < jsonQuotes.length(); i++) {

                    JSONObject jObj = jsonQuotes.getJSONObject(i);
                    models[i] = new ModelStock();
                    models[i].setSymbol(jObj.getString(Constants.SYMBOL));
                    models[i].setOpen(Double.parseDouble(jObj.getString(Constants.OPEN)));
                    models[i].setClose(Double.parseDouble(jObj.getString(Constants.CLOSE)));
                    models[i].setHigh(Double.parseDouble(jObj.getString(Constants.HIGH)));
                    models[i].setLow(Double.parseDouble(jObj.getString(Constants.LOW)));
                    models[i].setDate(jObj.getString(Constants.DATE));
                    models[i].process();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //Error
        }

        return models;
    }

    private ModelStock[] differentDays(String strStockPriceList) {

        ModelStock[] models = null;

        ModelStockDao modelStockDao = new ModelStockDao(
                mContext, DbHelper.getInstance(
                mContext,
                DbConfiguration.getInstance(mContext)
        ).getSQLiteDatabase()
        );
        Log.d("WASTE", "In Different Days");
        try {

            HTTPHelper.ResponseObject responseObjectStartDate = null, responseObjectEndDate = null;
            String url = "https://query.yahooapis.com/v1/public/yql?";


            ContentValues valuesStartDate = new ContentValues();
            valuesStartDate.put("q", "select Symbol,Date,Open,High,Low,Close from yahoo.finance.historicaldata where symbol IN(" + strStockPriceList
                                        + ")  and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"");
            valuesStartDate.put("format", "json");
            valuesStartDate.put("env", "store://datatables.org/alltableswithkeys");


            HTTPHelper helper = new HTTPHelper(HTTPHelper.CONTENT_TYPE_JSON);


            responseObjectStartDate = helper.executeHttpGet(url, null, valuesStartDate);


            try {
                JSONArray jsonStartArray = new JSONObject(responseObjectStartDate.getStrResponse()).getJSONObject(Constants.QUERY).getJSONObject(Constants.RESULTS).getJSONArray(Constants.QUOTE);


                for (int i = 0; i < jsonStartArray.length(); i++) {

                    JSONObject jObjStart = jsonStartArray.getJSONObject(i);

                    ModelStock model = new ModelStock();
                    model.setSymbol(jObjStart.getString(Constants.SYMBOL));
                    model.setOpen(Double.parseDouble(jObjStart.getString(Constants.OPEN)));
                    model.setClose(Double.parseDouble(jObjStart.getString(Constants.CLOSE)));
                    model.setHigh(Double.parseDouble(jObjStart.getString(Constants.HIGH)));
                    model.setLow(Double.parseDouble(jObjStart.getString(Constants.LOW)));
                    model.setDate(jObjStart.getString(Constants.DATE));

                    modelStockDao.create(model);

                }


                Cursor cursorMax = modelStockDao.cursorRawQuery("SELECT " + ModelStockDao.symbol + ",max(" + ModelStockDao.high + ") high,min(" + ModelStockDao.low + ") low FROM " + ModelStockDao.TABLE_NAME + " group by symbol order by symbol");


                Cursor cursorOpen = modelStockDao.cursorRawQuery("SELECT " + ModelStockDao.symbol + "," + ModelStockDao.open + " FROM " + ModelStockDao.TABLE_NAME + " WHERE " + ModelStockDao.date + " LIKE '" + startDate + "' group by symbol order by symbol");


                Cursor cursorClose = modelStockDao.cursorRawQuery("SELECT " + ModelStockDao.symbol + "," + ModelStockDao.close + "," + ModelStockDao.date + " FROM " + ModelStockDao.TABLE_NAME + " WHERE " + ModelStockDao.date + " LIKE '" + endDate + "' group by symbol order by symbol");


                models = new ModelStock[cursorMax.getCount()];
                while (cursorMax.moveToNext()) {

                    int currentPos = cursorMax.getPosition();

                    cursorOpen.moveToNext();
                    cursorClose.moveToNext();

                    String symbol = cursorMax.getString(cursorMax.getColumnIndex(ModelStockDao.symbol));
                    Double high = cursorMax.getDouble(cursorMax.getColumnIndex(ModelStockDao.high));
                    Double low = cursorMax.getDouble(cursorMax.getColumnIndex(ModelStockDao.low));

                    Double open = cursorOpen.getDouble(cursorOpen.getColumnIndex(ModelStockDao.open));
                    Double close = cursorClose.getDouble(cursorClose.getColumnIndex(ModelStockDao.close));


                    models[currentPos] = new ModelStock();
                    models[currentPos].setSymbol(symbol);
                    models[currentPos].setOpen(open);
                    models[currentPos].setClose(close);
                    models[currentPos].setHigh(high);
                    models[currentPos].setLow(low);
                    models[currentPos].setDate(cursorClose.getString(cursorClose.getColumnIndex(ModelStockDao.date)));
                    models[currentPos].process();


                }
                cursorMax.close();
                cursorOpen.close();
                cursorClose.close();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            modelStockDao.deleteAll();
        }

        return models;
    }

    private String getListToCommaSeperatedValues(List<String> list) {


        String[] values = list.toArray(new String[]{});

        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            value += ".ns";
            builder.append("\"" + value + "\"");
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
