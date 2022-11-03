package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Seat extends BaseFrame {

	CSpinner[] spinner = new CSpinner[4];
	int price[] = { 15000, 13000, 10000, 7000 };

	ArrayList<Object> movie;
	ArrayList<Object> schedule;
	ArrayList<Object> theater;
	ArrayList<Object> pomaes;
	JLabel pricelbl;
	HashMap<String, JLabel> seatMap = new HashMap<>();
	int sum = 0;

	public Seat() {
		super("좌석", 1000, 600);

		movie = getrows("select * from movie where m_no = ?", mno).get(0);
		schedule = getrows("select * from schedule where sc_no = ?", scno).get(0);
		theater = getrows("select * from theater where t_no = ?", tno).get(0);
		pomaes = getrows("select * from pomaes where p_no = ?", schedule.get(3)).get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(nw = newJPanel(new BorderLayout()), "West");
		n.add(nc = newJPanel(new FlowLayout(FlowLayout.RIGHT)));

		nw.add(new JLabel(
				getIcon("./datafile/영화/" + movie.get(5).toString().replaceAll("[\r\n]", "") + ".jpg", 60, 100)),
				"West");
		var nwc = newJPanel(new GridLayout(0, 1));
		nw.add(nwc);

		var title = lbl(movie.get(5) + " (" + pomaes.get(1) + ")", JLabel.LEFT, 13);
		title.setIcon(getIcon("./datafile/아이콘/" + movie.get(2).toString().replaceAll("[\r\n]", "") + ".png", 30, 30));
		var temp = newJPanel(new FlowLayout(FlowLayout.LEFT));
		temp.add(lbl(LocalDate.parse(schedule.get(5) + "", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				.format(DateTimeFormatter.ofPattern("yy.MM.dd(EEE)")), JLabel.LEFT));
		temp.add(mkBar(13));
		temp.add(lbl(schedule.get(6) + "", JLabel.LEFT));
		var lbl3 = lbl(theater.get(1) + "ㆍ" + schedule.get(4), JLabel.LEFT);
		nwc.add(title);
		nwc.add(temp);
		nwc.add(lbl3);

		var cap = "성인,청소년,시니어,장애인".split(",");

		for (int i = 0; i < cap.length; i++) {
			spinner[i] = new CSpinner(0, 8, 0) {
				@Override
				void cValue(int dir) {
					if (spinner[0].value + spinner[1].value + spinner[2].value + spinner[3].value + dir > 8)
						return;
					super.cValue(dir);
					sum = 0;

					for (int i = 0; i < 4; i++)
						sum += price[i] * spinner[i].value;
					pricelbl.setText("총 합계:" + df.format(sum) + "원");
				};
			};
			sz(spinner[i], 80, 30);
			nc.add(lbl(cap[i], 0));
			nc.add(spinner[i]);
		}

		add(c = new JPanel(new GridLayout(0, 11, 5, 5)));

		String[] alph = "A,B,C,D,E,F".split(",");

		var reserved = getrows("select r_seat from reservation where sc_no = ?", scno).stream()
				.flatMap(a -> Arrays.stream(a.get(0).toString().split(","))).map(a -> a.trim())
				.collect(Collectors.toList());
		System.out.println(reserved);
		System.out.println(reserved);
		for (int i = 0; i < alph.length; i++) {
			c.add(sz(lbl(alph[i], JLabel.CENTER), 60, 60)).setForeground(Color.WHITE);
			for (int j = 0; j < 10; j++) {
				var cod = alph[i] + "-" + String.format("%02d", j + 1);
				var l = lbl(j + 1 + "", JLabel.CENTER);
				l.setOpaque(true);
				if (reserved.contains(cod)) {
					l.setBackground(Color.GRAY);
				} else {
					evt(l, a -> {
						if (l.getName() == null) {
							l.setName(cod);
							l.setBackground(Color.red);
						} else {
							l.setName(null);
							l.setBackground(Color.WHITE);
						}
					});
					l.setBackground(Color.WHITE);
				}
				seatMap.put(cod, l);

				c.add(l);
			}
		}

		add(s = new JPanel(new BorderLayout()), "South");

		s.add(pricelbl = lbl("총 합계 0원", JLabel.LEFT, 15));

		s.add(rbtn("결제하기", a -> {
			if (sum == 0) {
				emsg("인원을 선택해주세요.");
				return;
			}

			if (seatMap.entrySet().stream().map(x -> x.getValue()).filter(x -> x.getName() != null).count() == 0) {
				emsg("좌석을 선택해주세요.");
				return;
			}

			var lst = new ArrayList<String>();
			for (int i = 0; i < 4; i++) {
				if (spinner[i].value != 0)
					lst.add(i + 1 + "");
			}

			imsg("예매가 완료되었습니다.");
			execute("insert reservation values(0,?,?,?,?,?,?,?)", uno, String.join(",", lst), scno,
					seatMap.entrySet().stream().map(x -> x.getValue()).filter(x -> x.getName() != null).count(),
					seatMap.entrySet().stream().map(x -> x.getValue()).filter(x -> x.getName() != null)
							.map(x -> x.getName()).collect(Collectors.joining(", ")),
					LocalDate.now(), LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
		}), "East");
		c.setBackground(Color.BLACK);
		s.setBackground(Color.GRAY);
		n.setBackground(Color.WHITE);

		c.setBorder(new EmptyBorder(50, 200, 50, 200));

		setVisible(true);
	}

}
