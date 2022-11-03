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

	JTextField txt[] = { new Hint("User ID").txt(15), new Hint("User Password").pw(15, '��') };

	public Login() {
		super("Login", 400, 300);
		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(ss = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		n.add(lbl("�α���", JLabel.LEFT, 30), "North");
		n.add(lbl("�� ���� ���񽺸� �̿��ϱ� ���� �α����ϼ���!", JLabel.LEFT));

		c.add(txt[0]);
		c.add(txt[1]);
		s.add(rndBtn("�α���", a -> {
			if (txt[0].getText().isEmpty()) {
				emsg("���̵� �Է��ϼ���.");
				txt[0].setText("");
				txt[0].requestFocus();
				return;
			}

			if (txt[1].getText().isEmpty()) {
				emsg("��й�ȣ�� �Է��ϼ���.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				imsg("�����ڴ� ȯ���մϴ�.");
				setVisible(false);
				new Admin();
				return;
			}

			var rs = getrows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				emsg("���̵� �Ǵ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			uno = rs.get(0).get(0) + "";
			setLogin();
			imsg(rs.get(0).get(3) + "�� ȯ���մϴ�.");
			dispose();
		}));

		ss.add(lbl("���� ������ �����Ű���?", 0)).setForeground(Color.LIGHT_GRAY);
		ss.add(evt(lbl("���� ����� >", 0), a -> {
			new Sign().addWindowListener(new before(this));
		}));

		border((JComponent) getContentPane(), new EmptyBorder(20, 20, 20, 20));
		setVisible(true);
	}
}
