package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Login extends BaseFrame {
	String cap[] = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(), new JPasswordField() };
	JCheckBox chk = new JCheckBox("아이디 저장");
	Preferences pre = Preferences.userNodeForPackage(BaseFrame.class);

	public Login() {
		super("로그인", 350, 230);
		var title = new JLabel("Orange Ticket", JLabel.CENTER);
		title.setFont(new Font("HY헤드라인M", Font.ITALIC + Font.BOLD, 35));
		add(title, "North");

		var c = new JPanel(new GridLayout(0, 1));
		var e = new JPanel(new BorderLayout());
		var s = new JPanel(new BorderLayout());

		add(c);
		add(e, "East");
		add(s, "South");

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();
			tmp.add(sz(new JLabel(cap[i], 2), 40, 25));
			tmp.add(sz(txt[i], 150, 25));
			c.add(tmp);
		}

		txt[0].setText(pre.get("id", ""));

		chk.setSelected(!pre.get("id", "").equals(""));
		chk.addActionListener(a -> pre.remove("id"));
		e.add(btn("로그인", a -> {
			var id = txt[0].getText();
			var pw = txt[1].getText();
			if (id.isEmpty() || pw.isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where u_id = '" + id + "' and u_pw = '" + pw + "'");
				if (rs.next()) {
					iMsg(rs.getString("u_name") + "님 환영합니다.");
					setLogin(rs.getString(1));

					Main.uImg.setIcon(getIcon("./Datafiles/회원사진/" + u_no + ".jpg", 30, 30));
					Main.uImg.setBorder(new LineBorder(Color.BLACK));
					Main.loginlbl.setText("LOGOUT");
					if (chk.isSelected()) {
						pre.put("id", id);
					}
					dispose();
				} else {
					eMsg("ID 또는 PW가 일치하지 않습니다.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));
		s.add(chk, "West");
		s.add(btn("회원가입", a -> {
			new Sign().addWindowListener(new before(Login.this));
		}), "East");
		setVisible(true);
	}
}
