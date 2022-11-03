package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ScheduleSelect extends BasePage {
	DefaultTableModel m = model("번호,출발지,도착지,출발시간,도착시간,가격,잔여좌석".split(","));
	JTable t = table(m);
	JLabel date, icon = new JLabel(getIcon("./datafiles/달력.png", 50, 50));
	int dist = (int) distance(depart, arrival);
	DateTimeFormatter dt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter dt2 = DateTimeFormatter.ofPattern("MM.dd(EEE)");
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 3) {
				((JComponent) c).setToolTipText((dist / 60) + "시간 " + (dist % 60) + "분 소요");
			}
			return c;
		};
	};

	public ScheduleSelect() {
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(date = lbl("", JLabel.LEFT, 20), "West");
		n.add(icon, "East");
		date.addPropertyChangeListener("name", a -> {
			date.setText(LocalDate.parse(date.getName(), dt1).format(dt2));
			data();
		});
		date.setName(BasePage.date);
		add(scrollpane(t));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		t.getColumn("출발시간").setCellRenderer(dtcr);
		s.add(bbtn("확인", a -> {
			if (t.getSelectedRow() == -1) {
				emsg("항공권을 선택해주세요.");
				return;
			}

			if (cint(t.getValueAt(t.getSelectedRow(), 6)) < companions.size()) {
				emsg("좌석이 부족합니다.");
				return;
			}
			sno = t.getValueAt(t.getSelectedRow(), 0) + "";
			mf.swap(new CompanionInfo());
		}));

		evt(icon, a -> {
			new Calc(date).setVisible(true);
		});
		data();
	}

	void data() {
		var d = getrows(
				"select s_no, a1.a_name, a2.a_name, s_time, s_time, format(s_price, '#,##0'), (select 140 - count(*) from reservation r, companion c where r.s_no = s.s_no and r.r_no = c.r_no and r.r_date = ?) from schedule s , airport a1, airport a2 where s.s_depart = a1.a_no and s.s_arrival = a2.a_no and s.s_depart = ? and s.s_arrival = ? and concat(? ,' ' ,s.s_time) >= now()",
				BasePage.date, depart, arrival, BasePage.date);

		for (var dd : d) {
			dd.set(4, LocalTime.parse(dd.get(4).toString(), DateTimeFormatter.ofPattern("H:mm")).plusMinutes(dist)
					.format(DateTimeFormatter.ofPattern("H:mm")));
		}
		addrow(d, m);

	}
}
