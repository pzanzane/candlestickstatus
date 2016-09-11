package pankaj.CandleStickStatus.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import pankaj.CandleStickStatus.R;
import pankaj.CandleStickStatus.db.DbConfiguration;
import pankaj.CandleStickStatus.db.DbHelper;
import pankaj.CandleStickStatus.db.Models.ModelCategory;

/**
 * Created by pankaj on 8/26/16.
 */
public class Utility {

    public static List<ModelCategory> getList(Context context) throws IOException, JSONException {

        List<ModelCategory> listModelStocks = new ArrayList<>();

        String stringJson = IOUtils.readFromFile(context.getResources().openRawResource(R.raw.stock_list));
        JSONObject jsonParent = new JSONObject(stringJson);

        Iterator<String> iterator = jsonParent.keys();

        while (iterator.hasNext()){
            String key = iterator.next();
            JSONArray jsonArray = jsonParent.getJSONArray(key);

            ModelCategory model = new ModelCategory();
            model.setStrCategory(key);
            model.setListStocks(Arrays.asList(StringUtils.getStringArray(jsonArray.toString().replaceAll("\"",""))));
            listModelStocks.add(model);
        }

        return listModelStocks;
    }

    public static List<ModelCategory> getList(String filePath) throws IOException, JSONException {

        String stringJson = IOUtils.readFromFile(filePath);
        JSONObject jsonParent = new JSONObject(stringJson);

        Iterator<String> iterator = jsonParent.keys();

        while (iterator.hasNext()){
            String key = iterator.next();
            System.out.println(key);
        }

        return null;
    }


    public static String readFile(Context context,int resource) {

        try {
            return IOUtils.readFromFile(context.getResources().openRawResource(resource));
        } catch (IOException e) {
            e.printStackTrace();
            return " Error While Reading File";
        }

    }

    public static void showAlertDialog(Context context,String message) {


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

    public static void writeDBTOSD(Context context) throws IOException {

        String dbPath = DbConfiguration.getInstance(context).getDatabasePath()+ File.separator+DbConfiguration.getInstance(context).getDatabaseName();
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+File.separator+DbConfiguration.getInstance(context).getDatabaseName();
        Log.d("WASTE","Path::"+dbPath);

        BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(new File(dbPath)));
        BufferedOutputStream buffOut = new BufferedOutputStream(new FileOutputStream(new File(filePath)));

        int len=0;
        byte[] byteIn = new byte[1024];
        while ((len = buffIn.read(byteIn)) > 0){
            buffOut.write(byteIn, 0, len);
        }

        buffIn.close();
        buffOut.flush();
        buffOut.close();
    }
}
