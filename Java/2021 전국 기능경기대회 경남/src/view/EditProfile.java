package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class EditProfile extends BaseFrame {

	String cap[] = "아이디,비밀번호,비밀번호 확인,이름,전화번호,생년월일,이메일,주소".split(",");
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField(), new JTextField(), new JTextField(), new JTextField() };
	JComboBox combo[] = { new JComboBox(), new JComboBox(), new JComboBox() };
	boolean state;

	public EditProfile() {
		super("회원수정", 500, 480);

		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(), "South");

		var c_c = new JPanel(new GridLayout(0, 1));
		c.add(c_c);
		c_c.setBorder(new TitledBorder(new LineBorder(Color.black), "회원정보", TitledBorder.LEFT, TitledBorder.TOP));

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(new JLabel(cap[i]), 100, 25));
			if (i < 6) {
				tmp.add(sz(txt[i], 150, 25));
			} else {
				if (i == 6) {
					tmp.add(sz(txt[6], 80, 25));
					tmp.add(new JLabel("@", 0));
					tmp.add(sz(txt[7], 80, 25));
					tmp.add(sz(combo[0], 80, 25));
				} else {
					tmp.add(sz(combo[1], 100, 25));
					tmp.add(sz(txt[8], 170, 25));
				}
			}
			c_c.add(tmp);
		}
		txt[0].setEnabled(false);

		s.add(sz(btn("가입하기", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}
			if (!state) {
				eMsg("중복확인을 해주세요.");
				return;
			}
			String pw = txt[1].getText();
			if (!(pw.matches(".*\\d.*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$%^&*].*")
					&& pw.length() >= 8 && pw.length() <= 16)) {
				eMsg("비밀번호 형식이 아닙니다.");
				return;
			}
			if (!pw.contentEquals(txt[2].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}
			if (txt[4].getText().length() != 11) {
				eMsg("전화번호는 11자리여야 합니다.");
				return;
			}

			try {
				LocalDate l = LocalDate.parse(txt[5].getText(), DateTimeFormatter.ofPattern("yyyyMMdd"));
				if (l.isAfter(LocalDate.now())) {
					eMsg("생년월일을 다시 입력해주세요.");
					return;
				}
//				SELECT distinct SUBSTRING_INDEX(address, ' ', 1) FROM albajava.company where left(SUBSTRING_INDEX(address, ' ', 1), 2)='인천'
				iMsg("가입이 완료되었습니다.");
				String mail = ((combo[1].getSelectedIndex() == 0) ? txt[7].getText() : combo[1].getSelectedItem() + "");
				String phone = txt[4].getText().replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
				execute("update set user pw = '" + txt[1].getText() + "', name ='" + txt[3].getText() + "', email = '"
						+ mail + "', phone ='" + phone + "', birth ='" + l.toString() + "', address ='" + combo[2] + " "
						+ txt[8].getText() + "' where u_no =" + uno);
				execute("insert into user values(0,'" + txt[0].getText() + "','" + txt[1].getText() + "','"
						+ txt[3].getText() + "','" + txt[4].getText() + "@" + mail + "','" + phone + "','"
						+ l.toString() + "','" + combo[2] + " " + txt[8].getText() + "')");
			} catch (Exception e1) {
				eMsg("생년월일을 다시 입력해주세요.");
				return;
			}

		}), 100, 25));

		String mail[] = "기타,empal.com,gmail.com,hanmail.net,kebi.com,korea.com,nate.com,naver.com,yahoo.com".split(",");
		for (int i = 0; i < mail.length; i++) {
			combo[0].addItem(mail[i]);
		}
		combo[1].addItemListener(a -> {
			txt[7].setEditable(combo[1].getSelectedIndex() == 0);
		});

		for (int i = 0; i < addr.length; i++) {
			combo[1].addItem(addr[i]);
		}

		txt[0].addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				state = false;
			}
		});

		txt[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!isNumeric(e.getKeyChar())) {
					e.consume();
				}
			}
		});
		txt[5].addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!isNumeric(e.getKeyChar())) {
					e.consume();
				}
			}
		});

		setInfo();

		setEmpty((JPanel) getContentPane(), 5, 10, 5, 10);

		this.setVisible(true);
	}

	void setInfo() {
		try {
			var rs = stmt.executeQuery("select * from user where u_no = " + uno);
			if (rs.next()) {
				txt[0].setText(rs.getString(2));
				txt[1].setText(rs.getString(3));
				txt[3].setText(rs.getString(4));
				String[] phone = rs.getString(6).split("-");
				String[] birth = rs.getString(7).split("-");
				txt[4].setText(String.join("", phone));
				txt[5].setText(String.join("", birth));

				String date[] = rs.getString(5).split("@");
				txt[6].setText(date[0]);
				txt[7].setText(date[1]);
				combo[0].setSelectedItem(date[1]);

				String addr = rs.getString(8);
				combo[1].setSelectedItem(addr.substring(0, 2));
				txt[8].setText(addr.substring(addr.indexOf(" "), addr.length()));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
