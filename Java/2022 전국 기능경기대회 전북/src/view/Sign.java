package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField(), new JTextField(), new JTextField() };
	JComboBox<String> combo = new JComboBox<String>("남,여".split(","));
	boolean flag;

	public Sign() {
		super("회원가입", 400, 600);
		add(lbl("회원가입", JLabel.CENTER, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		var cap = "아이디,비밀번호,비밀번호 확인,이름(한글),이름(영문),연락처,생년월일,이메일,성별".split(",");

		ArrayList<JComponent> jc = new ArrayList<>();

		jc.addAll(Arrays.asList(txt));
		jc.add(combo);
		combo.setSelectedIndex(-1);
		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 100, 0), "West");
			temp.add(jc.get(i));
			temp.add(sz(i == 0 ? btn("중복확인", a -> {
				if (txt[0].getText().isEmpty()) {
					emsg("아이디를 입력하세요.");
					return;
				}
				if (!getrows("select * from member where m_id = ? ", txt[0].getText()).isEmpty()) {
					emsg("아이디가 중복되었습니다.");
					txt[0].setText("");
					flag = false;
					return;
				}
				imsg("사용가능한 아이디입니다.");
				flag = true;
			}) : lbl("", 0), 100, 0), "East");
			c.add(temp);
		}

		for (var bcap : "회원가입,취소".split(",")) {
			s.add(btn(bcap, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty() || combo.getSelectedIndex() == -1) {
							emsg("빈칸이 존재합니다.");
							return;
						}
					}

					if (!flag) {
						emsg("아이디 중복확인을 해주세요.");
						return;
					}

					if (!txt[1].getText().equals(txt[2].getText())) {
						emsg("비밀번호 확인이 일치하지 않습니다.");
						return;
					}

					if (txt[3].getText().matches(".*[^ㄱ-힣].*")) {
						emsg("한글 이름을 확인해주세요.");
						return;
					}

					var 이름 = txt[4].getText().split(" ");

					if (이름.length < 2) {
						emsg("영문 이름은 성과 이름을 구분해주세요.");
						return;
					}

					if (이름[0].matches(".*[^a-zA-Z]") || 이름[1].matches(".*[^a-zA-Z].*")) {
						emsg("영문 이름을 확인해주세요.");
						return;
					}

					if (!txt[5].getText().matches("^\\d{3}-\\d{4}-\\d{4}$")) {
						emsg("전화번호를 확인해주세요.");
						return;
					}

					if (!txt[7].getText().contains("@") || !txt[7].getText().contains(".")) {
						emsg("이메일을 확인해주세요.");
						return;
					}

					try {
						var ld = LocalDate.parse(txt[6].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						if (ld.isAfter(LocalDate.now())) {
							emsg("생년월일을 확인해주세요.");
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						emsg("생년월일을 확인해주세요.");
						return;
					}

					imsg("회원가입이 완료되었습니다.");
					execute("insert member values(0,?,?,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(),
							txt[3].getText(), txt[4].getText(), txt[6].getText(), txt[5].getText(), txt[7].getText(),
							combo.getSelectedIndex());
					dispose();
				}
			}));
		}
		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
