package view;

import java.awt.FlowLayout;

import javax.swing.JPanel;

public class Receipt extends BaseFrame {

	public Receipt(Purchase purchase) {
		super("영수증", 300, 600);

		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");
		s.add(btn("확인", a -> {
			for (var w : getWindows())
				w.setVisible(w instanceof Main);
		}));
		setVisible(true);
	}
}
