package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BaseDialog extends JDialog implements Base{
	JPanel n, c, w, e, s;
	JPanel nn, cn, wn, en, sn;
	JPanel nc, cc, wc, ec, sc;
	JPanel nw, cw, ww, ew, sw;
	JPanel ne, ce, we, ee, se;
	JPanel ns, cs, ws, es, ss;

	public BaseDialog(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}
