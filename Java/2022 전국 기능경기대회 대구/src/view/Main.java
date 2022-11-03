package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	public Main() {
		super("Main", 800, 600);

		add(n = newJPanel(new BorderLayout()), "North");
		add(c = newJPanel(null));

		sz(n, 0, 100);
		border(n, new EmptyBorder(5, 150, 5, 150));
		n.add(lbl("<html><font color = 'white'>CINEMA", JLabel.CENTER, 20), "North");
		n.add(nc = newJPanel(new GridLayout(1, 0, 5, 5)));

		var rs = getrows(
				"select m.*, count(*) cnt from reservation r , movie m where r.m_no = m.m_no  group by m.m_no order by cnt desc limit 0,5");

		var cap = "Movie,Reservation,Mypage,Login".split(",");

		for (var c : cap) {
			var l = lbl(c, JLabel.CENTER);
			nc.add(l).setForeground(Color.WHITE);
			if (c.equals("Login"))
				loginlbl = l;
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (l.getText().equals("Login")) {
						new Login().addWindowListener(new before(Main.this));
					} else if (l.getText().equals("Logout")) {
						uno = "";
						setLogin();
					} else {
						if (l.getText().equals("Mypage") && uno.isEmpty()) {
							if (msg() == 0)
								new Login().addWindowListener(new before(Main.this));
							return;
						}
						new CinemaFrame(l.getText()).addWindowListener(new before(Main.this));
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					l.setBorder(new MatteBorder(0, 0, 1, 0, red));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					l.setBorder(null);
				}
			});
		}

		var itemp = newJPanel(new GridLayout(1, 0));
		var ma = new MouseAdapter() {
			int sx;

			@Override
			public void mouseClicked(MouseEvent e) {
				var c = itemp.getComponentAt(e.getPoint());

				if (c != null) {
					var idx = cint(c.getName());
					new MovieInformation(rs.get(idx)).setVisible(true);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				sx = e.getX();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int diff = e.getX() - sx;
				itemp.setLocation(itemp.getX() + diff, 0);
				setScroll();
			}

			void setScroll() {
				if (itemp.getX() >= 0) {
					itemp.setLocation(0, 0);
				} else if (itemp.getX() <= -725) {
					itemp.setLocation(-725, 0);
				}
			}
		};

		for (var r : rs) {
			var poster = new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 300, 400)) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = tog2d(g);
					g2.setColor(new Color(0, 0, 0, 20));
					g2.fillRect(0, 0, getWidth(), getHeight());
					g2.setColor(Color.WHITE);
					g2.setFont(lbl("", 0, 60).getFont());
					g2.drawString(rs.indexOf(r) + 1 + "", 20, getHeight() - 20);
					var fm = g2.getFontMetrics();
					g2.setFont(lbl("", 0).getFont());
					g2.drawString(r.get(1) + "", 20 + fm.stringWidth(rs.indexOf(r) + "") + 2, getHeight() - 20);
				}
			};
			poster.setName(rs.indexOf(r) + "");
			itemp.add(poster);
		}

		itemp.addMouseListener(ma);
		itemp.addMouseMotionListener(ma);

		c.add(itemp).setBounds(0, 0, 300 * 5 + 5 * 5, 400);

		b((JComponent) getContentPane(), Color.BLACK);
		border((JComponent) getContentPane(), new EmptyBorder(10, 0, 10, 0));
		border(nc, new MatteBorder(1, 0, 0, 0, Color.WHITE));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
