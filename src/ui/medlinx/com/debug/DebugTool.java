package ui.medlinx.com.debug;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DebugTool {

	private static LogType[] logTypes = { LogType.ERROR, LogType.WARN,
			LogType.EMPUTENT, LogType.DEBUG };

	public static void printLog(String log, LogType logType) {
		if (!isPermissionPrint(logType))
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(logType.toString() + ": "
				+ sdf.format(Calendar.getInstance().getTime()) + ": " + log);
	}

	public static void printLogDebug(String log) {
		if (!isPermissionPrint(LogType.DEBUG))
			return;
		LogType logType = LogType.DEBUG;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(logType.toString() + ": "
				+ sdf.format(Calendar.getInstance().getTime()) + ": " + log);
	}

	public static void printLogDebug(int log) {
		if (!isPermissionPrint(LogType.DEBUG))
			return;
		LogType logType = LogType.DEBUG;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(logType.toString() + ": "
				+ sdf.format(Calendar.getInstance().getTime()) + ": " + log);
	}

	private static boolean isPermissionPrint(LogType logType) {
		for (int i = 0; i < logTypes.length; i++) {
			if (logType.compareTo(logTypes[i]) == 0) {
				return true;
			}
		}
		return false;
	}

}
