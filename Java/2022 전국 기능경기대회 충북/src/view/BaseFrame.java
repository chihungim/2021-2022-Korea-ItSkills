package view;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BaseFrame extends JFrame implements Base {
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;

	static String uno, scno, mno, tno;

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);
	}
}
