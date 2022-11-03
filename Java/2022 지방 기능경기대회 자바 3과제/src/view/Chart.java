package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Chart extends BaseFrame {

	JComboBox<String> combo;
	JPanel chart;
	Color col[] = { Color.BLACK, Color.BLUE, Color.RED, Color.green, Color.YELLOW };

	public Chart() {
		super("지원자 분석", 700, 400);
		add(chart = new JPanel(new BorderLayout()) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				var rs = getValues("select \r\n"
						+ "	sum(if(year(now()) - year(u_birth) + 1 >= 10 and year(now()) - year(u_birth) + 1 < 20, 1,0)) one,\r\n"
						+ "    sum(if(year(now()) - year(u_birth) + 1 >= 20 and year(now()) - year(u_birth) + 1 < 30, 1,0)) two,\r\n"
						+ "    sum(if(year(now()) - year(u_birth) + 1 >= 30 and year(now()) - year(u_birth) + 1 < 40, 1,0)) three,\r\n"
						+ "    sum(if(year(now()) - year(u_birth) + 1 >= 40 and year(now()) - year(u_birth) + 1 < 50, 1,0)) four,\r\n"
						+ "    sum(if(year(now()) - year(u_birth) + 1 >= 50 and year(now()) - year(u_birth) + 1 < 60, 1,0)) five\r\n"
						+ "from\r\n" + "	user u, applicant a , employment e , company c\r\n"
						+ "where u.u_no = a.u_no and e.e_no = a.e_no and c.c_no = e.c_no and c.c_name = ?",
						combo.getSelectedItem()).get(0);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (rs.get(0) == null) {
					emsg("지원자 또는 공고가 없습니다.");
					combo.setSelectedIndex(0);
					chart.paint(g);
					return;
				} else { 
					var p = "10,20,30,40,50".split(",");
					int w = 50, h = 250, base = 80;
					int max = rs.stream().mapToInt(a -> toInt(a)).max().getAsInt();

					for (int i = 0; i < 5; i++) {
						g2.setColor(col[i]);
						var pr = (double) toInt(rs.get(i)) / max;
						var v = (int) (pr * h);
						g2.fillRect(30 + (i * 100), (h - v) + base, w, v);
						g2.fillRect(550, 150 + (i * 25), 20, 20);

						g2.setColor(Color.BLACK);
						g2.drawRect(30 + (i * 100), (h - v) + base, w, v);
						g2.drawString(cap[i] + "대", 40 + (i * 100), h + base + 20);
						g2.drawString(cap[i] + "대:" + rs.get(i) + "명", 580, 160 + (i * 25));
					}
				}
			}
		});

		chart.add(n = new JPanel(new BorderLayout()), "North");
		n.add(ne = new JPanel(new FlowLayout()), "East");
		n.add(hylbl("회사별 지원자 (연령별)", 0, 30));
		ne.add(combo = new JComboBox<String>());

		for (var w : getValues("select * from company")) {
			combo.addItem(w.get(1) + "");
		}
		combo.addItemListener(i -> {
			if (i.getStateChange() == 1)
				chart.repaint();
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}

}
