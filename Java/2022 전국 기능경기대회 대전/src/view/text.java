package view;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class text {
	public static void main(String[] args) {
		var f = new JFrame();
		f.setSize(300, 300);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		var l = new JLabel("<html>&#128204");
		f.add(l);
		l.setFont(new Font("", Font.PLAIN,12));
		f.setVisible(true);
	}
}
