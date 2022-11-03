package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JTextField txt[] = { new JTextField(15), new JTextField(15) };

	public Login(Main m) {
		super("로그인", 350, 160);

		add(hylbl("아르바이트", 0, 30), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var cap = "아이디,비밀번호".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			temp.add(txt[i]);
			cc.add(temp);
		}

		c.add(btn("로그인", a -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				emsg("빈칸이 존재합니다.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("관리자로 로그인 하셨습니다.");
				new Admin();
				setVisible(false);
				return;
			}

			var rs = getValues("Select u_img, u_no,u_name from user where u_id = ? and u_pw = ?", txt[0].getText(),
					txt[1].getText());

			if (rs.isEmpty()) {
				emsg("회원정보가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			m.img.setIcon(toIcon(rs.get(0).get(0), 30, 30));

			uno = rs.get(0).get(1) + "";
			uname = rs.get(0).get(2) + "";

			m.name.setText(uname + "님 환영합니다.");
			imsg(uname + "님 환영합니다.");
			m.login();
			dispose();
		}), "East");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

}
