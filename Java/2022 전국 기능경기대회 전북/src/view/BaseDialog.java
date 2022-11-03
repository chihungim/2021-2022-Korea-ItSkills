package view;

import java.awt.event.WindowAdapter;

import javax.swing.JDialog;

public class BaseDialog extends BaseFrame {
	WindowAdapter wa = new WindowAdapter() {
		public void windowLostFocus(java.awt.event.WindowEvent e) {
			if (e.getOppositeWindow() instanceof JDialog)
				return;
			dispose();
		};
	};

	public BaseDialog(String tile, int w, int h) {
		super(tile, w, h);
		addWindowFocusListener(wa);
	}
}
