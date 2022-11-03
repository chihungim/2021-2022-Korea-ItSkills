package view;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Admin extends BaseFrame {
	public Admin() {
		super("Admin", 300, 300);
		setLayout(new GridLayout(0, 1));

		for (var bcap : "영화 편집,극장 편집,회원 편집".split(",")) {
			add(btn(bcap, a -> {
				if (a.getActionCommand().equals("회원 편집")) {

				} else if (a.getActionCommand().equals("극장 편집")) {

				}
			}));
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				for (var w : getWindows())
					w.setVisible(w instanceof Main);
			}
		});

		setVisible(true);
	}
}
