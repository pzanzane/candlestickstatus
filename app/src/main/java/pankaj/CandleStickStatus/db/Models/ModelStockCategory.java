package pankaj.CandleStickStatus.db.Models;

import java.util.List;

/**
 * Created by pankaj on 8/26/16.
 */
public class ModelStockCategory {

    private String strCategory = null;
    private List<ModelStock> listStockPrices = null;

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public List<ModelStock> getListStockPrices() {
        return listStockPrices;
    }

    public void setListStockPrices(List<ModelStock> listStockPrices) {
        this.listStockPrices = listStockPrices;
    }
}
