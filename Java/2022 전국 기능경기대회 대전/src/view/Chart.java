package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Chart extends BaseFrame {

	ArrayList<Object> base;
	JLabel chart;

	public Chart(String bno) {
		super("차트", 600, 500);
		base = getrows("select * from base where b_no = ?", bno).get(0);
		var l = new JLabel(base.get(2) + " 가격 비교", JLabel.CENTER);
		add(l, "North");
		int xx[] = new int[5];
		int yy[] = new int[5];

		var d = getrows(
				"select f.*, c.c_name, t.t_name, u.u_name from farm f , user u, town t, city c where f.b_no = ? and f.u_no = u.u_no and u.t_no = t.t_no and c.c_no = t.c_no order by f_amount asc limit 0,5",
				bno);
		add(chart = new JLabel(getBlob(base.get(5), 600, 350)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = tog2d(g);
				int max = d.stream().mapToInt(c -> cint(c.get(3))).max().getAsInt();
				int h = 300, x = 0;
				for (int i = 0; i < d.size(); i++) {
					double pr = cdbl(d.get(i).get(3)) / max;
					xx[i] = 20 + x * 120;
					yy[i] = h - (int) (h * pr) + 100;
					g2.drawString(d.get(i).get(3) + "", xx[i], yy[i] - 20);
					x++;
					g2.setFont(new Font("", Font.BOLD, 15));
					g2.drawString(d.get(i).get(5) + "" + d.get(i).get(6), xx[i], 380);
					g2.drawString(d.get(i).get(7) + "", xx[i], 395);

				}

				g2.setColor(Color.BLUE);
				g2.setStroke(new BasicStroke(3f));
				g2.drawPolyline(xx, yy, xx.length);
				for (int i = 0; i < 5; i++) {
					g2.setColor(i == 0 ? Color.RED : Color.BLUE);
					g2.fillOval(xx[i] - 10, yy[i] - 10, 20, 20);
				}
			}
		});
		evt(chart, e -> {
			if (e.getX() >= xx[0] - 10 && e.getX() <= xx[0] - 10 + 20 && e.getY() >= yy[0] - 10
					&& e.getY() <= yy[0] - 10 + 20) {
				new Purchase(d.get(0)).addWindowListener(new before(this));
			}
		});

		chart.setVerticalAlignment(JLabel.TOP);
		chart.setLayout(null);
		l.setFont(new Font("맑은 고딕", 0, 20));

		setVisible(true);
	}

	public static void main(String[] args) {
		디버그("1");
		new Chart("42");
	}

}
