package view;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class ManagerSign extends BaseFrame {

	int xywh[][] = { { 23, 30, 108, 27 }, { 138, 29, 128, 27 }, { 23, 67, 108, 24 }, { 139, 67, 127, 23 },
			{ 24, 102, 107, 27 }, { 140, 101, 127, 27 }, { 274, 100, 96, 27 }, { 25, 137, 106, 31 },
			{ 142, 137, 124, 29 }, { 24, 180, 108, 34 }, { 143, 178, 123, 35 }, { 28, 228, 105, 38 },
			{ 142, 228, 126, 37 }, { 27, 277, 105, 32 }, { 143, 279, 127, 28 }, { 29, 326, 99, 33 },
			{ 147, 324, 88, 33 }, { 245, 333, 28, 23 }, { 285, 321, 91, 37 }, { 389, 320, 78, 39 },
			{ 361, 415, 60, 30 }, { 417, 415, 60, 29 }, { 6, 7, 472, 376 } };
	boolean isChecked;
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField(), new JTextField(), new JTextField(), new JTextField(), };

	JButton btn = new JButton("중복확인");

	JComboBox<String> mail = new JComboBox<>();
	{
		for (String str : "기타,empal.com,gmail.com,hanmail.net,kebi.com,korea.com,nate.com,naver.com,yahoo.com"
				.split(",")) {
			mail.addItem(str);
		}
	}

	JPanel jpn = new JPanel();
	{
		jpn.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK), "기업회원 가입"));
	}

	JComponent[] jcp = { lbl("사업자등록번호", JLabel.LEFT), txt[0], lbl("기업명", JLabel.LEFT), txt[1], lbl("아이디", JLabel.LEFT),
			txt[2], btn, lbl("비밀번호", JLabel.LEFT), txt[3], lbl("비밀번호 확인", JLabel.LEFT), txt[4], lbl("담장자", JLabel.LEFT),
			txt[5], lbl("전화번호", JLabel.LEFT), txt[6], lbl("이메일", JLabel.LEFT), txt[7], lbl("@", JLabel.LEFT), txt[8],
			mail, btn("이전", a -> dispose()), btn("등록", a -> {

			}), jpn };

	CompanyRegisteration cr;

	public ManagerSign(CompanyRegisteration cr) {
		super("등록", 500, 500);
		this.cr = cr;
		setUI();

		setVisible(true);
	}

	public static void main(String[] args) {
		new ManagerSign(null);
	}

	void setUI() {
		setLayout(null);
		for (int i = 0; i < jcp.length; i++) {
			add(jcp[i]);
			jcp[i].setBounds(xywh[i][0], xywh[i][1], xywh[i][2], xywh[i][3]);
		}

		mail.addItemListener(i -> {
			if (mail.getSelectedIndex() == 0) {
				txt[8].setText("");
				txt[8].setEditable(mail.getSelectedIndex() == 0);
			} else {
				txt[8].setText(mail.getSelectedItem().toString());
				txt[8].setEditable(mail.getSelectedIndex() == 0);
			}

		});

		txt[0].setEditable(1 < 0);
		txt[1].setEditable(1 < 0);

		txt[0].setText(cr.txt[0].getText());
		txt[1].setText(cr.txt[1].getText());

		btn.addActionListener(a -> {
			if (txt[2].getText().isEmpty()) {
				eMsg("빈칸입니다.");
				return;
			}

			if (checkIsExists("select * from manager where id ='" + txt[2].getText() + "'")) {
				eMsg("사용 불가능합니다.");
				isChecked = false;
				return;
			} else {
				iMsg("사용 가능합니다.");
				isChecked = true;
			}
		});
	}

	void check() {

		for (int i = 0; i < txt.length; i++) {
			if (txt[i].getText().isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}
		}
		if (!isChecked) {
			eMsg("중복확인을 해주세요.");
			return;
		}
		String pw = txt[3].getText();
		if (!(pw.matches(".*\\d.*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$%^&*].*") && pw.length() >= 8
				&& pw.length() <= 16)) {
			eMsg("비밀번호 형식이 아닙니다.");
			return;
		}
		if (!pw.contentEquals(txt[4].getText())) {
			eMsg("비밀번호가 일치하지 않습니다.");
			return;
		}
		if (txt[6].getText().length() != 11) {
			eMsg("전화번호는 11자리여야 합니다.");
			return;
		}

		iMsg("등록이 완료되었습니다.");

		try {
			if (new File(cr.fpath).exists()) {
				iMsg(cr.fpath);
				ImageIO.write(ImageIO.read(new File(cr.fpath)), "jpg",
						new File("./지급자료/기업/" + txt[1].getText() + ".jpg"));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int dno = rei(getOne("select d_no from details where name ='" + cr.txt[3].getText() + "'"));
		String phone = txt[6].getText().replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
		String address = cr.local.getSelectedItem().toString() + " " + cr.txt[7].getText();
		String email = txt[7].getText() + "@" + txt[8].getText();
		execute("insert into company values(0, '" + txt[0].getText() + "','" + txt[1].getText() + "','" + dno + "','"
				+ cr.txt[2].getText() + "','" + phone + "','" + cr.txt[4].getText() + "','" + address + "')");
		execute("insert into manager values(0, '" + txt[2].getText() + "','" + txt[3].getText() + "','"
				+ txt[1].getText() + "','" + "','" + email + "','" + phone + "')");

		dispose();
	}
}
