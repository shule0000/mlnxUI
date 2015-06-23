package ui.medlinx.com.doctor_tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.management.MXBean;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.databuff.DataBufferInterface;

public class PrintPatientInfor extends BufferedImage implements Runnable {

	private int canvasWidth;
	private int canvasHeight;
	private boolean[] linkChannelFlag;
	private int[] yChannel;
	private List<Integer>[] xIndex;
	private int maxHeight;
	private float amplitudeScaling;
	private float timeWindow;
	private float timeWindow2;
	private int ptPerSecond;
	private int maxIndexDrawPT;
	private float pixelRateF;
	private String destFileName = null;
	private SettingParameters parameters;
	public SettingParameters getParameters() {
		return parameters;
	}

	public void setParameters(SettingParameters parameters) {
		this.parameters = parameters;
	}

	private ArrayList<float[]> displayBufferList;
	private int maxTimeWindow;
	DataBufferInterface dataBuffer;
	private ArrayList<String> channelList;
	private float scaleTemp = 0.5f;
	private int showLineNumb = 12;
	private int showRowNumb = 1;
	private float paperMmPerPixelVertical;
	List<ItemAndPosition> itemAndPosition = new ArrayList<ItemAndPosition>();
	private int indexDrawPT;
	private boolean firstRound;

	public PrintPatientInfor(ArrayList<float[]> displayBufferList,
			float verticalRangePanel, float v2hRatioPanel,
			DataBufferInterface dataBuffer, SettingParameters parameters,
			String saveFilePath, int indexDrawPT, float timeWindow2,
			boolean firstRound) {

		super((int) parameters.getPaperSize().getHeight(), (int) parameters
				.getPaperSize().getWidth(), BufferedImage.TYPE_INT_RGB);

		DebugTool.printLogDebug(getWidth() + "  " + getHeight());
		this.destFileName = saveFilePath;
		this.parameters = parameters;
		this.displayBufferList = displayBufferList;
		this.dataBuffer = dataBuffer;
		this.indexDrawPT = indexDrawPT;
		this.timeWindow2 = timeWindow2;
		this.firstRound = firstRound;
		paperMmPerPixelVertical = 0.334f;
		linkChannelFlag = new boolean[SystemConstant.ECGCHANNELNUM];
		yChannel = new int[SystemConstant.ECGCHANNELNUM];
		System.out.println("ECGCHANNELNUM = " + SystemConstant.ECGCHANNELNUM);
		maxTimeWindow = dataBuffer.getSecondBuffer();
		ptPerSecond = dataBuffer.getFrequency();
		xIndex = new ArrayList[12];
		channelList = new ArrayList<String>();
		setupChannelList();

		InitBufferedImage(this);
		ItemAndPosition item = new ItemAndPosition(this);
		item.RenewItem();
	}

	/**
	 * This run function is starting a thread to save ECGimage into pdf
	 */
	public void run() {
		SavePdf(true);
	}

	/**
	 * 只生成图片，不进行保存
	 * 
	 * @param gid
	 */
	public void drawAll(boolean gid) {
		BufferedImage ecgImage = new BufferedImage(this.getWidth(),
				(int) (this.getHeight() - Style.StringDimension.getHeight()),
				BufferedImage.TYPE_INT_RGB);
		InitBufferedImage(ecgImage);
		Graphics2D g2D = ecgImage.createGraphics();
		prepareCanvas(ecgImage.getWidth(), ecgImage.getHeight());
		paint(g2D, gid);
		g2D.dispose();

		// group ecg and patient information to the same image
		Graphics g = this.createGraphics();
		g.drawImage(DrawString(), 0, 0, (int) parameters.getPaperSize()
				.getHeight(), (int) (Style.StringDimension.getHeight()), null);
		g.drawImage(ecgImage, 0, (int) (Style.StringDimension.getHeight()),
				ecgImage.getWidth(), ecgImage.getHeight(), null);
	}
	/**
	 * This SavePdf functionto save ECGimage into pdf
	 */
	public void SavePdf(boolean gid) {
		// get ecg image
		drawAll(gid);
		try {
			ImageIO.write(this, "jpg", new File(destFileName + ".jpg"));
		} catch (IOException e) {

		}
		CreatePdf();
	}

	private void InitBufferedImage(BufferedImage bufferedImage) {
		for (int y = 0; y < bufferedImage.getHeight(); ++y) {
			for (int x = 0; x < bufferedImage.getWidth(); ++x) {
				bufferedImage.setRGB(x, y, Style.PrintBackColor.getRGB());
			}
		}
	}

