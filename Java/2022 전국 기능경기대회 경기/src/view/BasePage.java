package view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import model.User;

public class BasePage extends JPanel implements Base {
	JPanel n, cn, w, e, s;
	JPanel nn, cc, wn, en, sn;
	JPanel nc, cw, wc, ec, sc;
	JPanel nw, ce, ww, ew, sw;
	JPanel ne, cs, we, ee, se;
	JPanel ns, c, ws, es, ss;

	static User user;
	static MainFrame mf;

	public BasePage(String title) {
		setLayout(new BorderLayout(5, 5));
		setBackground(back);
		mf.add(title, this);
	}

	static void µð¹ö±×(String n) {
		user = new User(base.getrows("select * from user where u_no = ?", n).get(0));
		new MainFrame();
	}

	void update() {

	}
}
