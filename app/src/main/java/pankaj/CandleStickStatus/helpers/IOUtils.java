/*! * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * @File:
 *		IOUtils.java
 * @Project:
 *		Rhythm
 * @Abstract:
 *		
 * @Copyright:
*     		Copyright © 2014 Saregama India Ltd. All Rights Reserved
*			Written under contract by Robosoft Technologies Pvt. Ltd.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* 
 *  Created by pankaj on 24-Jun-2014
 */

package pankaj.CandleStickStatus.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author pankaj
 *
 */
public class IOUtils {


	public static String readFromFile(InputStream fis) throws IOException{


		StringBuilder strBuilder = new StringBuilder();
		byte[] bytes = new byte[1024];
		int count;

		while ((count = fis.read(bytes, 0, bytes.length)) > -1) {
			strBuilder.append(new String(bytes, 0, count, Charset
									  .forName("UTF-8")));
		}
		fis.close();

		return strBuilder.toString();
	}

	public static String readFromFile(String filePath) throws IOException{

		return readFromFile(new FileInputStream(filePath));
	}
	
	public static void writeToFile(String info,String filePath){

		try {
			new File(filePath).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fos;
		try {

			fos = new FileOutputStream(new File(filePath));
			fos.write(info.trim()
					.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
