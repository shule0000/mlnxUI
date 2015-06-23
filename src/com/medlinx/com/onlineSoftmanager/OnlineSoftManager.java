package com.medlinx.com.onlineSoftmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

public class OnlineSoftManager {

	private final static String CONSTR = "http://121.40.137.14:8080";
	public final static String DownLoadUIUrl = CONSTR+"/pms-server/rest/DownLoadUI/";
	public final static String DownLoadUIInfoUrl = CONSTR+"/pms-server/rest/UI/info/";
	public final static String DownLoadDoctorUrl = CONSTR+"/pms-server/rest/DownLoadDoctorApp/";
	public final static String DownLoadDoctorInfoUrl = CONSTR+"/pms-server/rest/Doctor/info/";
	public final static String UpLoadUIUrl = CONSTR+"/pms-server/rest/UpLoadUI/";
	public final static String UpLoadDoctorUrl = CONSTR+"/pms-server/rest/UpLoadDoctorApp/";

	public static long uploadSum = 0;

	public static boolean upLoadFile(String urlString, String path) {
		URL url;
		OutputStream outputStream = null;
		FileInputStream inputStream = null;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept", "application/json");
			outputStream = conn.getOutputStream();

			File file = new File(path);
			inputStream = new FileInputStream(file);
			System.out.println(inputStream.available());

			byte[] data = new byte[10000];
			int size = 0;
			uploadSum = 0;
			while ((size = inputStream.read(data)) != -1) {
				outputStream.write(data, 0, size);

				uploadSum += size;
			}
			outputStream.close();
			inputStream.close();
			System.out.println("conn.getResponseCode() = "
					+ conn.getResponseCode());
			if (conn.getResponseCode() == 204 || conn.getResponseCode() == 200)
				return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static FileInfo getSoftInfo(String urlString) {

		URL url;
		InputStream inputStream = null;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			inputStream = conn.getInputStream();
			byte[] data = new byte[10000];
			int size = 0;
			int b;
			while ((b = inputStream.read()) != -1) {
				data[size++] = (byte) b;
			}
			String jsonString = new String(data, 0, size, "utf-8");
			Gson gson = new Gson();
			return gson.fromJson(jsonString, FileInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static long downloadSum = 0;

	public static boolean downLoadFile(String urlString, String path)
			throws IOException {
		URL url;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		inputStream = conn.getInputStream();

		File file = new File(path);
		if (file.exists())
			file.delete();
		outputStream = new FileOutputStream(file);
		byte[] data = new byte[1000000];
		int size = 0;
		downloadSum = 0;
		while ((size = inputStream.read(data)) != -1) {
			outputStream.write(data, 0, size);
			downloadSum += size;
		}
		outputStream.close();
		return true;
	}
}
