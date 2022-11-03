package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Search extends BaseFrame {

	JTextField txt = new JTextField(15);

	DefaultListModel<String> model = new DefaultListModel<>();
	JList<String> list = new JList<String>(model);
	JPanel ccc;
	HashMap<String, ImageIcon> imgMap = new HashMap<>();

	public Search() {
		super("검색", 1200, 600);
		add(n = newJPanel(new BorderLayout()), "North");
		add(c = newJPanel(new BorderLayout()));

		for (var r : getrows("select * from base"))
			imgMap.put(r.get(0) + "", getBlob(r.get(5), 200, 100));
		c.add(cn = newJPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		c.add(cc = newJPanel(new BorderLayout()));

		cn.add(lbl(cint(user.get(5)) == 1 ? "과일" : "야채", JLabel.CENTER));
		cn.add(txt);
		cn.add(btn("검색", a -> search(txt.getText())));

		txt.addKeyListener(new KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent e) {
				setList();
			};
		});

		var temp = newJPanel(new BorderLayout());
		temp.add(lbl("관련 검색어", JLabel.CENTER), "North");
		temp.add(list);

		cc.add(sz(temp, 250, 0), "West");
		cc.add(new JScrollPane(ccc = newJPanel(new GridLayout(0, 3, 5, 5))));
		ccc.setBackground(Color.WHITE);
		evt(list, a -> {
			if (list.getSelectedIndex() == -1)
				return;
			search(list.getSelectedValue());
		});
		search("");
		setVisible(true);
	}

	public static void main(String[] args) {
		디버그("1");
		new Search();
	}

	void search(String keyword) {
		ccc.removeAll();
		var rs = getrows(
				"select f.*, b.* from farm f, base b where f.b_no = b.b_no and b.division <> ? and b.b_name like ? order by  f.u_no, b.b_no",
				user.get(5), "%" + keyword + "%");
		for (var r : rs) {
			var user = getrows("select * from user where u_no = ?", r.get(1) + "").get(0);
			var town = getrows("Select * from town where t_no = ?", user.get(6)).get(0);
			var city = getrows("Select * from city where c_no = ?", town.get(1) + "").get(0);
			var img = new JLabel(user.get(1) + "(" + city.get(3) + "," + town.get(4) + ")" + decformat(r.get(3)) + "원",
					imgMap.get(r.get(5) + ""), JLabel.CENTER);
			img.setVerticalTextPosition(JLabel.BOTTOM);
			img.setHorizontalTextPosition(JLabel.CENTER);
			img.setBorder(new LineBorder(Color.BLACK));
			evt(img, a -> new Purchase(r).addWindowListener(new before(this)));
			ccc.add(img);
		}

		while (ccc.getComponentCount() < 6)
			ccc.add(new JLabel());

		revalidate();
		repaint();
	}

	void setList() {
		model.clear();
		if (txt.getText().isEmpty())
			return;
		var rs = getrows("select b_name from farm f , base b where b.b_no = f.b_no and b.b_name like '%" + txt.getText()
				+ "%' and division <> ? group by b_name", user.get(5));
		for (var r : rs)
			model.addElement(r.get(0).toString());

	}
}
