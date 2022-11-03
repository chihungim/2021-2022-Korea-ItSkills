package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15) };

	JComboBox combo[] = { new JComboBox<String>(), new JComboBox<String>() };
	JRadioButton radio[] = { new JRadioButton("야채", true), new JRadioButton("과일") };
	ButtonGroup bg = new ButtonGroup();
	boolean flag = false;

	public Sign() {
		super("회원가입", 450, 350);
		for (var r : radio)
			bg.add(r);
		add(c = newJPanel(new GridLayout(0, 1)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		var cap = "이름,아이디,비밀번호,생년월일,지역,세부지역,구분".split(",");

		JComponent jc[][] = { { txt[0] }, { txt[1], btn("중복확인", a -> {
			if (txt[1].getText().isEmpty()) {
				emsg("아이디를 입력하세요.");
				return;
			}

			if (!getrows("select * from user where u_id = ?", txt[1].getText()).isEmpty()) {
				emsg("이미 존재하는 아이디 입니다.");
				txt[1].setText("");
				return;
			}

			imsg("사용가능한 아이디 입니다.");
			flag = true;
		}) }, { txt[2] }, { txt[3] }, { combo[0] }, { combo[1] }, { radio[0], radio[1] } };

		txt[2].setEnabled(false);
		evt(txt[2], a -> new KnockCode(txt[2]).addWindowListener(new before(this)));
		combo[0].addItem("");
		for (var a : getrows("select c_name from city"))
			combo[0].addItem(a.get(0).toString());

		combo[0].setSelectedIndex(0);

		combo[0].addItemListener(i -> {
			combo[1].removeAllItems();
			if (combo[0].getSelectedIndex() == 0)
				return;
			for (var t : getrows("select t_name from town where c_no = ?", combo[0].getSelectedIndex()))
				combo[1].addItem(t.get(0) + "");
		});

		for (int i = 0; i < cap.length; i++) {
			var temp = newJPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(new JLabel(cap[i], JLabel.LEFT), 100, 30));
			for (var j : jc[i])
				temp.add(j);
			c.add(temp);
		}

		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || combo[0].getSelectedItem().toString().isEmpty()) {
					emsg("공백이 존재합니다.");
					return;
				}
			}

			if (!flag) {
				emsg("중복확인을 해주세요.");
				return;
			}

			if (txt[2].getText().length() != 4) {
				emsg("비밀번호는 4글자로 해주세요.");
				return;
			}
			try {
				var ld = LocalDate.parse(txt[3].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
			var tno = getrows("select t_no from town where c_no = ? and t_name = ?", combo[0].getSelectedIndex(),
					combo[1].getSelectedItem() + "").get(0).get(0);

			execute("insert user values(0,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), radio[0].isSelected() ? 1 : 2, tno);
			dispose();
		}));

		setVisible(true);
	}
}
