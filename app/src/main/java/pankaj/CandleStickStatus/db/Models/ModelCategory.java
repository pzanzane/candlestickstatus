package pankaj.CandleStickStatus.db.Models;

import java.util.List;

/**
 * Created by pankaj on 8/26/16.
 */
public class ModelCategory {

    private String strCategory=null;
    private List<String> listStocks=null;

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public List<String> getListStocks() {
        return listStocks;
    }

    public void setListStocks(List<String> listStocks) {
        this.listStocks = listStocks;
    }
}
