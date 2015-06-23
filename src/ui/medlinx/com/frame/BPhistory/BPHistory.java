package ui.medlinx.com.frame.BPhistory;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.border.BevelBorder;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

public class BPHistory extends JPanel {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BPHistory window = new BPHistory();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BPHistory() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		BorderLayout borderLayout = (BorderLayout) frame.getContentPane()
				.getLayout();
		borderLayout.setVgap(10);
		frame.setTitle("历史血压");
		frame.setBounds(
				(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - 900) / 2,
				100, 900, 620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 70));
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 2));

		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(10, 28));
		panel.add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_5 = new JPanel();
		panel_5.setBackground(new Color(0, 139, 139));
		panel_3.add(panel_5);

		JLabel label = new JLabel("郑博（18岁）");
		label.setFont(new Font("微软雅黑", Font.BOLD, 13));
		label.setForeground(new Color(255, 255, 255));
		panel_5.add(label);

		JPanel panel_6 = new JPanel();
		panel_6.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_6.setBackground(new Color(0, 139, 139));
		panel_3.add(panel_6);
		panel_6.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));

		JButton btnNewButton = new JButton("主页");
		btnNewButton.setBorder(null);
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("幼圆", Font.BOLD, 14));
		btnNewButton.setBackground(new Color(30, 144, 255));
		btnNewButton.setPreferredSize(new Dimension(65, 28));
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel_6.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("退出");
		btnNewButton_1.setBorder(null);
		btnNewButton_1.setBackground(new Color(30, 144, 255));
		btnNewButton_1.setForeground(new Color(255, 255, 255));
		btnNewButton_1.setFont(new Font("幼圆", Font.BOLD, 14));
		btnNewButton_1.setPreferredSize(new Dimension(65, 28));
		btnNewButton_1
				.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel_6.add(btnNewButton_1);

		JPanel panel_4 = new JPanel();
		panel_4.setPreferredSize(new Dimension(10, 40));
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setVgap(3);
		flowLayout.setHgap(0);
		panel.add(panel_4, BorderLayout.CENTER);

		JPanel panel_7 = new JPanel();
		panel_7.setBackground(new Color(238, 232, 170));
		panel_7.setBorder(new LineBorder(new Color(245, 245, 245), 3, true));
		FlowLayout flowLayout_1 = (FlowLayout) panel_7.getLayout();
		flowLayout_1.setVgap(3);
		flowLayout_1.setHgap(0);
		panel_4.add(panel_7);

		JLabel label_5 = new JLabel("           ");
		panel_7.add(label_5);

		JLabel label_1 = new JLabel("诊疗室血压");
		label_1.setForeground(new Color(0, 139, 139));
		label_1.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_7.add(label_1);

		JLabel label_2 = new JLabel("      ");
		panel_7.add(label_2);

		textField = new JTextField();
		panel_7.add(textField);
		textField.setColumns(3);

		JLabel label_3 = new JLabel("/");
		label_3.setForeground(new Color(0, 139, 139));
		label_3.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_7.add(label_3);

		textField_1 = new JTextField();
		panel_7.add(textField_1);
		textField_1.setColumns(3);

		JLabel lblMmhg = new JLabel("mmHg");
		lblMmhg.setFont(new Font("幼圆", Font.BOLD, 14));
		lblMmhg.setForeground(new Color(0, 139, 139));
		panel_7.add(lblMmhg);

		JLabel label_4 = new JLabel("    ");
		panel_7.add(label_4);

		textField_2 = new JTextField();
		panel_7.add(textField_2);
		textField_2.setColumns(3);

		JLabel lblBpm = new JLabel("bpm");
		lblBpm.setFont(new Font("幼圆", Font.BOLD, 14));
		lblBpm.setForeground(new Color(0, 139, 139));
		panel_7.add(lblBpm);

		JLabel label_6 = new JLabel("        ");
		panel_7.add(label_6);

		JButton button_7 = new JButton("重新计算");
		button_7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_7.setBackground(new Color(30, 144, 255));
		button_7.setForeground(new Color(255, 255, 255));
		button_7.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_7.add(button_7);

		JLabel label_11 = new JLabel("          ");
		panel_7.add(label_11);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new GridLayout(0, 3, 10, 0));

		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new LineBorder(new Color(238, 232, 170), 4, true));
		panel_2.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));

		JPanel panel_11 = new JPanel();
		panel_11.setBackground(new Color(238, 232, 170));
		panel_11.setPreferredSize(new Dimension(10, 30));
		panel_8.add(panel_11, BorderLayout.NORTH);
		panel_11.setLayout(new BorderLayout(0, 0));

		JPanel panel_13 = new JPanel();
		panel_13.setPreferredSize(new Dimension(30, 10));
		panel_11.add(panel_13, BorderLayout.WEST);
		panel_13.setLayout(new GridLayout(0, 1, 0, 0));

		JButton button = new JButton("");
		button.setBackground(new Color(238, 232, 170));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		button.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/search.png")));
		panel_13.add(button);

		JPanel panel_14 = new JPanel();
		panel_14.setBackground(new Color(238, 232, 170));
		panel_11.add(panel_14, BorderLayout.CENTER);

		JLabel label_7 = new JLabel("各期间的平均值（最近7天）");
		label_7.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_14.add(label_7);

		JPanel panel_15 = new JPanel();
		panel_15.setPreferredSize(new Dimension(30, 10));
		panel_11.add(panel_15, BorderLayout.EAST);
		panel_15.setLayout(new GridLayout(0, 1, 0, 0));

		JButton btnNewButton_2 = new JButton("");
		btnNewButton_2.setBackground(new Color(238, 232, 170));
		btnNewButton_2
				.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton_2.setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		btnNewButton_2.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/help.png")));
		panel_15.add(btnNewButton_2);

		JPanel panel_12 = new JPanel();
		panel_8.add(panel_12, BorderLayout.CENTER);
		panel_12.setLayout(new BorderLayout(0, 0));

		JPanel panel_31 = new JPanel();
		panel_31.setPreferredSize(new Dimension(10, 20));
		panel_12.add(panel_31, BorderLayout.NORTH);
		panel_31.setLayout(new BorderLayout(0, 0));

		JPanel panel_34 = new JPanel();
		panel_31.add(panel_34, BorderLayout.CENTER);
		panel_34.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_37 = new JPanel();
		panel_34.add(panel_37);

		JLabel label_12 = new JLabel("上次");
		label_12.setForeground(Color.GRAY);
		label_12.setFont(new Font("幼圆", Font.BOLD, 13));
		panel_37.add(label_12);

		JPanel panel_38 = new JPanel();
		panel_34.add(panel_38);

		JLabel label_13 = new JLabel("本次");
		label_13.setFont(new Font("幼圆", Font.BOLD, 13));
		panel_38.add(label_13);

		JPanel panel_36 = new JPanel();
		panel_36.setPreferredSize(new Dimension(40, 10));
		panel_31.add(panel_36, BorderLayout.EAST);

		JLabel lblmmhg = new JLabel("[mmHg]");
		lblmmhg.setForeground(Color.DARK_GRAY);
		lblmmhg.setFont(new Font("幼圆", Font.BOLD, 11));
		panel_36.add(lblmmhg);

		JPanel panel_35 = new JPanel();
		panel_35.setPreferredSize(new Dimension(30, 10));
		panel_31.add(panel_35, BorderLayout.WEST);

		JPanel panel_33 = new JPanel();
		panel_33.setPreferredSize(new Dimension(10, 40));
		panel_12.add(panel_33, BorderLayout.SOUTH);
		panel_33.setLayout(new BorderLayout(0, 0));

		JPanel panel_39 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_39.getLayout();
		flowLayout_2.setVgap(0);
		flowLayout_2.setHgap(0);
		panel_39.setPreferredSize(new Dimension(10, 16));
		panel_33.add(panel_39, BorderLayout.NORTH);

		JLabel label_14 = new JLabel("（脉搏数，测量天数）");
		label_14.setForeground(Color.DARK_GRAY);
		label_14.setHorizontalTextPosition(SwingConstants.CENTER);
		label_14.setHorizontalAlignment(SwingConstants.CENTER);
		label_14.setFont(new Font("幼圆", Font.BOLD, 11));
		label_14.setBorder(null);
		panel_39.add(label_14);

		JPanel panel_40 = new JPanel();
		FlowLayout flowLayout_15 = (FlowLayout) panel_40.getLayout();
		flowLayout_15.setAlignment(FlowLayout.LEFT);
		panel_33.add(panel_40, BorderLayout.CENTER);

		JLabel lblmmhg_1 = new JLabel("降压目标135/85mmHg。早晨夜晚目标都达成。");
		lblmmhg_1.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_40.add(lblmmhg_1);

		JPanel panel_32 = new JPanel();
		panel_12.add(panel_32, BorderLayout.CENTER);
		panel_32.setLayout(new BorderLayout(0, 0));

		JPanel panel_41 = new JPanel();
		panel_32.add(panel_41, BorderLayout.CENTER);
		panel_41.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel panel_46 = new JPanel();
		panel_41.add(panel_46);
		panel_46.setLayout(new BorderLayout(0, 0));

		JPanel panel_48 = new JPanel();
		panel_46.add(panel_48, BorderLayout.CENTER);
		panel_48.setLayout(new GridLayout(1, 0, 0, 0));
		DrawingLeft_Top drawingLeft_Top = new DrawingLeft_Top();
		panel_48.add(drawingLeft_Top);

		JPanel panel_49 = new JPanel();
		panel_49.setPreferredSize(new Dimension(10, 18));
		panel_46.add(panel_49, BorderLayout.SOUTH);
		panel_49.setLayout(new BorderLayout(0, 0));

		JPanel panel_50 = new JPanel();
		panel_50.setPreferredSize(new Dimension(20, 10));
		panel_49.add(panel_50, BorderLayout.WEST);

		JPanel panel_51 = new JPanel();
		panel_49.add(panel_51, BorderLayout.CENTER);
		panel_51.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_52 = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panel_52.getLayout();
		flowLayout_5.setVgap(2);
		panel_51.add(panel_52);

		JLabel lblbpm = new JLabel("（89bpm, 4天）");
		lblbpm.setForeground(Color.DARK_GRAY);
		lblbpm.setFont(new Font("幼圆", Font.PLAIN, 12));
		panel_52.add(lblbpm);

		JPanel panel_53 = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) panel_53.getLayout();
		flowLayout_6.setVgap(2);
		panel_51.add(panel_53);

		JLabel lblNewLabel = new JLabel("（78bpm, 7天）");
		lblNewLabel.setForeground(Color.DARK_GRAY);
		lblNewLabel.setFont(new Font("幼圆", Font.PLAIN, 12));
		panel_53.add(lblNewLabel);

		JPanel panel_47 = new JPanel();
		panel_41.add(panel_47);
		panel_47.setLayout(new BorderLayout(0, 0));

		JPanel panel_54 = new JPanel();
		DrawingLeft_Bottom drawingLeft_Bottom = new DrawingLeft_Bottom();
		panel_54.add(drawingLeft_Bottom);
		panel_47.add(panel_54, BorderLayout.CENTER);
		panel_54.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_55 = new JPanel();
		panel_55.setPreferredSize(new Dimension(10, 18));
		panel_47.add(panel_55, BorderLayout.SOUTH);
		panel_55.setLayout(new BorderLayout(0, 0));

		JPanel panel_56 = new JPanel();
		panel_56.setPreferredSize(new Dimension(20, 10));
		panel_55.add(panel_56, BorderLayout.WEST);

		JPanel panel_57 = new JPanel();
		panel_55.add(panel_57, BorderLayout.CENTER);
		panel_57.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_58 = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) panel_58.getLayout();
		flowLayout_7.setVgap(2);
		panel_57.add(panel_58);

		JLabel lblbpm_1 = new JLabel("（76bpm, 5天）");
		lblbpm_1.setFont(new Font("幼圆", Font.PLAIN, 12));
		lblbpm_1.setForeground(Color.DARK_GRAY);
		panel_58.add(lblbpm_1);

		JPanel panel_59 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_59.getLayout();
		flowLayout_8.setVgap(2);
		panel_57.add(panel_59);

		JLabel lblbpm_2 = new JLabel("（78bpm, 7天）");
		lblbpm_2.setForeground(Color.DARK_GRAY);
		lblbpm_2.setFont(new Font("幼圆", Font.PLAIN, 12));
		panel_59.add(lblbpm_2);

		JPanel panel_42 = new JPanel();
		panel_42.setPreferredSize(new Dimension(40, 10));
		panel_32.add(panel_42, BorderLayout.EAST);

		JPanel panel_43 = new JPanel();
		panel_43.setPreferredSize(new Dimension(30, 10));
		panel_32.add(panel_43, BorderLayout.WEST);
		panel_43.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_44 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_44.getLayout();
		flowLayout_3.setVgap(10);
		panel_43.add(panel_44);

		JButton button_8 = new JButton("");
		button_8.setBorder(null);
		button_8.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/sun.png")));
		panel_44.add(button_8);

		JPanel panel_45 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panel_45.getLayout();
		flowLayout_4.setVgap(10);
		panel_43.add(panel_45);

		JButton button_9 = new JButton("");
		button_9.setBorder(null);
		button_9.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/moon.png")));
		panel_45.add(button_9);

		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new LineBorder(new Color(238, 232, 170), 4));
		panel_2.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));

		JPanel panel_16 = new JPanel();
		panel_16.setBackground(new Color(238, 232, 170));
		panel_16.setPreferredSize(new Dimension(10, 30));
		panel_9.add(panel_16, BorderLayout.NORTH);
		panel_16.setLayout(new BorderLayout(0, 0));

		JPanel panel_18 = new JPanel();
		panel_18.setPreferredSize(new Dimension(30, 10));
		panel_16.add(panel_18, BorderLayout.WEST);
		panel_18.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_1 = new JButton("");
		button_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_1.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/search.png")));
		button_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_1.setBackground(new Color(238, 232, 170));
		panel_18.add(button_1);

		JPanel panel_19 = new JPanel();
		panel_19.setBackground(new Color(238, 232, 170));
		panel_16.add(panel_19, BorderLayout.CENTER);

		JLabel label_8 = new JLabel("最近一周每天的平均值");
		label_8.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_19.add(label_8);

		JPanel panel_20 = new JPanel();
		panel_20.setPreferredSize(new Dimension(30, 10));
		panel_16.add(panel_20, BorderLayout.EAST);
		panel_20.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_2 = new JButton("");
		button_2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_2.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/help.png")));
		button_2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_2.setBackground(new Color(238, 232, 170));
		panel_20.add(button_2);

		JPanel panel_17 = new JPanel();
		panel_9.add(panel_17, BorderLayout.CENTER);
		panel_17.setLayout(new BorderLayout(0, 5));

		JPanel panel_60 = new JPanel();
		panel_60.setPreferredSize(new Dimension(10, 20));
		panel_17.add(panel_60, BorderLayout.NORTH);
		panel_60.setLayout(new BorderLayout(0, 0));

		JPanel panel_65 = new JPanel();
		panel_65.setPreferredSize(new Dimension(30, 10));
		panel_60.add(panel_65, BorderLayout.WEST);

		JPanel panel_66 = new JPanel();
		panel_60.add(panel_66, BorderLayout.CENTER);

		JPanel panel_67 = new JPanel();
		panel_67.setPreferredSize(new Dimension(40, 10));
		panel_60.add(panel_67, BorderLayout.EAST);

		JLabel label_17 = new JLabel("[mmHg]");
		label_17.setForeground(Color.DARK_GRAY);
		label_17.setFont(new Font("幼圆", Font.BOLD, 11));
		panel_67.add(label_17);

		JPanel panel_62 = new JPanel();
		panel_62.setPreferredSize(new Dimension(10, 40));
		panel_17.add(panel_62, BorderLayout.SOUTH);
		panel_62.setLayout(new BorderLayout(0, 0));

		JPanel panel_63 = new JPanel();
		FlowLayout flowLayout_9 = (FlowLayout) panel_63.getLayout();
		flowLayout_9.setHgap(0);
		flowLayout_9.setVgap(0);
		panel_63.setPreferredSize(new Dimension(10, 16));
		panel_62.add(panel_63, BorderLayout.NORTH);

		JLabel label_15 = new JLabel("※ 红色代表超出降压目标");
		label_15.setFont(new Font("幼圆", Font.BOLD, 11));
		label_15.setForeground(Color.DARK_GRAY);
		panel_63.add(label_15);

		JPanel panel_64 = new JPanel();
		FlowLayout flowLayout_16 = (FlowLayout) panel_64.getLayout();
		flowLayout_16.setAlignment(FlowLayout.LEFT);
		panel_62.add(panel_64, BorderLayout.CENTER);

		JLabel label_16 = new JLabel("最近一周的平均值为早晨130/78, 夜晚129/76。");
		label_16.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_64.add(label_16);

		JPanel panel_61 = new JPanel();
		panel_17.add(panel_61, BorderLayout.CENTER);
		panel_61.setLayout(new GridLayout(2, 0, 0, 10));

		JPanel panel_68 = new JPanel();
		panel_61.add(panel_68);
		panel_68.setLayout(new BorderLayout(0, 0));

		JPanel panel_70 = new JPanel();
		panel_70.setPreferredSize(new Dimension(10, 30));
		panel_68.add(panel_70, BorderLayout.NORTH);
		panel_70.setLayout(new BorderLayout(0, 0));

		JPanel panel_73 = new JPanel();
		FlowLayout flowLayout_10 = (FlowLayout) panel_73.getLayout();
		flowLayout_10.setVgap(0);
		panel_73.setPreferredSize(new Dimension(30, 10));
		panel_70.add(panel_73, BorderLayout.WEST);

		JButton button_10 = new JButton("");
		button_10.setBorder(null);
		button_10.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/sun.png")));
		panel_73.add(button_10);

		JPanel panel_74 = new JPanel();
		panel_70.add(panel_74, BorderLayout.CENTER);
		panel_74.setLayout(new GridLayout(0, 7, 0, 0));

		JLabel label_18 = new JLabel("7/26");
		label_18.setForeground(Color.DARK_GRAY);
		label_18.setHorizontalTextPosition(SwingConstants.CENTER);
		label_18.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_18);

		JLabel label_19 = new JLabel("7/27");
		label_19.setForeground(Color.DARK_GRAY);
		label_19.setHorizontalTextPosition(SwingConstants.CENTER);
		label_19.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_19);

		JLabel label_20 = new JLabel("7/28");
		label_20.setForeground(Color.DARK_GRAY);
		label_20.setHorizontalTextPosition(SwingConstants.CENTER);
		label_20.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_20);

		JLabel label_21 = new JLabel("7/29");
		label_21.setForeground(Color.DARK_GRAY);
		label_21.setHorizontalTextPosition(SwingConstants.CENTER);
		label_21.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_21);

		JLabel label_22 = new JLabel("7/30");
		label_22.setForeground(Color.RED);
		label_22.setHorizontalTextPosition(SwingConstants.CENTER);
		label_22.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_22);

		JLabel label_23 = new JLabel("7/31");
		label_23.setForeground(Color.DARK_GRAY);
		label_23.setHorizontalTextPosition(SwingConstants.CENTER);
		label_23.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_23);

		JLabel label_24 = new JLabel("8/1");
		label_24.setForeground(Color.RED);
		label_24.setHorizontalTextPosition(SwingConstants.CENTER);
		label_24.setHorizontalAlignment(SwingConstants.CENTER);
		panel_74.add(label_24);

		JPanel panel_71 = new JPanel();
		panel_71.setBorder(null);
		panel_68.add(panel_71, BorderLayout.CENTER);
		panel_71.setLayout(new GridLayout(2, 7, 0, 0));

		JLabel lblNewLabel_1 = new JLabel("128");
		lblNewLabel_1.setForeground(Color.DARK_GRAY);
		lblNewLabel_1.setFont(new Font("幼圆", Font.BOLD, 13));
		lblNewLabel_1.setBorder(new LineBorder(new Color(128, 128, 128)));
		lblNewLabel_1.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(lblNewLabel_1);

		JLabel label_26 = new JLabel("128");
		label_26.setFont(new Font("幼圆", Font.BOLD, 13));
		label_26.setForeground(Color.DARK_GRAY);
		label_26.setBorder(new LineBorder(new Color(128, 128, 128)));
		label_26.setHorizontalTextPosition(SwingConstants.CENTER);
		label_26.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_26);

		JLabel label_27 = new JLabel("133");
		label_27.setForeground(Color.DARK_GRAY);
		label_27.setFont(new Font("幼圆", Font.BOLD, 13));
		label_27.setBorder(new LineBorder(Color.GRAY));
		label_27.setHorizontalTextPosition(SwingConstants.CENTER);
		label_27.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_27);

		JLabel label_28 = new JLabel("130");
		label_28.setForeground(Color.DARK_GRAY);
		label_28.setFont(new Font("幼圆", Font.BOLD, 13));
		label_28.setBorder(new LineBorder(Color.GRAY));
		label_28.setHorizontalTextPosition(SwingConstants.CENTER);
		label_28.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_28);

		JLabel label_37 = new JLabel("138");
		label_37.setForeground(Color.RED);
		label_37.setFont(new Font("幼圆", Font.BOLD, 13));
		label_37.setBorder(new LineBorder(Color.GRAY));
		label_37.setHorizontalTextPosition(SwingConstants.CENTER);
		label_37.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_37);

		JLabel label_29 = new JLabel("115");
		label_29.setForeground(Color.DARK_GRAY);
		label_29.setFont(new Font("幼圆", Font.BOLD, 13));
		label_29.setBorder(new LineBorder(Color.GRAY));
		label_29.setHorizontalTextPosition(SwingConstants.CENTER);
		label_29.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_29);

		JLabel label_30 = new JLabel("137");
		label_30.setForeground(Color.RED);
		label_30.setFont(new Font("幼圆", Font.BOLD, 13));
		label_30.setBorder(new LineBorder(Color.GRAY));
		label_30.setHorizontalTextPosition(SwingConstants.CENTER);
		label_30.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_30);

		JLabel label_31 = new JLabel("70");
		label_31.setForeground(Color.DARK_GRAY);
		label_31.setFont(new Font("幼圆", Font.BOLD, 13));
		label_31.setBorder(new LineBorder(Color.GRAY));
		label_31.setHorizontalTextPosition(SwingConstants.CENTER);
		label_31.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_31);

		JLabel label_32 = new JLabel("78");
		label_32.setForeground(Color.DARK_GRAY);
		label_32.setFont(new Font("幼圆", Font.BOLD, 13));
		label_32.setBorder(new LineBorder(Color.GRAY));
		label_32.setHorizontalTextPosition(SwingConstants.CENTER);
		label_32.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_32);

		JLabel label_35 = new JLabel("84");
		label_35.setForeground(Color.DARK_GRAY);
		label_35.setFont(new Font("幼圆", Font.BOLD, 13));
		label_35.setBorder(new LineBorder(Color.GRAY));
		label_35.setHorizontalTextPosition(SwingConstants.CENTER);
		label_35.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_35);

		JLabel label_33 = new JLabel("74");
		label_33.setForeground(Color.DARK_GRAY);
		label_33.setFont(new Font("幼圆", Font.BOLD, 13));
		label_33.setBorder(new LineBorder(Color.GRAY));
		label_33.setHorizontalTextPosition(SwingConstants.CENTER);
		label_33.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_33);

		JLabel label_34 = new JLabel("72");
		label_34.setForeground(Color.DARK_GRAY);
		label_34.setFont(new Font("幼圆", Font.BOLD, 13));
		label_34.setBorder(new LineBorder(Color.GRAY));
		label_34.setHorizontalTextPosition(SwingConstants.CENTER);
		label_34.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_34);

		JLabel label_36 = new JLabel("76");
		label_36.setForeground(Color.DARK_GRAY);
		label_36.setFont(new Font("幼圆", Font.BOLD, 13));
		label_36.setBorder(new LineBorder(Color.GRAY));
		label_36.setHorizontalTextPosition(SwingConstants.CENTER);
		label_36.setHorizontalAlignment(SwingConstants.CENTER);
		panel_71.add(label_36);

		JLabel label_38 = new JLabel("90");
		label_38.setForeground(Color.RED);
		label_38.setFont(new Font("幼圆", Font.BOLD, 13));
		label_38.setBorder(new LineBorder(Color.GRAY));
		label_38.setHorizontalAlignment(SwingConstants.CENTER);
		label_38.setHorizontalTextPosition(SwingConstants.CENTER);
		panel_71.add(label_38);

		JPanel panel_72 = new JPanel();
		panel_72.setPreferredSize(new Dimension(30, 10));
		panel_68.add(panel_72, BorderLayout.WEST);
		panel_72.setLayout(new GridLayout(2, 0, 0, 0));

		JLabel lblSbp = new JLabel("SBP");
		lblSbp.setFont(new Font("幼圆", Font.BOLD, 12));
		lblSbp.setForeground(Color.DARK_GRAY);
		lblSbp.setHorizontalTextPosition(SwingConstants.CENTER);
		lblSbp.setHorizontalAlignment(SwingConstants.CENTER);
		panel_72.add(lblSbp);

		JLabel lblDbp = new JLabel("DBP");
		lblDbp.setFont(new Font("幼圆", Font.BOLD, 12));
		lblDbp.setForeground(Color.DARK_GRAY);
		lblDbp.setHorizontalTextPosition(SwingConstants.CENTER);
		lblDbp.setHorizontalAlignment(SwingConstants.CENTER);
		panel_72.add(lblDbp);

		JPanel panel_75 = new JPanel();
		FlowLayout flowLayout_11 = (FlowLayout) panel_75.getLayout();
		flowLayout_11.setVgap(2);
		flowLayout_11.setAlignment(FlowLayout.LEFT);
		panel_75.setPreferredSize(new Dimension(10, 20));
		panel_68.add(panel_75, BorderLayout.SOUTH);

		JLabel label_25 = new JLabel("最高一次测量值 为138/72（7/30）");
		label_25.setForeground(Color.GRAY);
		label_25.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_75.add(label_25);

		JPanel panel_69 = new JPanel();
		panel_61.add(panel_69);
		panel_69.setLayout(new BorderLayout(0, 0));

		JPanel panel_76 = new JPanel();
		panel_76.setPreferredSize(new Dimension(10, 30));
		panel_69.add(panel_76, BorderLayout.NORTH);
		panel_76.setLayout(new BorderLayout(0, 0));

		JPanel panel_79 = new JPanel();
		FlowLayout flowLayout_13 = (FlowLayout) panel_79.getLayout();
		flowLayout_13.setVgap(0);
		panel_79.setPreferredSize(new Dimension(30, 10));
		panel_76.add(panel_79, BorderLayout.WEST);

		JButton button_11 = new JButton("");
		button_11.setBorder(null);
		button_11.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/moon.png")));
		panel_79.add(button_11);

		JPanel panel_80 = new JPanel();
		panel_76.add(panel_80, BorderLayout.CENTER);
		panel_80.setLayout(new GridLayout(1, 0, 0, 0));

		JLabel label_40 = new JLabel("7/26");
		label_40.setHorizontalTextPosition(SwingConstants.CENTER);
		label_40.setHorizontalAlignment(SwingConstants.CENTER);
		label_40.setForeground(Color.DARK_GRAY);
		panel_80.add(label_40);

		JLabel label_41 = new JLabel("7/27");
		label_41.setHorizontalTextPosition(SwingConstants.CENTER);
		label_41.setHorizontalAlignment(SwingConstants.CENTER);
		label_41.setForeground(Color.RED);
		panel_80.add(label_41);

		JLabel label_42 = new JLabel("7/28");
		label_42.setHorizontalTextPosition(SwingConstants.CENTER);
		label_42.setHorizontalAlignment(SwingConstants.CENTER);
		label_42.setForeground(Color.DARK_GRAY);
		panel_80.add(label_42);

		JLabel label_43 = new JLabel("7/29");
		label_43.setHorizontalTextPosition(SwingConstants.CENTER);
		label_43.setHorizontalAlignment(SwingConstants.CENTER);
		label_43.setForeground(Color.DARK_GRAY);
		panel_80.add(label_43);

		JLabel label_44 = new JLabel("7/30");
		label_44.setHorizontalTextPosition(SwingConstants.CENTER);
		label_44.setHorizontalAlignment(SwingConstants.CENTER);
		label_44.setForeground(Color.DARK_GRAY);
		panel_80.add(label_44);

		JLabel label_45 = new JLabel("7/31");
		label_45.setHorizontalTextPosition(SwingConstants.CENTER);
		label_45.setHorizontalAlignment(SwingConstants.CENTER);
		label_45.setForeground(Color.DARK_GRAY);
		panel_80.add(label_45);

		JLabel label_46 = new JLabel("8/1");
		label_46.setHorizontalTextPosition(SwingConstants.CENTER);
		label_46.setHorizontalAlignment(SwingConstants.CENTER);
		label_46.setForeground(Color.DARK_GRAY);
		panel_80.add(label_46);

		JPanel panel_77 = new JPanel();
		FlowLayout flowLayout_12 = (FlowLayout) panel_77.getLayout();
		flowLayout_12.setAlignment(FlowLayout.LEFT);
		flowLayout_12.setVgap(2);
		panel_77.setPreferredSize(new Dimension(10, 20));
		panel_69.add(panel_77, BorderLayout.SOUTH);

		JLabel label_39 = new JLabel("最高一次测量值为132/68（7/26）");
		label_39.setForeground(Color.GRAY);
		label_39.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_77.add(label_39);

		JPanel panel_78 = new JPanel();
		panel_69.add(panel_78, BorderLayout.CENTER);
		panel_78.setLayout(new BorderLayout(0, 0));

		JPanel panel_81 = new JPanel();
		panel_81.setPreferredSize(new Dimension(30, 10));
		panel_78.add(panel_81, BorderLayout.WEST);
		panel_81.setLayout(new GridLayout(2, 0, 0, 0));

		JLabel label_47 = new JLabel("SBP");
		label_47.setHorizontalTextPosition(SwingConstants.CENTER);
		label_47.setHorizontalAlignment(SwingConstants.CENTER);
		label_47.setForeground(Color.DARK_GRAY);
		label_47.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_81.add(label_47);

		JLabel label_48 = new JLabel("DBP");
		label_48.setHorizontalTextPosition(SwingConstants.CENTER);
		label_48.setHorizontalAlignment(SwingConstants.CENTER);
		label_48.setForeground(Color.DARK_GRAY);
		label_48.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_81.add(label_48);

		JPanel panel_82 = new JPanel();
		panel_78.add(panel_82, BorderLayout.CENTER);
		panel_82.setLayout(new GridLayout(2, 7, 0, 0));

		JLabel label_49 = new JLabel("132");
		label_49.setHorizontalTextPosition(SwingConstants.CENTER);
		label_49.setHorizontalAlignment(SwingConstants.CENTER);
		label_49.setForeground(Color.DARK_GRAY);
		label_49.setFont(new Font("幼圆", Font.BOLD, 13));
		label_49.setBorder(new LineBorder(new Color(128, 128, 128)));
		panel_82.add(label_49);

		JLabel label_50 = new JLabel("130");
		label_50.setHorizontalTextPosition(SwingConstants.CENTER);
		label_50.setHorizontalAlignment(SwingConstants.CENTER);
		label_50.setForeground(Color.DARK_GRAY);
		label_50.setFont(new Font("幼圆", Font.BOLD, 13));
		label_50.setBorder(new LineBorder(new Color(128, 128, 128)));
		panel_82.add(label_50);

		JLabel label_51 = new JLabel("128");
		label_51.setHorizontalTextPosition(SwingConstants.CENTER);
		label_51.setHorizontalAlignment(SwingConstants.CENTER);
		label_51.setForeground(Color.DARK_GRAY);
		label_51.setFont(new Font("幼圆", Font.BOLD, 13));
		label_51.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_51);

		JLabel label_52 = new JLabel("126");
		label_52.setHorizontalTextPosition(SwingConstants.CENTER);
		label_52.setHorizontalAlignment(SwingConstants.CENTER);
		label_52.setForeground(Color.DARK_GRAY);
		label_52.setFont(new Font("幼圆", Font.BOLD, 13));
		label_52.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_52);

		JLabel label_53 = new JLabel("131");
		label_53.setHorizontalTextPosition(SwingConstants.CENTER);
		label_53.setHorizontalAlignment(SwingConstants.CENTER);
		label_53.setForeground(Color.DARK_GRAY);
		label_53.setFont(new Font("幼圆", Font.BOLD, 13));
		label_53.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_53);

		JLabel label_54 = new JLabel("123");
		label_54.setHorizontalTextPosition(SwingConstants.CENTER);
		label_54.setHorizontalAlignment(SwingConstants.CENTER);
		label_54.setForeground(Color.DARK_GRAY);
		label_54.setFont(new Font("幼圆", Font.BOLD, 13));
		label_54.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_54);

		JLabel label_55 = new JLabel("130");
		label_55.setHorizontalTextPosition(SwingConstants.CENTER);
		label_55.setHorizontalAlignment(SwingConstants.CENTER);
		label_55.setForeground(Color.DARK_GRAY);
		label_55.setFont(new Font("幼圆", Font.BOLD, 13));
		label_55.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_55);

		JLabel label_56 = new JLabel("68");
		label_56.setHorizontalTextPosition(SwingConstants.CENTER);
		label_56.setHorizontalAlignment(SwingConstants.CENTER);
		label_56.setForeground(Color.DARK_GRAY);
		label_56.setFont(new Font("幼圆", Font.BOLD, 13));
		label_56.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_56);

		JLabel label_57 = new JLabel("90");
		label_57.setHorizontalTextPosition(SwingConstants.CENTER);
		label_57.setHorizontalAlignment(SwingConstants.CENTER);
		label_57.setForeground(Color.RED);
		label_57.setFont(new Font("幼圆", Font.BOLD, 13));
		label_57.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_57);

		JLabel label_58 = new JLabel("84");
		label_58.setHorizontalTextPosition(SwingConstants.CENTER);
		label_58.setHorizontalAlignment(SwingConstants.CENTER);
		label_58.setForeground(Color.DARK_GRAY);
		label_58.setFont(new Font("幼圆", Font.BOLD, 13));
		label_58.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_58);

		JLabel label_59 = new JLabel("65");
		label_59.setHorizontalTextPosition(SwingConstants.CENTER);
		label_59.setHorizontalAlignment(SwingConstants.CENTER);
		label_59.setForeground(Color.DARK_GRAY);
		label_59.setFont(new Font("幼圆", Font.BOLD, 13));
		label_59.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_59);

		JLabel label_60 = new JLabel("67");
		label_60.setHorizontalTextPosition(SwingConstants.CENTER);
		label_60.setHorizontalAlignment(SwingConstants.CENTER);
		label_60.setForeground(Color.DARK_GRAY);
		label_60.setFont(new Font("幼圆", Font.BOLD, 13));
		label_60.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_60);

		JLabel label_61 = new JLabel("85");
		label_61.setHorizontalTextPosition(SwingConstants.CENTER);
		label_61.setHorizontalAlignment(SwingConstants.CENTER);
		label_61.setForeground(Color.DARK_GRAY);
		label_61.setFont(new Font("幼圆", Font.BOLD, 13));
		label_61.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_61);

		JLabel label_62 = new JLabel("71");
		label_62.setHorizontalTextPosition(SwingConstants.CENTER);
		label_62.setHorizontalAlignment(SwingConstants.CENTER);
		label_62.setForeground(Color.DARK_GRAY);
		label_62.setFont(new Font("幼圆", Font.BOLD, 13));
		label_62.setBorder(new LineBorder(Color.GRAY));
		panel_82.add(label_62);

		JPanel panel_10 = new JPanel();
		panel_10.setBorder(new LineBorder(new Color(238, 232, 170), 4));
		panel_2.add(panel_10);
		panel_10.setLayout(new BorderLayout(0, 0));

		JPanel panel_21 = new JPanel();
		panel_21.setBackground(new Color(238, 232, 170));
		panel_21.setPreferredSize(new Dimension(10, 30));
		panel_10.add(panel_21, BorderLayout.NORTH);
		panel_21.setLayout(new BorderLayout(0, 0));

		JPanel panel_24 = new JPanel();
		panel_24.setPreferredSize(new Dimension(30, 10));
		panel_21.add(panel_24, BorderLayout.WEST);
		panel_24.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_3 = new JButton("");
		button_3.setBackground(new Color(238, 232, 170));
		button_3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_3.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/search.png")));
		panel_24.add(button_3);

		JPanel panel_23 = new JPanel();
		panel_23.setBackground(new Color(238, 232, 170));
		panel_21.add(panel_23, BorderLayout.CENTER);

		JLabel label_9 = new JLabel("血压平均值（一周内）");
		label_9.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_23.add(label_9);

		JPanel panel_25 = new JPanel();
		panel_25.setPreferredSize(new Dimension(30, 10));
		panel_21.add(panel_25, BorderLayout.EAST);
		panel_25.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_4 = new JButton("");
		button_4.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/help.png")));
		button_4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_4.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_4.setBackground(new Color(238, 232, 170));
		panel_25.add(button_4);

		JPanel panel_22 = new JPanel();
		panel_10.add(panel_22, BorderLayout.CENTER);
		panel_22.setLayout(new BorderLayout(0, 0));

		JPanel panel_83 = new JPanel();
		panel_83.setPreferredSize(new Dimension(10, 20));
		panel_22.add(panel_83, BorderLayout.NORTH);
		panel_83.setLayout(new BorderLayout(0, 0));

		JPanel panel_88 = new JPanel();
		panel_88.setPreferredSize(new Dimension(40, 10));
		panel_83.add(panel_88, BorderLayout.EAST);

		JLabel label_64 = new JLabel("[mmHg]");
		label_64.setForeground(Color.DARK_GRAY);
		label_64.setFont(new Font("幼圆", Font.BOLD, 11));
		panel_88.add(label_64);

		JPanel panel_84 = new JPanel();
		panel_84.setLayout(new GridLayout(0, 1, 0, 0));
		DrawingRight drawingRight = new DrawingRight();
		panel_84.add(drawingRight);
		panel_22.add(panel_84, BorderLayout.CENTER);

		JPanel panel_85 = new JPanel();
		panel_85.setPreferredSize(new Dimension(10, 40));
		panel_10.add(panel_85, BorderLayout.SOUTH);
		panel_85.setLayout(new BorderLayout(0, 0));

		JPanel panel_86 = new JPanel();
		FlowLayout flowLayout_14 = (FlowLayout) panel_86.getLayout();
		flowLayout_14.setVgap(2);
		panel_86.setPreferredSize(new Dimension(10, 16));
		panel_85.add(panel_86, BorderLayout.NORTH);

		JLabel label_63 = new JLabel("○ 白色空心为测量天数未满5天");
		label_63.setForeground(Color.DARK_GRAY);
		label_63.setFont(new Font("幼圆", Font.BOLD, 11));
		panel_86.add(label_63);

		JPanel panel_87 = new JPanel();
		FlowLayout flowLayout_17 = (FlowLayout) panel_87.getLayout();
		flowLayout_17.setAlignment(FlowLayout.LEFT);
		panel_85.add(panel_87, BorderLayout.CENTER);

		JLabel lblsbp = new JLabel("每天达成早晚的SBP目标。");
		lblsbp.setFont(new Font("幼圆", Font.BOLD, 12));
		panel_87.add(lblsbp);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.2);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(panel_2);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(238, 232, 170), 4));
		panel_1.setLayout(new BorderLayout(0, 0));
		splitPane.setRightComponent(panel_1);

		JPanel panel_26 = new JPanel();
		panel_26.setBackground(new Color(238, 232, 170));
		panel_26.setPreferredSize(new Dimension(10, 30));
		panel_1.add(panel_26, BorderLayout.NORTH);
		panel_26.setLayout(new BorderLayout(0, 0));

		JPanel panel_28 = new JPanel();
		panel_28.setPreferredSize(new Dimension(30, 10));
		panel_26.add(panel_28, BorderLayout.WEST);
		panel_28.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_6 = new JButton("");
		button_6.setBackground(new Color(238, 232, 170));
		button_6.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_6.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/search.png")));
		panel_28.add(button_6);

		JPanel panel_29 = new JPanel();
		panel_29.setBackground(new Color(238, 232, 170));
		panel_26.add(panel_29, BorderLayout.CENTER);

		JLabel label_10 = new JLabel("血压变化（显示60天数据）");
		label_10.setFont(new Font("幼圆", Font.BOLD, 14));
		panel_29.add(label_10);

		JPanel panel_30 = new JPanel();
		panel_30.setPreferredSize(new Dimension(30, 10));
		panel_26.add(panel_30, BorderLayout.EAST);
		panel_30.setLayout(new GridLayout(1, 0, 0, 0));

		JButton button_5 = new JButton("");
		button_5.setIcon(new ImageIcon(BPHistory.class
				.getResource("/ui_icons/help.png")));
		button_5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_5.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		button_5.setBackground(new Color(238, 232, 170));
		panel_30.add(button_5);

		JPanel panel_27 = new JPanel();
		panel_27.setLayout(new GridLayout(1, 1, 0, 0));
		DrawingBottom drawingBottom = new DrawingBottom();
		panel_27.add(drawingBottom);
		drawingBottom.setLayout(new GridLayout(0, 1, 0, 0));
		panel_1.add(panel_27, BorderLayout.CENTER);
	}

}
