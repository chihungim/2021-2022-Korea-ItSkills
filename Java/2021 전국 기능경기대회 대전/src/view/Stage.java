package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Stage extends BaseFrame {

	JLabel seats[][] = new JLabel[6][10];
	JLabel people[] = new JLabel[5];
	JLabel sumlbl;
	JPanel basketcc = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final String ABCDEF[] = "A,B,C,D,E,F".split(",");
	int pcnt = 1;
	static int sum = 0;
	ArrayList<BasketItem> list = new ArrayList<>() {
		public int indexOf(Object o) {
			for (int i = 0; i < super.size(); i++) {
				if (super.get(i).seat.equals(o)) {
					return super.indexOf(super.get(i));
				}
			}
			return -1;

		};
	};
	/*
	 * pinfo O009 오페라카니발2021 용인포은아트홀 110000 김승직 2021-09-16
	 */
	JButton btn[] = new JButton[2];

	public Stage() {
		super("좌석", 1200, 700);
		var seatp = new JPanel(new BorderLayout());
		var seatc = new JPanel(new BorderLayout());
		var seatcc = new JPanel(new GridLayout(0, 11, 5, 5));
		var basketp = new JPanel(new BorderLayout());
		var basketc = new JPanel(new BorderLayout());
		var basketcn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var baskets = new JPanel(new GridLayout(0, 2, 5, 5));

		add(setB(seatp, new EmptyBorder(5, 5, 5, 5)));
		add(setB(sz(basketp, 300, 1), new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5))),
				"East");

		seatp.add(seatc);
		seatc.add(seatcc);
		basketp.add(setB(basketc, new TitledBorder(new LineBorder(Color.BLACK), "선택한 좌석")));
		basketp.add(baskets, "South");
		basketc.add(basketcn, "North");
		basketc.add(basketcc);

		seatp.add(
				setB(lbl("STAGE", JLabel.CENTER, 40),
						new CompoundBorder(new MatteBorder(0, 0, 4, 0, Color.BLACK), new EmptyBorder(5, 5, 5, 5))),
				"North");
		seatc.add(lbl(pinfo[6], JLabel.RIGHT, 15), "North");

		for (int i = 0; i < seats.length; i++) {

			seatcc.add(lbl(ABCDEF[i], JLabel.CENTER, 15));
			for (int j = 0; j < seats[i].length; j++) {
				var seat = seats[i][j] = new JLabel(j + 1 + "", JLabel.CENTER);
				seatcc.add(seat);
				seat.setName(ABCDEF[i] + ":" + String.format("%02d", j + 1));
				seat.setOpaque(true);
				seat.setBorder(new LineBorder(Color.BLACK));
				seat.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var me = (JLabel) e.getSource();
						if (me.getBackground().equals(Color.LIGHT_GRAY))
							return;

						if (!me.getBackground().equals(Color.ORANGE)) {
							addItem(me);
						} else if (me.getBackground().equals(Color.ORANGE)) {
							removeItem(me);
						}
						super.mousePressed(e);
					}
				});
			}
		}

		try {
			var rs = stmt.executeQuery("select * from ticket where p_no = " + pinfo[0]);
			while (rs.next()) {
				for (var s : rs.getString(4).split(",")) {
					int r = Arrays.binarySearch(ABCDEF, s.substring(0, 1));
					int c = toInt(s.substring(1)) - 1;
					seats[r][c].setBackground(Color.LIGHT_GRAY);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		basketp.add(
				setB(lbl(pinfo[2], JLabel.LEFT, 20),
						new CompoundBorder(new MatteBorder(0, 0, 4, 0, Color.BLACK), new EmptyBorder(5, 5, 5, 5))),
				"North");
		basketcn.add(lbl("인원수:", JLabel.LEFT));

		for (int i = 0; i < people.length; i++) {
			if (i == 0)
				people[i] = lbl("●", JLabel.CENTER);
			else
				people[i] = lbl("○", JLabel.CENTER);
			people[i].setName((i + 1) + "");
			people[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();

					if (!me.isEnabled())
						return;

					for (int i = 0; i < people.length; i++) {
						people[i].setText("○");
					}

					pcnt = toInt(me.getName());
					for (int i = 0; i < pcnt; i++) {
						people[i].setText("●");
					}
					super.mousePressed(e);
				}
			});
			basketcn.add(people[i]);
		}

		basketc.add(sumlbl = lbl("총금액: " + sum, JLabel.LEFT), "South");

		for (int i = 0; i < btn.length; i++) {
			baskets.add(btn[i] = btn("이전으로,다음으로".split(",")[i], a -> {

				String seats = "", prices = "", options = "";

				for (var v : list) {
					seats += seats.equals("") ? v.seat : "," + v.seat;
					prices += prices.equals("") ? v.title.getText() : "#" + v.title.getText();
					if (v.box[0].isSelected()) {
						options += options.equals("") ? "1" : ",1";
					} else if (v.box[1].isSelected()) {
						options += options.equals("") ? "2" : ",2";
					} else if (v.box[2].isSelected()) {
						options += options.equals("") ? "3" : ",3";
					}
				}

				switch (a.getActionCommand()) {
				case "이전으로":
					dispose();
					break;
				case "다음으로":
					if (list.size() != pcnt) {
						eMsg("인원수에 맞게 선택해주세요.");
						return;
					}
					new Purchase(prices, options).addWindowListener(new before(this));
					break;
				case "취소하기":
					dispose();
					break;
				default:
					execute("update ticket set t_seat = '" + seats + "', t_discount = '" + options + "' where t_no = "
							+ t_no);
					MyPage.refresh();
					dispose();
					break;
				}
			}));
		}

		setVisible(true);
	}

	public Stage(String t_no) {
		this();
		BaseFrame.t_no = toInt(t_no);
		btn[0].setText("취소하기");
		btn[1].setText("수정하기");
		try {
			var rs = stmt.executeQuery("select * from ticket t, perform p where t.p_no = p.p_no and t_no = " + t_no);
			if (rs.next()) {
				pcnt = rs.getString(4).split(",").length;
				for (int i = 0; i < people.length; i++) {
					people[i].setEnabled(false);
				}

				for (int i = 0; i < pcnt; i++) {
					people[i].setText("●");
				}

				String seats[] = rs.getString(4).split(",");
				String discounts[] = rs.getString(5).split(",");
				for (int i = 0; i < discounts.length; i++) {

					var r = Arrays.binarySearch("A,B,C,D,E,F".split(","), seats[i].substring(0, 1));
					var c = toInt(seats[i].substring(1, 3)) - 1;
					if (!discounts[i].isEmpty())
						addItem(this.seats[r][c], toInt(discounts[i]));
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void addItem(JLabel me, int... discount) {
		if (list.size() == 5) {
			eMsg("더이상 선택이 불가능합니다.");
			return;
		}
		me.setBackground(Color.ORANGE);
		var v = new BasketItem(me.getName());
		list.add(v);
		basketcc.add(v);
		if (discount.length > 0)
			v.box[discount[0]].setSelected(true);
		setSum();
		v.setBorder(new LineBorder(Color.BLACK));
		revalidate();
		repaint();
	}

	void removeItem(JLabel me) {
		me.setBackground(null);

		int idx = list.indexOf(me.getName());
		if (idx != -1)
			list.remove(idx);
		basketcc.removeAll();
		list.forEach(basketcc::add);
		setSum();
		revalidate();
		repaint();
	}

	void setSum() {
		sum = 0;
		list.forEach(el -> {
			sum += el.price;
		});
		sumlbl.setText("총금액:" + df.format(sum));
	}

	class BasketItem extends JPanel {
		JCheckBox[] box = { new JCheckBox("청소년 할인 20%"), new JCheckBox("어린이 할인 40%"), new JCheckBox("장애인 할인 50%") };
		ButtonGroup bg = new ButtonGroup();
		JLabel options = lbl("▼", 0, 15);
		JLabel title;
		String seat;
		int price = toInt(pinfo[4]);

		public BasketItem(String seat) {
			super(null);
			this.seat = seat;
			add(title = lbl(seat + ":" + df.format(price), JLabel.CENTER));
			add(options);
			options.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (options.getText().equals("▼")) {
						options.setText("▲");
						sz(BasketItem.this, 250, 90);
					} else {
						options.setText("▼");
						sz(BasketItem.this, 250, 20);
					}
				}
			});

			for (int i = 0; i < box.length; i++) {
				add(box[i]);
				box[i].setBounds(10, (20 * (i + 1)), 120, 20);
				bg.add(box[i]);
				box[i].addActionListener(a -> evnt());
			}
			sz(this, 250, 20);
			title.setBounds(70, 1, 100, 20);
			options.setBounds(230, 0, 20, 20);
			setB(this, new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.BLACK)));
		}

		void evnt() {
			if (box[0].isSelected()) {
				price = (int) (toInt(pinfo[4]) * 0.8);
			} else if (box[1].isSelected()) {
				price = (int) (toInt(pinfo[4]) * 0.6);
			} else if (box[2].isSelected()) {
				price = (int) (toInt(pinfo[4]) * 0.5);
			}
			title.setText(seat + ":" + df.format(price));
			sum = 0;
			list.forEach(el -> {
				sum += el.price;
			});

			sumlbl.setText("총금액:" + df.format(sum));
		}
	}
}
