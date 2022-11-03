package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Cal extends BaseFrame {
	String week[] = "일,월,화,수,목,금,토".split(",");
	LocalDate date = LocalDate.now();
	DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy년 MM월");
	JLabel lblAll, lblDate;
	DayBox day[] = new DayBox[42];
	DecimalFormat dec = new DecimalFormat("#,##0");

	public Cal() {
		super("월별 분석", 650, 700);
		ui();
		setVisible(true);
	}

	void ui() {
		var n = new JPanel(new FlowLayout(1, 60, 0));
		var c = new JPanel(new GridLayout(0, 7));
		add(n, "North");
		add(c);

		n.add(btn("◀", a -> {
			setMonth(-1);
		}));
		n.add(lblDate = lbl(f.format(date), 0, 25));
		n.add(btn("▶", a -> {
			setMonth(1);
		}));

		n.add(lblAll = lbl("", 2, 20));

		for (int i = 0; i < week.length; i++) {
			var lbl = new JLabel(week[i], 0);
			c.add(lbl);
			if (i == 0) {
				lbl.setForeground(Color.red);
			} else if (i == 6)
				lbl.setForeground(Color.blue);
		}

		for (int i = 0; i < day.length; i++) {
			day[i] = new DayBox(i + "", "");
			c.add(day[i]);
		}

		n.setOpaque(false);
		c.setOpaque(false);

		drawCal();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

	}

	void setMonth(int m) {
		if (date.plusMonths(m).isAfter(LocalDate.now())) {
			iMsg("미래는 매출실적을 확인할 수 없습니다.");
			return;
		}
		date = date.plusMonths(m);
		drawCal();
	}

	void drawCal() {
		LocalDate sdate = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
		int sday = date.getDayOfWeek().getValue() % 7;
		lblDate.setText(f.format(date));
		int person = 0, price = 0;

		for (int i = 0; i < 42; i++) {
			LocalDate tmp = sdate.plusDays(i - sday);
			day[i].setDay(tmp.getDayOfMonth() + "");
			if (tmp.getMonthValue() != date.getMonthValue()) {
				day[i].setVisible(false);
			} else {
				day[i].setVisible(true);

			}
			day[i].setContent("", 0);

			try {
				var rs = stmt.executeQuery(
						"SELECT count(*), sum(r_money) FROM ticket t, ride r where r.r_no=t.r_no and t.t_date='" + tmp
								+ "' group by t.t_date");

				if (rs.next()) {
					if (tmp.getMonthValue() == date.getMonthValue()) {
						day[i].setContent(rs.getString(1), rs.getInt(2));
						person += rs.getInt(1);
						price += rs.getInt(2);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		lblAll.setText("<html><font color = 'green'>" + person + "명<br><font color = 'red'>"
				+ new DecimalFormat("#,##0").format(price) + "원");

	}

	class DayBox extends JPanel {
		JLabel lbl_day;

		JLabel lbl_content;

		public DayBox(String day, String content) {
			super(new BorderLayout());
			add(lbl_day = new JLabel(), "North");
			add(lbl_content = new JLabel());

			lbl_day.setForeground(toInt(day) % 7 == 0 ? Color.RED : toInt(day) % 7 == 6 ? Color.BLUE : Color.BLACK);
			lbl_day.setHorizontalTextPosition(JLabel.RIGHT);
			lbl_content.setHorizontalTextPosition(JLabel.LEFT);
			setBorder(new LineBorder(Color.BLACK));
		}

		void setDay(String day) {
			lbl_day.setText(day);
		}

		void setContent(String person, int price) {
			if (person.equals(""))
				lbl_content.setText("");
			else
				lbl_content.setText("<html><font color = \"green\"><left>" + person + "명<br><font color = \"red\">"
						+ dec.format(price) + "원");
		}

	}

	public static void main(String[] args) {
		new Cal();
	}
}
