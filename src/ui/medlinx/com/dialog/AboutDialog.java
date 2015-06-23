package ui.medlinx.com.dialog;
import java.awt.Color;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import ui.medlinx.com.debug.DebugTool;

/**
 * The about information dialog, to show Logo, Copyright, etc.
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		ImageIcon imageIcon = new ImageIcon("res/icon.png");

		int loadingDone = MediaTracker.ABORTED | MediaTracker.ERRORED | MediaTracker.COMPLETE;

		while((imageIcon.getImageLoadStatus() & loadingDone) == 0){
		   //just wait a bit...
		}
		if(imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE)
		    setIconImage(imageIcon.getImage());
		else {
		    //something went wrong loading the image...
			DebugTool.printLogDebug("Error when load icon image.");
		} 
		getContentPane().setBackground(Color.WHITE);
		setTitle("About");
		getContentPane().setLayout(null);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBackground(Color.WHITE);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutDialog.this.dispose();
			}
		});
		btnOk.setBounds(439, 104, 89, 23);
		getContentPane().add(btnOk);
		
		JTextArea txtrcCopyrightMlnx = new JTextArea();
		txtrcCopyrightMlnx.setText("(C) Copyright MLnx Corp\r\nNingbo, Zhejiang\r\nChina\r\n2011-2013");
		txtrcCopyrightMlnx.setBackground(Color.WHITE);
		txtrcCopyrightMlnx.setBounds(335, 13, 193, 76);
		getContentPane().add(txtrcCopyrightMlnx);
		
		//need to get a logo of the company
		ImageIcon image = new ImageIcon("\\res\\image\\logo.png");
		JLabel lblNewLabel = new JLabel("", image, JLabel.CENTER);
		lblNewLabel.setBounds(27, 18, 298, 83);
		getContentPane().add(lblNewLabel);
		
		setBounds(300, 300, 554, 177);
	}
}
