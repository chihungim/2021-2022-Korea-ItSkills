package view;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Admin extends BaseFrame {
	public Admin() {
		super("Admin", 300, 300);
		setLayout(new GridLayout(0, 1));

		for (var bcap : "��ȭ ����,���� ����,ȸ�� ����".split(",")) {
			add(btn(bcap, a -> {
				if (a.getActionCommand().equals("ȸ�� ����")) {

				} else if (a.getActionCommand().equals("���� ����")) {

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
