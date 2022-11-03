import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class Account extends BaseDialog {
	JLabel lbl[] = new JLabel[5];
	JLabel title;
	String lcap[] = "Id,pwd,name,email,point".split(",");
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField() };

	public Account() {
		super("계정", 400, 530);

		UI();
		setVisible(true);
	}

	void UI() {
		setLayout(null);

		setBounds(title = lbl("계정", 2, 25), 30, 30, 60, 30);
		for (int i = 0; i < lbl.length; i++) {
			lbl[i] = new JLabel(lcap[i]);
			setBounds(lbl[i], 30, 100 + i * 50, 50, 10);
			setBounds(txt[i], 100, 95 + i * 50, 250, 25);
		}
		setBounds(btn("수정", e -> edit()), 30, 340, 150, 30);
		setBounds(btn("취소", e -> dispose()), 200, 340, 150, 30);

		txt[0].setEditable(false);
		txt[4].setEditable(false);

		try {
			var rs = stmt.executeQuery("select * from user where id = '" + u_id + "'");
			if (rs.next()) {
				txt[0].setText(rs.getString(2));
				txt[1].setText(rs.getString(3));
				txt[2].setText(rs.getString(4));
				txt[3].setText(rs.getString(5));
				txt[4].setText(rs.getInt(6) + "");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	void edit() {
		for (int i = 0; i < txt.length; i++) {
			if (txt[i].getText().isEmpty()) {
				emsg = new eMsg("공란을 확인해주세요.");
				return;
			}
		}

		execute("update user set pwd = '" + txt[1].getText() + "', name = '" + txt[2].getText() + "', email = '"
				+ txt[3].getText() + "' where id = '" + txt[0].getText() + "'");
	}

	public static void main(String[] args) {
		u_id = "VRIdflpkE686";
		new Account();
	}
}
