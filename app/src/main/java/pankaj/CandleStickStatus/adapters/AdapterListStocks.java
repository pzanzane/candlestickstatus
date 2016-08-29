package pankaj.CandleStickStatus.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pankaj.CandleStickStatus.R;
import pankaj.CandleStickStatus.db.Models.ModelStock;

/**
 * Created by pankaj on 8/26/16.
 */
public class AdapterListStocks extends ArrayAdapter<ModelStock> {


    private List<ModelStock> listStocks = null;
    private Context mContext = null;
    private LayoutInflater inflater = null;
    private ViewGroup mViewGroup = null;

    public AdapterListStocks(Context context, int resource, List<ModelStock> list) {
        super(context, resource);
        listStocks = list;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewGroup = new ViewGroup();
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.view_stock_list, null);

            mViewGroup = new ViewGroup();
            mViewGroup.mTextCandleStatus = (TextView) convertView.findViewById(R.id.txtCandleStatus);
            mViewGroup.mTextStockName = (TextView) convertView.findViewById(R.id.txtStockName);
            mViewGroup.mTextPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            mViewGroup.relInternalView = convertView.findViewById(R.id.relInternalView);

            convertView.setTag(mViewGroup);

        }

        ModelStock model = listStocks.get(position);
        mViewGroup = (ViewGroup) convertView.getTag();

        mViewGroup.mTextStockName.setText(model.getSymbol().replace(".ns", ""));
        mViewGroup.mTextPrice.setText("Open: " + model.getOpen() + "\t\t" + " Close: " + model.getClose() + "\n" + " High: " + model.getHigh() + "\t\t" + " Low: " + model.getLow());
        mViewGroup.mTextCandleStatus.setText(model.getCandleStatus().name());

        switch (model.getCandleStatus()) {
            case BORING:
                mViewGroup.relInternalView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                break;
            case EXCITING_GREEN:
                mViewGroup.relInternalView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_green_dark));
                break;
            case EXCITING_RED:
                mViewGroup.relInternalView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
                break;
        }


        return convertView;
    }

    @Override
    public int getCount() {
        return listStocks.size();
    }

    public void clear() {
        listStocks.clear();
        notifyDataSetChanged();
    }

    public void sort() {

    }

    public List<ModelStock> getList(){
        return listStocks;
    }
    private static final class ViewGroup {

        private TextView mTextStockName = null, mTextPrice = null, mTextCandleStatus = null;
        private View relInternalView = null;
    }
}
