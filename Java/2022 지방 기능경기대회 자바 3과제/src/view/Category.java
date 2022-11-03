package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Category extends BaseFrame {

	JCheckBox box[] = new JCheckBox[category.length - 1];

	JTextField txt;

	public Category(JTextField out) {
		super("직종선택", 300, 400);
		add(c = new JPanel(new GridLayout(0, 2)));
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(sc = new JPanel(new FlowLayout(1)));
		s.add(ss = new JPanel(new FlowLayout(1)), "South");

		for (int i = 0; i < box.length; i++) {
			box[i] = new JCheckBox(category[i + 1]);
			box[i].addItemListener(j -> {
				txt.setText(String.join(",",
						Arrays.stream(box).filter(a -> a.isSelected()).map(a -> a.getText()).toArray(String[]::new)));
				txt.setEnabled(txt.getText().isEmpty());
			});
			c.add(box[i]);
		}
		
		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK),"직종선택" , 0, 0, new Font("", Font.BOLD,20)));

		sc.add(hylbl("선택직종명", 0, 13));
		sc.add(txt = new JTextField(15));
		ss.add(btn("선택", a -> {
			if (txt.getText().isEmpty()) {
				emsg("직종을 선택하세요.");
				return;
			}

			out.setText(txt.getText());
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

}
