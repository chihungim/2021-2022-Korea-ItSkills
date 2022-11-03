package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Pattern extends JDialog {
	ArrayList<Point> points = new ArrayList();
	ArrayList<String> pw = new ArrayList<String>();
	boolean flag, chance = true;
	JLabel dots[] = new JLabel[9];

	public Pattern(JTextField txt) {
		super(BasePage.mf, true);
		setTitle("패턴");
		setSize(400, 400);
		setLocationRelativeTo(null);
		var c = new JPanel(new GridLayout(0, 3)) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(7));
				g2.setColor(Color.BLUE.darker());
				int xx[] = new int[points.size()], yy[] = new int[xx.length];
				for (int i = 0; i < xx.length; i++) {
					xx[i] = points.get(i).x + 8;
					yy[i] = points.get(i).y + 8;
				}
				g2.drawPolyline(xx, yy, xx.length);
			}
		};
		var s = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(c);
		add(s, "South");

		for (int i = 0; i < dots.length; i++) {
			var tmp = new JPanel(new GridBagLayout());
			tmp.add(dots[i] = BasePage.lbl("●", JLabel.CENTER, 15));
			dots[i].setName(i + 1 + "");
			tmp.setOpaque(false);
			tmp.setBorder(new LineBorder(Color.LIGHT_GRAY));
			dots[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					flag = true;
					mouseEntered(e);
					super.mousePressed(e);
				}

				@Override

				public void mouseReleased(MouseEvent e) {
					flag = false;
					chance = false;
					super.mouseReleased(e);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (flag && chance) {
						var me = (JLabel) e.getSource();
						var rpos = me.getRootPane().getLocationOnScreen();
						var mpos = me.getLocationOnScreen();
						var dif = new Point(mpos.x - rpos.x, mpos.y - rpos.y);
						if (!points.contains(dif)) {
							points.add(dif);
							pw.add(me.getName());
						}
						repaint();
					}
					super.mouseEntered(e);
				}
			});
			c.add(tmp);
		}
		s.add(BasePage.btn("확인", a -> {
			txt.setText(String.join("", pw));
			setVisible(false);
		}));
	}
}
