package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import view.Base.before;

public class Reservation extends BaseFrame {

	JPanel p1, p2, p3, p4, m;
	JPanel p1c, p2c, p3c, p4c;
	JPanel area, theater;
	JLabel header1, header2, header3, header4;
	LocalDate d;
	LocalDate ld = LocalDate.now();
	ArrayList<JPanel> time = new ArrayList<>();
	ArrayList<JLabel> movies = new ArrayList<>();

	public Reservation() {
		super("예매", 1200, 600);
		add(m = new JPanel());
		m.setLayout(new BoxLayout(m, BoxLayout.X_AXIS));
		m.add(p1 = new JPanel(new BorderLayout()));
		m.add(sz(p2 = new JPanel(new BorderLayout()), 60, 0));
		m.add(sz(p3 = new JPanel(new BorderLayout()), 200, 0));
		m.add(sz(p4 = new JPanel(new BorderLayout()), 200, 0));

		p1.add(header1 = lbl("영화관", JLabel.CENTER), "North");
		p2.add(header2 = lbl("날짜", JLabel.CENTER), "North");
		p3.add(header3 = lbl("영화 선택", JLabel.CENTER), "North");
		p4.add(header4 = lbl("시간", JLabel.CENTER), "North");

		p1.add(p1c = new JPanel(new GridLayout(1, 0)));

		p1c.add(area = new JPanel(new GridLayout(0, 1)));
		p1c.add(new JScrollPane(theater = new JPanel(new GridLayout(0, 1))));

		p2.add(p2c = new JPanel(new GridLayout(0, 1)));
		p3.add(new JScrollPane(p3c = new JPanel(new GridLayout(0, 1))));
		p4.add(new JScrollPane(p4c = new JPanel(new GridLayout(0, 1))));

		for (var a : getrows("select a_no, a_name from area")) {
			var l = lbl(a.get(1) + "", JLabel.LEFT);
			area.add(l);
			l.setOpaque(true);
			evt(l, x -> {
				intiMovie();
				tno = null;
				for (var aa : area.getComponents())
					aa.setBackground(null);
				l.setBackground(Color.WHITE);
				setTheater(a.get(0) + "");
			});
		}

		for (int i = 0; i < 7; i++) {
			var l1 = lbl("<html>" + String.format("%02d", ld.getMonthValue()) + "<br>"
					+ ld.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()), JLabel.LEFT, 15);
			var l2 = lbl(String.format("%02d", ld.getDayOfMonth()), JLabel.LEFT, 20);

			var temp = newJPanel(new BorderLayout(5, 5));
			temp.add(l1, "West");
			temp.add(l2, "East");
			var cur = ld;
			evt(temp, a -> {
				d = cur;
				time.forEach(x -> x.setBorder(null));
				temp.setBorder(new LineBorder(Color.BLACK));
				setMovie();
			});
			time.add(temp);
			p2c.add(temp);
			ld = ld.plusDays(1);
		}

