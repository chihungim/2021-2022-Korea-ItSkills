package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MovieList extends BaseFrame {
	JTextField txt;
	JComboBox<String> combo;

	public MovieList() {
		super("무비리스트", 800, 600);
		add(n = newJPanel(new BorderLayout()), "North");
		n.add(nc = newJPanel(new FlowLayout(FlowLayout.CENTER)));

		n.add(lbl("MovieList", JLabel.CENTER, 20), "North");
		nc.add(combo = new JComboBox<String>());

		combo.addItem("전체");
		for (var g : getrows("select * from genre"))
			combo.addItem(g.get(1) + "");

		nc.add(txt = new JTextField(50));
		nc.add(evt(new JLabel(getIcon("./datafile/아이콘/search.png", 25, 25)), a -> {
			search();
		}));

		add(new JScrollPane(c = new JPanel(new GridLayout(0, 4, 5, 5))));
		search();
		c.setBackground(Color.WHITE);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void search() {
		var rs = getrows(
				"select m.* from movie m where m.m_name like ?"
						+ (combo.getSelectedIndex() == 0 ? "" : " and g_no = " + combo.getSelectedIndex()) + " ",
				"%" + txt.getText() + "%");
		System.out.println(rs);
		c.removeAll();
		for (var r : rs) {
			var temp = newJPanel(new BorderLayout());
			var poster = new JLabel(getIcon("./datafile/영화/" + r.get(5) + ".jpg", 180, 250));
			evt(poster, a -> {
				new MovieInfo(r).addWindowListener(new before(this));
			});
			temp.add(poster);
			temp.add(lbl(r.get(5) + "", JLabel.CENTER), "South");
			c.add(sz(temp, 180, 250));
			c.setBorder(new LineBorder(Color.BLACK));
		}
		while (c.getComponentCount() < 8)
			c.add(newJPanel(null));
	}

	public static void main(String[] args) {
		new MovieList();
	}

}
