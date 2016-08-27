package pankaj.CandleStickStatus.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by pankaj on 8/26/16.
 */
public class AdapterCategoryListStocks extends BaseAdapter {

    private Context context = null;
    private String[] catagoriesArray = null;
    private List models = null;

    public AdapterCategoryListStocks(Context context,String[] catagoriesArray) {
    }

    public static enum ITEM_TYPE{
        TITLE,CATEGORY;
    }
    @Override
    public int getViewTypeCount() {
        return catagoriesArray.length;
    }

    @Override
    public int getItemViewType(int position) {


        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
