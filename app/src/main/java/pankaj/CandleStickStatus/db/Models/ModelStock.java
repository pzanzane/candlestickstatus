package pankaj.CandleStickStatus.db.Models;

import pankaj.CandleStickStatus.db.DbModel;
import pankaj.CandleStickStatus.db.dao.ModelStockDao;

public class ModelStock implements DbModel {

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void setId(long id) {

    }

    @Override
    public String getTableName() {
        return ModelStockDao.TABLE_NAME;
    }

    @Override
    public String getCreateStatement() {
        return ModelStockDao.CREATE_TABLE;
    }

    @Override
    public String getPrimaryKey() {
        return ModelStockDao.ID;
    }

    public enum CandleStatus{
        BORING,EXCITING_GREEN,EXCITING_RED
    }
    private String symbol = null;

    private String date=null;

    private Double open=null;

    private Double high = null;

    private Double low=null;

    private Double close=null;

    private CandleStatus candleStatus;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getOpen() {
        return open;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void Double(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public CandleStatus getCandleStatus() {
        return candleStatus;
    }

    public void process(){
        if((Math.abs(open-close)<=((Math.abs(high-low)/2)))){
            candleStatus = CandleStatus.BORING;
        }else if(open<close){
             candleStatus = CandleStatus.EXCITING_GREEN;
        }else{
            candleStatus = CandleStatus.EXCITING_RED;
        }
    }
}
