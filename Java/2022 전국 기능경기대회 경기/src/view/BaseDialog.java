package view;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BaseDialog extends JDialog implements Base {
	JPanel n, cn, w, e, s;
	JPanel nn, cc, wn, en, sn;
	JPanel nc, cw, wc, ec, sc;
	JPanel nw, ce, ww, ew, sw;
	JPanel ne, cs, we, ee, se;
	JPanel ns, c, ws, es, ss;

	public BaseDialog(String title, int w, int h) {
		setModal(true);
		setTitle(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기본사진/1.png"));
		getContentPane().setBackground(back);
	}
}
