package view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Mileage extends BaseFrame {

	DefaultTableModel m = model("no,income,expense,total".split(","));
	JTable t = table(m);

	public Mileage() {
		super("마일리지", 400, 300);
		add(scrollpane(t));

		var d = getrows(
				"SELECT mi_no, mi_income, mi_expense, (select sum(m2.mi_income - m2.mi_expense) from mileage m2 where m2.mi_no <= m1.mi_no and m2.m_no = m1.m_no) FROM airline.mileage m1 where m1.m_no = ?",
				BasePage.member.get(0));

		for (var r : d) {
			r.set(1, cint(r.get(1)) == 0 ? "-" : df.format(cint(r.get(1))));
			r.set(2, cint(r.get(2)) == 0 ? "-" : df.format(cint(r.get(2))));
			r.set(3, cint(r.get(3)) == 0 ? "-" : df.format(cint(r.get(3))));
		}

		addrow(d, m);

		add(lbl("총 마일리지:" + (d.isEmpty() ? 0 : t.getValueAt(t.getRowCount() - 1, 3)), JLabel.RIGHT), "South");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

}
