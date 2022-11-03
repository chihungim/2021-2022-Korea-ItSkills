package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Reserve extends BasePage {

	class Airport {
		String no, name;

		public Airport(String no, String name) {
			this.no = no;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	JComboBox combo[] = { new JComboBox<Airport>(), new JComboBox<Airport>() };

	JTextField txt[] = { new JTextField(15), new JTextField(15) };

	public Reserve() {
		add(c = new JPanel(new GridBagLayout()));
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		for (var r : getrows("select a_no, a_name from airport")) {
			var a = new Airport(r.get(0) + "", r.get(1) + "");
			combo[0].addItem(a);
		}
		combo[0].setSelectedIndex(-1);
		combo[0].addItemListener(i -> {
			combo[1].removeAllItems();
			if (i.getStateChange() == ItemEvent.SELECTED) {
				for (var r : getrows("select a_no, a_name from airport where a_no <> ?",
						((Airport) combo[0].getSelectedItem()).no)) {
					combo[1].addItem(new Airport(r.get(0) + "", r.get(1) + ""));
				}
			}
		});

		var cap = "출발지,도착지,출발날짜,도착날짜".split(",");
		var icon = " , ,달력,사람".split(",");

		JComponent jc[] = { combo[0], combo[1], txt[0], txt[1] };

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 80, 30), "West");
			temp.add(jc[i]);
			var l = new JLabel(getIcon("./datafiles/" + icon[i] + ".png", 50, 50));
			temp.add(sz(l, 50, 50), "East");
			var cur = icon[i];
			if (!icon[i].isEmpty()) {
				evt(l, a -> {
					if (cur.equals("달력")) {
						new Calc(txt[0]).setVisible(true);
					} else {
						new Companion(txt[1]).setVisible(true);
					}
				});
			}
			cc.add(temp);
		}

		s.add(btn("확인", a -> {
			if (combo[0].getSelectedIndex() == -1 || combo[1].getSelectedIndex() == -1 || txt[0].getText().isEmpty()
					|| txt[1].getText().isEmpty()) {
				emsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
				return;
			}
			var rs = getrows("select * from schedule where s_depart = ? and s_arrival = ?",
					((Airport) combo[0].getSelectedItem()).no, ((Airport) combo[1].getSelectedItem()).no);

			if (rs.isEmpty()) {
				emsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
				return;
			}
			int type = 1;
			companions = new ArrayList<Object[]>();
			for (var t : txt[1].getText().split(",")) {
				for (var j = 0; j < cint(t.split("-")[1]); j++)
					companions.add(new Object[] { j + 1, null, null, null, null, type });
				type++;
			}
			date = txt[0].getText();
			depart = ((Airport) combo[0].getSelectedItem()).no;
			arrival = ((Airport) combo[1].getSelectedItem()).no;
			mf.swap(new ScheduleSelect());
		}));
	}
}
