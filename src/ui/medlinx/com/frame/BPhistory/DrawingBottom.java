package ui.medlinx.com.frame.BPhistory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class DrawingBottom extends JPanel {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D) g;

		g2D.setFont(new Font("BOLD", 1, 13));
		double rateH = (double) (getHeight() - 10) / 180;
		g2D.translate(0, getHeight());
		g2D.setColor(Color.GRAY);
		g2D.drawString("180", 10, -(int) ((180 - 40) * rateH) - 10);
		g2D.drawString("140", 10, -(int) ((140 - 40) * rateH) - 10);
		g2D.drawString("100", 10, -(int) ((100 - 40) * rateH) - 10);
		g2D.drawString("60", 10, -(int) ((60 - 40) * rateH) - 10);
		g2D.drawString("180", getWidth() - 30, -(int) ((180 - 40) * rateH) - 10);
		g2D.drawString("140", getWidth() - 30, -(int) ((140 - 40) * rateH) - 10);
		g2D.drawString("100", getWidth() - 30, -(int) ((100 - 40) * rateH) - 10);
		g2D.drawString("60", getWidth() - 30, -(int) ((60 - 40) * rateH) - 10);

		Stroke dash1 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 1,}, 0f);
		g2D.setStroke(dash1);
		g2D.drawLine(0, -(int) ((240 - 40) * rateH) - 10, getWidth(),
				-(int) ((240 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((200 - 40) * rateH) - 10, getWidth(),
				-(int) ((200 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((180 - 40) * rateH) - 10, getWidth(),
				-(int) ((180 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((160 - 40) * rateH) - 10, getWidth(),
				-(int) ((160 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((140 - 40) * rateH) - 10, getWidth(),
				-(int) ((140 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((120 - 40) * rateH) - 10, getWidth(),
				-(int) ((120 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((100 - 40) * rateH) - 10, getWidth(),
				-(int) ((100 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((80 - 40) * rateH) - 10, getWidth(),
				-(int) ((80 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((60 - 40) * rateH) - 10, getWidth(),
				-(int) ((60 - 40) * rateH) - 10);

		// 显示计算结果最高，最低值
		g2D.setColor(Color.RED);
		Stroke dash4 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{5, 2,}, 0f);
		g2D.setStroke(dash4);
		g2D.drawLine(0, -(int) ((138 - 40) * rateH) - 10, getWidth(),
				-(int) ((138 - 40) * rateH) - 10);
		g2D.drawLine(0, -(int) ((84 - 40) * rateH) - 10, getWidth(),
				-(int) ((84 - 40) * rateH) - 10);

		g2D.setColor(Color.GRAY);
		Stroke dash2 = new BasicStroke(0.1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash2);
		g2D.drawLine(0, -10, getWidth(), -10);

		Stroke dash3 = new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash3);

		g2D.setFont(new Font("BOLD", 1, 10));
		g2D.drawLine(50, -10, 50, -getHeight());
		g2D.drawString("6/1", 44, 0);

		g2D.drawLine(50 + (getWidth() - 100) / 2, -10,
				50 + (getWidth() - 100) / 2, -getHeight());
		g2D.drawString("7/1", 44 + (getWidth() - 100) / 2, 0);

		g2D.drawLine(50 + (getWidth() - 100), -10, 50 + (getWidth() - 100),
				-getHeight());
		g2D.drawString("8/1", 44 + (getWidth() - 100), 0);

		// 模拟数据
		int[] yPoints1 = new int[60];
		for (int i = 0; i < yPoints1.length; i++) {
			yPoints1[i] = -(int) ((100 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints2 = new int[60];
		for (int i = 0; i < yPoints2.length; i++) {
			yPoints2[i] = -(int) ((100 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints3 = new int[60];
		for (int i = 0; i < yPoints3.length; i++) {
			yPoints3[i] = -(int) ((60 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints4 = new int[60];
		for (int i = 0; i < yPoints4.length; i++) {
			yPoints4[i] = -(int) ((60 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}

		int[] xPoints = new int[60];
		double rateW = (double) (getWidth() - 100) / 59;
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = (int) Math.ceil(rateW * i) + 50;
		}
		g2D.setColor(new Color(13, 104, 107));
		g2D.drawPolyline(xPoints, yPoints1, 60);
		g2D.drawPolyline(xPoints, yPoints3, 60);
		g2D.setColor(new Color(255, 194, 14));
		g2D.drawPolyline(xPoints, yPoints2, 60);
		g2D.drawPolyline(xPoints, yPoints4, 60);
	}
}
