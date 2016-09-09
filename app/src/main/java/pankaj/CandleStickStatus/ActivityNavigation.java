package pankaj.CandleStickStatus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import pankaj.CandleStickStatus.fragments.DialogDateRange;
import pankaj.CandleStickStatus.fragments.DialogDateRange.IDateRangePicker;
import pankaj.CandleStickStatus.fragments.DialogSortOptions;
import pankaj.CandleStickStatus.helpers.PreferenceUtils;
import pankaj.CandleStickStatus.helpers.UtilDateFormat;
import pankaj.CandleStickStatus.helpers.Utility;

/**
 * Created by pankaj on 9/9/16.
 */
public class ActivityNavigation extends AppCompatActivity implements View.OnClickListener,
        DialogSortOptions.ISortCallBack, IDateRangePicker {

    private long delayMillis = 50;
    private long speed = 150;

    private Button
            btnSort = null,
            nseHoliday = null,
            btnCustomDate = null,
            btnReadMe = null;
    private RelativeLayout relLastWeek, relLastMonth, relLastDay;
    private TextView txtLastWeekDate, txtLastMonthDate, txtLastDayhDate;


    private Calendar calendarMonday = null, calendarFriday = null,
            calendarLastDayOfMonth = null, calendarFirstDayOfMonth = null, calendarPreviousDay = null;

    private static void showAlertDialog(Context context, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        relLastWeek = (RelativeLayout) findViewById(R.id.btnWeek);
        relLastMonth = (RelativeLayout) findViewById(R.id.btnMonth);
        relLastDay = (RelativeLayout) findViewById(R.id.btnDay);

        btnSort = (Button) findViewById(R.id.btnSort);
        nseHoliday = (Button) findViewById(R.id.nseHoliday);
        btnCustomDate = (Button) findViewById(R.id.btnCustomDate);
        btnReadMe = (Button) findViewById(R.id.btnReadMe);

        txtLastWeekDate = (TextView) findViewById(R.id.txtLastWeekDate);
        txtLastMonthDate = (TextView) findViewById(R.id.txtLastMonthDate);
        txtLastDayhDate = (TextView) findViewById(R.id.txtLastDayhDate);

        relLastWeek.setOnClickListener(this);
        relLastMonth.setOnClickListener(this);
        relLastDay.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        nseHoliday.setOnClickListener(this);
        btnCustomDate.setOnClickListener(this);
        btnReadMe.setOnClickListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        animateViews();

        setAllDays();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnReadMe:
                Utility.showAlertDialog(this, Utility.readFile(this, R.raw.how_to_use));
                break;

            case R.id.nseHoliday:
                Utility.showAlertDialog(this, Utility.readFile(this, R.raw.nse_holidays));
                break;

            case R.id.btnSort:
                DialogSortOptions diaologFrag = new DialogSortOptions();
                diaologFrag.show(getSupportFragmentManager(), "DialogSortOptions");
                break;
            case R.id.btnCustomDate:
                DialogDateRange dialogDateRange = new DialogDateRange();
                dialogDateRange.show(getSupportFragmentManager(), "DialogDateRange");
                break;
            case R.id.btnWeek:
                callStockList(calendarMonday.getTime(), calendarFriday.getTime());
                break;
            case R.id.btnMonth:
                callStockList(calendarFirstDayOfMonth.getTime(), calendarLastDayOfMonth.getTime());
                break;
            case R.id.btnDay:
                callStockList(calendarPreviousDay.getTime(), calendarPreviousDay.getTime());
                break;
        }
    }


    private void animateViews() {
        int random = randInt(2, 4);
        switch (random) {
            case 2:
                startAllScaleAnim();
                break;
            case 3:
                startAllTiltAnim1();
                break;
            case 4:
                startAllTiltAnim2();
                break;
        }
    }

    private void startAllTiltAnim1() {
        float tiltAngleX = -10f, tiltAngleY = -5f;
        delayMillis = 0;
        tiltAnimation(relLastWeek, tiltAngleX, tiltAngleY);
        tiltAnimation(relLastMonth, tiltAngleX, tiltAngleY);
        tiltAnimation(relLastDay, tiltAngleX, tiltAngleY);
        tiltAnimation(btnCustomDate, tiltAngleX, tiltAngleY);
        tiltAnimation(btnSort, tiltAngleX, tiltAngleY);
        tiltAnimation(nseHoliday, tiltAngleX, tiltAngleY);
        tiltAnimation(btnReadMe, tiltAngleX, tiltAngleY);
    }

    private void startAllTiltAnim2() {
        float tiltAngleX = 0f, tiltAngleY = -10f;
        delayMillis = 0;
        tiltAnimation(relLastWeek, tiltAngleX, tiltAngleY);
        tiltAnimation(relLastMonth, tiltAngleX, tiltAngleY);
        tiltAnimation(relLastDay, tiltAngleX, tiltAngleY);
        tiltAnimation(btnCustomDate, tiltAngleX, tiltAngleY);
        tiltAnimation(btnSort, tiltAngleX, tiltAngleY);
        tiltAnimation(nseHoliday, tiltAngleX, tiltAngleY);
        tiltAnimation(btnReadMe, tiltAngleX, tiltAngleY);
    }

    private void startAllScaleAnim() {


        delayMillis = 0;
        relLastWeek.setVisibility(View.INVISIBLE);
        relLastMonth.setVisibility(View.INVISIBLE);
        relLastDay.setVisibility(View.INVISIBLE);
        btnSort.setVisibility(View.INVISIBLE);
        nseHoliday.setVisibility(View.INVISIBLE);
        btnCustomDate.setVisibility(View.INVISIBLE);
        btnReadMe.setVisibility(View.INVISIBLE);

        scaleAnimation(relLastWeek);
        scaleAnimation(relLastMonth);
        scaleAnimation(relLastDay);
        scaleAnimation(btnCustomDate);
        scaleAnimation(btnSort);
        scaleAnimation(nseHoliday);
        scaleAnimation(btnReadMe);


    }

    private void scaleAnimation(final View view) {
        delayMillis += 200;
        ObjectAnimator scalXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scalYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(scalXAnim, scalYAnim);
        animSetXY.setStartDelay(delayMillis);
        animSetXY.setInterpolator(new DecelerateInterpolator());
        animSetXY.start();

        scalXAnim.addListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }
        );


    }

    private void tiltAnimation(final View view, final float tiltAngleX, final float tiltAngleY) {
        delayMillis += 200;
        final ObjectAnimator titlX = ObjectAnimator.ofFloat(view, "rotationX", 0f, tiltAngleX);
        final ObjectAnimator titlY = ObjectAnimator.ofFloat(view, "rotationY", 0f, tiltAngleY);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(titlX, titlY);
        animSetXY.setStartDelay(delayMillis);
        animSetXY.setInterpolator(new DecelerateInterpolator());
        animSetXY.setDuration(speed);
        animSetXY.start();

        titlX.addListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reverseAnimation(view, tiltAngleX, tiltAngleY);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }
        );


    }

    private void reverseAnimation(final View view, float tiltAngleX, float tiltAngleY) {
        delayMillis += 200;
        final ObjectAnimator titlX = ObjectAnimator.ofFloat(view, "rotationX", tiltAngleX, 0f);
        final ObjectAnimator titlY = ObjectAnimator.ofFloat(view, "rotationY", tiltAngleY, 0f);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(titlX, titlY);
        animSetXY.setDuration(100);
        animSetXY.setInterpolator(new AccelerateInterpolator());
        animSetXY.start();


    }

    @Override
    public void sortCallBack(int checkId) {

        PreferenceUtils.putInteger(StockpricetickerApplication.getBasicApplicationContext(), PreferenceUtils.SORT_TYPE, checkId);

    }


    @Override
    public void dateRangePicker(DialogFragment dialog, Date startDate, Date endDate) {
        dialog.dismiss();

        callStockList(startDate, endDate);

    }

    private void callStockList(Date startDate, Date endDate) {

        Intent intent = new Intent(this, ActivityStocksList.class);
        intent.putExtra(ActivityStocksList.START_DATE, startDate.getTime());
        intent.putExtra(ActivityStocksList.END_DATE, endDate.getTime());

        startActivity(intent);
    }

    private void getLastWeekCaleder() {
        Calendar calendar = Calendar.getInstance();
        calendarMonday = Calendar.getInstance();
        calendarFriday = Calendar.getInstance();

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendarMonday.setFirstDayOfWeek(Calendar.MONDAY);
        calendarFriday.setFirstDayOfWeek(Calendar.MONDAY);

        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

            calendarMonday.add(Calendar.WEEK_OF_YEAR, -1);
            calendarFriday.add(Calendar.WEEK_OF_YEAR, -1);


        }
        calendarMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendarFriday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        Log.d("WASTE", "Last monday:" + calendarMonday.getTime());
        Log.d("WASTE", "Last friday:" + calendarFriday.getTime());
    }

    private void setAllDays() {
        getLastMonthCaleder();
        getLastWeekCaleder();
        getLastDay();

        String weekData = UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, calendarMonday.getTime()) + " to " + UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, calendarFriday.getTime());
        String monthData = UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, calendarFirstDayOfMonth.getTime()) + " to " + UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, calendarLastDayOfMonth.getTime());
        String prevData = UtilDateFormat.format(UtilDateFormat.yyyy_MMM_dd, calendarPreviousDay.getTime());

        txtLastWeekDate.setText(weekData);
        txtLastMonthDate.setText(monthData);
        txtLastDayhDate.setText(prevData);
    }

    private void getLastMonthCaleder() {

        calendarFirstDayOfMonth = Calendar.getInstance();
        calendarFirstDayOfMonth.add(Calendar.MONTH, -1);
        calendarFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        if (calendarFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendarFirstDayOfMonth.add(Calendar.DAY_OF_MONTH, 2);
        } else if (calendarFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendarFirstDayOfMonth.add(Calendar.DAY_OF_MONTH, 1);
        }


        calendarLastDayOfMonth = Calendar.getInstance();
        calendarLastDayOfMonth.add(Calendar.MONTH, -1);
        calendarLastDayOfMonth.set(Calendar.DAY_OF_MONTH, calendarLastDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        if (calendarLastDayOfMonth.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendarLastDayOfMonth.add(Calendar.DAY_OF_MONTH, -1);
        } else if (calendarLastDayOfMonth.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendarLastDayOfMonth.add(Calendar.DAY_OF_MONTH, -2);
        }

        Log.d("WASTE", "Month First:" + calendarFirstDayOfMonth.getTime());
        Log.d("WASTE", "Month Last:" + calendarLastDayOfMonth.getTime());
    }

    private void getLastDay() {

        calendarPreviousDay = Calendar.getInstance();


        if (calendarPreviousDay.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            calendarPreviousDay.add(Calendar.DAY_OF_MONTH, -3);
        } else if (calendarPreviousDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendarPreviousDay.add(Calendar.DAY_OF_MONTH, -2);
        } else {
            calendarPreviousDay.add(Calendar.DAY_OF_MONTH, -1);
        }
        Log.d("WASTE", "previouse Date:" + calendarPreviousDay.getTime());
    }
}
