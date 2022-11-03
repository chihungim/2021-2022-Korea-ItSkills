package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Mypage extends BaseFrame {

	DefaultTableModel m = model("날짜,시간,카페 이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m);
	JComboBox<String> combo;
	JLabel sumlbl;

	public Mypage() {
		super("마이페이지", 800, 400);
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(ne = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "East");
		n.add(nw = new JPanel(new FlowLayout(FlowLayout.LEFT)), "West");
		nw.add(lbl("날짜:", 0));
		nw.add(combo = new JComboBox<String>());

		ne.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1)
				return;

			var d = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0) + "",
					DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			var ti = LocalTime.parse(t.getValueAt(t.getSelectedRow(), 1) + "", DateTimeFormatter.ofPattern("HH:mm"));

			var dti = LocalDateTime.of(d, ti);

			if (dti.isBefore(LocalDateTime.now())) {
				emsg("지난 예약은 삭제할 수 없습니다");
				return;
			}
			setValues("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 6));
			imsg("삭제가 완료되었습니다");
			data();
		}));

		combo.addItem("전체");

		for (int i = 1; i <= 12; i++) {
			combo.addItem(i + "월");
		}

		add(new JScrollPane(t));
		add(sumlbl = lbl("", JLabel.RIGHT), "South");
		combo.addItemListener(i -> {
			if (i.getStateChange() == 1)
				data();
		});

		data();

		int idx[] = { 0, 1, 4, 5, 6 };
		int w[] = { 100, 100, 80, 100, 0 };

		for (int i = 0; i < idx.length; i++) {
			t.getColumnModel().getColumn(idx[i]).setMinWidth(w[i]);
			t.getColumnModel().getColumn(idx[i]).setMaxWidth(w[i]);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void data() {
		String sql = "select r_date, r_time, c_name, t_name, r_people, format(c_price*r_people, '#,##0'),r_no from reservation r, theme t , cafe c where r.c_no = c.c_no and t.t_no = r.t_no and r.u_no = "
				+ uno;
		String sumSql = "select format(sum(c_price*r_people), '#,##0') from reservation r, theme t , cafe c where r.c_no = c.c_no and t.t_no = r.t_no and r.u_no = "
				+ uno;
		String chkSql = "select format(sum(c_price*r_people), '#,##0') from reservation r, theme t , cafe c where r.c_no = c.c_no and t.t_no = r.t_no and r.u_no = "
				+ uno;

		if (combo.getSelectedIndex() != 0) {
			sql += " and month(r_date) = " + combo.getSelectedIndex();
			sumSql += " and month(r_date) = " + combo.getSelectedIndex();
		}

		var rs = getValues(sql);

		if (getValue(chkSql).get(0) != null && rs.isEmpty()) {
			emsg("예약현황이 없습니다.");
			combo.setSelectedIndex(0);
			data();
			return;
		}
		if (getValue(sumSql).get(0) == null) {
			sumlbl.setText("총 금액:0");
		} else {
			sumlbl.setText("총 금액:" + getValue(sumSql).get(0) + "");
		}

		addRow(m, rs);
	}

	public static void main(String[] args) {
		uno = "1";
		new Mypage();
	}
}
