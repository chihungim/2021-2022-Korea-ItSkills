package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class KnockCode extends BaseFrame {

	JButton knock[] = new JButton[9];
	String code = "";

	public KnockCode(JTextField txt) {
		super("노크코드", 300, 400);
		add(c = newJPanel(new GridLayout(0, 3)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		for (int i = 0; i < knock.length; i++) {
			int j = i + 1;
			knock[i] = btn("●", a -> code = code += j);
			knock[i].setBackground(Color.WHITE);
			knock[i].setForeground(Color.BLACK);
			
			c.add(knock[i]);
		}

		s.add(btn("확인", a -> {
			txt.setText(code);
			dispose();
		}));

		setVisible(true);
	}

}
