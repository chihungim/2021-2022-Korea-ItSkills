package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

public class Chart extends BaseFrame {

	JPanel map, chart;
	String sel = "";
	Color col[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, new Color(0, 200, 255), Color.blue, Color.PINK,
			Color.MAGENTA, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.WHITE };

	public Chart() {
		super("차트", 1200, 800);
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(1, 0)));

		n.add(lbl("지역별 예약 현황", 0, 25));
		n.add(lbl("C H A R T", 0, 15), "South");

		c.add(map = new JPanel(null));
		c.add(chart = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (!sel.isEmpty()) {
					var rs = getValues(
							"select left(c_name, instr(c_name,' ')-1), count(r.r_no) cnt from cafe c , reservation r where c.c_no = r.c_no and c.a_no = ? group by left(c_name, instr(c_name,' ')-1) order by cnt desc limit 0,13",
							sel);
					if (!rs.isEmpty()) {
						int sum = rs.stream().mapToInt(a -> toInt(a.get(1))).sum();
						int sarc = 90;
						for (int i = 0; i < rs.size(); i++) {
							var r = rs.get(i);
							var pr = Math.round((double) toInt(r.get(1)) / (double) sum * 360 * -1);
							g2.setColor(col[i]);
							g2.fillArc(50, 200, 300, 300, sarc, (int) pr);
							g2.fillRect(400, 200 + (i * 30), 20, 20);
							g2.setColor(Color.BLACK);
							g2.drawString(r.get(0) + ":" + r.get(1) + "개", 430, 215 + (i * 30));
							sarc += pr;
						}
					} else {
						emsg("예약현황이 없습니다.");
						for (var m : map.getComponents())
							m.setEnabled(false);
						revalidate();
						sel = "";
						super.paint(g);
					}
				}
			}
		});

		for (var rs : getValues(
				"select a_name, a.a_no, m_x, m_y, p_x, p_y from area a, ping p, map m where a.a_no = p.a_no and m.a_no = a.a_no")) {
			var icon = getIcon("./datafiles/지도/" + rs.get(0) + ".png");

			var loc = new JLabel(icon);
			var ping = new JLabel(getIcon("./datafiles/마커.png", 30, 30)) {
				@Override
				public JToolTip createToolTip() {
					return new JToolTip();
				}
			};

			ping.setToolTipText(rs.get(0) + "");

			ping.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var m : map.getComponents())
						m.setEnabled(false);
					loc.setEnabled(true);
					ping.setEnabled(true);
					chart.repaint();
					sel = rs.get(1) + "";
				}
			});

			map.add(loc);
			map.add(ping);
			ping.setBounds(toInt(rs.get(4)), toInt(rs.get(5)), 30, 30);
			loc.setBounds(toInt(rs.get(2)), toInt(rs.get(3)), icon.getIconWidth(), icon.getIconHeight());
			loc.setEnabled(false);
			ping.setEnabled(false);
			map.setComponentZOrder(ping, 0);
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