		time.get(0).setBorder(new LineBorder(Color.BLACK));
		d = LocalDate.now();
		b(Color.white, (JComponent) area.getComponent(0));
		setTheater("1");
		intiMovie();
		sz(header1, 0, 30);
		sz(header2, 0, 30);
		sz(header3, 0, 30);
		sz(header4, 0, 30);
		header1.setBorder(new MatteBorder(0, 1, 0, 0, Color.WHITE));
		header2.setBorder(new MatteBorder(0, 1, 0, 0, Color.WHITE));
		header3.setBorder(new MatteBorder(0, 1, 0, 0, Color.WHITE));
		header4.setBorder(new MatteBorder(0, 1, 0, 0, Color.WHITE));
		b(Color.BLACK, header1, header2, header3, header4);
		f(Color.WHITE, header1, header2, header3, header4);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Reservation();
	}

	void intiMovie() {
		header3.setText("영화");
		p3c.removeAll();
		header1.setText("영화관");
		for (var m : getrows("select m.* from movie m")) {
			var l = lbl(m.get(5) + "", JLabel.LEFT, 15);
			p3c.add(l);
			l.setIcon(getIcon("./datafile/아이콘/" + m.get(2).toString().replaceAll("[\r\n]", "") + ".png", 20, 20));
			evt(l, a -> {
				emsg("지점을 먼저 선택해주세요.");
				return;
			});
		}

		while (p3c.getComponentCount() < 16) {
			p3c.add(newJPanel(null));
		}
		p4c.removeAll();
		revalidate();
		repaint();
	}

	void setTheater(String ano) {
		header3.setText("영화");
		theater.removeAll();
		for (var r : getrows("select * from theater where a_no = ?", ano)) {
			var l = lbl(r.get(2) + "", JLabel.LEFT);
			l.setOpaque(true);
			evt(l, a -> {
				for (var t : theater.getComponents()) {
					t.setForeground(null);
					t.setBackground(null);
				}
				l.setBackground(Color.black);
				l.setForeground(Color.white);
				header1.setText(l.getText());
				tno = r.get(0) + "";
				setMovie();

			});
			theater.add(l);
		}
		while (theater.getComponentCount() < 25)
			theater.add(lbl("", 0));

		revalidate();
		repaint();

	}

	void setMovie() {
		if (tno == null)
			return;
		header3.setText("영화");
		movies = new ArrayList<>();
		p3c.removeAll();
		var rs = getrows(
				"select m.* from schedule sc inner join movie m on sc.m_no=  m.m_no where t_no = ? and sc_date = ? group by m_no",
				tno, d.toString());
		for (var r : rs) {
			var l = lbl(r.get(5) + "", JLabel.LEFT, 15);
			p3c.add(l);
			l.setIcon(getIcon("./datafile/아이콘/" + r.get(2).toString().replaceAll("[\r\n]", "") + ".png", 20, 20));
			evt(l, a -> {
				mno = rs.get(0) + "";
				setSchedule();
				movies.forEach(x -> {
					x.setBackground(Color.white);
					x.setForeground(Color.BLACK);
					mno = r.get(0) + "";
					setSchedule();
					header3.setText(r.get(5) + "");
				});

				l.setBackground(Color.BLACK);
				l.setForeground(Color.white);
			});
			l.setOpaque(true);
			movies.add(l);
		}
		while (p3c.getComponentCount() < 16) {
			p3c.add(newJPanel(null));
		}
		revalidate();
		repaint();
	}

	void setSchedule() {
		p4c.removeAll();
		var rs = getrows(
				"select (select 60 - ifnull(sum(length(r_seat) - length(replace(r_seat, ',', ''))+1),0) from reservation r where r.sc_no = sc.sc_no ), sc.*, p.* from schedule sc, pomaes p where sc.p_no = p.p_no and sc.sc_date = ? and sc.t_no = ? and sc.m_no = ? order by sc_theater, time(sc_time)",
				d.toString(), tno, mno);
		var map = new HashMap<String, JPanel>();
		for (var r : rs) {
			if (!map.containsKey(r.get(9) + " " + r.get(5))) {
				var p = newJPanel(new GridLayout(0, 4, 5, 5));
				p.setBorder(new TitledBorder(new LineBorder(Color.BLACK), r.get(5) + " " + r.get(9)));
				map.put(r.get(9) + " " + r.get(5) + "", p);
			}
			var time = newJPanel(new BorderLayout(5, 5));
			var l1 = lbl(r.get(7) + "", JLabel.CENTER, 13);
			var l2 = lbl(r.get(0) + "/60", JLabel.CENTER);
			l2.setForeground(new Color(0, 123, 255).brighter());
			time.add(l1);
			time.add(l2, "South");
			map.get(r.get(9) + " " + r.get(5)).add(time);
			evt(time, a -> {
				scno = r.get(1) + "";
				new Seat().addWindowListener(new before(this));
			});
			time.setBorder(new LineBorder(Color.BLACK));

		}

		map.entrySet().stream().map(a -> a.getValue()).forEach(a -> {
			while (a.getComponentCount() < 8)
				a.add(newJPanel(null));
		});

		for (var m : map.keySet())
			p4c.add(map.get(m));

		while (p4c.getComponentCount() < 5)
			p4c.add(newJPanel(null));

		revalidate();
		repaint();
	}
}
