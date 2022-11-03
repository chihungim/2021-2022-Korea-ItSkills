package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BasePage extends JPanel implements Base {
	JPanel n, c, w, e, s;
	JPanel nn, cn, wn, en, sn;
	JPanel nc, cc, wc, ec, sc;
	JPanel nw, cw, ww, ew, sw;
	JPanel ne, ce, we, ee, se;
	JPanel ns, cs, ws, es, ss;
	static CinemaFrame cf;

	public BasePage(String title) {
		cf.setTitle(title);
		setLayout(new BorderLayout(5, 5));

	}

}
