package pankaj.CandleStickStatus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import pankaj.CandleStickStatus.adapters.AdapterListStocks;
import pankaj.CandleStickStatus.db.Models.ModelCategory;
import pankaj.CandleStickStatus.db.Models.ModelStock;
import pankaj.CandleStickStatus.fragments.DialogSortOptions;
import pankaj.CandleStickStatus.fragments.DialogSortOptions.ISortCallBack;
import pankaj.CandleStickStatus.helpers.AsyncTaskArrayCompletionListener;
import pankaj.CandleStickStatus.helpers.PreferenceUtils;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;
import pankaj.CandleStickStatus.helpers.Utility;

public class ActivityStocksList extends AppCompatActivity implements AsyncTaskArrayCompletionListener<ModelStock>,
        ISortCallBack {

    public static final String START_DATE = "START_DATE", END_DATE = "END_DATE";
    private ListView mListViewStocks = null;
    private TextView txtFromToDate = null;
    private ProgressBar progressbar = null;
    private AdapterListStocks adapterListStocks = null;
    private Date startDate = null, endDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        txtFromToDate = (TextView) findViewById(R.id.txtFromToDate);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        mListViewStocks = (ListView) findViewById(R.id.listStocks);


        startDate = new Date(getIntent().getExtras().getLong(START_DATE));
        endDate = new Date(getIntent().getExtras().getLong(END_DATE));

        Log.d("WASTE", "Start:" + startDate + "\n" + " End:" + endDate);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        process(startDate, endDate);
    }

    @Override
    public void onTaskComplete(ArrayList<ModelStock> listModelPrices) {


        progressbar.setVisibility(View.GONE);
        String dateTime = UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, startDate) + "     To   " + UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, endDate);
        String candlesCount = " Green: " + PreferenceUtils.getInteger(this, PreferenceUtils.GREEN_CANDLE_COUNT)
                + " Red: " + PreferenceUtils.getInteger(this, PreferenceUtils.RED_CANDLE_COUNT)
                + " Boring: " + PreferenceUtils.getInteger(this, PreferenceUtils.BORING_CANDLE_COUNT);

        txtFromToDate.setText(dateTime + "\n" + candlesCount);
        adapterListStocks = new AdapterListStocks(this, 0, listModelPrices);
        sortList(PreferenceUtils.getInteger(this, PreferenceUtils.SORT_TYPE));
        mListViewStocks.setAdapter(adapterListStocks);
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void process(Date cStart, Date cEnd) {


        try {
            fetchStocks();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {

            DialogSortOptions diaologFrag = new DialogSortOptions();
            diaologFrag.show(getSupportFragmentManager(), "DialogSortOptions");
        }
        return true;
    }


    private void fetchStocks() throws IOException, JSONException {

        if (adapterListStocks != null) {
            adapterListStocks.clear();
        }

        progressbar.setVisibility(View.VISIBLE);
        List<ModelCategory> modelCategory = Utility.getList(ActivityStocksList.this);


        HashSet<String> hashList = new HashSet<>();
        for (ModelCategory category : modelCategory) {

            hashList.addAll(category.getListStocks());
        }

        ArrayList<String> list = new ArrayList<>();
        list.addAll(hashList);
        Collections.sort(list);
        AsyncStockCandleStatus async = new AsyncStockCandleStatus(
                ActivityStocksList.this, ActivityStocksList.this,
                list, startDate,
                endDate
        );
        async.execute();
    }

    @Override
    public void sortCallBack(int checkId) {

        PreferenceUtils.putInteger(StockpricetickerApplication.getBasicApplicationContext(), PreferenceUtils.SORT_TYPE, checkId);
        sortList(checkId);
    }

    private void sortList(int selectedPreferenceId) {

        if (adapterListStocks != null) {
            Collections.sort(adapterListStocks.getList(), new SortComparator(PreferenceUtils.getInteger(this, PreferenceUtils.SORT_TYPE)));
            adapterListStocks.notifyDataSetChanged();
        }

    }

    private static class SortComparator implements Comparator<ModelStock> {

        private int selectionPreference = -1;

        public SortComparator(int selectionPreference) {
            this.selectionPreference = selectionPreference;
        }

        @Override
        public int compare(ModelStock lhs, ModelStock rhs) {

            switch (selectionPreference) {
                case R.id.radioName:
                    return lhs.getSymbol().compareTo(rhs.getSymbol());

                case R.id.radioCandleStatus:
                    if (lhs.getCandleStatus().ordinal() > rhs.getCandleStatus().ordinal()) {
                        return 1;
                    } else if (lhs.getCandleStatus().ordinal() < rhs.getCandleStatus().ordinal()) {
                        return -1;
                    } else {
                        return 0;
                    }

            }
            return 0;
        }
    }
}

