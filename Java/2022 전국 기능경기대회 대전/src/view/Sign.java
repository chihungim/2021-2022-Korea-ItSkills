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
	JRadioButton radio[] = { new JRadioButton("��ä", true), new JRadioButton("����") };
	ButtonGroup bg = new ButtonGroup();
	boolean flag = false;

	public Sign() {
		super("ȸ������", 450, 350);
		for (var r : radio)
			bg.add(r);
		add(c = newJPanel(new GridLayout(0, 1)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		var cap = "�̸�,���̵�,��й�ȣ,�������,����,��������,����".split(",");

		JComponent jc[][] = { { txt[0] }, { txt[1], btn("�ߺ�Ȯ��", a -> {
			if (txt[1].getText().isEmpty()) {
				emsg("���̵� �Է��ϼ���.");
				return;
			}

			if (!getrows("select * from user where u_id = ?", txt[1].getText()).isEmpty()) {
				emsg("�̹� �����ϴ� ���̵� �Դϴ�.");
				txt[1].setText("");
				return;
			}

			imsg("��밡���� ���̵� �Դϴ�.");
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

		s.add(btn("ȸ������", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || combo[0].getSelectedItem().toString().isEmpty()) {
					emsg("������ �����մϴ�.");
					return;
				}
			}

			if (!flag) {
				emsg("�ߺ�Ȯ���� ���ּ���.");
				return;
			}

			if (txt[2].getText().length() != 4) {
				emsg("��й�ȣ�� 4���ڷ� ���ּ���.");
				return;
			}
			try {
				var ld = LocalDate.parse(txt[3].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				if (ld.isAfter(LocalDate.now())) {
					emsg("��������� Ȯ�����ּ���.");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				emsg("��������� Ȯ�����ּ���.");
				return;
			}

			imsg("ȸ�������� �Ϸ�Ǿ����ϴ�.");
			var tno = getrows("select t_no from town where c_no = ? and t_name = ?", combo[0].getSelectedIndex(),
					combo[1].getSelectedItem() + "").get(0).get(0);

			execute("insert user values(0,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), radio[0].isSelected() ? 1 : 2, tno);
			dispose();
		}));

		setVisible(true);
	}
}
