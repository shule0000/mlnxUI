package ui.medlinx.com.dialog;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import ui.medlinx.com.doctor_tool.PrintECG;
import ui.medlinx.com.doctor_tool.PrintPatientInfor;
import ui.medlinx.com.extra.SettingParameters;

import java.awt.event.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import javax.swing.JButton;

import com.itextpdf.text.Rectangle;
import com.medlinx.core.databuff.DataBufferInterface;

import java.awt.FlowLayout;

public class PrintPreviewDialog extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @throws IOException
	 */
	public PrintPreviewDialog(final JFileChooser saveFile,
			final String tempSavePath,
			final ArrayList<float[]> displayBufferList,
			final float verticalRangePanel, final float v2hRatioPanel,
			final DataBufferInterface dataBuffer,
			final SettingParameters parameters, final int indexDrawPT,
			final float timeWindow2, final boolean firstRound) throws IOException {
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				new File(tempSavePath + "/temp1.pdf").delete();
				new File(tempSavePath + "/temp2.pdf").delete();
				new File(tempSavePath + "/temp1.pdf.jpg").delete();
				new File(tempSavePath + "/temp2.pdf.jpg").delete();
			}

		});
		float h = 600;// 重新设定预览的高度
		float w = (parameters.getPaperSize().getHeight() * h / parameters
				.getPaperSize().getWidth());// 按纸张比例计算预览宽度
		Rectangle papersize = new Rectangle(w, h);
		parameters.setPaperSize(papersize);
		PrintPatientInfor printPatientInfor = new PrintPatientInfor(
				displayBufferList, verticalRangePanel, v2hRatioPanel,
				dataBuffer, parameters, "", indexDrawPT, timeWindow2, firstRound);
		printPatientInfor.drawAll(true);
		ImagePanel imagePanel = new ImagePanel(printPatientInfor);
		setBounds(
				0,
				0,
				java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
				java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 30);
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
			JPanel panel_1 = new JPanel();

			panel_1.setBounds((int) ((getWidth() - w) / 2), (int) ((getHeight()
					- h - 69) / 2), (int) w, (int) h);
			panel_1.add(imagePanel);
			panel.add(panel_1);
			panel_1.setLayout(new GridLayout(1, 1, 0, 0));
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setHgap(30);
			panel.setPreferredSize(new Dimension(10, 30));
			getContentPane().add(panel, BorderLayout.SOUTH);

			final JRadioButton rdbtnNewRadioButton = new JRadioButton("打印网格");
			panel.add(rdbtnNewRadioButton);

			JButton btnNewButton = new JButton("打印");
			panel.add(btnNewButton);
			btnNewButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (rdbtnNewRadioButton.isSelected()) {
						PrintECG printECG = new PrintECG();
						printECG.drawImage(tempSavePath + "/temp1.pdf.jpg");
					} else {
						PrintECG printECG = new PrintECG();
						printECG.drawImage(tempSavePath + "/temp2.pdf.jpg");
					}

				}

			});

			JButton btnNewButton_1 = new JButton("保存");
			panel.add(btnNewButton_1);
			btnNewButton_1.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					int result = saveFile.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						String saveFilePath = saveFile.getSelectedFile()
								.getAbsolutePath();
						PrintPatientInfor printPatientInfor = new PrintPatientInfor(
								displayBufferList, verticalRangePanel,
								v2hRatioPanel, dataBuffer, parameters,
								saveFilePath, indexDrawPT, timeWindow2,firstRound);
						Thread printPatientInforThread = new Thread(
								printPatientInfor);
						printPatientInforThread.start();
					}
				}
			});
		}

	}

	class ImagePanel extends JPanel {
		private BufferedImage bufferedImage;

		public ImagePanel(PrintPatientInfor printPatientInfor)
				throws IOException {
			bufferedImage = printPatientInfor;
		}

		@Override
		public void paint(Graphics g) {
			g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
		}

		@Override
		public void setSize(int width, int height) {
			super.setSize(width, height);
		}
	}
}
