package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BaseFrame extends JFrame implements Base {
	JPanel n, c, w, e, s;
	JPanel nn, cn, wn, en, sn;
	JPanel nc, cc, wc, ec, sc;
	JPanel nw, cw, ww, ew, sw;
	JPanel ne, ce, we, ee, se;
	JPanel ns, cs, ws, es, ss;

	static String uno = "";

	JLabel loginlbl;

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	void login() {
		if (loginlbl != null)
			if (uno.isEmpty()) {
				loginlbl.setText("Login");
			} else {
				loginlbl.setText("Logout");
			}
	}

	void setLogin() {
		for (var w : getWindows()) {
			if (w instanceof BaseFrame)
				((BaseFrame) w).login();
		}
	}


}
