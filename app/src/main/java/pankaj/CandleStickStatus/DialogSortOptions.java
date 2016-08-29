package pankaj.CandleStickStatus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class DialogSortOptions extends DialogFragment implements OnCheckedChangeListener {

    private RadioGroup rdGroup = null;
    private ISortCallBack sortCallBack = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.view_dialog_sort, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rdGroup = (RadioGroup) view.findViewById(R.id.rdGroup);
        rdGroup.setOnCheckedChangeListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sortCallBack = (ISortCallBack) context;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        sortCallBack.sortCallBack(checkedId);
        dismiss();
    }

    public static interface ISortCallBack {
        void sortCallBack(int checkId);
    }
}
