package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Calc extends BaseDialog {

	JLabel prev, next, date;

	JLabel days[] = new JLabel[42];
	Border selected = new LineBorder(Color.BLUE);
	LocalDate d = LocalDate.now();
	JComponent jc;

	public Calc(JComponent jc) {
		super("날짜선택", 400, 400);
		this.jc = jc;
		add(n = new JPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		var cap = "일,월,화,수,목,금,토".split(",");

		for (int i = 0; i < cap.length; i++) {
			var l = lbl(cap[i], JLabel.CENTER);
			c.add(l).setForeground(i == 0 ? Color.RED : i == 6 ? Color.BLUE : Color.black);
		}

		n.add(prev = lbl("◀", 0));
		n.add(date = lbl("", 0));
		n.add(next = lbl("▶", 0));

		evt(prev, a -> {
			if (prev.isEnabled()) {
				d = d.plusMonths(-1);
				setDays();
			}
			if (d.getMonthValue() == LocalDate.now().getMonthValue()) {
				prev.setEnabled(false);
			}
		});
		evt(next, a -> {
			d = d.plusMonths(1);
			prev.setEnabled(true);
			setDays();
		});
		for (int i = 0; i < 42; i++) {
			days[i] = lbl(i + "", JLabel.CENTER);
			days[i].setBorder(new LineBorder(Color.LIGHT_GRAY));
			c.add(days[i]);
			evt(days[i], a -> {
				var me = ((JLabel) a.getSource()).getName();
				var d = LocalDate.parse(me, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				if (d.isBefore(LocalDate.now())) {
					emsg("이전 날짜는 선택이 불가능 합니다.");
					return;
				}

				if (((JLabel) a.getSource()).getBorder() == selected) {
					if (jc instanceof JTextField) {
						((JTextField) jc).setText(me);
					} else {
						((JLabel) jc).setName(me);
					}
					dispose();
				}
				Arrays.stream(days).forEach(x -> x.setBorder(new LineBorder(Color.lightGray)));
				((JLabel) a.getSource()).setBorder(selected);
			});
		}
		setDays();
		prev.setEnabled(false);
		setVisible(true);
	}

	void setDays() {
		int year = d.getYear(), month = d.getMonthValue();
		date.setText(year + "년 " + month + "월");
		var sdate = LocalDate.of(year, month, 1);
		var sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < 42; i++) {
			var temp = sdate.plusDays(i - sday);
			days[i].setText(temp.getDayOfMonth() + "");
			days[i].setName(temp + "");
			days[i].setEnabled(temp.getMonthValue() == d.getMonthValue());

			if (jc instanceof JTextField) {
				if (((JTextField) jc).getText().equals(temp.toString()))
					days[i].setBorder(selected);
				else
					days[i].setBorder(new LineBorder(Color.LIGHT_GRAY));
			}
		}
		revalidate();
		repaint();

	}
}
