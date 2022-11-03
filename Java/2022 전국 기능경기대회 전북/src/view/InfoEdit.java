package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InfoEdit extends BaseDialog {
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField(), new JTextField() };
	JComboBox<String> combo = new JComboBox<String>("남,여".split(","));

	public InfoEdit() {
		super("회원가입", 400, 600);
		add(lbl("회원가입", JLabel.CENTER, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		var cap = "아이디,비밀번호,비밀번호 확인,이름(한글),이름(영문),연락처,이메일,성별".split(",");

		ArrayList<JComponent> jc = new ArrayList<>();

		jc.addAll(Arrays.asList(txt));
		jc.add(combo);
		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 100, 0), "West");
			temp.add(jc.get(i));
			c.add(temp);
		}

		combo.setSelectedIndex(cint(BasePage.member.get(8)));

		txt[0].setText(BasePage.member.get(1) + "");
		txt[1].setText(BasePage.member.get(2) + "");
		txt[2].setText(BasePage.member.get(2) + "");
		txt[3].setText(BasePage.member.get(3) + "");
		txt[4].setText(BasePage.member.get(4) + "");
		txt[5].setText(BasePage.member.get(6) + "");
		txt[6].setText(BasePage.member.get(7) + "");

		txt[0].setEditable(false);
		combo.setEditable(false);
		for (var bcap : "정보수정,취소".split(",")) {
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

					if (!txt[6].getText().contains("@") || !txt[6].getText().contains(".")) {
						emsg("이메일을 확인해주세요.");
						return;
					}

					imsg("정보수정이 완료되었습니다.");
					execute("update member set m_pw = ?, m_name1 = ? , m_name2 = ?, m_phone = ?, m_email =? where m_no = ?",
							txt[1].getText(), txt[3].getText(), txt[4].getText(), txt[5].getText(), txt[6].getText(),
							BasePage.member.get(0));
					dispose();
				}
			}));
		}
	}

	public static void main(String[] args) {
		Base.디버그("1");
		new InfoEdit().setVisible(true);

	}
}
