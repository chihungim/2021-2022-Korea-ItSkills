package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	JComboBox<String> combo;
	JTextField txt;
	DefaultTableModel m = model("지역".split(","));
	JTable t = table(m);

	public Search() {
		super("검색", 1000, 600);
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(lbl("방탈출 카페 검색", JLabel.LEFT, 30), "North");
		n.add(nc = new JPanel(new FlowLayout(FlowLayout.RIGHT)));

		nc.add(lbl("장르", 0));
		nc.add(combo = new JComboBox<String>("전체,추리,공포,스릴러,모험,감성,코미디,성인".split(",")));
		nc.add(lbl("테마", 0));
		nc.add(txt = new JTextField(15));
		nc.add(btn("검색", a -> search()));

		m.addRow(new Object[] { "전체" });

		for (var w : getValues("select a_name from area")) {
			m.addRow(new Object[] { w.get(0) });
		}

		t.setRowSelectionInterval(0, 0);

		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(sz(new JScrollPane(t), 100, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));
		search();
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void search() {
		cc.removeAll();

		String sql = "select left(c_name, instr(c_name, ' ')-1), c_name, c_no from cafe c , theme t where find_in_set(t.t_no, c.t_no) > 0 ";

		if (t.getSelectedRow() != 0)
			sql += " and c.a_no = " + t.getSelectedRow();

		if (combo.getSelectedIndex() != 0)
			sql += " and t.g_no = " + combo.getSelectedIndex();

		sql += " and t_name like '%" + txt.getText() + "%' group by c.c_no";

		var rs = getValues(sql);

		if (rs.isEmpty()) {
			emsg("검색결과가 없습니다.");
			combo.setSelectedIndex(0);
			t.setRowSelectionInterval(0, 0);
			search();
			return;
		}

		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			var img = new JLabel(getIcon("./datafiles/지점/" + r.get(0) + ".jpg", 270, 150));
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2)
						new Branch(r.get(2) + "").addWindowListener(new before(Search.this));
				}
			});
			tmp.add(img);
			tmp.add(lbl(r.get(1) + "", 0), "South");
			cc.add(tmp);
			tmp.setBorder(new LineBorder(Color.BLACK));
		}

		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		new Search();
	}
}
