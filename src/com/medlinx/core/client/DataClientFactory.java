package com.medlinx.core.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.debug.LogType;
import ui.medlinx.com.extra.SettingParameters;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.DataClientBuilder;
import com.mlnx.pms.core.User;

public class DataClientFactory {

	private final static int EMPTY_DATACLIENT_TOTAL = 3;
	private static List<DataClient> clients = new ArrayList<DataClient>();
	private static Thread produceDCThread;
	private static boolean produceDCThreadRun = false;
	private static boolean produceDCThreadStop = false;

	private static int rconnectTime = 0;

	public static boolean produceDataClient() {
		if (produceDCThread != null && produceDCThreadRun)
			return false;
		produceDCThreadRun = true;
		produceDCThread = new Thread(new Runnable() {

			@Override
			public void run() {

				final User loginUser = SettingParameters.getInstance()
						.getLoginUser();
				if (loginUser != null) {
					DebugTool.printLog("start create DataClient",
							LogType.EMPUTENT);
					Thread[] connectThreads = new Thread[EMPTY_DATACLIENT_TOTAL];
					for (int i = 0; i < connectThreads.length; i++) {
						connectThreads[i] = new Thread(new Runnable() {

							@Override
							public void run() {
								DataClient dataClient = DataClientBuilder
										.newBuilder()
										.withServerHostname(
												SettingParameters.getInstance()
														.getIpString())
										.withCredentials(loginUser.getId(),
												loginUser.getPassword())
										.build();
								Boolean successful = null;
								try {
									successful = dataClient.isConnect();
								} catch (JSONObjectException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								if (successful != null) {
									addDataClient(dataClient);
								}
							}
						});
						connectThreads[i].start();
					}
					for (int i = 0; i < connectThreads.length; i++) {
						try {
							connectThreads[i].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					DebugTool.printLog("create DataClient sucessful",
							LogType.EMPUTENT);
					while (produceDCThreadRun) {
						if (loginUser != null
								&& clients.size() < EMPTY_DATACLIENT_TOTAL) {
							DataClient dataClient = DataClientBuilder
									.newBuilder()
									.withServerHostname(
											SettingParameters.getInstance()
													.getIpString())
									.withCredentials(loginUser.getId(),
											loginUser.getPassword()).build();
							Boolean successful = null;
							try {
								successful = dataClient.isConnect();
							} catch (JSONObjectException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (successful != null) {
								addDataClient(dataClient);
							}
						}

						if (clients.size() > EMPTY_DATACLIENT_TOTAL) {
							closeOneDataClient();
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// 重新连接服务器
						rconnectTime++;
						if (rconnectTime >= 10) {
							rconnectTime = 0;
							try {
								rconnect();
							} catch (JSONObjectException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					produceDCThreadStop = true;
				}
			}
		});
		produceDCThread.start();

		return true;
	}

	public static synchronized void rconnect() throws JSONObjectException,
			IOException {

		DebugTool.printLogDebug("start rconnect");
		for (DataClient dataClient : clients) {
			dataClient.isConnect();
		}
		DebugTool.printLogDebug("end rconnect");
	}

	public static void stopProduce() {
		if (produceDCThread != null) {
			produceDCThreadStop = false;
			produceDCThreadRun = false;
			while (!produceDCThreadStop) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			produceDCThread = null;
		}
	}

	public static synchronized void clearDataClients() {
		if (clients.size() == 0)
			return;
		for (Iterator iterator = clients.iterator(); iterator.hasNext();) {
			DataClient dataClient = (DataClient) iterator.next();
			dataClient.close();
			iterator.remove();
		}
	}

	public static synchronized void closeOneDataClient() {
		DataClient dataClient = clients.get(0);
		dataClient.close();
		clients.remove(dataClient);
	}

	public static synchronized DataClient getLoginDataClient() {

		// 没有空闲的已经登入的连接
		if (clients.size() == 0) {
			User loginUser = SettingParameters.getInstance().getLoginUser();
			if (loginUser == null) {
				return null;
			}
			DebugTool.printLogDebug("new client");
			return DataClientBuilder
					.newBuilder()
					.withServerHostname(
							SettingParameters.getInstance().getIpString())
					.withCredentials(loginUser.getId(), loginUser.getPassword())
					.build();
		} else {
			DebugTool.printLogDebug("exist client");
			DataClient dataClient = clients.get(0);
			clients.remove(dataClient);
			return dataClient;
		}
	}

	public static synchronized void addDataClient(DataClient dataClient) {
		clients.add(dataClient);
	}
}
