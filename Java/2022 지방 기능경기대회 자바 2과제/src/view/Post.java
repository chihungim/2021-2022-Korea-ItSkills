package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

public class Post extends BaseFrame {

	JTextComponent[] txt = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextArea(8, 15) };

	JRadioButton[] rbtn = { new JRadioButton("비공개"), new JRadioButton("공개") };

	JComponent jc[][] = { { txt[0] }, { txt[1] }, { txt[2] }, { txt[3] }, { rbtn[0], rbtn[1] } };

	public Post(Notice n) {
		super("등록", 300, 400);
		add(c = new JPanel());
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			for (int j = 0; j < jc[i].length; j++) {
				temp.add(jc[i][j]);
			}
			c.add(temp);
		}

		ButtonGroup bg = new ButtonGroup();
		for (var r : rbtn)
			bg.add(r);

		rbtn[1].setSelected(true);

		for (var bc : "등록,취소".split(",")) {
			s.add(btn(bc, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							emsg("빈칸이 존재합니다.");
							return;
						}
					}
					setValues("insert notice values(0,?,?,?,?,?,?)", uno, txt[1].getText(), txt[2].getText(),
							txt[3].getText(), 0, rbtn[1].isSelected() ? 1 : 0);
					imsg("게시물 등록이 완료되었습니다.");
					n.search();
					dispose();
				}
			}));
		}

		txt[0].setText(uid);
		txt[1].setText(LocalDate.now() + "");

		txt[0].setEnabled(false);
		txt[1].setEnabled(false);

		txt[2].setBorder(new LineBorder(Color.BLACK));
		txt[3].setBorder(new LineBorder(Color.BLACK));

		((JTextArea) txt[3]).setLineWrap(true);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

}
