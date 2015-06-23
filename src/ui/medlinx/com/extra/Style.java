package ui.medlinx.com.extra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

public class Style {
	/**
	 * menu
	 */

	/**
	 * Drawing Panel
	 */
	public static int DrawingPanelBorderSize = 2;
	public static Color DrawingPanelBorderColor = Color.GRAY;
	public static int DrawingPanelMinHeight = 30;
	/**
	 * Drawing Area
	 */

	/**
	 * Information Area
	 */
	public static int InfoAreaWidth = 180;
	public static Color InfoAreaForegroundColor = Color.CYAN;
	public static Color InfoAreaBackgroundColor = Color.BLACK;
	public static Font InfoTitleFont = new Font("宋体", Font.PLAIN, 18);
	public static Font InfoSubTitleFont = new Font("宋体", Font.PLAIN, 12);
	public static Dimension InfoSubSectionDimension = new Dimension(150, 80);
	public static final Dimension InfoButtonDimension = new Dimension(25, 25);
	public static final Dimension ControlPanelDimension = new Dimension(150,
			150);
	public static final Dimension ScaleBarDimension = new Dimension(150, 20);
	public static final Dimension HistoryButtonDimension = new Dimension(100,
			100);
	public static Font signalFont = new Font("宋体", Font.PLAIN, 12);

	/*
	 * label show rate and show time percent时间频率和百分比
	 */
	public static float highLabelAlarmHZ = 1.5f;
	public static float mediumLabelAlarmHZ = 0.7f;
	public static float lowLabelAlarmHZ = 0.5f;
	public static float highLabelShowPercent = 0.6f;
	public static float mediumLabelShowPercent = 0.6f;
	public static float lowLabelShowPercent = 0.8f;

	public static int highAlarmDelay[] = {
			(int) (1 / Style.highLabelAlarmHZ * 1000 * Style.highLabelShowPercent),
			(int) (1 / Style.highLabelAlarmHZ * 1000 * (1 - Style.highLabelShowPercent))};
	public static int mediumAlarmDelay[] = {
			(int) (1 / Style.mediumLabelAlarmHZ * 1000 * Style.mediumLabelShowPercent),
			(int) (1 / Style.mediumLabelAlarmHZ * 1000 * (1 - Style.mediumLabelShowPercent))};
	public static int lowAlarmDelay[] = {
			(int) (1 / Style.lowLabelAlarmHZ * 1000 * Style.lowLabelShowPercent),
			(int) (1 / Style.lowLabelAlarmHZ * 1000 * (1 - Style.lowLabelShowPercent))};

	/*
	 * print style
	 */
	public static final Color GridPrintColor = Color.RED;
	public static final Color PrintForegroundColor = Color.BLACK;
	public static final Color PrintBackColor = Color.WHITE;
	public static final float PrintLineWidth = 1.6f;
	public static final int PrintFontSize = 13;
	public static Dimension StringDimension = new Dimension(800, 85);
	/**
	 * page size
	 */
	public static final Rectangle nomalPaperSize = new Rectangle(1100, 500);
	public static final Rectangle[] paperSizeArr = {PageSize.A0, PageSize.A1,
			PageSize.A2, PageSize.A3, PageSize.A4, nomalPaperSize};

	// login bg color
	public static final Color loginBGColor = new Color(176, 224, 230);
}
