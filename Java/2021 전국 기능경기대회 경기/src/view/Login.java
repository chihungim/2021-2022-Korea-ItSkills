package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	String cap[] = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(10), new JPasswordField(10) };

	public Login() {
		super("로그인", 350, 200);
		ui();
		setVisible(true);
	}

	void ui() {
		var c = new JPanel(new BorderLayout());
		var c_c = new JPanel(new GridLayout(0, 1, 10, 10));
		add(c);
		c.add(c_c);

		add(new JLabel(getIcon("datafiles/캐릭터/로티1.jpg", 150, 150)), "West");

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp.add(sz(lbl(cap[i] + ":", JLabel.LEFT), 45, 30), "West");
			tmp.add(txt[i]);
			c_c.add(tmp);
			tmp.setBorder(new EmptyBorder(0, 5, 0, 0));
		}

		c_c.setBorder(new EmptyBorder(20, 5, 20, 5));

		c_c.add(btn("로그인", a -> {
			String pw = txt[1].getText(), id = txt[0].getText();

			if (id.isEmpty() || pw.isEmpty()) {
				eMsg("ID 또는 PW를 입력해주세요.");
				return;
			}

			if (pw.equals("1234") && id.equals("admin")) {
				Main.lblEnable(3);
				iMsg("관리자님 환영합니다.");
				dispose();
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where u_id = '" + id + "' and u_pw = '" + pw + "'");
				if (rs.next()) {
					iMsg(rs.getString(2) + "고객님 환영합니다.(" + age[rs.getInt(7)] + ")");
					uage = age[rs.getInt(7) - 1];
					uname = rs.getString(2);
					uno = rs.getInt(1);
					uold = rs.getInt("u_age");
					uheight = rs.getInt("u_height");
					udisable = rs.getInt("u_disable");
					Main.lblEnable(2);
					dispose();
				} else {
					eMsg("회원정보를 다시 확인해주세요.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}), "South");
	}

	public static void main(String[] args) {
		new Login();
	}
}
