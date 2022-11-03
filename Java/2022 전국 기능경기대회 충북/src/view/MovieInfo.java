package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MovieInfo extends BaseFrame {

	public MovieInfo(ArrayList<Object> movie) {
		super("영화상세정보", 600, 300);
		setLayout(new BorderLayout(5, 5));
		add(new JLabel(getIcon("./datafile/영화/" + movie.get(5) + ".jpg", 200, 250)), "West");
		add(c = newJPanel(new BorderLayout(5, 5)));
		c.add(lbl(movie.get(5) + "", JLabel.LEFT, 20), "North");
		c.add(cc = newJPanel(new BorderLayout()));
		var l = lbl("<html>장르 <font color = 'gray'>" + getrows("select g_name from genre where g_no= ?", movie.get(1))
				+ "</font><br>평점 <font color = 'gray'>" + movie.get(4) + "", JLabel.LEFT);

		cc.add(l, "West");
		cc.add(rbtn("예매하기", a -> {
			new Reservation().addWindowListener(new before(this));
		}), "East");

		sz(cc, 0, 30);

		JTextArea area = new JTextArea(movie.get(6) + "");
		area.setLineWrap(true);
		area.setFont(lbl("", 0, 13).getFont());
		c.add(area, "South");
		sz(area, 0, 170);

		area.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "설명", 0, 0, lbl("", 0, 20).getFont()));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

}
