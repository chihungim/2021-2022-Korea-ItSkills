package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자", 300, 300);

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
