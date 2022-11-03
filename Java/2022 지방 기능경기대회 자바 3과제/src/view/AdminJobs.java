package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminJobs extends BaseFrame {

	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);

	public AdminJobs() {
		super("관리자 채용정보", 1000, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(ne = new JPanel(new FlowLayout(0)), "East");
		n.add(hylbl("관리자 채용정보", 0, 30));
		n.add(ne, "East");
		ne.add(btn("공고수정", a -> {
			if (t.getSelectedRow() == -1) {
				emsg("수정할 공고를 선택하세요.");
				return;
			}

			new Post(t.getValueAt(t.getSelectedRow(), 8) + "").addWindowListener(new before(this));
		}));

		add(new JScrollPane(t));

		search();

		int w[] = { 80, 80, 60, 120, 40, 0 };
		int idx[] = { 0, 2, 3, 6, 7, 8 };

		for (int i = 0; i < idx.length; i++) {
			t.getColumnModel().getColumn(idx[i]).setMinWidth(w[i]);
			t.getColumnModel().getColumn(idx[i]).setMaxWidth(w[i]);
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				search();
			}
		});

		t.setRowHeight(80);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminJobs();
	}

	void search() {
		String sql = "SELECT \r\n" + "    c_name,\r\n" + "    e_title,\r\n" + "    e_people,\r\n"
				+ "    FORMAT(e_pay, '#,##0'),\r\n" + "    c_category,\r\n" + "    c_address,\r\n"
				+ "    e_graduate,\r\n" + "    e_gender\r\n, e.e_no " + "FROM\r\n" + "    employment e,\r\n"
				+ "    company c\r\n" + "WHERE\r\n" + "    e.c_no = c.c_no\r\n"
				+ "    and (select count(*) from applicant a where a.e_no = e.e_no and (a_apply = 1 or a_apply = 0)) <> e.e_people";

		var rs = getValues(sql);

		for (var r : rs) {
			r.set(0, new JLabel(getIcon("./datafiles/기업/" + r.get(0) + "2.jpg", 80, 80)));
			r.set(4, String.join(",",
					Arrays.stream(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));

			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7))]);
		}
		addRow(m, rs);
	}
}
