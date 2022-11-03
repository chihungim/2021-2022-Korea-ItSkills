package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.User;

public class Login extends BaseFrame {

	JTextField txt[] = { new JTextField(), new JPasswordField() };

	public Login() {
		super("�α���", 300, 300);
		setLayout(new BorderLayout(5, 5));
		add(wlbl("�����������", JLabel.CENTER, 20), "North");
		add(c = newJPanel(new GridLayout(0, 1, 5, 5)));

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var temp = newJPanel(new BorderLayout());
			temp.add(sz(wlbl(cap[i], JLabel.LEFT), 60, 0), "West");
			temp.add(txt[i]);
			c.add(temp);
		}

		((JPasswordField) txt[1]).setEchoChar('��');

		add(bbtn("�α���", a -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				emsg("��ĭ�� �ֽ��ϴ�.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				new MainFrame().addWindowListener(new before(this));
				new AdminMain();
				BasePage.mf.setVisible(true);
				return;
			}

			var rs = getrows("select * from user where u_id= ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				emsg("ȸ�������� ��ġ���� �ʽ��ϴ�.");
				return;
			}

			BasePage.user = new User(rs.get(0));
			imsg(BasePage.user.u_name + "�� ȯ���մϴ�.");
			new MainFrame().addWindowListener(new before(this));
			new UserMain();
			BasePage.mf.setVisible(true);

		}), "South");

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
