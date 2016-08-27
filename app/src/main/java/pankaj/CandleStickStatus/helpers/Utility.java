package pankaj.CandleStickStatus.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import pankaj.CandleStickStatus.R;
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
}
