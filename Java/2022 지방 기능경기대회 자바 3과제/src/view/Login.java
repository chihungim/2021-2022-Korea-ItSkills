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
		super("�α���", 350, 160);

		add(hylbl("�Ƹ�����Ʈ", 0, 30), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var cap = "���̵�,��й�ȣ".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			temp.add(txt[i]);
			cc.add(temp);
		}

		c.add(btn("�α���", a -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				emsg("��ĭ�� �����մϴ�.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("�����ڷ� �α��� �ϼ̽��ϴ�.");
				new Admin();
				setVisible(false);
				return;
			}

			var rs = getValues("Select u_img, u_no,u_name from user where u_id = ? and u_pw = ?", txt[0].getText(),
					txt[1].getText());

			if (rs.isEmpty()) {
				emsg("ȸ�������� ��ġ���� �ʽ��ϴ�.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			m.img.setIcon(toIcon(rs.get(0).get(0), 30, 30));

			uno = rs.get(0).get(1) + "";
			uname = rs.get(0).get(2) + "";

			m.name.setText(uname + "�� ȯ���մϴ�.");
			imsg(uname + "�� ȯ���մϴ�.");
			m.login();
			dispose();
		}), "East");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

}
