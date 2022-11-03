package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class Cal extends BaseFrame {
	JLabel prev, next, date;
	LocalDate ld = LocalDate.now();

	public Cal() {
		super("날씨 정보", 800, 800);
		add(n = newJPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		add(c = newJPanel(new GridLayout(0, 7)));

		n.add(prev = lbl("◀", 0, 20));
		n.add(date = lbl("", JLabel.CENTER, 15));
		n.add(next = lbl("▶", 0, 20));

		for (var cap : "일,월,화,수,목,금,토".split(",")) {
			var l = new JLabel(cap, JLabel.CENTER);
			c.add(l);

			if (cap.equals("일"))
				l.setForeground(Color.RED);
			else if (cap.equals("토"))
				l.setForeground(Color.BLUE);
		}

		evt(prev, a -> {
			if (ld.minusMonths(1).getYear() != LocalDate.now().getYear()) {
				emsg("올해부터 정보가 제공됩니다.");
			}
			ld = ld.minusMonths(1);
			setDays();
		});

		evt(next, a -> {
			if (ld.plusMonths(1).getYear() != LocalDate.now().getYear()) {
				emsg("올해부터 정보가 제공됩니다.");
			}
			ld = ld.plusMonths(1);
			setDays();
		});
		setDays();
		setVisible(true);
	}

	void setDays() {
		c.removeAll();
		for (var cap : "일,월,화,수,목,금,토".split(",")) {
			var l = new JLabel(cap, JLabel.CENTER);
			c.add(l);

			if (cap.equals("일"))
				l.setForeground(Color.RED);
			else if (cap.equals("토"))
				l.setForeground(Color.BLUE);
		}
		var month = ld.getMonthValue();
		var year = ld.getYear();

		date.setText(year + "년 " + month + "월");
		var sdate = LocalDate.of(year, month, 1);
		var sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < 42; i++) {
			var temp = sdate.plusDays(i - sday);
			var p = newJPanel(new BorderLayout());
			var pc = newJPanel(new GridLayout(0, 1));
			var ps = newJPanel(new BorderLayout());
			var l = new JLabel(temp.getDayOfMonth() + "", JLabel.RIGHT);
			p.add(l, "North");
			p.add(new JScrollPane(pc));
			p.add(ps, "South");
			var weather = getrows("Select * from weather where t_day = ?", temp.toString());
			p.setVisible(temp.getMonthValue() == ld.getMonthValue());
			if (i % 7 == 6)
				l.setForeground(Color.BLUE);
			else if (i % 7 == 0)
				l.setForeground(Color.red);
			c.add(p);
			p.setBorder(new LineBorder(Color.BLACK));

		}
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		new Cal();
	}
}
