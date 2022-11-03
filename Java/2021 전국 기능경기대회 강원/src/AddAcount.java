import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AddAcount extends BaseDialog {
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField() };
	JPasswordField pw[] = { new JPasswordField(), new JPasswordField() };
	String pcap[] = "Password,Password 확인".split(",");
	String cap[] = "Name,Id,E-mail".split(",");
	JLabel lbl;

	public AddAcount() {
		super("계정 등록하기", 450, 600);

		UI();
		setVisible(true);
	}

	void UI() {
		setLayout(null);

		setBounds(lbl = lbl("계정 정보", 2, 30), 20, 30, 200, 70);
		for (int i = 0; i < 2; i++) {
			HintText(txt[i], cap[i]);
			setBounds(txt[i], 25, 120 + i * 50, 380, 30);
		}
		for (int i = 0; i < pcap.length; i++) {
			HintText(pw[i], pcap[i]);
			setBounds(pw[i], 25, 220 + i * 50, 380, 30);
		}

		HintText(txt[2], cap[2]);
		setBounds(txt[2], 25, 320, 380, 30);

		setBounds(btn("회원가입", e -> {
			if (txt[0].getText().contentEquals("") || txt[0].getText().equals("Name")
					|| txt[1].getText().contentEquals("") || txt[1].getText().equals("Id")
					|| pw[0].getText().contentEquals("") || pw[0].getText().equals("Password")
					|| pw[1].getText().contentEquals("") || pw[1].getText().equals("Password 확인")
					|| txt[2].getText().contentEquals("") || txt[2].getText().equals("E-mail")) {
				emsg = new eMsg("공란을 확인해주세요.");
				return;
			}

			if (!pw[0].getText().equals(pw[1].getText())) {
				emsg = new eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!pw[0].getText().matches(".*[\\W].*") || !pw[1].getText().matches(".*[\\W].*")) {
				emsg = new eMsg("특수문자를 포함해주세요.");
				return;
			}

			try {
				var rs1 = stmt.executeQuery("select * from user where id = '" + txt[1].getText() + "'");
				if (rs1.next()) {
					emsg = new eMsg("Id가 중복되었습니다.");
					return;
				}

				var rs2 = stmt.executeQuery("select * from user where email = '" + txt[2].getText() + "'");
				if (rs2.next()) {
					emsg = new eMsg("E-mail이 중복되었습니다.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			imsg = new iMsg("회원가입이 완료되었습니다.");
			dispose();
		}), 25, 390, 380, 40);
	}

	public static void main(String[] args) {
		new AddAcount();
	}
}
