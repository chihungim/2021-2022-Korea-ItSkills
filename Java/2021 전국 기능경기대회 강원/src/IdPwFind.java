import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class IdPwFind extends BaseDialog {
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField() };
	String cap[] = "Name,E-mail,Name,Id,E-mail".split(",");
	JLabel lbl[] = new JLabel[2];

	public IdPwFind() {
		super("아이디/비밀번호 찾기", 450, 600);

		UI();
		setVisible(true);
	}

	void UI() {
		setLayout(null);

		setBounds(lbl[0] = lbl("아이디 찾기", 2, 30), 20, 10, 200, 70);
		for (int i = 0; i < 2; i++) {
			HintText(txt[i], cap[i]);
			setBounds(txt[i], 20, 80 + i * 50, 380, 30);
		}
		setBounds(btn("계속", e -> {
			if ((txt[0].getText().contentEquals("") || txt[0].getText().equals("Name"))
					|| (txt[1].getText().contentEquals("") || txt[1].getText().equals("E-mail"))) {
				emsg = new eMsg("공란을 확인해주세요.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where name = '" + txt[0].getText() + "' and email = '"
						+ txt[1].getText() + "'");
				if (rs.next()) {
					imsg = new iMsg("귀하의 Id는 " + rs.getString(2) + "입니다.");
				} else {
					emsg = new eMsg("존재하지 않는 정보입니다.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}), 20, 190, 380, 40);

		setBounds(lbl[1] = lbl("비밀번호 찾기", 2, 30), 20, 250, 230, 70);
		for (int i = 2; i < 5; i++) {
			HintText(txt[i], cap[i]);
			setBounds(txt[i], 20, 200 + i * 60, 380, 30);
		}

		setBounds(btn("계속", e -> {
			if ((txt[2].getText().contentEquals("") || txt[2].getText().equals("Name"))
					|| (txt[3].getText().contentEquals("") || txt[3].getText().equals("Id"))
					|| (txt[4].getText().contentEquals("") || txt[4].getText().equals("E-mail"))) {
				emsg = new eMsg("공란을 확인해주세요.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where name = '" + txt[2].getText() + "' and id = '"
						+ txt[3].getText() + "' and email = '" + txt[4].getText() + "'");
				if (rs.next()) {
					imsg = new iMsg("귀하의 Id에 PW는 " + rs.getString(3) + "입니다.");
				} else {
					emsg = new eMsg("존재하지 않는 정보입니다.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}), 20, 500, 380, 40);
	}

	public static void main(String[] args) {
		new IdPwFind();
	}
}
