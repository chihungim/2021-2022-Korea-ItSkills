package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MovieInformation extends BaseDialog {

	JPanel m;

	public MovieInformation(ArrayList<Object> movie) {
		super(movie.get(1) + "", 600, 600);
		add(scrollPane(c = new JPanel(new BorderLayout(5, 5))));
		c.add(cn = new JPanel(new BorderLayout(5, 5)), "North");
		c.add(cc = new JPanel(new BorderLayout(5, 5)));
		c.add(cs = new JPanel(new BorderLayout(5, 5)), "South");
		var poster = new JLabel(getIcon("./지급자료/image/movie/" + movie.get(0) + ".jpg", 200, 250));
		poster.setLayout(new FlowLayout(FlowLayout.LEFT));
		poster.add(ageLimit(movie.get(5) + ""));

		var cnc = new JPanel(new BorderLayout());
		cn.add(poster, "West");
		cn.add(cnc);
		cnc.add(border(lbl(movie.get(1) + "", JLabel.LEFT, 20), new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)),
				"North");
		var avg = cdbl(getrows("select ifnull(round(avg(c_rate),1),0) from comment where m_no = ?", movie.get(0)).get(0)
				.get(0));

		String star = "<html><font color = 'yellow'>";
		for (int i = 0; i < 5; i++) {
			if (i > avg - 1)
				star += "<font color = 'rgb(200,200,200)'>";
			star += "★";
		}
		String descrip = "<html>감독:" + movie.get(6) + "<br>장르:"
				+ Arrays.stream(movie.get(3).toString().split("\\.")).map(a -> genre[cint(a)])
						.collect(Collectors.joining(","))
				+ "<br>평점:" + star + "<font color = 'black'>(" + String.format("%.1f", avg) + "점)";
		var d = lbl(descrip, JLabel.LEFT);
		cnc.add(d);
		d.setVerticalAlignment(JLabel.TOP);
		var p1 = new JPanel(new BorderLayout());
		var p2 = new JPanel(new BorderLayout());
		cc.add(p1, "North");
		cc.add(p2);
		var h1 = lbl("시놉시스", JLabel.LEFT);
		h1.setBorder(new MatteBorder(0, 1, 1, 0, Color.BLACK));
		p1.add(h1, "North");
		var area = new JTextArea(movie.get(2) + "");
		area.setLineWrap(true);
		p1.add(sz(scrollPane(area), 500, 150));
		area.setBorder(null);
		area.setOpaque(false);
		area.setLineWrap(true);
		var h2 = lbl("주시청층", JLabel.LEFT);
		h2.setBorder(new MatteBorder(0, 1, 1, 0, Color.BLACK));
		area.setEditable(false);
		p2.add(h2, "North");

		p2.add(sz(new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				var g2 = tog2d(g);
				// 원형
				// 시간이 남으면 한다.
			};
		}, 500, 200));

		var h3 = lbl("리뷰", JLabel.LEFT);
		h3.setBorder(new MatteBorder(0, 1, 1, 0, Color.BLACK));

		cs.add(h3, "North");
		var temp = new JPanel(new GridLayout(0, 1));
		var page = new CardLayout();

		var csc = new JPanel(page);
		var css = new JPanel(new FlowLayout(FlowLayout.CENTER));
		cs.add(csc);
		cs.add(css, "South");
		int idx = 0;
		for (var r : getrows(
				"select u.u_no, u_id, c_rate, c_text from comment c ,user u where u.u_no = c.u_no and c.m_no = ?",
				movie.get(0))) {
			if (temp.getComponentCount() == 5) {
				idx++;
				final String cur = idx + "";
				csc.add(temp, idx + "");
				css.add(evt(lbl(idx + "", JLabel.CENTER), a -> {
					for (var i : css.getComponents())
						i.setForeground(Color.BLACK);
					page.show(csc, cur);
					((JLabel) a.getSource()).setForeground(Color.RED);
				}));
				temp = new JPanel(new GridLayout(0, 1));
			}
			String stars = "<html><font color = 'yellow'>";
			for (int i = 0; i < 5; i++) {
				if (i > cint(r.get(2)) - 1)
					stars += "<font color = 'rgb(200,200,200)'>";
				stars += "★";
			}
			var item = new JPanel(new BorderLayout());
			var itemn = new JPanel(new FlowLayout(FlowLayout.LEFT));
			item.add(itemn, "North");
			itemn.add(new JLabel(rndImg("./지급자료/image/user/" + r.get(0) + ".jpg", 30, 30)));
			itemn.add(lbl(r.get(1) + "", 0));
			itemn.add(lbl(stars, 0));
			item.add(sz(lbl(r.get(3) + "", JLabel.LEFT), 500, 30));
			temp.add(item);
		}

		if (temp.getComponentCount() > 0) {
			while (temp.getComponentCount() < 5)
				temp.add(new JLabel());
			idx++;
			csc.add(temp, idx + "");
			temp = new JPanel(new GridLayout(0, 1));
			final String cur = idx + "";
			css.add(evt(lbl(idx + "", JLabel.CENTER), a -> {
				for (var i : css.getComponents())
					i.setForeground(Color.BLACK);
				page.show(csc, cur);
				((JLabel) a.getSource()).setForeground(red);
			}));
		}

		page.show(csc, 1 + "");
		css.getComponent(0).setForeground(red);

		c.setBorder(new EmptyBorder(0, 20, 5, 20));
	}

	/*
	 * var temp = new JPanel(new BorderLayout(5, 5)); var header = lbl("No." +
	 * (rs.indexOf(r) + 1), JLabel.CENTER, 15); var temps = new JPanel(new
	 * GridLayout(0, 1));
	 * 
	 * f(header, Color.WHITE); b(header, rs.indexOf(r) + 1 <= 5 ? red :
	 * Color.DARK_GRAY); temp.add(header, "North"); var poster = new
	 * JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 200, 300));
	 * temp.add(poster); temp.add(temps, "South");
	 * 
	 * temps.add(lbl(r.get(1) + "", JLabel.LEFT, 15));
	 * temps.add(lbl(Arrays.stream(r.get(3).toString().split("\\.")).map(a ->
	 * genre[cint(a)]) .collect(Collectors.joining(",")), JLabel.LEFT));
	 * 
	 * String star = "<html><font color = 'yellow'>";
	 * 
	 * for (int i = 0; i < 5; i++) { if (i > cdbl(r.get(7)) - 1) star +=
	 * "<font color = 'rgb(200,200,200)'>"; star += "★"; }
	 * 
	 * evt(poster, a -> new MovieInformation(r).setVisible(true));
	 * 
	 * star += "<font color = 'black'>" + r.get(7); temps.add(lbl(star,
	 * JLabel.LEFT));
	 * 
	 * poster.setLayout(new FlowLayout(FlowLayout.LEFT));
	 * poster.add(ageLimit(r.get(5) + "")); cc.add(sz(temp, 200, 350));
	 */

}
