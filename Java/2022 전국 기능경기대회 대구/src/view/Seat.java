package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Seat extends BaseFrame {

	String tno;
	ArrayList<Object> movie;
	JLabel seat[][] = new JLabel[10][10];
	JLabel infolbl;
	JLabel costlbl1, costlbl2;
	int[] cnt = new int[3];
	LocalDateTime d;
	String info;
	int sum = 0;
	int price[] = { 14000, 10000, 5000 };

	public Seat(String name, ArrayList<Object> r, LocalDateTime d, String tno) {
		super("좌석선택", 1300, 800);
		this.d = d;
		this.movie = r;
		this.tno = tno;
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.setBackground(Color.DARK_GRAY);
		var screen = lbl("SCREEN", JLabel.CENTER, 15);
		b(screen, Color.LIGHT_GRAY);

		c.add(sz(screen, 0, 50), "North");
		c.add(cc = newJPanel(new GridLayout(0, 11, 5, 5)));

		var f = "A,B,C,D,E,F,G,H,I,J".split(",");

		var reserved = getrows(
				"Select r_seat from reservation where r_time = ? and r_date = ? and m_no = ? and t_no = ?",
				d.toLocalTime() + "", d.toLocalDate() + "", r.get(0), tno).stream().map(a -> a.get(0) + "")
				.flatMap(a -> Arrays.stream(a.split("\\."))).collect(Collectors.toList());

		for (int i = 0; i < f.length; i++) {
			for (int j = 0, k = 0; j < 11; j++) {
				if (j != 2) {
					var code = f[i] + String.format("%02d", k + 1);
					boolean flag = reserved.contains(code);

					var l = new JLabel(code, JLabel.CENTER) {
						protected void paintComponent(java.awt.Graphics g) {
							super.paintComponent(g);
							var g2 = tog2d(g);
							g2.setStroke(new BasicStroke(2f));
							if (flag) {
								g2.setColor(Color.white);
								g2.drawLine(0, 0, getWidth(), getHeight());
								g2.drawLine(getWidth(), 0, 0, getHeight());
							}
						};
					};

					seat[i][k] = l;
					if (!flag) {
						l.addMouseListener(new MouseAdapter() {
							public void mousePressed(java.awt.event.MouseEvent e) {
								var cnt = Arrays.stream(seat).flatMap(Arrays::stream).filter(x -> x.getName() != null)
										.count();
								var max = Seat.this.cnt[0] + Seat.this.cnt[1] + Seat.this.cnt[2];
								if (l.getName() == null) {
									if (max == 0) {
										emsg("관람 인원을 선택해주세요.");
										return;
									}

									if (cnt == max) {
										emsg("이미 좌석을 모두 선택하였습니다.");
										return;
									}

									l.setBackground(red);
									l.setName("a");
								} else {
									l.setBackground(Color.GRAY);
									l.setName(null);
								}
							};

							public void mouseEntered(java.awt.event.MouseEvent e) {
								if (l.getName() == null)
									l.setBackground(red);
								return;
							};

							public void mouseExited(java.awt.event.MouseEvent e) {
								if (l.getName() == null)
									l.setBackground(Color.GRAY);
								return;
							};
						});
					}

					b(l, flag ? Color.LIGHT_GRAY : Color.gray);
					cc.add(l);
					k++;
				} else {
					cc.add(lbl("", 0));
				}
			}
		}

		add(sz(s = new JPanel(new BorderLayout(5, 5)), 0, 170), "South");

		var p1 = newJPanel(new BorderLayout());
		var p2 = newJPanel(new BorderLayout());
		var p3 = newJPanel(new BorderLayout());
		var p4 = newJPanel(new BorderLayout());

		p1.add(new JLabel(getIcon("./지급자료/image/movie/" + movie.get(0) + ".jpg", 100, 150)), "West");
		var p1c = newJPanel(new BorderLayout());
		p1.add(p1c);
		p1c.setBorder(new EmptyBorder(20, 5, 20, 5));
		p1c.add(f(lbl(movie.get(1) + ">", JLabel.LEFT, 13), Color.WHITE), "North");
		p1c.add(f(
				lbl(movie.get(5).toString().equals("1") ? "전체 관람가"
						: movie.get(5).toString().equals("2") ? "12세 이상 관람가" : "15세 이상 관람가", JLabel.LEFT, 13),
				Color.WHITE), "South");

		p2.add(f(infolbl = lbl("", JLabel.LEFT), Color.WHITE));

		var p3c = newJPanel(new GridLayout(0, 1, 5, 5));
		p3.add(p3c);
		p3.add(lbl("<html><font color = 'red'>※최대 8명 선택가능", JLabel.CENTER), "South");
		var cap = "일반,청소년,우대".split(",");

		var lst = new ArrayList<JComponent>();
		for (int i = 0; i < 3; i++) {
			var temp = newJPanel(new BorderLayout());
			temp.add(sz(f(lbl(cap[i], JLabel.LEFT), Color.WHITE), 60, 0), "West");
			var tempc = newJPanel(new GridLayout(1, 0, 5, 5));
			temp.add(tempc);
			final int k = i;
			for (int j = 0; j < 8; j++) {
				var p = new JLabel(j + 1 + "", JLabel.CENTER);
				p.setForeground(Color.LIGHT_GRAY);
				p.setBackground(Color.GRAY);
				p.setOpaque(true);
				final int v = j + 1;
				lst.add(p);
				p.addMouseListener(new MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e) {
						cnt[k] = v;
						System.out.println(cnt[0] + cnt[1] + cnt[2]);
						if (cnt[0] + cnt[1] + cnt[2] > 8) {
							emsg("관람 인원은 최대 8명 입니다.");
							lst.stream().forEach(a -> a.setBackground(Color.GRAY));
							cnt[0] = cnt[1] = cnt[2] = 0;
							setInfo();
							Arrays.stream(seat).flatMap(Arrays::stream)
									.filter(a -> !a.getBackground().equals(Color.LIGHT_GRAY)).forEach(a -> {
										a.setName(null);
										a.setBackground(Color.GRAY);
									});
							return;
						}
						for (var p : tempc.getComponents())
							p.setBackground(Color.GRAY);

						p.setBackground(Color.WHITE);
						setInfo();
					};
				});

				tempc.add(p);
			}

			p3c.add(temp);
		}

		p4.add(f(costlbl1 = lbl("", JLabel.LEFT), Color.WHITE), "West");
		p4.add(f(costlbl2 = lbl("", JLabel.LEFT), Color.white), "East");

		s.add(sc = newJPanel(null));
		sc.setLayout(new BoxLayout(sc, BoxLayout.X_AXIS));
		s.setBackground(Color.BLACK);
		p1.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.WHITE), new EmptyBorder(0, 5, 0, 5)));
		p2.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.WHITE), new EmptyBorder(0, 5, 0, 5)));
		p3.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.WHITE), new EmptyBorder(0, 5, 0, 5)));
		sc.add(sz(p1, 300, 0));
		sc.add(sz(p2, 300, 0));
		sc.add(p3);
		s.setBorder(new EmptyBorder(5, 20, 5, 20));
		sc.add(sz(p4, 200, 0));
		s.setBackground(Color.BLACK);
		c.setBorder(new EmptyBorder(5, 350, 5, 350));
		s.add(sz(rndBtn("<html><center>><br>결제하기", a -> {

			if (cnt[0] + cnt[1] + cnt[2] == 0) {
				emsg("관람 인원을 선택해주세요.");
				return;
			}

			if (cnt[0] + cnt[1] + cnt[2] != Arrays.stream(seat).flatMap(Arrays::stream).filter(x -> x.getName() != null)
					.count()) {
				emsg("관람 인원과 좌석 수가 일치하지 않습니다.");
				return;
			}

			int gr = cint(getrows("select gr_no from user where u_no = ?", uno).get(0).get(0));

			double discount = new double[] { 0, 1, 0.9, 0.85, 0.80, 0.75 }[gr];
			if (JOptionPane.showConfirmDialog(this,
					grade[gr] + "" + (int) ((1 - discount) * 100) + "%할인\n총 "
							+ new DecimalFormat("#,##0").format(sum * discount) + "원 결제하시겠습니까?",
					"질문", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
				execute("insert reservation values(0,?,?,?,?,?,?,?)", uno, movie.get(0), tno,
						d.toLocalDate().toString(), d.toLocalTime().format(DateTimeFormatter.ofPattern("H:mm")),
						Arrays.stream(seat).flatMap(Arrays::stream).filter(x -> x.getName() != null)
								.map(x -> x.getText()).collect(Collectors.joining(".")),
						sum * discount);
				imsg("예매가 완료되었습니다.");
				for (var w : getWindows())
					w.setVisible(w instanceof Main);
			}
		}), 180, 0), "East");
		setInfo();
		setVisible(true);
	}

	void setInfo() {
		infolbl.setText("<html>극장 " + getrows("select t_name from theater where t_no =?", tno).get(0).get(0)
				+ "<br><br>일시" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd(EEE) HH:mm")) + " ~ "
				+ d.plusMinutes(cint(movie.get(4))).format(DateTimeFormatter.ofPattern("HH:mm")) + "<br><br>인원 "
				+ (cnt[0] + cnt[1] + cnt[2]) + "명");
		var cap = "일반,청소년,우대".split(",");
		String str1 = "<html><font color = 'white'>";
		String str2 = "<html><font color = 'white'>";
		sum = 0;
		for (int i = 0; i < price.length; i++) {
			if (cnt[i] == 0)
				continue;
			str1 += cap[i] + "<br><br>";
			str2 += new DecimalFormat("#,##0").format(price[i]) + "원 * " + cnt[i] + "<br><br>";
			sum += price[i] * cnt[i];
		}

		str1 += "총금액";
		str2 += "<font color = 'rgb(" + red.getRed() + "," + red.getGreen() + "," + red.getBlue() + ")'>"
				+ new DecimalFormat("#,##0").format(sum) + "원";

		costlbl1.setText(str1);
		costlbl2.setText(str2);
	}

}
