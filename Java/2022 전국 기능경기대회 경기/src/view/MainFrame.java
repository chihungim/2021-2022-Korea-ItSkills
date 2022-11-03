package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.User;

public class MainFrame extends BaseFrame {

	CardLayout layout;

	List<JLabel> history = new ArrayList<>();
	JLabel uname;

	public MainFrame() {
		super("�����������", 800, 700);
		add(n = newJPanel(new BorderLayout()), "North");
		add(c = newJPanel(layout = new CardLayout()));
		n.add(nw = newJPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)), "West");
		n.add(ne = newJPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5)), "East");

		n.add(wlbl(BasePage.user != null ? "����� �޴�" : "������ �޴�", JLabel.CENTER, 20), "North");
		BasePage.mf = this;

		if (BasePage.user != null) {
			var img = new JLabel(BasePage.user.u_img(40, 40));
			uname = wlbl(BasePage.user.u_name + "", 0);
			ne.add(uname);
			ne.add(img);

			evt(img, a -> {
				var pop = new JPopupMenu();
				var menu = new JMenu("������");
				var item1 = new JMenuItem("��ٱ���");
				var item2 = new JMenuItem("������");
				var item3 = new JMenuItem("������");
				var item4 = new JMenuItem("��������");
				pop.add(menu);
				pop.add(item1);
				pop.add(item2);
				menu.add(item3);
				menu.add(item4);

				if (a.getButton() == 3)
					pop.show(img, a.getX(), a.getY());

				Stream.of(item1, item2, item3, item4).forEach(x -> {
					x.addActionListener(c -> {
						history = history.subList(0, 1);
						if (c.getActionCommand().equals("��ٱ���")) {
							new Cart();
						} else if (c.getActionCommand().equals("������")) {
							new Storage(true);
						} else if (c.getActionCommand().equals("������")) {
							new Profile(BasePage.user);
						} else if (c.getActionCommand().equals("��������")) {
							new EditInfo();
						}
					});
				});
			});
		}

		ne.add(evt(wlbl("�α׾ƿ�", 0), a -> {
			BasePage.user = null;
			dispose();
		}));

		setVisible(true);
	}

	void add(String p, BasePage page) {
		c.add(page, p);
		swap(p);
		revalidate();
		repaint();
	}

	void updateALL() {
		BasePage.user = new User(getrows("select * from user where u_no = ?", BasePage.user.u_no).get(0));
		for (var cc : c.getComponents())
			((BasePage) cc).update();
		if (uname != null)
			uname.setText(BasePage.user.u_name + "");
	}

	void swap(String p) {
		nw.removeAll();
		var l = wlbl(p, 0);
		evt(l, a -> {
			history = history.subList(0, history.indexOf(l));
			swap(p);
		});
		history.add(l);
		layout.show(c, p);
		for (int i = 0; i < history.size(); i++) {
			nw.add(history.get(i));
			if (i != history.size() - 1)
				nw.add(wlbl(">", 0));
		}
	}

}