	private void prepareCanvas(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		maxHeight = canvasHeight / 2;

		float heightPerChannel = (float) canvasHeight / showLineNumb;
		for (int i = 0; i < showRowNumb; ++i) {
			for (int j = 0; j < showLineNumb; ++j) {
				yChannel[i * showLineNumb + j] = (int) (j * heightPerChannel);
			}
		}

		if (heightPerChannel > maxHeight) {
			heightPerChannel = maxHeight;
			DebugTool.printLogDebug("heightPerChannel is too large");
		} else if (heightPerChannel < Style.DrawingPanelMinHeight) {
			heightPerChannel = Style.DrawingPanelMinHeight;
			DebugTool.printLogDebug("heightPerChannel is too small");
		}

		amplitudeScaling = (float) (10.0 / paperMmPerPixelVertical);
		timeWindow = (float) ((canvasWidth - amplitudeScaling * 6 / 8)
				/ amplitudeScaling * 0.4);
		maxIndexDrawPT = (int) (timeWindow * ptPerSecond);

		float showTime = timeWindow2 / showRowNumb;

		pixelRateF = (float) canvasWidth / (float) maxIndexDrawPT;

		for (int j = 0; j < showLineNumb; ++j) {
			xIndex[j] = new ArrayList<Integer>();
			for (int z = 0; z < showTime * ptPerSecond; ++z) {
				xIndex[j]
						.add((int) (z * pixelRateF + amplitudeScaling * 6 / 8));
			}

		}

	}

