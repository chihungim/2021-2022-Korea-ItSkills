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
		super("���� ����", 800, 800);
		add(n = newJPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		add(c = newJPanel(new GridLayout(0, 7)));

		n.add(prev = lbl("��", 0, 20));
		n.add(date = lbl("", JLabel.CENTER, 15));
		n.add(next = lbl("��", 0, 20));

		for (var cap : "��,��,ȭ,��,��,��,��".split(",")) {
			var l = new JLabel(cap, JLabel.CENTER);
			c.add(l);

			if (cap.equals("��"))
				l.setForeground(Color.RED);
			else if (cap.equals("��"))
				l.setForeground(Color.BLUE);
		}

		evt(prev, a -> {
			if (ld.minusMonths(1).getYear() != LocalDate.now().getYear()) {
				emsg("���غ��� ������ �����˴ϴ�.");
			}
			ld = ld.minusMonths(1);
			setDays();
		});

		evt(next, a -> {
			if (ld.plusMonths(1).getYear() != LocalDate.now().getYear()) {
				emsg("���غ��� ������ �����˴ϴ�.");
			}
			ld = ld.plusMonths(1);
			setDays();
		});
		setDays();
		setVisible(true);
	}

	void setDays() {
		c.removeAll();
		for (var cap : "��,��,ȭ,��,��,��,��".split(",")) {
			var l = new JLabel(cap, JLabel.CENTER);
			c.add(l);

			if (cap.equals("��"))
				l.setForeground(Color.RED);
			else if (cap.equals("��"))
				l.setForeground(Color.BLUE);
		}
		var month = ld.getMonthValue();
		var year = ld.getYear();

		date.setText(year + "�� " + month + "��");
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
