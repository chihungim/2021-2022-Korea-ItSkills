package view;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Comment extends BaseDialog {

	JLabel star[] = new JLabel[5];

	int crate = 5;
	JTextField txt;
	JButton btn;

	public Comment(String mno, Mypage mypage) {
		super("Comment", 500, 200);
		add(lbl("별점을 선택해주세요.", JLabel.CENTER, 25), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		for (int i = 0; i < 5; i++) {
			int idx = i;
			c.add(star[i] = lbl("★", 0, 55));

			star[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (int j = 0; j < 5; j++)
						star[j].setText("☆");

					for (int k = 0; k < idx + 1; k++)
						star[k].setText("★");
					crate = idx + 1;
				}
			});
			star[i].setForeground(Color.ORANGE);
		}

		s.add(txt = new Hint("감성평을 등록해주세요.").txt(15));
		s.add(btn = btn("등록", a -> {
			if (btn.getBackground().equals(Color.LIGHT_GRAY))
				return;
			else {
				execute("insert comment values(0,?,?,?,?)", BaseFrame.uno, mno, txt.getText(), crate);
				mypage.data();
				dispose();
			}
		}));

		btn.setForeground(Color.WHITE);
		btn.setBackground(Color.LIGHT_GRAY);

		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txt.getText().isEmpty())
					btn.setBackground(Color.LIGHT_GRAY);
				else
					btn.setBackground(red);
			}
		});

	}

}
