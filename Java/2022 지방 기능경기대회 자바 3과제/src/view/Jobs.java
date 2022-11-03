package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class Jobs extends BaseFrame {

	{
		gender[0] = "전체";
	}

	JTextField txt[] = { new JTextField(15), new JTextField(15) };
	JComboBox combo[] = { new JComboBox<String>(local), new JComboBox<String>(graduate),
			new JComboBox<String>(gender) };

	JButton btn[] = { new JButton("검색하기"), new JButton("지원하기") };

	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);

	public Jobs() {
		super("채용정보", 1000, 800);
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(hylbl("채용정보", 0, 30), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		var cap = "공고명,직종".split(",");

		for (int i = 0; i < 3; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (i < 2) {
				temp.add(sz(lbl(cap[i], 0, 15), 60, 30));
				temp.add(txt[i]);
			} else {
				temp.setLayout(new BorderLayout());
				var tmp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var tmp2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				temp.add(tmp1);
				temp.add(tmp2, "East");

				tmp1.add(sz(lbl("지역", 0, 15), 60, 30));
				tmp1.add(combo[0]);
				tmp1.add(sz(lbl("학력", 0, 15), 60, 30));
				tmp1.add(combo[1]);
				tmp1.add(sz(lbl("성별", 0, 15), 60, 30));
				tmp1.add(combo[2]);

				for (var b : btn)
					tmp2.add(b);
			}

			nc.add(temp);
		}
		combo[1].insertItemAt("전체", 0);
		combo[1].setSelectedIndex(0);

		txt[1].setEnabled(false);
		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Category(txt[1]).addWindowListener(new before(Jobs.this));
			}
		});

		btn[1].setEnabled(false);
		btn[0].addActionListener(a -> search());
		btn[1].addActionListener(a -> {
			setValues("insert applicant values(0,?,?,0)", t.getValueAt(t.getSelectedRow(), 8), uno);
			imsg("신청이 완료되었습니다.");
			search();
			btn[1].setEnabled(false);
		});

		add(new JScrollPane(t));

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				var r = t.getSelectedRow();
				btn[1].setEnabled(false);
				if (r == -1)
					return;

				new Avaliable(t.getValueAt(r, 8) + "", btn[1]).addWindowListener(new before(Jobs.this));
			};
		});

		search();

		int w[] = { 80, 80, 60, 120, 40, 0 };
		int idx[] = { 0, 2, 3, 6, 7, 8 };

		for (int i = 0; i < idx.length; i++) {
			t.getColumnModel().getColumn(idx[i]).setMinWidth(w[i]);
			t.getColumnModel().getColumn(idx[i]).setMaxWidth(w[i]);
		}

		t.setRowHeight(80);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		uno = "1";
		new Jobs();
	}

	void search() {
		String sql = "select \r\n"
				+ "	c_name,\r\n"
				+ "    e_title,\r\n"
				+ "    concat((select count(*) from applicant a where a.e_no = e.e_no and (a_apply = 0 or a_apply = 1)), '/', e_people),\r\n"
				+ "    format(e_pay, '#,##0'),\r\n"
				+ "    c_category,\r\n"
				+ "    c_address,\r\n"
				+ "    e_graduate,\r\n"
				+ "    e_gender,\r\n"
				+ "    e_no\r\n"
				+ "from \r\n"
				+ "	company c, employment e\r\n"
				+ "where \r\n"
				+ "	c.c_no = e.c_no\r\n"
				+ "	and (e_people <>  (select count(*) from applicant a where a.e_no = e.e_no and (a_apply = 0 or a_apply = 1)))";

		if (!txt[1].getText().isEmpty()) {
			var tmp = txt[1].getText().split(",");
			sql += " and concat(',', c_category, ',') regexp ',("
					+ String.join("|",
							Arrays.stream(tmp).map(a -> Arrays.asList(category).indexOf(a) + "").toArray(String[]::new))
					+ "),'";
		}

		if (combo[0].getSelectedIndex() != 0) {
			sql += " and left(c_address,2) = '" + combo[0].getSelectedItem() + "'";
		}
		if (combo[1].getSelectedIndex() != 0) {
			sql += " and e_graduate = '" + (combo[1].getSelectedIndex() - 1) + "'";
		}
		if (combo[2].getSelectedIndex() != 0) {
			sql += " and e_gender = '" + (combo[2].getSelectedIndex()) + "'";
		}

		sql += " and e_title like '%" + txt[0].getText() + "%'";

		var rs = getValues(sql);

		if (rs.isEmpty()) {
			emsg("검색결과가 없습니다.");
			combo[0].setSelectedIndex(0);
			combo[1].setSelectedIndex(0);
			combo[2].setSelectedIndex(0);
			txt[0].setText("");
			txt[1].setText("");
			search();
			return;
		}

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
