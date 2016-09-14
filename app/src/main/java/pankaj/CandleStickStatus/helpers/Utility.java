package pankaj.CandleStickStatus.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pankaj.CandleStickStatus.R;
import pankaj.CandleStickStatus.db.Models.ModelCategory;

/**
 * Created by pankaj on 8/26/16.
 */
public class Utility {

    private static List<String> lisHoliday = null;

    public static List<ModelCategory> getList(Context context) throws IOException, JSONException {

        List<ModelCategory> listModelStocks = new ArrayList<>();

        String stringJson = IOUtils.readFromFile(context.getResources().openRawResource(R.raw.stock_list));
        JSONObject jsonParent = new JSONObject(stringJson);

        Iterator<String> iterator = jsonParent.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONArray jsonArray = jsonParent.getJSONArray(key);

            ModelCategory model = new ModelCategory();
            model.setStrCategory(key);
            model.setListStocks(Arrays.asList(StringUtils.getStringArray(jsonArray.toString().replaceAll("\"", ""))));
            listModelStocks.add(model);
        }

        return listModelStocks;
    }

    public static List<String> getNSEHolidayList(Context context) throws IOException, JSONException {

        List<String> listHoliday = new ArrayList<>();

        String stringJson = IOUtils.readFromFile(context.getResources().openRawResource(R.raw.nse_holiday_json));
        JSONObject jsonParent = new JSONObject(stringJson);
        JSONArray jsonArray = jsonParent.getJSONArray("nse_holiday");

        for (int count = 0; count < jsonArray.length(); count++) {
            listHoliday.add(jsonArray.getString(count).replaceAll("\"", ""));
        }

        return listHoliday;
    }

    public static HashMap<String, List<String>> getHashMap(Context context) throws IOException, JSONException {

        HashMap<String, List<String>> map = new HashMap<>();
        String stringJson = IOUtils.readFromFile(context.getResources().openRawResource(R.raw.stock_list));
        JSONObject jsonParent = new JSONObject(stringJson);

        Iterator<String> iterator = jsonParent.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONArray jsonArray = jsonParent.getJSONArray(key);
            map.put(key, Arrays.asList(StringUtils.getStringArray(jsonArray.toString().replaceAll("\"", ""))));
        }

        return map;
    }


    public static String readFile(Context context, int resource) {

        try {
            return IOUtils.readFromFile(context.getResources().openRawResource(resource));
        } catch (IOException e) {
            e.printStackTrace();
            return " Error While Reading File";
        }

    }

    public static void showAlertDialog(Context context, String message) {


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

    public static boolean isHoliday(Context context, String date) throws IOException, JSONException, ParseException {

        if (lisHoliday == null) {
            lisHoliday = Utility.getNSEHolidayList(context);

        }
        return lisHoliday.contains(
                String.valueOf(
                        UtilDateFormat.format(
                                UtilDateFormat.yyyy_MMM_dd,
                                UtilDateFormat.yyyy_MM_dd,
                                date
                        )
                )
        );
    }
}
