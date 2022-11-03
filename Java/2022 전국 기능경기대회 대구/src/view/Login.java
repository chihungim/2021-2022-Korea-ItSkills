package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JTextField txt[] = { new Hint("User ID").txt(15), new Hint("User Password").pw(15, '●') };

	public Login() {
		super("Login", 400, 300);
		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(ss = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		n.add(lbl("로그인", JLabel.LEFT, 30), "North");
		n.add(lbl("더 많은 서비스를 이용하기 위해 로그인하세요!", JLabel.LEFT));

		c.add(txt[0]);
		c.add(txt[1]);
		s.add(rndBtn("로그인", a -> {
			if (txt[0].getText().isEmpty()) {
				emsg("아이디를 입력하세요.");
				txt[0].setText("");
				txt[0].requestFocus();
				return;
			}

			if (txt[1].getText().isEmpty()) {
				emsg("비밀번호를 입력하세요.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("관리자님 환영합니다.");
				setVisible(false);
				new Admin();
				return;
			}

			var rs = getrows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				emsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			uno = rs.get(0).get(0) + "";
			setLogin();
			imsg(rs.get(0).get(3) + "님 환영합니다.");
			dispose();
		}));

		ss.add(lbl("아직 계정이 없으신가요?", 0)).setForeground(Color.LIGHT_GRAY);
		ss.add(evt(lbl("계정 만들기 >", 0), a -> {
			new Sign().addWindowListener(new before(this));
		}));

		border((JComponent) getContentPane(), new EmptyBorder(20, 20, 20, 20));
		setVisible(true);
	}
}
