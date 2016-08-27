package pankaj.CandleStickStatus;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pankaj.CandleStickStatus.adapters.AdapterListStocks;
import pankaj.CandleStickStatus.db.Models.ModelCategory;
import pankaj.CandleStickStatus.db.Models.ModelStock;
import pankaj.CandleStickStatus.db.Models.ModelStockCategory;
import pankaj.CandleStickStatus.helpers.AsyncTaskArrayCompletionListener;
import pankaj.CandleStickStatus.helpers.IOUtils;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;
import pankaj.CandleStickStatus.helpers.Utility;

public class ActivityHome extends AppCompatActivity implements AsyncTaskArrayCompletionListener<ModelStock>, View.OnClickListener {

    private Button btnStartDate, btnEndDate = null, btnProcess = null;
    private ListView mListViewStocks = null;
    private Calendar calendarStartDate, calendarEndDate;
    private TextView txtFromToDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnProcess = (Button) findViewById(R.id.btnProcess);
        txtFromToDate = (TextView) findViewById(R.id.txtFromToDate);

        mListViewStocks = (ListView) findViewById(R.id.listStocks);

        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnProcess.setOnClickListener(this);


    }

    @Override
    public void onTaskComplete(ArrayList<ModelStock> listModelPrices) {


        mListViewStocks.setAdapter(new AdapterListStocks(this, 0, listModelPrices));
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(final View v) {

        if (v.getId() == btnProcess.getId()) {

            if (calendarStartDate == null || calendarEndDate == null) {
                Toast.makeText(ActivityHome.this, "Select Start and End Date", Toast.LENGTH_SHORT).show();
                calendarStartDate = null;
                calendarEndDate = null;
                btnStartDate.setText("Start Date");
                btnEndDate.setText("End Date");

                return;
            }else if(!(calendarStartDate.getTime().getTime()<=calendarEndDate.getTime().getTime())){

                Toast.makeText(ActivityHome.this, "Start Date should be Less than End Date", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                fetchStocks();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            DatePickerDialog datepicker = new DatePickerDialog(
                    this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    if (v.getId() == btnStartDate.getId()) {
                        calendarStartDate = Calendar.getInstance();
                        calendarStartDate.set(Calendar.YEAR, year);
                        calendarStartDate.set(Calendar.MONTH, monthOfYear);
                        calendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    } else if (v.getId() == btnEndDate.getId()) {
                        calendarEndDate = Calendar.getInstance();
                        calendarEndDate.set(Calendar.YEAR, year);
                        calendarEndDate.set(Calendar.MONTH, monthOfYear);
                        calendarEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                    ((Button) v).setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
            }, Calendar.getInstance()
                            .get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            datepicker.getDatePicker().setMaxDate(c.getTimeInMillis());
            datepicker.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_readme) {
            showAlertDialog(readFile(R.raw.how_to_use));
        }else if(item.getItemId() == R.id.action_holidays){
            showAlertDialog(readFile(R.raw.nse_holidays));
        }
        return true;
    }

    private void showAlertDialog(String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNeutralButton(
                "Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.create().show();
    }

    private String readFile(int resource) {

        try {
            return IOUtils.readFromFile(getResources().openRawResource(resource));
        } catch (IOException e) {
            e.printStackTrace();
            return " Error While Reading File";
        }

    }

    private void fetchStocks() throws IOException, JSONException {
        txtFromToDate.setText(UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, calendarStartDate.getTime()) + "     To   " + UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, calendarEndDate.getTime()));


        List<ModelCategory> modelCategory = Utility.getList(ActivityHome.this);


        HashSet<String> hashList = new HashSet<>();
        for (ModelCategory category : modelCategory) {

            hashList.addAll(category.getListStocks());
        }

        ArrayList<String> list = new ArrayList<>();
        list.addAll(hashList);
        Collections.sort(list);
        AsyncStockCandleStatus async = new AsyncStockCandleStatus(
                ActivityHome.this, ActivityHome.this,
                list, calendarStartDate.getTime(),
                calendarEndDate.getTime()
        );
        async.execute();
    }

    private void fetchStocksWithCategories() throws IOException, JSONException {

        txtFromToDate.setText(UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, calendarStartDate.getTime()) + "     To   " + UtilDateFormat.format(UtilDateFormat.yyyy_MM_dd, calendarEndDate.getTime()));


        AsyncCategoryCandleStatus async = new AsyncCategoryCandleStatus(
                ActivityHome.this, new AsyncTaskArrayCompletionListener<ModelStockCategory>() {
            @Override
            public void onTaskComplete(ArrayList<ModelStockCategory> list) {
                for (ModelStockCategory model : list) {
                    Log.d("WASTE", "Category:: " + model.getStrCategory());
                    Log.d("WASTE", "List:: " + String.valueOf(model.getListStockPrices()));
                }
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        }, Utility.getList(ActivityHome.this), calendarStartDate.getTime(),
                calendarEndDate.getTime()
        );
        async.execute();


    }
}

