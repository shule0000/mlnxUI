package ui.medlinx.com.frame.BPhistory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class DrawingRight extends JPanel {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.translate(0, getHeight());

		Stroke dash1 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash1);
		g2D.setColor(Color.gray);
		g2D.drawLine(50, -25, 50, -(getHeight() - 5));
		g2D.drawLine(getWidth() - 45, -25, getWidth() - 45, -(getHeight() - 5));
		g2D.drawLine(50, -(getHeight() - 5), getWidth() - 45,
				-(getHeight() - 5));
		g2D.drawLine(50, -25, getWidth() - 45, -25);

		double rateH = (double) (getHeight() - 30) / 140;
		double rateW = (double) (getWidth() - 95) / 8;

		g2D.setFont(new Font("BOLD", 1, 11));
		g2D.drawString("60", 30, -(int) ((60 - 50) * rateH + 25));
		g2D.drawString("80", 30, -(int) ((80 - 50) * rateH + 25));
		g2D.drawString("100", 30, -(int) ((100 - 50) * rateH + 25));
		g2D.drawString("120", 30, -(int) ((120 - 50) * rateH + 25));
		g2D.drawString("140", 30, -(int) ((140 - 50) * rateH + 25));
		g2D.drawString("160", 30, -(int) ((160 - 50) * rateH + 25));
		g2D.drawString("180", 30, -(int) ((180 - 50) * rateH + 25));

		g2D.drawString("日", (int) (1 * rateW + 50), -10);
		g2D.drawString("一", (int) (2 * rateW + 50), -10);
		g2D.drawString("二", (int) (3 * rateW + 50), -10);
		g2D.drawString("三", (int) (4 * rateW + 50), -10);
		g2D.drawString("四", (int) (5 * rateW + 50), -10);
		g2D.drawString("五", (int) (6 * rateW + 50), -10);
		g2D.drawString("六", (int) (7 * rateW + 50), -10);

		Stroke dash2 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 1,}, 0f);
		g2D.setStroke(dash2);
		g2D.drawLine(50, -(int) ((60 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((60 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((70 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((70 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((80 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((80 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((90 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((90 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((100 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((100 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((110 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((110 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((120 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((120 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((130 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((130 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((140 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((140 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((150 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((150 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((160 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((160 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((170 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((170 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((180 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((180 - 50) * rateH + 25));

		Stroke dash4 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{5, 2,}, 0f);
		g2D.setStroke(dash4);
		g2D.setColor(Color.red);
		g2D.drawLine(50, -(int) ((138 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((138 - 50) * rateH + 25));
		g2D.drawLine(50, -(int) ((84 - 50) * rateH + 25), getWidth() - 45,
				-(int) ((84 - 50) * rateH + 25));

		// 模拟数据
		int[] yPoints1 = new int[7];
		for (int i = 0; i < yPoints1.length; i++) {
			yPoints1[i] = -(int) ((100 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints2 = new int[7];
		for (int i = 0; i < yPoints2.length; i++) {
			yPoints2[i] = -(int) ((100 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints3 = new int[7];
		for (int i = 0; i < yPoints3.length; i++) {
			yPoints3[i] = -(int) ((60 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}
		int[] yPoints4 = new int[7];
		for (int i = 0; i < yPoints4.length; i++) {
			yPoints4[i] = -(int) ((60 + (int) (Math.random() * 40) - 40) * rateH) - 10;
		}

		int[] xPoints = new int[7];
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = (int) Math.ceil(rateW * (i + 1)) + 50;
		}

		Stroke dash3 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash3);
		g2D.setColor(new Color(13, 104, 107));
		g2D.drawPolyline(xPoints, yPoints1, 7);
		g2D.drawPolyline(xPoints, yPoints3, 7);

		g2D.fillArc(xPoints[0] - 3, yPoints3[0] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[1] - 3, yPoints3[1] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[2] - 3, yPoints3[2] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[3] - 3, yPoints3[3] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[4] - 3, yPoints3[4] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[5] - 3, yPoints3[5] - 3, 6, 6, 0, 360);
		g2D.drawArc(xPoints[6] - 3, yPoints3[6] - 3, 6, 6, 0, 360);

		g2D.fillArc(xPoints[0] - 3, yPoints1[0] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[1] - 3, yPoints1[1] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[2] - 3, yPoints1[2] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[3] - 3, yPoints1[3] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[4] - 3, yPoints1[4] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[5] - 3, yPoints1[5] - 3, 6, 6, 0, 360);
		g2D.drawArc(xPoints[6] - 3, yPoints1[6] - 3, 6, 6, 0, 360);
		g2D.setColor(new Color(255, 194, 14));
		g2D.drawPolyline(xPoints, yPoints2, 7);
		g2D.drawPolyline(xPoints, yPoints4, 7);
		g2D.fillArc(xPoints[0] - 3, yPoints2[0] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[1] - 3, yPoints2[1] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[2] - 3, yPoints2[2] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[3] - 3, yPoints2[3] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[4] - 3, yPoints2[4] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[5] - 3, yPoints2[5] - 3, 6, 6, 0, 360);
		g2D.drawArc(xPoints[6] - 3, yPoints2[6] - 3, 6, 6, 0, 360);

		g2D.fillArc(xPoints[0] - 3, yPoints4[0] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[1] - 3, yPoints4[1] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[2] - 3, yPoints4[2] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[3] - 3, yPoints4[3] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[4] - 3, yPoints4[4] - 3, 6, 6, 0, 360);
		g2D.fillArc(xPoints[5] - 3, yPoints4[5] - 3, 6, 6, 0, 360);
		g2D.drawArc(xPoints[6] - 3, yPoints4[6] - 3, 6, 6, 0, 360);
	}
}
