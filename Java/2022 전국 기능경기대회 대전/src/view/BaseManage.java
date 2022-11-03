package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class BaseManage extends BaseFrame {

	JButton btn[] = new JButton[2];
	JComboBox<String> combo = new JComboBox<String>(getrows("Select b_name from base where division = ?", user.get(5))
			.stream().map(a -> a.get(0)).toArray(String[]::new));

	JLabel img;
	JTextField txt[] = { new JTextField(15), new JTextField(15) };
	String bno, uno;

	public BaseManage() {
		super("농산물관리", 600, 300);
		setLayout(new BorderLayout(5, 5));
		add(c = newJPanel(new BorderLayout(5, 5)));
		add(s = newJPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		c.add(img = new JLabel(), "West");
		c.add(cc = newJPanel(new GridLayout(0, 1)));

		sz(img, 200, 200);
		img.setBorder(new LineBorder(Color.BLACK));
		uno = user.get(0) + "";
		var cap = "제품명,단가,수량".split(",");

		JComponent jc[] = { combo, txt[0], txt[1] };

		combo.setSelectedIndex(0);

		for (int i = 0; i < cap.length; i++) {
			var temp = newJPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(new JLabel(cap[i], JLabel.LEFT), 60, 30));
			temp.add(jc[i]);
			cc.add(temp);
		}

		var bcap = "수정,수정".split(",");

		for (int i = 0; i < bcap.length; i++) {
			btn[i] = btn(bcap[i], a -> {

				if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
					emsg("공백이 있습니다.");
					return;
				}

				if (!isNumeric(txt[0].getText()) || !isNumeric(txt[1].getText())) {
					emsg("1이상의 숫자로 입력해주세요.");
					return;
				}

				if (cint(txt[0].getText()) < 1 || cint(txt[1].getText()) < 1) {
					emsg("1이상의 숫자로 입력해주세요.");
					return;
				}

				if (a.getActionCommand().equals("수정")) {
					execute("update farm set f_quantity  = ? , f_amount = ? where u_no = ? and b_no = ?",
							txt[1].getText(), txt[0].getText(), user.get(0), bno);
				} else if (a.getActionCommand().equals("삭제")) {
					execute("delete from farm where b_no = ? and u_no = ?", bno, uno);
				} else if (a.getActionCommand().equals("등록")) {
					execute("insert farm values(0,?,?,?,?)", uno, bno, txt[0].getText(), txt[1].getText());
				}

				imsg(a.getActionCommand() + "가 완료되었습니다.");
				change();
			});
			s.add(btn[i]);
		}

		combo.addItemListener(i -> change());

		setVisible(true);
	}

	void change() {
		var base = getrows("select * from base where b_name = ?", combo.getSelectedItem() + "").get(0);
		bno = base.get(0) + "";
		var farm = getrows("select * from farm where u_no = ? and b_no = ?", user.get(0), base.get(0));
		btn[0].setVisible(false);
		img.setIcon(getBlob(base.get(5), 200, 200));

		if (!farm.isEmpty()) {
			txt[0].setText(farm.get(0).get(3) + "");
			txt[1].setText(farm.get(0).get(4) + "");
			btn[1].setText("수정");
			if (cint(txt[1].getText()) == 0) {
				btn[0].setVisible(true);
				btn[1].setText("삭제");
			}
		} else {
			txt[0].setText("");
			txt[1].setText("");
			btn[1].setText("등록");
		}
	}

	public BaseManage(String bno, String uno, boolean b) {
		this();
		this.bno = bno;
		this.uno = uno;
		var base = getrows("select * from base where b_no = ?", bno);
		combo.setSelectedItem(base.get(0).get(2) + "");
		change();

		if (!b) {
			combo.setEnabled(false);
			txt[0].setEnabled(false);
			txt[1].setEnabled(false);
			btn[0].setVisible(false);
			btn[1].setVisible(false);
		}
	}

}