	/**
	 * set up the list of channel names, i.e. "I", "II", "III"
	 */
	private void setupChannelList() {
		if (dataBuffer == null)
			return;
		channelList.clear();
		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			channelList.add(SystemConstant.ECGLEADNAMES.get(i));
			if (dataBuffer.getPatient().getChannelFlag(i)) {
				linkChannelFlag[i] = true;
			} else {
				linkChannelFlag[i] = false;
			}
		}
	}

	/**
	 * paint function for this component
	 */
	public void paint(Graphics2D g2D, boolean gid) {
		if (g2D == null)
			return;

		// draw grid show in print ECG
		if (parameters.isExistGid() && gid)
			drawGrid(g2D, Style.GridPrintColor);

		g2D.setStroke(new BasicStroke(Style.PrintLineWidth));
		g2D.setColor(Style.PrintForegroundColor);

		drawInformation(g2D, Style.PrintForegroundColor);

		int y1 = 0, y2 = 0;
		float heightPerChannel = (float) canvasHeight / showLineNumb;
		int indexC = 0, displayBufferListIndex;
		for (indexC = 0, displayBufferListIndex = 0; indexC < SystemConstant.ECGCHANNELNUM; ++indexC) {
			if (linkChannelFlag[indexC]) {
				if (indexDrawPT - maxIndexDrawPT >= 0) {
					for (int i = indexDrawPT - maxIndexDrawPT, j = 1; i < indexDrawPT; ++i, ++j) {
						y1 = (int) (displayBufferList
								.get(displayBufferListIndex)[i - 1] * amplitudeScaling);
						y2 = (int) (displayBufferList
								.get(displayBufferListIndex)[i] * amplitudeScaling);
						y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
						y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
						g2D.drawLine(xIndex[indexC].get(j - 1) + 32, y1,
								xIndex[indexC].get(j) + 32, y2);
					}
				} else {
					if (!firstRound) {
						for (int i = xIndex[indexC].size()
								- (maxIndexDrawPT - indexDrawPT), j = 1; i < xIndex[indexC]
								.size(); ++i, ++j) {
							y1 = (int) (displayBufferList
									.get(displayBufferListIndex)[i - 1] * amplitudeScaling);
							y2 = (int) (displayBufferList
									.get(displayBufferListIndex)[i] * amplitudeScaling);
							y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
							y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
							g2D.drawLine(xIndex[indexC].get(j - 1) + 32, y1,
									xIndex[indexC].get(j) + 32, y2);
						}
						for (int i = 1, j = maxIndexDrawPT - indexDrawPT; i < indexDrawPT; ++i, ++j) {
							y1 = (int) (displayBufferList
									.get(displayBufferListIndex)[i - 1] * amplitudeScaling);
							y2 = (int) (displayBufferList
									.get(displayBufferListIndex)[i] * amplitudeScaling);
							y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
							y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
							g2D.drawLine(xIndex[indexC].get(j - 1) + 32, y1,
									xIndex[indexC].get(j) + 32, y2);
						}
					} else {
						for (int i = 1, j = 1; i < indexDrawPT; ++i, ++j) {
							y1 = (int) (displayBufferList
									.get(displayBufferListIndex)[i - 1] * amplitudeScaling);
							y2 = (int) (displayBufferList
									.get(displayBufferListIndex)[i] * amplitudeScaling);
							y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
							y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
							g2D.drawLine(xIndex[indexC].get(j - 1) + 32, y1,
									xIndex[indexC].get(j) + 32, y2);
						}
					}

				}
				displayBufferListIndex++;

			} else {
				g2D.drawLine(xIndex[indexC].get(0) + 32,
						(int) (yChannel[indexC] + heightPerChannel / 2),
						xIndex[indexC].get(xIndex[indexC].size() - 1) + 32,
						(int) (yChannel[indexC] + heightPerChannel / 2));
			}

		}
	}

	/**
	 * Drawing patient information and title of leads
	 * 
	 * @param g
	 *            graphics object for drawing
	 * @param c
	 *            color of background
	 */
	private void drawInformation(Graphics2D g, Color c) {

		if (timeWindow > maxTimeWindow)
			return;

		// erase previous two PT
		float heightPerChannel = (float) canvasHeight / (float) showLineNumb;
		g.setColor(c);

		for (int i = 0; i < showLineNumb; ++i) {
			DrawStartJump(g, 32,
					(int) (heightPerChannel / 2 * (i * 2 + 1)) + 15);
		}

		for (int j = 0; j < SystemConstant.ECGCHANNELNUM; j = j + 3) {
			for (int i = 0; i < timeWindow / showRowNumb; ++i) {
				g.drawString("" + i + "s", xIndex[j].get(i * ptPerSecond),
						getHeight() - 20);
			}
		}

		g.setFont(new Font("TimesRoman", Font.BOLD, Style.PrintFontSize));

		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			if (linkChannelFlag[i])
				g.drawString(channelList.get(i), xIndex[i].get(0) - 15,
						yChannel[i] + heightPerChannel / 2 + 10);
			else
				g.drawString(channelList.get(i) + "未连接", xIndex[i].get(0) - 15,
						yChannel[i] + heightPerChannel / 2 + 10);
		}
	}

	/*
	 * Drwa start jump rectangle
	 */
	private void DrawStartJump(Graphics2D g, int startX, int startY) {
		g.drawLine(startX, startY, (int) (startX + amplitudeScaling / 8),
				startY);
		g.drawLine((int) (startX + amplitudeScaling / 8), startY,
				(int) (startX + amplitudeScaling / 8),
				(int) (startY - amplitudeScaling));
		g.drawLine((int) (startX + amplitudeScaling / 8),
				(int) (startY - amplitudeScaling),
				(int) (startX + 3 * amplitudeScaling / 8),
				(int) (startY - amplitudeScaling));
		g.drawLine((int) (startX + 3 * amplitudeScaling / 8),
				(int) (startY - amplitudeScaling),
				(int) (startX + 3 * amplitudeScaling / 8), startY);
		g.drawLine((int) (startX + 3 * amplitudeScaling / 8), startY,
				(int) (startX + 5 * amplitudeScaling / 8), startY);
	}

	/**
	 * draw background, e.g. for ECG we draw grid as background
	 * 
	 * @param g
	 *            graphics object for drawing
	 * @param c
	 *            color of background
	 */
	private void drawGrid(Graphics2D g, Color c) {

		// small grid = 0.5v
		float timeInterval = amplitudeScaling * scaleTemp;
		int heightTemp = canvasHeight;

		g.setColor(c.darker());
		g.setStroke(new BasicStroke(0.05F));
		// To draw thin lines
		float startIndex = 0;
		// DebugTool.printLogDebug("painting grids:"+timeInterval+":"+startIndex+":"+canvasWidth+":"+borderSize);
		while (startIndex < canvasWidth) {
			g.drawLine((int) startIndex, 0, (int) startIndex, heightTemp);
			startIndex += timeInterval;
		}

		startIndex = 0;
		while (startIndex < heightTemp) {
			g.drawLine(0, (int) startIndex, canvasWidth, (int) startIndex);
			startIndex += timeInterval;
		}

		int strokeWidth = 2;
		g.setColor(c);
		g.setStroke(new BasicStroke(strokeWidth));
		// To draw bold lines
		startIndex = timeInterval * 5;
		while (startIndex < canvasWidth) {
			g.drawLine((int) startIndex, 0, (int) startIndex, heightTemp);
			startIndex += (timeInterval * 5);
		}
		startIndex = timeInterval * 5;
		while (startIndex < heightTemp) {
			g.drawLine(0, (int) startIndex, canvasWidth, (int) startIndex);
			startIndex += (timeInterval * 5);
		}

	}

	/**
	 * This SavePdf functionto save ECGimage into pdf
	 */
	public BufferedImage DrawString() {
		BufferedImage stringImage = new BufferedImage((int) this.getWidth(),
				(int) Style.StringDimension.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		InitBufferedImage(stringImage);

		Graphics2D g = stringImage.createGraphics();

		// draw font
		g.setFont(new Font("TimesRoman", Font.PLAIN, Style.PrintFontSize));
		g.setColor(Color.BLACK);
		for (int i = 0; i < itemAndPosition.size(); ++i) {
			if (itemAndPosition.get(i).GetY() == 20)
				g.setFont(new Font("TimesRoman", Font.PLAIN,
						Style.PrintFontSize));
			g.drawString(itemAndPosition.get(i).GetItem(),
					itemAndPosition.get(i).GetX(), itemAndPosition.get(i)
							.GetY());
			g.setFont(new Font("TimesRoman", Font.PLAIN, Style.PrintFontSize));
		}
		g.setStroke(new BasicStroke(Style.PrintLineWidth));
		g.dispose();
		return stringImage;
	}
	/**
	 * This CreatePdf functionto to create pdffile
	 */
	public void CreatePdf() {
		Rectangle rectangle = new Rectangle(parameters.getPaperSize()
				.getHeight(), parameters.getPaperSize().getWidth());
		Document document = new Document(rectangle, 0, 0, 0, 0);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(new File(
					destFileName)));
			document.open();
			try {
				Image image = Image.getInstance(destFileName + ".jpg", true);
				image.setAlignment(Image.MIDDLE);
				document.add(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

}

/**
 * This ItemAndPosition class to show instruction up of ECG image
 */
class ItemAndPosition {
	private String item;
	private int x;
	private int y;
	private PrintPatientInfor printPatientInfor;

	public ItemAndPosition(int x, int y, String item) {
		this.x = x;
		this.y = y;
		this.item = item;
	}

	public ItemAndPosition(PrintPatientInfor printPatientInfor) {
		this.printPatientInfor = printPatientInfor;
	}

	public void RenewItem() {
		float paperWidth = printPatientInfor.getParameters().getPaperSize()
				.getHeight();

		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.03), 20, "姓名: "
						+ GetSpace(20)
						+ printPatientInfor.dataBuffer.getPatient()
								.getPatientName()));
		// printPatientInfor.itemAndPosition.add(new ItemAndPosition(20, 40,
		// "ID: " + GetSpace(34) + "0"));
		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.03), 40, "病人 ID: "
						+ GetSpace(16)
						+ printPatientInfor.dataBuffer.getPatient()
								.getPatientID()));
		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.03), 60, "身份证: "
						+ GetSpace(5)
						+ printPatientInfor.dataBuffer.getPatient()
								.getPatientInfo().getLastFourNumber()));
		String gender = printPatientInfor.dataBuffer.getPatient().getGender()
				.equals("FAMALE") ? "男" : "女";
		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.03), 80, "年龄: "
						+ GetSpace(0)
						+ printPatientInfor.dataBuffer.getPatient()
								.getPatientInfo().getAge() + GetSpace(17)
						+ "性别: " + gender));

		printPatientInfor.itemAndPosition
				.add(new ItemAndPosition((int) (paperWidth * 0.52), 40,
						"QT/QTc: " + GetSpace(18) + "HR:"
								+ printPatientInfor.dataBuffer.getBPM() + "bpm"));
		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.52), 20, "Time:     "
						+ GetSpace(5)
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
								.format(new Date())));
		printPatientInfor.itemAndPosition.add(new ItemAndPosition(
				(int) (paperWidth * 0.52), 60, "PR:" + GetSpace(26) + "QRS: "));
		printPatientInfor.itemAndPosition
				.add(new ItemAndPosition((int) (paperWidth * 0.52), 80,
						"P-QRS-T Axes: " + GetSpace(24)));
	}
	public String GetSpace(int spaceNumb) {
		String space = "";
		for (int i = 0; i < spaceNumb; ++i) {
			space += " ";
		}
		return space;
	}

	public int GetX() {
		return x;
	}

	public int GetY() {
		return y;
	}

	public String GetItem() {
		return item;
	}
}
