package com.hfour.base.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import android.util.Log;

public class CRCUtils {
	private static final String LOG_TAG = "ChecksumCRC32";
	/**
	 * -----------------------------------------------------------------------------
	 * Used to provide an example of how to calculate the checksum of a file using
	 * the CRC-32 checksum engine.
	 *
	 * @version 1.0
	 * @author  Jeffrey M. Hunter  (jhunter@idevelopment.info)
	 * @author  http://www.idevelopment.info
	 * -----------------------------------------------------------------------------
	 */
		public static void doChecksum(String fileName) {

	        try {
	            CheckedInputStream cis = null;
	            long fileSize = 0;
	            try {
	                // Computer CRC32 checksum
	                cis = new CheckedInputStream(
	                        new FileInputStream(fileName), new CRC32());

	                fileSize = new File(fileName).length();
	               
	            } catch (FileNotFoundException e) {
	                System.err.println("File not found.");
	                System.exit(1);
	            }

	            byte[] buf = new byte[128];
	            while(cis.read(buf) >= 0) {
	            }

	            long checksum = cis.getChecksum().getValue();
	            System.out.println(checksum + " " + fileSize + " " + fileName);

	        } catch (IOException e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
	    }
	    
	    public boolean checkCRC(String filepath, long crc) {
			Log.d(LOG_TAG, "checking:" + filepath + "->" + String.format("%x", crc));
			
			File f = new File(filepath);
			if (!f.exists()) {
				Log.e(LOG_TAG, "plugin file not found");
				return false;
			}
			BufferedInputStream bis = null;
			FileInputStream fis = null;
			long fcrc = 0;
			Adler32 adler = new Adler32();
			try {
				fis = new FileInputStream(f);
				bis = new BufferedInputStream(fis);
				byte[] b = new byte[8192];
				int len = bis.read(b, 0, b.length);
				while (len != -1) {
					adler.update(b, 0, len);
					len = bis.read(b, 0, b.length);
				}
				bis.close();
				bis = null;
				fis.close();
				fis = null;
			} catch (Exception e) {
				Log.e(LOG_TAG, "plugin file read failed", e);
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (Exception e) {
						Log.e(LOG_TAG, "maybe some file not closed", e);
					}
					bis = null;
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						Log.e(LOG_TAG, "maybe some file not closed", e);
					}
					bis = null;
				}
			}
			fcrc = adler.getValue();
			if (fcrc != crc) {
				Log.e(LOG_TAG, "plugin file invalid");
				return false;
			}
			return true;
		}
}
