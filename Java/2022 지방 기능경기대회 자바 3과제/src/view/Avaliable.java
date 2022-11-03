package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Avaliable extends BaseFrame {

	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField() };

	public Avaliable(String eno, JButton jButton) {
		super("지원가능여부", 300, 400);
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(1)), "South");

		var info = getValues(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from employment e, company c where e.c_no = c.c_no and e.e_no = ?",
				eno).get(0);

		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기업/" + info.get(0) + "2.jpg"));

		var cap = "기업이름,대표자,주소,모집성별,최종학력".split(",");

		for (int i = 0; i < txt.length; i++) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30), "West");
			temp.add(txt[i]);
			txt[i].setText(info.get(i) + "");
			txt[i].setEnabled(false);
			c.add(temp);
		}

		int egender = toInt(txt[3].getText());
		int egraduate = toInt(txt[4].getText());

		txt[3].setText(gender[egender]);
		txt[4].setText(graduate[egraduate]);

		s.add(btn("지원가능여부보기", a -> {
			if (egender != 3 && egender != toInt(getOne("select u_gender from user where u_no = ?", uno))) {
				emsg("지원이 불가합니다.(성별)");
				return;
			}

			if (egraduate != 3 && egraduate < toInt(getOne("select u_graduate from user where u_no = ?", uno))) {
				emsg("지원이 불가합니다.(학력)");
				return;
			}

			if (getOne("select * from applicant where u_no = ? and e_no = ? and (a_apply = 1 or a_apply = 0)", uno,
					eno) != null) {
				emsg("합격자 또는 심사중 입니다.");
				return;
			}

			imsg("지원 가능한 공고입니다.");
			jButton.setEnabled(true);
			dispose();
		}));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

}
