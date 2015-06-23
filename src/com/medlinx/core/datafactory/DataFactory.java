package com.medlinx.core.datafactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import ui.medlinx.com.extra.ParamenterStore;
import ui.medlinx.com.extra.SettingParameters;

import com.google.gson.Gson;

public class DataFactory {

	
	private final static String DIR_STRING = "etc";
	/*
	 * 保存配置信息
	 */
	public static void save() {
		ParamenterStore paramenterStore = new ParamenterStore(
				SettingParameters.getInstance());
		Gson gson = new Gson();
		String paramenterStoreJson = gson.toJson(paramenterStore);

		File file = new File(DIR_STRING);
		if (!file.exists())
			file.mkdir();
		file = new File(DIR_STRING + File.separator + "mlnx.ini");
		if (file.exists())
			file.delete();

		try {
			file.createNewFile();
			Writer writer = new FileWriter(file);
			writer.write(paramenterStoreJson.toCharArray());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 读取配置信息
	 */
	public static ParamenterStore readParameters() {
		File file = new File(DIR_STRING + File.separator + "mlnx.ini");
		if (!file.exists())
			return null;
		ParamenterStore paramenterStore = null;
		try {
			Reader reader = new FileReader(file);
			char read[] = new char[5000];
			int size = reader.read(read);
			String ParamenterStoreJson = new String(read, 0, size);
			Gson gson = new Gson();
			paramenterStore = gson.fromJson(ParamenterStoreJson,
					ParamenterStore.class);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}

		return paramenterStore;
	}
}
