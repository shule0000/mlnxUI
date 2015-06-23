package ui.medlinx.com.frame.BPhistory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class DrawingLeft_Bottom extends JPanel {
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.translate(0, getHeight());

		Stroke dash1 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash1);
		g2D.setColor(Color.gray);
		g2D.setFont(new Font("BOLD", 1, 11));
		g2D.drawLine(20, -1, getWidth(), -1);
		g2D.drawLine(20, -(getHeight() - 5), getWidth(), -(getHeight() - 5));
		g2D.drawLine(20 + (getWidth() - 21) / 2, 0, 20 + (getWidth() - 21) / 2,
				-(getHeight() - 5));

		double rateH = (double) (getHeight() - 5) / 160;
		g2D.drawString("180", 1, -(int) (140 * rateH));
		g2D.drawString("140", 1, -(int) (100 * rateH));
		g2D.drawString("100", 1, -(int) (60 * rateH));
		g2D.drawString("60", 1, -(int) (20 * rateH));

		g2D.setColor(Color.LIGHT_GRAY);
		g2D.drawLine(20, 0, 20, -(getHeight() - 5));
		g2D.drawLine(getWidth() - 1, 0, getWidth() - 1, -(getHeight() - 5));

		Stroke dash2 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 1,}, 0f);
		g2D.setStroke(dash2);
		g2D.drawLine(20, -(int) (140 * rateH), getWidth(), -(int) (140 * rateH));
		g2D.drawLine(20, -(int) (120 * rateH), getWidth(), -(int) (120 * rateH));
		g2D.drawLine(20, -(int) (100 * rateH), getWidth(), -(int) (100 * rateH));
		g2D.drawLine(20, -(int) (80 * rateH), getWidth(), -(int) (80 * rateH));
		g2D.drawLine(20, -(int) (60 * rateH), getWidth(), -(int) (60 * rateH));
		g2D.drawLine(20, -(int) (40 * rateH), getWidth(), -(int) (40 * rateH));
		g2D.drawLine(20, -(int) (20 * rateH), getWidth(), -(int) (20 * rateH));

		g2D.setColor(Color.RED);
		Stroke dash3 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{5, 2,}, 0f);
		g2D.setStroke(dash3);
		g2D.drawLine(20, -(int) ((138 - 40) * rateH), getWidth(),
				-(int) ((138 - 40) * rateH));
		g2D.drawLine(20, -(int) ((84 - 40) * rateH), getWidth(),
				-(int) ((84 - 40) * rateH));

		g2D.setColor(new Color(0, 169, 157));
		Stroke dash4 = new BasicStroke(18f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 3.5f, new float[]{1, 0,}, 0f);
		g2D.setStroke(dash4);
		g2D.drawLine((int) ((getWidth() - 20) / 2.5),
				-(int) ((81 - 40) * rateH), (int) ((getWidth() - 20) / 2.5),
				-(int) ((125 - 40) * rateH));

		g2D.setColor(new Color(13, 104, 107));
		g2D.setStroke(dash4);
		g2D.drawLine((int) ((getWidth() - 20) / 1.1),
				-(int) ((76 - 40) * rateH), (int) ((getWidth() - 20) / 1.1),
				-(int) ((129 - 40) * rateH));

		g2D.setFont(new Font("BOLD", 1, 14));
		g2D.setColor(Color.gray);
		g2D.drawString(" 81", (int) ((getWidth() - 20) / 2.5 - 32),
				-(int) ((81 - 40) * rateH));
		g2D.drawString("125", (int) ((getWidth() - 20) / 2.5 - 32),
				-(int) ((125 - 40) * rateH));

		g2D.setColor(Color.black);
		g2D.drawString("76", (int) ((getWidth() - 20) / 1.1 + 10),
				-(int) ((76 - 40) * rateH));
		g2D.drawString("129", (int) ((getWidth() - 20) / 1.1 + 10),
				-(int) ((129 - 40) * rateH));

	}
}
