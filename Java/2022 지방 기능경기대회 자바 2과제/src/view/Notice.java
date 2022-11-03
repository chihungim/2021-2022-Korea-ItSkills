package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.Flow;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Notice extends BaseFrame {
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = table(m);
	ArrayList<ArrayList<ArrayList<Object>>> data = new ArrayList<ArrayList<ArrayList<Object>>>();
	int pageidx;
	JLabel pagelbl, datelbl;
	JButton edit, prev, next;
	JTextField txt, txt2;
	JComboBox<String> combo;
	JTextArea ta;

	public Notice() {
		super("게시판", 800, 400);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(nw = new JPanel(new FlowLayout(FlowLayout.LEFT)), "West");
		n.add(ne = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "East");

		nw.add(pagelbl = lbl("", JLabel.LEFT, 15));
		nw.add(prev = new JButton("◀"));
		nw.add(next = new JButton("▶"));

		ne.add(lbl("분류:", JLabel.CENTER));
		ne.add(combo = new JComboBox<String>("제목,아이디".split(",")));
		ne.add(txt = new JTextField(10));
		for (var bc : "검색,게시물 작성".split(",")) {
			ne.add(btn(bc, a -> {
				if (a.getActionCommand().equals("게시물 작성")) {
					new Post(this).addWindowListener(new before(this));
				} else {
					search();
				}
			}));
		}

		prev.addActionListener(a -> changePage(pageidx - 1));
		next.addActionListener(a -> changePage(pageidx + 1));

		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(new JScrollPane(t));
		c.add(sz(ce = new JPanel(new BorderLayout()), 200, 0), "East");

		var cen = new JPanel(new BorderLayout(5, 5));

		ce.add(cen, "North");
		cen.add(lbl("제목:", JLabel.RIGHT, 15), "West");
		cen.add(txt2 = new JTextField());
		cen.add(edit = btn("수정", a -> {
			setValues("update notice set n_title= ? , n_content = ? where n_no = ?", txt2.getText(), ta.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			imsg("수정이 완료되었습니다.");
			ce.setVisible(false);
			setSize(800, 400);
			search();
		}), "East");

		ce.add(ta = new JTextArea());
		ce.add(datelbl = lbl("", JLabel.RIGHT), "South");

		ce.setVisible(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				var r = t.getSelectedRow();
				if (r == -1)
					return;

				ce.setVisible(!ce.isVisible());

				if (!ce.isVisible()) {
					setSize(800, 400);
				} else {
					setSize(1000, 400);
				}

				txt2.setText(t.getValueAt(r, 1) + "");
				ta.setText(getValue("select n_content from notice where n_no =?", t.getValueAt(r, 0)).get(0) + "");
				datelbl.setText("작성일:" + t.getValueAt(r, 3) + "");
				edit.setVisible(t.getValueAt(t.getSelectedRow(), 2).toString().equals(uid));
				ta.setEnabled(t.getValueAt(t.getSelectedRow(), 2).toString().equals(uid));
				txt2.setEnabled(t.getValueAt(t.getSelectedRow(), 2).toString().equals(uid));
			};
		});

		t.setRowHeight(25);

		search();

		setVisible(true);
	}
	
	void changePage(int page) {
		pageidx = page;
		prev.setEnabled(pageidx > 0);
		next.setEnabled(pageidx < data.size() - 1);
		
		if (pageidx <= 0)
			pageidx = 0;
		else if (pageidx >= data.size() - 1)
			pageidx = data.size() - 1;

		pagelbl.setText("페이지 정보:" + (pageidx + 1) + "/" + data.size());
		addRow(m, data.get(pageidx));
	}

	void search() {
		String path = " and " + ("n_title like ?,u_id like ?".split(",")[combo.getSelectedIndex()]);
		String sql = "select n.n_no, n.n_title, u_id, n_date, n_viewcount from notice n, user u where n.u_no = u.u_no and (n_open = 1 or u.u_no = 1)"
				+ path;

		data = new ArrayList<ArrayList<ArrayList<Object>>>();
		var rs = getValues(sql, "%" + txt.getText() + "%");

		var temp = new ArrayList<ArrayList<Object>>();

		for (int i = 0; i < rs.size(); i++) {
			temp.add(rs.get(i));
			if (temp.size() == 10) {
				data.add(temp);
				temp = new ArrayList<ArrayList<Object>>();
			}
		}

		if (temp.size() > 0)
			data.add(temp);
		changePage(0);
	}
}
