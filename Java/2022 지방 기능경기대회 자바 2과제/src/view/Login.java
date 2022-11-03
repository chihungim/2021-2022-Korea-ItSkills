package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JTextField txt[] = { new JTextField(10), new JTextField(10) };

	public Login(Main m) {
		super("로그인", 250, 250);

		add(lbl("방탈출", 0, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "아이디,비밀번호".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			temp.add(txt[i]);
			c.add(temp);
		}

		for (var bc : "로그인,회원가입".split(",")) {
			s.add(btn(bc, a -> {
				if (a.getActionCommand().equals("로그인")) {
					if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
						emsg("빈칸이 있습니다.");
						return;
					}
					var rs = getValues("select u_no, u_name, u_id from user where u_id = ? and u_pw = ?",
							txt[0].getText(), txt[1].getText());

					if (rs.isEmpty()) {
						emsg("회원 정보가 일치하지 않습니다.");
						return;
					}

					uno = rs.get(0).get(0) + "";
					uname = rs.get(0).get(1) + "";
					uid = rs.get(0).get(2) + "";

					imsg(uname + "님 환영합니다.");
					m.status(true);
					dispose();
				} else {
					new Sign().addWindowListener(new before(this));
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

}
