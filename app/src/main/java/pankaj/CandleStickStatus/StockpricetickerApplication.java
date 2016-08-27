package pankaj.CandleStickStatus;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import pankaj.CandleStickStatus.db.DbConfiguration;
import pankaj.CandleStickStatus.db.DbHelper;


public class StockpricetickerApplication extends Application {

	private static Context applicationContext;


	public StockpricetickerApplication() {
		applicationContext = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		DbHelper.getInstance(
				applicationContext,
				DbConfiguration.getInstance(getApplicationContext())
		);


	}


	public static Context getBasicApplicationContext() {
		return applicationContext;
	}


	/* Checks if external storage is available for read and write */

	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(state)) {
			return true;
		}
		return false;
	}
}
