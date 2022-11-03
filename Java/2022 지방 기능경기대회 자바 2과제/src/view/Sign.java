package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseFrame {

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15) };
	JComboBox[] combo = { new JComboBox<String>(), new JComboBox<String>(), new JComboBox<String>() };

	JComponent jc[][] = { { txt[0] }, { txt[1] }, { txt[2] }, { txt[3] },
			{ combo[0], lbl("년", 0), combo[1], lbl("월", 0), combo[2], lbl("일", 0) } };

	public Sign() {
		super("회원가입", 320, 300);
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(1)), "South");
		var cap = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 80, 30));
			for (int j = 0; j < jc[i].length; j++) {
				temp.add(jc[i][j]);
			}
			c.add(temp);
		}

		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					emsg("빈칸이 있습니다.");
					return;
				}
			}

			var id = txt[1].getText();

			if (id.length() > 8 || id.length() < 4 || getValue("select * from user where u_id = ?", id) != null) {
				emsg("사용할 수 없는 아이디 입니다.");
				return;
			}

			if (txt[2].getText().equals(id)) {
				emsg("비밀번호는 아이디와 4글자 이상 연속으로 겹쳐질 수 없습니다.");
				return;
			}

			for (int i = 0; i < id.length() - 4; i++) {
				var tmp = id.substring(i, i + 4);
				if (txt[2].getText().contains(tmp)) {
					emsg("비밀번호는 아이디와 4글자 이상 연속으로 겹쳐질 수 없습니다.");
					return;
				}
			}

			if (!txt[2].getText().equals(txt[3].getText())) {
				emsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			setValues("insert user values(0,?,?,?,?)", txt[1].getText(), txt[2].getText(), txt[0].getText(),
					combo[0].getSelectedItem() + "-" + combo[1].getSelectedItem() + "-" + combo[2].getSelectedItem());
			imsg(txt[0].getText() + "님 가입을 환영합니다.");
			dispose();
		}));

		for (int i = 1900; i <= LocalDate.now().getYear(); i++)
			combo[0].addItem(i);

		combo[0].addItemListener(i -> {
			if (i.getStateChange() == 1) {
				combo[1].removeAllItems();
				var y = toInt(combo[0].getSelectedItem());
				for (int j = 1; j <= 12; j++) {
					if (!LocalDate.of(y, j, 1).isAfter(LocalDate.now())) {
						combo[1].addItem(j);
					}
				}
			}
		});

		combo[1].addItemListener(i -> {
			if (i.getStateChange() == 1) {
				combo[2].removeAllItems();
				var y = toInt(combo[0].getSelectedItem());
				var m = toInt(combo[1].getSelectedItem());
				for (int j = 1; j <= LocalDate.of(y, m, 1).lengthOfMonth(); j++) {
					if (!LocalDate.of(y, m, j).isAfter(LocalDate.now())) {
						combo[2].addItem(j);
					}
				}
			}
		});

		combo[0].setSelectedIndex(combo[0].getItemCount() - 1);
		combo[1].setSelectedIndex(combo[1].getItemCount() - 1);
		combo[2].setSelectedIndex(combo[2].getItemCount() - 1);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
