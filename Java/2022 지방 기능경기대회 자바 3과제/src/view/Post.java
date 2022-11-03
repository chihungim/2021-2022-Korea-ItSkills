package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.concurrent.Flow;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Post extends BaseFrame {
	JButton btn, btn2;
	JComboBox combo[] = { new JComboBox<String>(), new JComboBox<String>(graduate) };

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15) };

	JRadioButton rbtn[] = { new JRadioButton("��"), new JRadioButton("��"), new JRadioButton("����") };

	JComponent jc[][] = { { combo[0] }, { txt[0] }, { txt[1] }, { txt[2] }, { rbtn[0], rbtn[1], rbtn[2] },
			{ combo[1] } };

	public Post() {
		super("������", 500, 500);
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		var cap = "ȸ���,������,�ñ�,��������,����,�����з�".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			for (int j = 0; j < jc[i].length; j++) {
				temp.add(jc[i][j]);
			}
			c.add(temp);
		}

		var bg = new ButtonGroup();
		for (var r : rbtn)
			bg.add(r);

		for (var rs : getValues("select c_name from company where not c_no in (select c_no from employment)"))
			combo[0].addItem(rs.get(0));

		s.add(btn = btn("���", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					emsg("��ĭ�� �����մϴ�.");
					return;
				}
			}

			if (txt[1].getText().matches(".*[^0-9].*") || txt[2].getText().matches(".*[^0-9].*")) {
				emsg("���ڷ� �Է��ϼ���.");
				return;
			}

			if (a.getActionCommand().equals("���")) {
				setValues("insert employment values(0,?,?,?,?,?,?)",
						getOne("select c_no from company where c_name = ?", combo[0].getSelectedItem()),
						txt[0].getText(), txt[1].getText(), txt[2].getText(),
						rbtn[0].isSelected() ? 1 : rbtn[1].isSelected() ? 2 : 3, combo[1].getSelectedIndex());
				imsg("����� �Ϸ�Ǿ����ϴ�.");
				dispose();
			} else {
				if (toInt(txt[2].getText()) < toInt(
						getOne("select count(*) from applicant where e_no = ? ", employment.get(0)))) {
					emsg("���������� ������ ���� �����ϴ�.");
					return;
				}

				setValues("update employment set e_title = ? , e_pay = ? , e_people= ? where e_no = ?", txt[0].getText(),
						txt[1].getText(), txt[2].getText(), employment.get(0));
				imsg("������ �Ϸ�Ǿ����ϴ�.");
				dispose();
			}
		}));

		s.add(btn2 = btn("����", a -> {
			setValues("delete from employment where e_no = ?", employment.get(0));
			imsg("������ �Ϸ�Ǿ����ϴ�.");
			dispose();
		}));

		rbtn[0].setSelected(true);
		btn2.setVisible(false);

		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "��������"));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Post("1");
	}

	ArrayList<Object> employment;

	public Post(String eno) {
		this();
		btn.setText("����");

		combo[0].removeAllItems();

		for (var rs : getValues("select c_name from company where c_no in (select c_no from employment)"))
			combo[0].addItem(rs.get(0));

		combo[0].addItemListener(i -> {
			if (i.getStateChange() == 1) {
				employment = getValues("select e.* from employment e , company c where e.c_no = c.c_no and c.c_name = ?",
						combo[0].getSelectedItem()).get(0);

				txt[0].setText(employment.get(2) + "");
				txt[1].setText(employment.get(3) + "");
				txt[2].setText(employment.get(4) + "");
				
				rbtn[toInt(employment.get(5)) - 1].setSelected(true);
				combo[1].setSelectedIndex(toInt(employment.get(6)));

				btn2.setVisible(getOne("select * from applicant where e_no = ?", employment.get(0)) == null);
			}
		});

		var cname = getOne("select c_name from employment e , company c where e.c_no = c.c_no and e.e_no = ?", eno)
				+ "";
		for (int i = 0; i < combo[0].getItemCount(); i++) {
			if (combo[0].getItemAt(i).toString().equals(cname)) {
				combo[0].setSelectedIndex(1);
				combo[0].setSelectedIndex(i);
			}
		}

		for (var r : rbtn)
			r.setEnabled(false);
		combo[1].setEnabled(false);
	}
}
