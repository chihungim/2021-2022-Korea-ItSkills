package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Map extends BaseFrame {

	JLabel mcity;
	JLabel line;
	int fx, fy, tx, ty;
	ArrayList<JLabel> markers;

	public Map() {
		super("지도", 700, 800);
		add(lbl("지역별 농산물관리현황", JLabel.CENTER, 20), "North");
		add(c = new JPanel(null));
		c.add(line = new JLabel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = tog2d(g);
				System.out.println("Called");
				if (fx == tx && fy == ty)
					return;
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(3f));
				g2.drawLine(fx, fy, tx, ty);
			}
		}).setBounds(0, 0, 1000, 1000);
		markers = new ArrayList<>();
		for (var c : getrows("Select * from city")) {
//			var marker = new JLabel("<html><center>&#128204<br>" + c.get(3) + "");
			var icon = new ImageIcon("./datafiles/지역/" + c.get(0) + ".png");
			var city = new JLabel(icon);
			int cx = cint(c.get(1)), cy = cint(c.get(2));
			this.c.add(city).setBounds(cx, cy, icon.getIconWidth(), icon.getIconHeight());
//			marker.setFont(new Font("", Font.PLAIN, 12));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		디버그("1");
		new Map();
	}
}
