package view;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Reservation extends BasePage {
	DefaultTableModel m = model("번호,날짜,출발지,도착지,결제금액".split(","));
	JTable t = table(m);

	public Reservation() {
		add(scrollpane(t));
		setBorder(new EmptyBorder(20, 20, 20, 20));
		addrow(getrows(
				"select r_no, concat(r_date, ' ', s_time), a1.a_name, a2.a_name, format(r_price, '#,##0') from airport a1, airport a2, reservation r, schedule s where s.s_depart =  a1.a_no and s.s_arrival = a2.a_no and r.s_no = s.s_no and r.m_no = ? order by r_date, time(s_time)",
				member.get(0)), m);
		
	}

	void data() {
	}

	public static void main(String[] args) {
		Base.디버그("1");
		mf.swap(new Reservation());
	}
}
