package view;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class BaseFrame extends JFrame implements Base {
	JPanel n, cn, w, e, s;
	JPanel nn, cc, wn, en, sn;
	JPanel nc, cw, wc, ec, sc;
	JPanel nw, ce, ww, ew, sw;
	JPanel ne, cs, we, ee, se;
	JPanel ns, c, ws, es, ss;

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기본사진/1.png"));

		getContentPane().setBackground(back);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	class before extends WindowAdapter {
		BaseFrame b;

		public before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
}
