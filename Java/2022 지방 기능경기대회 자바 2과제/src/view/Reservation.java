package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Reservation extends BaseFrame {
	JTextField txt;
	JLabel days[] = new JLabel[42];
	ArrayList<JLabel> times = new ArrayList<JLabel>();
	JLabel hprev, hnext, vprev, vnext, datelbl;
	JLabel lbl[] = new JLabel[7];

	LocalDate cdate = LocalDate.now();

	ArrayList<Object> cafe, theme;
	CardLayout pages;

	DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM, d, yyyy", Locale.US);
	DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
	DecimalFormat dec = new DecimalFormat("#,##0");

	String wh = "<html><font color = 'white'>";
	JPanel timePanel, cec, ces, cce, ccc, cwn, cwc, cwcn, cwcc;

	public Reservation(String cno, String tno) {
		super("예약", 1000, 600);

		cafe = getValues("select * from cafe where c_no = ?", cno).get(0);
		theme = getValues("select * from theme where t_no = ?", tno).get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		n.add(lbl("방탈출 예약", 0, 30));
		n.add(lbl("Room Escape Reservation", 0), "South");

		c.add(sz(cw = new JPanel(new BorderLayout()), 400, 0), "West");
		c.add(cc = new JPanel(new BorderLayout(5, 5)));
		c.add(sz(ce = new JPanel(new BorderLayout()), 450, 0), "East");
		cw.add(sz(cwn = new JPanel(new BorderLayout()), 0, 30), "North");
		cw.add(cwc = new JPanel(new BorderLayout()));
		cwc.add(cwcn = new JPanel(new FlowLayout(1)), "North");
		cwc.add(cwcc = new JPanel(new GridLayout(0, 7)));
		cc.add(ccc = new JPanel(new BorderLayout()));
		cc.add(sz(cce = new JPanel(new BorderLayout()), 40, 30), "East");
		ce.add(cec = new JPanel(new GridLayout(0, 1, 5, 5)));
		ce.add(ces = new JPanel(new FlowLayout(1)), "South");

		cwn.add(hprev = lbl(wh + "◁", 0), "West");
		cwn.add(datelbl = lbl("", 0));
		cwn.add(hnext = lbl(wh + "▷", 0), "East");

		for (var cap : "SUN,MON,TUE,WED,THU,FRI,SAT".split(","))
			cwcn.add(lbl(cap, 0, 20));

		for (int i = 0; i < days.length; i++) {
			cwcc.add(days[i] = lbl(i + "", 0, 13));
			days[i].setOpaque(false);
			days[i].setBackground(Color.ORANGE);
			days[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();
					if (me.isVisible() && me.isEnabled()) {
						for (var d : days)
							d.setOpaque(false);
						me.setOpaque(true);
						datelbl.setText(wh + me.getName());
						lbl[0].setText(wh + LocalDate.parse(me.getName(), df));
						setTime();
						revalidate();
						repaint();
					}

				}
			});
		}

		var header = lbl(wh + "시간", 0);

		ccc.add(sz(header, 0, 30), "North");
		ccc.add(timePanel = new JPanel(pages = new CardLayout()));

		cce.add(vprev = lbl("▲", 0), "North");
		cce.add(vnext = lbl("▼", 0), "South");

		var cap = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(wh + cap[i], JLabel.LEFT), 80, 30));
			if (i != 5) {
				temp.add(lbl[i] = new JLabel("", 0));
			} else {
				temp.add(txt = new JTextField(15));
			}
			temp.setOpaque(false);
			cec.add(temp);
		}

		for (var bc : "예약,취소".split(",")) {
			ces.add(btn(bc, a -> {
				if (a.getActionCommand().equals("예약")) {
					for (var l : lbl)
						if (l != null && l.getText().isEmpty()) {
							emsg("빈칸이 있습니다.");
							return;
						}

					if (txt.getText().isEmpty()) {
						emsg("빈칸이 있습니다.");
						return;
					}

					setValues("insert reservation values(0,?,?,?,?,?,?,?)", uno, cno, tno,
							LocalDate.parse(datelbl.getText().replace(wh, ""), df), lbl[3].getText().replace(wh, ""),
							txt.getText(), 0);

					imsg("예약이 완료되었습니다.");

					for (var w : getWindows())
						w.setVisible(w instanceof Main);
				} else
					dispose();
			}));
		}

		lbl[1].setText(wh + cafe.get(1) + "");
		lbl[2].setText(wh + theme.get(1) + "");
		lbl[4].setText(wh + dec.format(toInt(cafe.get(6))));
		lbl[6].setText(wh + "0");

		cce.setBorder(new LineBorder(Color.BLACK));
		header.setOpaque(true);
		header.setBackground(Color.BLACK);
		cwn.setBackground(Color.BLACK);
		cec.setBackground(Color.BLACK);
		ces.setBackground(Color.BLACK);

		setDay();

		times.get(0).setOpaque(true);
		lbl[3].setText(wh + times.get(0).getText());

		Stream.of(vprev, vnext, hprev, hnext).forEach(a -> {
			a.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (a == vprev)
						pages.previous(timePanel);
					else if (a == vnext)
						pages.next(timePanel);
					else if (a == hprev) {
						if (LocalDate.now().isBefore(cdate)) {
							cdate = cdate.plusMonths(-1);
						}
						setDay();
					} else if (a == hnext) {
						cdate = cdate.plusMonths(1);
						setDay();
					}
				}

			});
		});

		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txt.getText().isEmpty())
					return;

				if (txt.getText().matches(".*[^0-9].*") || toInt(txt.getText()) < 1) {
					emsg("인원수를 확인하세요.");
					cleartxt(txt);
					return;
				}

				if (toInt(getValue("select t_personnel from theme where t_no = ?", tno).get(0)) < toInt(
						txt.getText())) {
					emsg("인원을 초과하였습니다.");
					cleartxt(txt);
					return;
				}

				lbl[6].setText(wh + dec.format(toInt(cafe.get(6)) * toInt(txt.getText())));
			}
		});

		setVisible(true);
	}

	void setDay() {
		int y = cdate.getYear(), m = cdate.getMonthValue();
		var sdate = LocalDate.of(y, m, 1);
		datelbl.setText("");
		var sday = sdate.getDayOfWeek().getValue() % 7;
		for (int i = 0; i < 42; i++) {
			var d = sdate.plusDays(i - sday);
			days[i].setOpaque(false);
			days[i].setText(d.getDayOfMonth() + "");
			days[i].setName(df.format(d));
			days[i].setVisible(d.getMonthValue() == m);
			days[i].setEnabled(!d.isBefore(LocalDate.now()));
			if (datelbl.getText().isEmpty() && days[i].isEnabled() && days[i].isVisible()) {
				days[i].setOpaque(true);
				datelbl.setText(wh + df.format(d));
				lbl[0].setText(wh + LocalDate.parse(days[i].getName(), df) + "");
			}
		}
		setTime();
	}

	void setTime() {
		timePanel.removeAll();
		times.clear();
		lbl[3].setText("");
		var st = LocalTime.of(0, 30);
		var p = new JPanel(new GridLayout(0, 1));
		for (; st.isAfter(LocalTime.of(0, 0)); st = st.plusMinutes(30)) {
			if (LocalDateTime.now()
					.isBefore(LocalDateTime.of(LocalDate.parse(datelbl.getText().replace(wh, ""), df), st))) {
				var t = lbl(tf.format(st), 0);
				t.setBackground(Color.ORANGE);
				t.setOpaque(false);
				t.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						for (var tt : times)
							tt.setOpaque(false);
						lbl[3].setText(wh + t.getText());
						t.setOpaque(true);
						revalidate();
						repaint();
					}
				});
				p.add(t);
				times.add(t);
			}

			if (p.getComponentCount() == 15) {
				timePanel.add(p, "");
				p = new JPanel(new GridLayout(0, 1));
			}
		}

		if (p.getComponentCount() > 0) {
			timePanel.add(p, "");
			while (p.getComponentCount() != 15)
				p.add(new JLabel());
		}
	}
}
