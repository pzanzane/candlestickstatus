package pankaj.CandleStickStatus.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pankaj.CandleStickStatus.ActivityStocksList;
import pankaj.CandleStickStatus.R;
import pankaj.CandleStickStatus.helpers.Utility;

/**
 * Created by pankaj on 9/14/16.
 */
public class DialogStockListSelection extends DialogFragment implements OnItemClickListener {

    private ListView listView = null;
    private Map<String, List<String>> map = null;
    private IDialogStockSelection mStockSelection = null;
    private AdapterSelection adapterSelection = null;
    private String mStartDate = null, mEndDate = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartDate = getArguments().getString(ActivityStocksList.START_DATE);
        mEndDate = getArguments().getString(ActivityStocksList.END_DATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.view_dialog_stocklist_selection, null);
        listView = (ListView) view.findViewById(R.id.listViewStockSelection);
        Set<String> set = map.keySet();
        List<String> list = Arrays.asList(set.toArray(new String[]{}));

        adapterSelection = new AdapterSelection(getActivity(), 0, list);
        listView.setAdapter(adapterSelection);
        listView.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mStockSelection = (IDialogStockSelection) activity;
        try {
            map = Utility.getHashMap(activity);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mStockSelection.onStockOptionSelected(map.get(adapterSelection.getItem(position)), mStartDate, mEndDate);
        dismiss();
    }

    public interface IDialogStockSelection {
        void onStockOptionSelected(List<String> list, String startDate, String endDate);
    }

    private static class AdapterSelection extends ArrayAdapter<String> {

        List<String> strList = null;
        LayoutInflater inflater = null;

        public AdapterSelection(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            strList = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.view_stock_list_selection, null);
            ((Button) convertView).setText(strList.get(position));
            return convertView;
        }

        @Override
        public String getItem(int position) {
            return strList.get(position);
        }
    }


}
