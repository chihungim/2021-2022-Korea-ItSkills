package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Store extends BaseFrame {
	public Store() {
		super("스토어", 1000, 700);
		add(new JLabel(getIcon("./datafile/스토어/배경2.jpg", 800, 300)), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 3))));

		for (var s : getrows("select * from store")) {
			var temp = newJPanel(new BorderLayout());
			var temps = newJPanel(new GridLayout(0, 1));

			var img = new JLabel(getIcon("./datafile/스토어/" + s.get(1) + ".jpg", 200, 200));
			temp.add(img);
			temp.add(temps, "South");
			var lbl1 = lbl(s.get(1) + "", JLabel.CENTER, 15);
			var lbl2 = lbl(s.get(2) + "", JLabel.CENTER, 13);
			var lbl3 = lbl(df.format(cint(s.get(3))) + "원", JLabel.CENTER, 15);
			temps.add(lbl1);
			temps.add(lbl2).setForeground(Color.LIGHT_GRAY);
			temps.add(lbl3);
			evt(temp, a -> {
				new Purchase(s).addWindowListener(new before(this));
			});
			c.add(sz(temp, 200, 250));
		}
		c.setBackground(Color.WHITE);
		setVisible(true);
	}

	public static void main(String[] args) {
		uno = "1";
		new Store();
	}
}
