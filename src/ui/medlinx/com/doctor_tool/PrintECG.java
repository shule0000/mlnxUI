package ui.medlinx.com.doctor_tool;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;

public class PrintECG {
	/**
	 * 画图片的方法
	 * 
	 * @param fileName
	 *            [图片的路径]
	 */
	public void drawImage(String fileName) {

		try {
			DocFlavor dof = DocFlavor.INPUT_STREAM.JPEG;
			// 获得打印属性
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			// 获得打印设备 ，字节流方式，图片格式
			PrintService pss[] = PrintServiceLookup.lookupPrintServices(dof,
					pras);
			MediaPrintableArea mp = new MediaPrintableArea(0f, 0f, 500f, 600f,
					Size2DSyntax.MM);
			pras.add(mp);
			// 定位默认打印服务
			PrintService defaultService = PrintServiceLookup
					.lookupDefaultPrintService();

			// 出现设置对话框
//			PrintService service = ServiceUI.printDialog(null, 200, 200, pss,
//					defaultService, dof, pras);

			// 如果没有获取打印机
			// if (service == null) {
			// System.out.println("555");
			// // 终止程序
			// return;
			// }

			// 获得打印工作
			DocPrintJob job = defaultService.createPrintJob();
			// 字节流获取图片信息
			FileInputStream fin = new FileInputStream(fileName);
			DocAttributeSet das = new HashDocAttributeSet();
			// 设置打印内容
			Doc doc = new SimpleDoc(fin, dof, das);

//			if (service != null) {
				// 开始打印
			job.print(doc, pras);
			fin.close();
			// }
		} catch (IOException ie) {
			// 捕获io异常
			ie.printStackTrace();
		} catch (PrintException pe) {
			// 捕获打印异常
			pe.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PrintECG printECG = new PrintECG();
		printECG.drawImage("C:/Users/dell-pc/Desktop/nbmlnxUI/temp/temp1.pdf.jpg");
	}
}
