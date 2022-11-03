package view;

import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Puzzle extends BaseFrame {

	public Puzzle(Purchase p) {
		super("퍼즐", 600, 300);
		add(bbtn("퍼즐클리어", a -> {
			p.discount = 0.9;
			p.setPricelbl();
		}));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				p.puzzle.setEnabled(false);
			}
		});
		
		setVisible(true);
	}

}
