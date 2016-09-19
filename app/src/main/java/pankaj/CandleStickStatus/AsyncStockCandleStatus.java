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
import java.util.Date;
import java.util.List;

import pankaj.CandleStickStatus.db.DbConfiguration;
import pankaj.CandleStickStatus.db.DbHelper;
import pankaj.CandleStickStatus.db.Models.ModelStock;
import pankaj.CandleStickStatus.db.dao.ModelStockDao;
import pankaj.CandleStickStatus.helpers.AsyncTaskArrayCompletionListener;
import pankaj.CandleStickStatus.helpers.HTTPHelper;
import pankaj.CandleStickStatus.helpers.PreferenceUtils;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;

/**
 * Created by pankaj on 8/16/16.
 */
public class AsyncStockCandleStatus extends AsyncTask<Void, Void, Void> {

    private AsyncTaskArrayCompletionListener<ModelStock> mCompleteListener = null;
    private List<String> stockList = null;

    private List<ModelStock> models = null;
    private String startDate = null, endDate = null;
    private Context mContext = null;
    private int greenCandles = 0, redCandles = 0, boringCandles = 0;

    public AsyncStockCandleStatus(Context context,
                                  AsyncTaskArrayCompletionListener completeListener, ArrayList stockList, Date startDate, Date endDate) {
        mContext = context;
        mCompleteListener = completeListener;
        this.stockList = stockList;

        this.startDate = UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, startDate);
        this.endDate = UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, endDate);

    }

    @Override
    protected Void doInBackground(Void... params) {

        String strStockPriceList = getListToCommaSeperatedValues(stockList);

        List<String> mStockExists = null, mStockDoesNotExists = null;
        if (startDate.equalsIgnoreCase(endDate)) {
            mStockExists = sameDay(strStockPriceList);
        } else {
            mStockExists = differentDays(strStockPriceList);
        }

        mStockDoesNotExists = new ArrayList<>(stockList);
        mStockDoesNotExists.removeAll(mStockExists);

        Log.d("WASTE", "Exists: " + mStockExists);
        Log.d("WASTE", "NoData: " + mStockDoesNotExists);
        for (int i = 0; i < mStockDoesNotExists.size(); i++) {

            ModelStock modelStock = new ModelStock();
            modelStock.setSymbol(mStockDoesNotExists.get(i));
            modelStock.process();

            models.add(modelStock);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        PreferenceUtils.putInteger(mContext, PreferenceUtils.GREEN_CANDLE_COUNT, greenCandles);
        PreferenceUtils.putInteger(mContext, PreferenceUtils.RED_CANDLE_COUNT, redCandles);
        PreferenceUtils.putInteger(mContext, PreferenceUtils.BORING_CANDLE_COUNT, boringCandles);

        if (models != null) {
            mCompleteListener.onTaskComplete((new ArrayList<ModelStock>(models)));
        } else {
            mCompleteListener.onException(new Exception("No Data"));
        }

    }

    private List<String> sameDay(String strStockPriceList) {

        List<String> mStockExists = new ArrayList<>();
        HTTPHelper.ResponseObject responseObject = null;
        String url = "https://query.yahooapis.com/v1/public/yql?";


        ContentValues values = new ContentValues();
        values.put("q", "select Symbol,Date,Open,High,Low,Close from yahoo.finance.historicaldata where symbol IN(" + strStockPriceList + ")  and startDate = \"" + startDate + "\" and endDate = \"" + startDate + "\"");
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


                models = new ArrayList<>();

                for (int i = 0; i < jsonQuotes.length(); i++) {

                    ModelStock model = new ModelStock();
                    JSONObject jObj = jsonQuotes.getJSONObject(i);

                    model.setSymbol(jObj.getString(Constants.SYMBOL));
                    model.setOpen(Double.parseDouble(jObj.getString(Constants.OPEN)));
                    model.setClose(Double.parseDouble(jObj.getString(Constants.CLOSE)));
                    model.setHigh(Double.parseDouble(jObj.getString(Constants.HIGH)));
                    model.setLow(Double.parseDouble(jObj.getString(Constants.LOW)));
                    model.setDate(jObj.getString(Constants.DATE));
                    ModelStock.CandleStatus candleStatuses = model.process();

                    mStockExists.add(jObj.getString(Constants.SYMBOL).replace(".ns", ""));
                    calculateNumberOfCandles(candleStatuses);

                    models.add(model);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //Error
        }
        return mStockExists;
    }

    private List<String> differentDays(String strStockPriceList) {

        List<String> mStockExists = new ArrayList<>();
        ModelStockDao modelStockDao = new ModelStockDao(mContext, DbHelper.getInstance(mContext, DbConfiguration.getInstance(mContext)).getSQLiteDatabase());
        Log.d("WASTE", "In Different Days");
        try {

            HTTPHelper.ResponseObject responseObjectStartDate = null, responseObjectEndDate = null;
            String url = "https://query.yahooapis.com/v1/public/yql?";


            ContentValues valuesStartDate = new ContentValues();
            valuesStartDate.put("q", "select Symbol,Date,Open,High,Low,Close from yahoo.finance.historicaldata where symbol IN(" + strStockPriceList + ")  and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"");
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


                models = new ArrayList<>();
                while (cursorMax.moveToNext()) {

                    int currentPos = cursorMax.getPosition();

                    cursorOpen.moveToNext();
                    cursorClose.moveToNext();

                    String symbol = cursorMax.getString(cursorMax.getColumnIndex(ModelStockDao.symbol));
                    Double high = cursorMax.getDouble(cursorMax.getColumnIndex(ModelStockDao.high));
                    Double low = cursorMax.getDouble(cursorMax.getColumnIndex(ModelStockDao.low));

                    Double open = cursorOpen.getDouble(cursorOpen.getColumnIndex(ModelStockDao.open));
                    Double close = cursorClose.getDouble(cursorClose.getColumnIndex(ModelStockDao.close));

                    ModelStock model = new ModelStock();
                    model.setSymbol(symbol);
                    model.setOpen(open);
                    model.setClose(close);
                    model.setHigh(high);
                    model.setLow(low);
                    model.setDate(cursorClose.getString(cursorClose.getColumnIndex(ModelStockDao.date)));
                    ModelStock.CandleStatus candleStatuses = model.process();

                    if(!mStockExists.contains(symbol.replace(".ns", ""))){
                        mStockExists.add(symbol.replace(".ns", ""));
                    }

                    calculateNumberOfCandles(candleStatuses);

                    models.add(model);

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

        return mStockExists;
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

    private void calculateNumberOfCandles(ModelStock.CandleStatus candleStatus) {

        switch (candleStatus) {
            case EXCITING_GREEN:
                greenCandles += 1;
                break;
            case EXCITING_RED:
                redCandles += 1;
                break;
            case BORING:
                boringCandles += 1;
                break;


        }
    }
}
