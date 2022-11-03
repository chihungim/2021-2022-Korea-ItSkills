package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Sign extends BaseFrame {
	JCheckBox chk[] = { new JCheckBox("<html>필수동의 항목 및 [선택] 개인정보 수집 및 이용동의, [선택] 광고성 정보 <br>이메일/SMS 수신 동의에 일괄 동의합니다."),
			new JCheckBox("[필수] 만 15세 이상입니다."), new JCheckBox("[필수] 서비스 이용약관 동의"),
			new JCheckBox("[필수] 개인정보 수집 및 이용 동의"), new JCheckBox("[선택] 광고성 정보 이메일/SMS 수신 동의"), };
	String cap[] = "아이디,비밀번호,비밀번호 확인,이름,전화번호,생년월일,이메일,주소".split(",");
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField(), new JTextField(), new JTextField(), new JTextField() };
	JComboBox combo[] = { new JComboBox(), new JComboBox(), new JComboBox<String>() };
	boolean state;

	public Sign() {
		super("회원가입", 450, 700);

		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(), "South");

		for (int i = 0; i < chk.length; i++) {
			n.add(chk[i]);
			chk[i].addActionListener(a -> {
				if (a.getSource().equals(chk[0])) {
					for (int j = 1; j < chk.length; j++) {
						chk[j].setSelected(chk[0].isSelected());
					}
				} else {
					for (int j = 1; j < chk.length; j++) {
						if (!chk[j].isSelected()) {
							chk[0].setSelected(false);
							return;
						}
					}
					chk[0].setSelected(true);
				}
			});
		}

		var c_c = new JPanel(new GridLayout(0, 1));
		c.add(c_c);
		c_c.setBorder(new TitledBorder(new LineBorder(Color.black), "회원정보", TitledBorder.LEFT, TitledBorder.TOP));
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(new JLabel(cap[i]), 100, 25));
			if (i < 6) {
				tmp.add(sz(txt[i], 150, 25));
				if (i == 0) {
					tmp.add(sz(btn("중복확인", a -> {
						if (txt[0].getText().isEmpty()) {
							eMsg("빈칸입니다.");
							return;
						}
						if (!checkIsExists("select * from user where id ='" + txt[0].getText() + "'")) {
							eMsg("사용 불가능합니다.");
							txt[0].setText("");
						} else {
							iMsg("사용 가능합니다.");
							state = true;
						}
					}), 100, 25));
				} else if (i == 5) {
					tmp.add(sz(new JLabel("-", 0), 20, 25));
					tmp.add(sz(combo[0], 50, 25));
				}
			} else {
				if (i == 6) {
					tmp.add(sz(txt[6], 80, 25));
					tmp.add(new JLabel("@", 0));
					tmp.add(sz(txt[7], 80, 25));
					tmp.add(sz(combo[1], 80, 25));
				} else {
					tmp.add(sz(combo[2], 100, 25));
					tmp.add(sz(txt[8], 170, 25));
				}
			}
			c_c.add(tmp);
		}

		s.add(sz(btn("가입하기", a -> {
			if (!(chk[1].isSelected() && chk[2].isSelected() && chk[3].isSelected())) {
				eMsg("회원 이용약관을 동의하셔야 가입이 가능합니다.");
				return;
			}
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
				String gender = (combo[2].getSelectedIndex() == 0) ? "남" : "여";
				execute("insert into user values(0,'" + txt[0].getText() + "','" + txt[1].getText() + "','"
						+ txt[3].getText() + "','" + txt[4].getText() + "@" + mail + "','" + phone + "','"
						+ l.toString() + "','" + gender + " " + txt[8].getText() + "')");
			} catch (Exception e1) {
				eMsg("생년월일을 다시 입력해주세요.");
				return;
			}

			combo[0].addItem("3");
			combo[0].addItem(4);

			String mail[] = "기타,empal.com,gmail.com,hanmail.net,kebi.com,korea.com,nate.com,naver.com,yahoo.com"
					.split(",");
			for (int i = 0; i < mail.length; i++) {
				combo[1].addItem(mail[i]);
			}

			combo[1].addItemListener(i -> {
				txt[7].setEditable(combo[1].getSelectedIndex() == 0);
			});

			for (int i = 0; i < addr.length; i++) {
				combo[2].addItem(addr[i]);
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

			setEmpty((JPanel) getContentPane(), 5, 10, 5, 10);
			this.setVisible(true);

		}), 100, 25));
		setVisible(true);
	}
}
