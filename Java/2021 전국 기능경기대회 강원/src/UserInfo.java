import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class UserInfo extends BaseDialog {
	DefaultTableModel m = model("no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	JPanel p;

	JLabel title;

	int 순번;

	public UserInfo(int 순번) {
		super("예매 정보", 800, 600);

		this.순번 = 순번;

		UI();
		setVisible(true);
	}

	void UI() {
		add(title = lbl("사용자 예매 정보", 2, 25), "North");

		setChart();
		add(size(jsc, 0, 200), "South");

		try {
			var rs = stmt.executeQuery(
					"SELECT a.name, b.name, date_add(date, interval elapsed_time HOUR_SECOND), date FROM busticketbooking.reservation r inner join schedule s on r.schedul e_no = s.no inner join a on s.departure_location2_no = a.no inner join a b on s.arrival_location2_no = b.no where user_no = "
							+ 순번 + " order by date asc");
			int no = 1;
			while (rs.next()) {
				Object row[] = { no, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4) };
				m.addRow(row);
				no++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	void setChart() {
		add(p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.drawLine(20, 30, 20, 250);
				g2d.drawLine(20, 40, 700, 40);
				g2d.drawLine(20, 145, 700, 145);
				g2d.drawLine(20, 250, 700, 250);

				int nums[] = { 10, 105, 210 };
				int h[] = { 240, 145, 40 };
				Integer[] val = new Integer[3];

				try {
					var rs = stmt.executeQuery(
							"SELECT date_format(date, '%m') m, count(*) from reservation r, schedule s, a b where r.schedule_no = s.no and s.arrival_location2_no = b.no and user_no = "
									+ 순번 + " group by m order by m desc limit 3");
					int i = 0;
					while (rs.next()) {
						val[i] = rs.getInt(2);
						g2d.setColor(new Color(0, 123, 255));
						g2d.fillRect(165 + (i * 200), h[i], 100, nums[i]);
						g2d.setColor(Color.BLACK);
						g2d.drawString(String.format("%d월", rs.getInt(1)), 600 - (i * 200), 270);
						i++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				g2d.drawLine(230, 240, 615, 40);
				for (int i = 0; i < h.length; i++) {
					g2d.fillOval(605 - (i * 200), nums[i] + ((i == 0) ? 25 : 35), 15, 15);
				}

				Arrays.sort(val, Collections.reverseOrder());
				for (int j = 0; j < val.length; j++) {
					g2d.drawString(val[j] + "", 2, 40 + (j * 105));
				}
			}
		});

		p.setBackground(whiteColor);
	}

	public static void main(String[] args) {
		new UserInfo(6);
	}
}
