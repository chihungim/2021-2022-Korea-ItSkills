package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Theater extends BaseFrame {

	JLabel theater;
	JLabel theaterpic;

	ArrayList<JLabel> area = new ArrayList<>();
	ArrayList<JLabel> t = new ArrayList<>();

	public Theater() {
		super("영화관", 1200, 500);

		add(sz(n = new JPanel(new BorderLayout()), 0, 120), "North");
		n.add(nn = newJPanel(new GridLayout(1, 0)), "North");
		n.add(nc = newJPanel(new FlowLayout(FlowLayout.CENTER)));
		n.setBackground(Color.BLACK);
		add(c = new JPanel(new BorderLayout()));
		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(theaterpic = new JLabel());
		cn.add(theater = lbl("", 0), "West");
		var cnc = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cn.add(cnc);
		cnc.add(rbtn("예약하기", a -> {
			new Reservation().addWindowListener(new before(this));
		}));
		cnc.add(bbtn("상영시간표", a -> {
			tno = theater.getName();
			new Schedule().addWindowListener(new before(this));
		}));

		for (var a : getrows("select a_no, a_name from area")) {
			var l = lbl(a.get(1) + "", JLabel.CENTER, 15);
			nn.add(l);
			evt(l, x -> {
				area.stream().forEach(c -> c.setForeground(Color.LIGHT_GRAY));
				l.setForeground(Color.WHITE);
				setTheater(a.get(0) + "");
			});
			area.add(l);

			l.setForeground(Color.LIGHT_GRAY);
		}

		area.get(0).setForeground(Color.white);
		setTheater("1");
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Theater();
	}

	void setTheater(String ano) {
		nc.removeAll();
		t = new ArrayList<>();
		var rs = getrows("select * from theater where a_no = ?", ano);
		for (var r : rs) {
			var l = lbl(r.get(2) + "", JLabel.LEFT);
			nc.add(l);
			t.add(l);
			evt(l, a -> {
				t.stream().forEach(x -> x.setForeground(Color.LIGHT_GRAY));
				theater.setText(r.get(2) + "");
				theaterpic.setIcon(
						getIcon("./datafile/지점/" + r.get(2).toString().replaceAll("[\r\n]", "") + ".jpg", 1200, 300));
				l.setForeground(Color.WHITE);
				theater.setName(r.get(2) + "");
			});
			l.setForeground(Color.LIGHT_GRAY);
			if (rs.indexOf(r) != rs.size() - 1)
				nc.add(sz(mkBar(12), 20, 10));
		}

		t.get(0).setForeground(Color.WHITE);
		theater.setText(rs.get(0).get(2) + "");
		theaterpic.setIcon(
				getIcon("./datafile/지점/" + rs.get(0).get(2).toString().replaceAll("[\r\n]", "") + ".jpg", 1200, 300));
		theater.setName(rs.get(0).get(0) + "");
		System.out.println(
				new File("./datafile/지점/" + rs.get(0).get(2).toString().replaceAll("[\r\n]", "") + ".jpg").exists());
		revalidate();
		repaint();
	}

}
