package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JTextField txt[] = { new JTextField(15), new JTextField(15) };

	public Login() {
		super("�α���", 300, 250);
		add(c = newJPanel(new GridLayout(0, 1)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		var cap = "ID,PW".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = newJPanel(new BorderLayout());
			temp.add(sz(lbl(cap[i] + ":", JLabel.LEFT), 60, 30), "West");
			temp.add(txt[i]);
			c.add(temp);
		}

		evt(txt[1], a -> new KnockCode(txt[1]).addWindowListener(new before(this)));
		txt[1].setEnabled(false);
		s.add(btn("�α���", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					emsg("������ �����մϴ�.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("�����ڷ� �α����Ͽ����ϴ�.");

				for (var w : getWindows()) {
					if (w instanceof Main)
						((Main) w).admin();
				}
				dispose();
				return;
			}

			var rs = getrows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				emsg("�α��ο� �����Ͽ����ϴ�.");
				txt[0].setText("");
				txt[1].setText("");
			}

			user = rs.get(0);

			imsg(user.get(1) + "�� �α��ο� �����Ͽ����ϴ�.");
			for (var w : getWindows()) {
				if (w instanceof Main)
					((Main) w).user();
			}
			dispose();
		}));

		setVisible(true);
	}
}
