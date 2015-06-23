package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jboss.resteasy.spi.metadata.SetterParameter;

import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;
import ui.medlinx.com.frame.Main.MLnxClient;
import ui.medlinx.com.resource.SystemResources;

public class InitScreemPhysicsSizeDialog extends JDialog {

	private final int DialogW = 600;
	private final int DialogH = 500;
	private final double CargPHYW = 85.60;
	private final double CargPHYH = 54.00;

	private MLnxClient client;

	private JLabel screemPHYHeightLabel, screemPHYWidthLabel;
	private CardPanel cardPanel;

	public InitScreemPhysicsSizeDialog(MLnxClient client) {
		super();
		this.client = client;
		getContentPane().setLayout(null);
		setIconImage(SystemResources.MlnxImageIcon.getImage());

		initUI();

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(DialogW, DialogH);
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		setAlwaysOnTop(true);
		this.setTitle("屏幕物理尺寸设置");
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				close();
			}
		});
	}
	
	private void close() {
		this.setVisible(false);
		this.dispose();
	}

	private void initUI() {
		int startX = 20;
		int startY = 20;

		SettingParameters parameters = SettingParameters.getInstance();

		JLabel widthLabel = new JLabel("屏幕物理宽度（mm）：");
		widthLabel.setBounds(startX, startY, 140, 20);
		this.add(widthLabel);
		screemPHYWidthLabel = new JLabel(String.format("%.4f",
				parameters.getScreemPHYW()), JLabel.CENTER);
		screemPHYWidthLabel.setText("");
		screemPHYWidthLabel.setBounds(startX + 150, startY, 120, 20);
		this.add(screemPHYWidthLabel);

		JLabel heightLabel = new JLabel("屏幕物理高度（mm）：", JLabel.CENTER);
		heightLabel.setBounds(startX + 275, startY, 140, 20);
		this.add(heightLabel);
		screemPHYHeightLabel = new JLabel(String.format("%.4f",
				parameters.getScreemPHYH()), JLabel.CENTER);
		screemPHYHeightLabel.setText("");
		screemPHYHeightLabel.setBounds(startX + 425, startY, 120, 20);
		this.add(screemPHYHeightLabel);

		// 确认按钮
		JButton button = new JButton("尺寸校准完毕确认");
		button.setBackground(Color.CYAN);
		button.setBounds(startX, startY + 30, DialogW - 50, 30);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension dimension = Toolkit.getDefaultToolkit()
						.getScreenSize();
				float mmPerPixelVertical = (float) (CargPHYH / cardPanel
						.getCardH());
				float mmperPixelHorizontal = (float) (CargPHYW / cardPanel
						.getCardW());
				float screemPHYW = (float) (mmPerPixelVertical * dimension
						.getWidth());
				float screemPHYH = (float) (mmperPixelHorizontal * dimension
						.getHeight());
				
				SettingParameters parameters = SettingParameters.getInstance();
				parameters.setMmPerPixelVertical(mmPerPixelVertical);
				parameters.setMmperPixelHorizontal(mmperPixelHorizontal);
				parameters.setScreemPHYW(screemPHYW);
				parameters.setScreemPHYH(screemPHYH);
				
				close();
			}
		});
		this.add(button);

		// tip
		JLabel label = new JLabel(
				"<html><body><p>使用身份证或者银行卡，将卡片的左上角和下图显示的卡片区域左上角对其,拖动鼠标是卡片区域和卡片的右下角</p><body></html>",
				JLabel.CENTER);
		label.setFont(new Font("楷体", Font.BOLD, 20));
		label.setForeground(Color.BLUE);
		label.setBounds(startX, startY + 60, DialogW - 50, 50);
		this.add(label);

		// card
		cardPanel = new CardPanel();
		TitledBorder titledBorder = BorderFactory.createTitledBorder("卡片位置");
		titledBorder.setTitleColor(Style.InfoAreaForegroundColor);
		titledBorder.setTitleFont(Style.InfoSubTitleFont);
		cardPanel.setBorder(titledBorder);

		cardPanel.setCardW((int) (CargPHYW/parameters.getMmperPixelHorizontal()));
		cardPanel.setCardH((int) (CargPHYH/parameters.getMmPerPixelVertical()));
		cardPanel.setBounds(startX, startY + 120, DialogW - 50, DialogH - 200);
		this.add(cardPanel);
	}

	private void initScreemPHYSize() {

		if (cardPanel == null || screemPHYWidthLabel == null
				|| screemPHYHeightLabel == null)
			return;

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		float screemPHYW = (float) (CargPHYW / cardPanel.getCardW() * dimension
				.getWidth());
		float screemPHYH = (float) (CargPHYH / cardPanel.getCardH() * dimension
				.getHeight());

		screemPHYWidthLabel.setText(String.format("%.4f", screemPHYW));
		screemPHYHeightLabel.setText(String.format("%.4f", screemPHYH));
	}

	class CardPanel extends JPanel {

		private final int BOEDER = 3;
		private int cardW, cardH;

		public CardPanel() {
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {

				}

				@Override
				public void mousePressed(MouseEvent e) {
					cardW = e.getX() - BOEDER;
					cardH = e.getY() - BOEDER;
					CardPanel.this.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseDragged(MouseEvent e) {
					cardW = e.getX() - BOEDER;
					cardH = e.getY() - BOEDER;
					CardPanel.this.repaint();
				}
			});
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2D = (Graphics2D) g;

			g2D.setColor(Color.GRAY);
			g2D.fill3DRect(0, 0, getWidth(), getHeight(), true);

			g2D.setColor(Color.RED);
			g2D.fill3DRect(0, 0, cardW + 2 * BOEDER, cardH + 2 * BOEDER, true);

			g2D.setColor(Color.CYAN);
			g2D.fill3DRect(BOEDER, BOEDER, cardW, cardH, true);

			g2D.setColor(Color.BLACK);
			g2D.setFont(new Font("楷体", Font.BOLD, 20));
			g2D.drawString("卡片放置区域", cardW / 2 - 50, 30);

			// 绘制鼠标
			g2D.drawImage(SystemResources.cursorArrowIcon.getImage(), cardW + 2
					* BOEDER - 8, cardH + 2 * BOEDER, 24, 24, null);
		}

		@Override
		public void repaint() {
			super.repaint();

			InitScreemPhysicsSizeDialog.this.initScreemPHYSize();
		}

		public int getCardW() {
			return cardW;
		}

		public void setCardW(int cardW) {
			this.cardW = cardW;
			this.repaint();
		}

		public int getCardH() {
			return cardH;
		}

		public void setCardH(int cardH) {
			this.cardH = cardH;
			this.repaint();
		}

	}

	public static void main(String[] args) {
		new InitScreemPhysicsSizeDialog(null);
	}
}
