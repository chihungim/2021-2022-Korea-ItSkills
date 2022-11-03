package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Schedule extends BaseFrame {
	String filter = "";
	JLabel plbl, mlbl, nlbl;
	Day d[] = new Day[42];
	Type t[] = { new Type("M", "뮤지컬"), new Type("O", "오페라"), new Type("C", "콘서트") };
	int month, year;
	LocalDate today = LocalDate.now();

	ArrayList<Type> all = new ArrayList<>();

	public Schedule() {
		super("월별 일정", 1000, 850);
		var n = new JPanel(new BorderLayout());
		var n_c = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var n_s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var c = new JPanel(new GridLayout(0, 7, 0, 0));

		add(n, "North");
		add(c);

		n.add(n_c);
		n.add(n_s, "South");

		n_c.add(plbl = lbl("◀", 0, 20));
		n_c.add(mlbl = lbl(today.getMonthValue() + "월", 0, 20));
		n_c.add(nlbl = lbl("▶", 0, 20));

		for (int i = 0; i < t.length; i++) {
			n_s.add(t[i]);
		}

		n_s.add(btn("전체", a -> {
			filter = "";
			all.forEach(e -> e.setVisible(true));
		}));

		for (var cap : "일,월,화,수,목,금,토".split(",")) {
			var lbl = lbl(cap, 0);
			c.add(lbl);

			if (cap.equals("일"))
				lbl.setForeground(Color.RED);
			else if (cap.equals("토"))
				lbl.setForeground(Color.BLUE);
		}

		for (int i = 0; i < 42; i++) {
			c.add(d[i] = new Day(i + ""));
			if (i % 7 == 0)
				d[i].daylbl.setForeground(Color.RED);
			else if (i % 7 == 6)
				d[i].daylbl.setForeground(Color.BLUE);
		}
		plbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (today.plusMonths(-1).getYear() != year)
					return;
				today = today.plusMonths(-1);
				setDay();
				super.mousePressed(e);
			}
		});

		nlbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (today.plusMonths(1).getYear() != year)
					return;
				today = today.plusMonths(1);
				setDay();
				super.mousePressed(e);
			}
		});

		for (int i = 0; i < t.length; i++) {
			t[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {

						var me = (Type) e.getSource();

						all.forEach(a -> a.setVisible(true));

						if (e.getClickCount() == 2) {
							filter = me.typelbl.getText();
							all.stream().filter(a -> !a.typelbl.getText().equals(filter))
									.forEach(el -> el.setVisible(false));
						}
					}
					super.mouseClicked(e);
				}
			});
		}
		setDay();
		setVisible(true);
	}

	public static void main(String[] args) {

	}

	void setDay() {
		month = today.getMonthValue();
		year = today.getYear();
		mlbl.setText(month + "월");

		LocalDate sdate = LocalDate.of(year, month, 1);
		int sday = sdate.getDayOfWeek().getValue() % 7;
		all.clear();
		for (int i = 0; i < 42; i++) {
			LocalDate tmp = sdate.plusDays(i - sday);
			d[i].daylbl.setText(tmp.getDayOfMonth() + "");
			if (tmp.getMonthValue() != month) {
				d[i].daylbl.setVisible(false);
				d[i].center.setVisible(false);
			} else {
				d[i].daylbl.setVisible(true);
				d[i].center.setVisible(true);
			}
			d[i].center.removeAll();

			try {
				var rs = stmt.executeQuery(
						"SELECT left(pf_no,1), p_name FROM 2021전국.perform where p_date = '" + tmp + "' limit 0, 3");
				while (rs.next()) {
					Type t = new Type(rs.getString(1), rs.getString(2));
					t.setAlignmentX(LEFT_ALIGNMENT);
					d[i].center.add(t);
					d[i].center.add(Box.createVerticalStrut(5));
					all.add(t);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d[i].center.revalidate();
			d[i].center.repaint();
		}

		if (!filter.isEmpty())
			all.stream().filter(a -> !a.typelbl.getText().equals(filter)).forEach(a -> a.setVisible(false));
	}

	class Type extends JPanel {

		JLabel typelbl;

		public Type(String type, String name) {
			setLayout(new BorderLayout());
			add(BaseFrame.sz(typelbl = lbl(type, JLabel.CENTER), 20, 20), "West");
			typelbl.setOpaque(true);
			switch (type) {
			case "M":
				typelbl.setBackground(Color.MAGENTA.darker());
				break;
			case "O":
				typelbl.setBackground(Color.BLUE);
				break;
			default:
				typelbl.setBackground(Color.ORANGE);
				break;
			}
			typelbl.setForeground(Color.WHITE);
			typelbl.setBorder(new LineBorder(Color.BLACK));
			add(lbl(name, JLabel.LEFT));
			setMaximumSize(new Dimension(100, 20));
		}
	}

	class Day extends JPanel {

		JPanel center;
		JLabel daylbl;

		public Day(String day) {
			setLayout(new BorderLayout());
			add(daylbl = lbl(day, JLabel.RIGHT), "North");
			add(center = new JPanel());
			center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
			setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		}
	}
}
