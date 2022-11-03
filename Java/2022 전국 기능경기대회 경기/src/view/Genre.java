package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Genre extends BaseDialog {

	JLabel lbl[] = new JLabel[g_genre.length - 1];

	public Genre(JPanel panel, boolean mod) {
		super("장르 선택", 300, 400);
		add(c = newJPanel(new GridLayout(0, 2)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");
		var selLst = new ArrayList<>();
		for (var p : panel.getComponents()) {
			var l = (JLabel) p;
			selLst.add(l.getText());
			System.out.println(selLst);

		}

		for (int i = 0; i < lbl.length; i++) {
			lbl[i] = wlbl(g_genre[i + 1], JLabel.LEFT);
			lbl[i].setForeground(selLst.contains(g_genre[i + 1]) ? Color.LIGHT_GRAY : Color.WHITE);
			evt(lbl[i], a -> {
				panel.removeAll();
				var me = (JLabel) a.getSource();
				if (me.getForeground().equals(Color.WHITE)) {
					me.setForeground(Color.LIGHT_GRAY);
				} else {
					me.setForeground(Color.WHITE);
				}

				for (var l : lbl) {
					if (!l.getForeground().equals(Color.WHITE)) {
						if (mod) {
							panel.add(lbl(l.getText(), JLabel.CENTER));
						} else {
							panel.add(wlbl(l.getText(), JLabel.CENTER));
						}
					}
				}
				panel.revalidate();
				panel.repaint();
			});

			c.add(lbl[i]);
		}

		s.add(btn("닫기", a -> dispose()));

	}

}
