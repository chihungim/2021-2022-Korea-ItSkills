package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Reservation extends BasePage {

	LocalDate d = LocalDate.now();
	JLabel theater;

	JPanel m = new JPanel();
	JPanel days[] = new JPanel[14];

	public Reservation() {
		super("극장 찾기");
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(nn = newJPanel(new GridLayout(1, 0)), "North");
		n.add(sz(nc = newJPanel(new GridLayout(0, 10)), 0, 100));

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		for (var r : getrows("select a_no, a_name from area")) {
			var l = lbl(r.get(1) + "", JLabel.CENTER);
			evt(l, a -> {
				for (var c : nn.getComponents())
					((JLabel) c).setBorder(null);
				setTheater(r.get(0) + "");
				l.setBorder(new LineBorder(Color.WHITE));
				for (var d : days) {
					((JLabel) d.getComponent(0)).setForeground(Color.lightGray);
					((JLabel) d.getComponent(1)).setForeground(Color.lightGray);
				}
				theater.setText("");
				cc.removeAll();
			});
			nn.add(l).setForeground(Color.WHITE);
		}

		border((JComponent) nn.getComponent(0), new LineBorder(Color.WHITE));
		cn.add(theater = lbl("", JLabel.LEFT, 20), "North");

		var cnc = new JPanel(null);

		cnc.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));

		cn.add(sz(cnc, 0, 60));
		for (int i = 0; i < 14; i++) {
			var current = d;
			var item = new JPanel(new BorderLayout(0, 0));

			String month = String.format("%02d", d.getMonthValue()) + "월";
			String week = d.getDayOfWeek().getDisplayName(TextStyle.NARROW, getLocale());

			var l1 = lbl("<html>" + month + "<br>" + week, JLabel.LEFT, 12);
			var l2 = lbl(String.format("%02d", d.getDayOfMonth()), JLabel.RIGHT, 0, 40);
			item.add(l1, "West");
			item.add(l2, "East");

			l1.setForeground(Color.LIGHT_GRAY);
			l2.setForeground(Color.LIGHT_GRAY);
			cnc.add(item);
			item.setBounds(i * 90, 0, 80, 50);
			days[i] = item;
			System.out.println(Arrays.asList(days));
			evt(item, a -> {
				if (theater.getText().trim().isEmpty())
					return;

				if (a.getButton() == 1) {
					for (var d : days) {
						((JLabel) d.getComponent(0)).setForeground(Color.lightGray);
						((JLabel) d.getComponent(1)).setForeground(Color.lightGray);
					}
					setTable(current);
					l1.setForeground(Color.BLACK);
					l2.setForeground(Color.BLACK);
				}
			});
			d = d.plusDays(1);
		}

		c.setBorder(new EmptyBorder(10, 100, 10, 100));

		border(n, new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.WHITE)));
		n.setBackground(olive);
		setTheater("1");

		setBorder(new EmptyBorder(10, 10, 10, 10));

	}

	public static void main(String[] args) {
		BaseFrame.uno = "1";
		new CinemaFrame("Reservation");
	}

	private void setTheater(String ano) {
		nc.removeAll();

		for (var r : getrows("select * from theater where a_no = ?", ano)) {
			var theaterlbl = lbl(r.get(1) + "", JLabel.CENTER, 13);
			evt(theaterlbl, a -> {
				if (a.getButton() == 1) {
					theater.setName(r.get(0) + "");
					theater.setText(r.get(1) + "");
					days[0].getComponent(0).setForeground(Color.BLACK);
					days[0].getComponent(1).setForeground(Color.BLACK);
					for (var c : nc.getComponents()) {
						((JLabel) c).setBorder(null);
					}
					theaterlbl.setBorder(new LineBorder(Color.WHITE));
					setTable(LocalDate.now());
				}
			});

			nc.add(theaterlbl).setForeground(Color.WHITE);
		}

		revalidate();
		repaint();
	}

	private void setTable(LocalDate now) {
		cc.removeAll();
		var rs = getrows(
				"select m.* from theater t , movie m where find_in_set(m.m_no, replace(t.m_no, '.', ',')) > 0 and t_no = ?",
				theater.getName());

		for (var r : rs) {
			var temp = new JPanel(new BorderLayout());
			var tempn = new JPanel(new FlowLayout(FlowLayout.LEFT));
			var tempc = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			var tempcc = new JPanel(new GridLayout(0, 4, 0, 20));

			temp.add(tempn, "North");
			temp.add(tempc);
			tempc.add(tempcc);
			var g = Arrays.stream(r.get(3).toString().split("\\.")).map(a -> genre[cint(a)])
					.collect(Collectors.joining(","));
			var 요약lbl = lbl("<html><font color = 'gray'>" + g + "/" + r.get(4) + "분/" + r.get(6), JLabel.LEFT, 12);

			tempn.add(ageLimit(r.get(5).toString()));
			tempn.add(lbl(r.get(1) + "", JLabel.LEFT, 18));
			tempn.add(요약lbl);

			var sdt = LocalDateTime.of(now, LocalTime.of(6, 0));
			var edt = LocalDateTime.of(now.plusDays(1), LocalTime.of(0, 0));
			while (!sdt.isAfter(edt)) {
				final var d = sdt;
				if (sdt.isAfter(LocalDateTime.now())) {
					var screen = new JPanel(new BorderLayout());
					var remain = cint(getrows(
							"select 100-ifnull(sum(char_length(r_seat) - char_length(replace(r_seat, '.', ''))+1),0) from reservation where r_time= ? and r_date = ? and m_no = ? and t_no = ?",
							sdt.toLocalTime() + "", sdt.toLocalDate() + "", r.get(0) + "", theater.getName()).get(0)
							.get(0));

					var timelbl = lbl(sdt.toLocalTime().toString(), JLabel.CENTER, 20);
					var seatlbl = lbl(remain + "석", JLabel.CENTER, 15);
					seatlbl.setForeground(Color.blue);
					if (remain == 0) {
						timelbl.setForeground(Color.LIGHT_GRAY);
						seatlbl.setForeground(Color.lightGray);
						seatlbl.setText("예약종료");
					}

					screen.add(timelbl);
					screen.add(seatlbl, "South");
					tempcc.add(screen);
					evt(screen, a -> {
						if (timelbl.getForeground().equals(Color.LIGHT_GRAY))
							return;
						if (BaseFrame.uno.isEmpty()) {
							if (msg() == 0) {
								new Login().addWindowListener(new before(cf));
							}
							return;
						}

						new Seat(theater.getName(), r, d, theater.getName()).addWindowListener(new before(cf));
					});
					screen.setBorder(new LineBorder(Color.LIGHT_GRAY));
				}
				sdt = sdt.plusMinutes(30 + cint(r.get(4)));
			}

			cc.add(temp);
		}

	}

}
