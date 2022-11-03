package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Schedule extends BaseFrame {

	JScrollPane pane;
	LocalDate ld = LocalDate.now();
	ArrayList<JPanel> time = new ArrayList<>();

	public Schedule() {
		super("상영 시간표", 600, 500);
		add(n = newJPanel(new GridLayout(1, 0, 5, 5)), "North");
		add(pane = new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		c.setBackground(Color.WHITE);
		for (int i = 0; i < 7; i++) {
			var l1 = lbl("<html>" + String.format("%02d", ld.getMonthValue()) + "<br>"
					+ ld.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()), JLabel.LEFT, 15);
			var l2 = lbl(String.format("%02d", ld.getDayOfMonth()), JLabel.LEFT, 20);

			var temp = newJPanel(new BorderLayout(5, 5));
			temp.add(l1, "West");
			temp.add(l2, "East");
			var cur = ld;
			evt(temp, a -> {
				time.forEach(x -> x.setBorder(null));
				temp.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));
				setSchedules(cur);
			});
			time.add(temp);
			n.add(temp);

			ld = ld.plusDays(1);
		}

		time.get(0).setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));
		setSchedules(LocalDate.now());
		n.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0, Color.BLACK), new EmptyBorder(0, 5, 0, 5)));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	void setSchedules(LocalDate d) {
		c.removeAll();
		var map = new HashMap<String, JPanel>();
		var rs = getrows(
				"select m.m_no, m.m_name, sc.sc_no, sc.sc_time,  (select 60 - ifnull(sum(length(r_seat) - length(replace(r_seat, ',', ''))+1),0) from reservation r where r.sc_no = sc.sc_no ) , sc.sc_time, sc.sc_date from schedule sc, movie m where sc.m_no = m.m_no and t_no = ? and sc_date = ?",
				tno, d.toString() + "");
		System.out.println(rs);
		for (var r : rs) {
			if (!map.containsKey(r.get(0) + "")) {
				var p = newJPanel(new GridLayout(0, 7, 5, 5));
				p.setBorder(new TitledBorder(new LineBorder(Color.BLACK), r.get(1) + ""));
				map.put(r.get(0) + "", p);
			}
			var time = newJPanel(new BorderLayout(5, 5));
			var l1 = lbl(r.get(3) + "", JLabel.CENTER, 13);
			var l2 = lbl(r.get(4) + "/60", JLabel.CENTER);
			l2.setForeground(new Color(0, 123, 255).brighter());
			time.add(l1);
			time.add(l2, "South");
			map.get(r.get(0) + "").add(time);
			evt(time, a -> {
				scno = r.get(2) + "";
				mno = r.get(0) + "";
				new Seat().addWindowListener(new before(this));
			});
			time.setBorder(new LineBorder(Color.BLACK));

		}

		map.entrySet().stream().map(a -> a.getValue()).forEach(a -> {
			while (a.getComponentCount() < 14)
				a.add(newJPanel(null));
		});

		for (var m : map.keySet())
			c.add(map.get(m));

		while (c.getComponentCount() < 3)
			c.add(newJPanel(null));

		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		tno = "1";
		new Schedule();

	}
}
