package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class BasePage extends JPanel implements Base {
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;
	static MainFrame mf;
	static ArrayList<Object> member;
	static ArrayList<Object[]> companions;
	static ArrayList<Object[]> bags;
	static String depart, arrival, date, sno;

	public BasePage() {
		setLayout(new BorderLayout());
	}

}
