package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Storage extends BasePage {

	Boolean b;
	DefaultTableModel m = model("아이템,게임명,아이템명,s_no,i_no".split(","));
	JTable t = table(m);
	JTextField txt = new JTextField();

	public Storage(boolean b) {
		super(b ? "보관함" : "장터");
		this.b = b;
		update();
	}

	JPanel p2c;
	JLabel icon = new JLabel(getIcon("./datafiles/기본사진/9.png", 30, 30));

	@Override
	void update() {
		removeAll();
		add(lbl("경험치:" + user.exp() + "(등급:" + g_gd[user.gd()] + ")", JLabel.RIGHT, 15), "North");
		add(c = new JPanel(new GridLayout(1, 0, 50, 20)));

		var p1 = new JPanel(new BorderLayout());
		var p2 = new JPanel(new BorderLayout());

		c.add(p1);
		c.add(p2);

		p1.add(lbl("보관함", JLabel.CENTER, 25), "North");
		p1.add(new JScrollPane(t));
		var p1s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p1.add(p1s, "South");
		p1s.add(bbtn("등록하기", a -> {
			if (t.getSelectedRow() == -1) {
				emsg("등록할 아이템을 선택하세요.");
				return;
			}
			new Market(t.getValueAt(t.getSelectedRow(), 3) + "", t.getValueAt(t.getSelectedRow(), 4) + "")
					.setVisible(true);
		}));
		var d = getrows(
				"select i_img, g.g_name ,i_name, s.s_no, i.i_no  from storage s , item i, game g where s.i_no = i.i_no and s.s_no not in (select s_no from market) and s.u_no = ? and i.g_no = g.g_no",
				user.u_no);
		for (var dd : d) {
			dd.set(0, new JLabel(getBlob(dd.get(0), 80, 80)));
		}

		t.setRowHeight(80);

		var col = "s_no,i_no,게임명".split(",");
		int w[] = { 0, 0, 120 };

		for (int i = 0; i < w.length; i++) {
			t.getColumn(col[i]).setMinWidth(w[i]);
			t.getColumn(col[i]).setMaxWidth(w[i]);
		}

		addrow(d, m);
		p2.add(new JScrollPane(p2c = new JPanel(new GridLayout(0, 1))));
		if (b) {
			p2.add(lbl("아이템세트", JLabel.CENTER, 25), "North");

			for (var g : getrows("Select * from game where g_ox = 1")) {
				var temp = new JPanel(new GridLayout(1, 0));
				int cnt = 0;
				for (var i : getrows("select * from item where g_no = ?", g.get(0))) {
					var item = new JLabel(getBlob(i.get(3), 80, 80));
					item.setEnabled(false);
					if (!getrows(
							"select distinct(i_no) from storage where u_no = 1 and s_no not in (select s_no from market) and i_no = ?",
							i.get(0)).isEmpty()) {
						cnt++;
						item.setEnabled(true);
					}
					temp.add(item);
				}
				temp.setBackground(Color.WHITE);
				if (cnt > 2) {
					temp.setBackground(Color.YELLOW);

				}
				temp.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.BLACK), g.get(2) + ""),
						new EmptyBorder(5, 5, 5, 5)));
				p2c.add(temp);
			}

		} else {
			var p2n = new JPanel(new BorderLayout());
			p2n.add(lbl("검색", 0), "West");
			p2n.add(txt);
			p2n.add(icon, "East");

			p2.add(p2n, "North");
			evt(icon, a -> search());
			search();
		}

		p1.setBorder(new LineBorder(Color.BLACK));
		p2.setBorder(new LineBorder(Color.BLACK));

		setBackground(null);
		setBorder(new EmptyBorder(5, 10, 5, 10));
	}

	void search() {
		p2c.removeAll();
		var rs = getrows(
				"select  i.i_img ,g_name, i_name, m_price, m_no ,  s.s_no, i.i_no, s.u_no from market m, item i, game g , storage s where m.m_no not in (select m_no from deal) and m.m_ox = 0 and m.s_no = s.s_no and i.i_no = s.i_no and g.g_no = i.g_no and (g.g_name like ? or i.i_name like ?)",
				"%" + txt.getText() + "%", "%" + txt.getText() + "%");
		for (var r : rs) {
			var temp = new JPanel(new BorderLayout());
			var poster = new JLabel(getBlob(r.get(0), 100, 120));
			temp.add(poster, "West");
			var tempc = new JPanel(new GridLayout(0, 1));
			temp.add(tempc);

			tempc.add(lbl("게임명:" + r.get(1) + "", JLabel.LEFT))
					.setForeground(cint(r.get(7)) == cint(user.u_no) ? Color.blue : Color.BLACK);
			tempc.add(lbl("아이템명:" + r.get(2) + "", JLabel.LEFT))
					.setForeground(cint(r.get(7)) == cint(user.u_no) ? Color.blue : Color.BLACK);
			tempc.add(lbl("가격:" + decformat(r.get(3)) + "원", JLabel.LEFT))
					.setForeground(cint(r.get(7)) == cint(user.u_no) ? Color.blue : Color.BLACK);
			p2c.add(temp);

			evt(temp, a -> {
				if (a.getButton() == 3) {
					var pop = new JPopupMenu();
					var item1 = new JMenuItem("구매하기");
					var item2 = new JMenuItem("판매취소");

					pop.add(item2);
					pop.add(item1);
					item2.setEnabled(cint(r.get(7)) == cint(user.u_no));
					item1.setEnabled(cint(r.get(7)) != cint(user.u_no));
					item2.addActionListener(x -> {
						execute("delete from market where m_no = ?", r.get(4));
						mf.updateALL();
					});

					item1.addActionListener(
							x -> new Market(r.get(4) + "", r.get(5) + "", r.get(6) + "", r.get(7) + "", cint(r.get(3)))
									.setVisible(true));
					pop.add(item1);
					pop.add(item2);
					pop.show(temp, a.getX(), a.getY());
				}
			});

			temp.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		}

		while (p2c.getComponentCount() < 4)
			p2c.add(new JLabel());
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		디버그("1");
		new Storage(false);
		mf.setVisible(true);
	}

}
