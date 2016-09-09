package pankaj.CandleStickStatus.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pankaj.CandleStickStatus.R;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;

/**
 * Created by pankaj on 9/9/16.
 */
public class DialogDateRange extends DialogFragment implements OnClickListener {

    private Button btnStartDate = null, btnEndDate = null, btnProcess = null;
    private IDateRangePicker dateRangePicker = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.view_dialog_date_range, null);

        btnStartDate = (Button) view.findViewById(R.id.btnStartDate);
        btnEndDate = (Button) view.findViewById(R.id.btnEndDate);
        btnProcess = (Button) view.findViewById(R.id.btnProcess);

        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dateRangePicker = (IDateRangePicker) activity;
    }

    private void showDatePicker(Activity activity, final Button btnDate) {
        DatePickerDialog datepicker = new DatePickerDialog(
                activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth);

                btnDate.setText(UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, c.getTime()));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProcess:
                try {

                    Date startDate = UtilDateFormat.toDate(UtilDateFormat.yyyy_MMM_dd, btnStartDate.getText().toString());
                    Date endDate = UtilDateFormat.toDate(UtilDateFormat.yyyy_MMM_dd, btnEndDate.getText().toString());

                    if (startDate.getTime() > endDate.getTime()) {
                        Toast.makeText(getActivity(), "Start Date should smaller than end date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateRangePicker.dateRangePicker(
                            this,
                            startDate, endDate

                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnStartDate:
            case R.id.btnEndDate:
                showDatePicker(getActivity(), (Button) v);
                break;
        }

    }

    public interface IDateRangePicker {
        void dateRangePicker(DialogFragment dialog, Date startDate, Date endDate);
    }
}
