package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class CinemaFrame extends BaseFrame {

	JScrollPane pane;
	JLabel lbl[] = new JLabel[3];

	public CinemaFrame(String page) {
		super("", 1200, 800);

		add(n = new JPanel(new BorderLayout()), "North");

		n.add(nc = newJPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)));
		BasePage.cf = this;
		nc.add(evt(lbl("CINEMA", JLabel.CENTER, 20), a -> dispose())).setForeground(Color.WHITE);
		var cap = "Movie,Reservation,Mypage".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = lbl(cap[i], 0);
			lbl[i] = l;
			final var temp = cap[i];
			nc.add(l).setForeground(Color.WHITE);
			evt(l, a -> swap(temp));
		}

		n.add(loginlbl = lbl("Login", JLabel.RIGHT), "East");
		evt(loginlbl, a -> {
			if (loginlbl.getText().equals("Login")) {
				new Login().addWindowListener(new before(this));
			} else {
				uno = "";
				setLogin();
				dispose();
			}
		});

		add(pane = scrollPane(null));

		setLogin();
		swap(page);
		loginlbl.setForeground(Color.WHITE);
		n.setBorder(new EmptyBorder(5, 50, 5, 50));
		n.setBackground(red);
		setVisible(true);
	}

	void swap(String cap) {
		if (uno.isEmpty() && cap.equals("Mypage")) {
			if (msg() == 0)
				new Login().addWindowListener(new before(this));
			return;
		}
		for (int i = 0; i < lbl.length; i++) {
			lbl[i].setForeground(Color.LIGHT_GRAY);

			if (lbl[i].getText().equals(cap))
				lbl[i].setForeground(Color.WHITE);
		}

		if (cap.equals("Movie")) {
			pane.setViewportView(new Movie());
		} else if (cap.equals("Reservation")) {
			pane.setViewportView(new Reservation());
		} else {
			pane.setViewportView(new Mypage());
		}

	}

	public static void main(String[] args) {
		uno = "1";
		new CinemaFrame("Movie");
	}

}
