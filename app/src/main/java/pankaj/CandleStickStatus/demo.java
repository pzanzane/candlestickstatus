package pankaj.CandleStickStatus;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pankaj.CandleStickStatus.db.Models.ModelStock;

/**
 * Created by pankaj on 8/26/16.
 */
public class demo {

    public static void main(String z[]){

        calculate();
    }
    public static void calculate() {


        POIFSFileSystem fs = null;
        try {
            fs = new POIFSFileSystem(new FileInputStream(new File("/home/pankaj/Desktop/excels/NiftyQuality30.xls")));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            List<ModelStock> greenModelStockList = new ArrayList<ModelStock>();
            List<ModelStock> redModelStockList = new ArrayList<ModelStock>();
            List<ModelStock> boringModelStockList = new ArrayList<ModelStock>();

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < rowCount; i++) {

                String name = sheet.getRow(i).getCell(1).getStringCellValue();
                builder.append("\""+name+"\",");

                double open = Double.parseDouble(sheet.getRow(i).getCell(6).getStringCellValue());
                double close = Double.parseDouble(sheet.getRow(i).getCell(9).getStringCellValue());
                double high = Double.parseDouble(sheet.getRow(i).getCell(7).getStringCellValue());
                double low = Double.parseDouble(sheet.getRow(i).getCell(8).getStringCellValue());

                ModelStock modelStock = new ModelStock();
                modelStock.setSymbol(name);
                modelStock.setOpen(open);
                modelStock.setClose(close);
                modelStock.setHigh(high);
                modelStock.setLow(low);
                modelStock.process();

                if (modelStock.getCandleStatus().ordinal() == ModelStock.CandleStatus.EXCITING_GREEN.ordinal()) {
                    greenModelStockList.add(modelStock);
                } else if (modelStock.getCandleStatus().ordinal() == ModelStock.CandleStatus.EXCITING_RED.ordinal()) {
                    redModelStockList.add(modelStock);
                } else if (modelStock.getCandleStatus().ordinal() == ModelStock.CandleStatus.BORING.ordinal()) {
                    boringModelStockList.add(modelStock);
                }


            }

            System.out.print(builder.toString());
            printGreenCandles(greenModelStockList);
            printRedCandles(redModelStockList);
            printBoringCandles(boringModelStockList);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void printGreenCandles(List<ModelStock> list) {

        System.out.println("-----------------------------------------------------");
        System.out.println("Green");
        for (ModelStock model : list) {
            System.out.println(model.getSymbol()+" open:"+model.getOpen()+" close:"+model.getClose()+" High:"+model.getHigh()+" Low:"+model.getLow());
        }
    }

    private static void printRedCandles(List<ModelStock> list) {

        System.out.println("-----------------------------------------------------");
        System.out.println("Red");
        for (ModelStock model : list) {
            System.out.println(model.getSymbol()+" open:"+model.getOpen()+" close:"+model.getClose()+" High:"+model.getHigh()+" Low:"+model.getLow());
        }
    }

    private static void printBoringCandles(List<ModelStock> list) {

        System.out.println("-----------------------------------------------------");
        System.out.println("Boring");
        for (ModelStock model : list) {
            System.out.println(model.getSymbol()+" open:"+model.getOpen()+" close:"+model.getClose()+" High:"+model.getHigh()+" Low:"+model.getLow());
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
