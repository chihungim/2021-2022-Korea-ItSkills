package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	Preferences prf = Preferences.systemNodeForPackage(BaseFrame.class);
	JTextField txt[] = { new Hint("아이디 또는 이메일을 입력해주세요.").txt(15), new Hint("비밀번호를 입력해주세요.").txt(15) };
	JCheckBox box = new JCheckBox("아이디 저장", !prf.get("id", "").isEmpty());

	public Login() {
		super("로그인", 500, 250);
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		cc.add(txt[0]);
		cc.add(txt[1]);

		c.add(rbtn("로그인", a -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				emsg("빈칸이 존재합니다.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("관리자님 환영합니다.");
				if (box.isSelected()) {
					prf.put("id", txt[0].getText());
				} else {
					prf.put("id", "");
				}
				new Admin();
				return;
			}

			var rs = getrows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				emsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				return;
			}

			uno = rs.get(0).get(0) + "";
			imsg(rs.get(0).get(3) + "님 환영합니다.");
			dispose();

			for (var w : getWindows()) {
				if (w instanceof Main)
					((Main) w).login();
			}

			if (box.isSelected()) {
				prf.put("id", txt[0].getText());
			} else {
				prf.put("id", "");
			}
		}), "East");

		add(s = new JPanel(new BorderLayout()), "South");
		s.add(box, "West");
		s.add(sc = new JPanel(new FlowLayout(FlowLayout.RIGHT)));
		for (var cap : "회원가입,아이디 찾기,비밀번호 찾기".split(",")) {
			var l = lbl(cap, JLabel.CENTER);
			sz(l, 80, 30);
			sc.add(l);
			evt(l, a -> {
				if (cap.equals("회원가입")) {
					new Sign().addWindowListener(new before(this));
				} else if (cap.equals("아이디 찾기")) {
					new FindID().addWindowListener(new before(this));
				} else {
					new FindPW().addWindowListener(new before(this));
				}
			});
			if (!cap.equals("비밀번호 찾기"))
				sc.add(mkBar(15));
		}

		txt[0].setText(prf.get("id", ""));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
