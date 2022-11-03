package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Purchase extends BaseFrame {

	JLabel img;
	ArrayList<Object> farm;
	ArrayList<Object> base;
	ArrayList<Object> seller;
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField() };
	JTextArea area = new JTextArea(3, 12);

	public Purchase(ArrayList<Object> farm) {
		super("��ǰ����", 600, 500);

		this.farm = farm;
		this.base = getrows("Select * from base where b_no = ?", farm.get(2)).get(0);
		this.seller = getrows("select * from user where u_no = ?", farm.get(1)).get(0);

		add(c = new JPanel(new GridLayout(1, 0, 5, 5)));
		add(s = new JPanel(new BorderLayout()), "South");

		s.add(sw = new JPanel(new BorderLayout()), "West");
		s.add(sc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)));

		c.add(img = new JLabel(getBlob(base.get(5), 300, 300)));
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		var cap = "��ǰ��,�Ǹ���,����,���,����,�հ�".split(",");

		for (int i = 0; i < cap.length; i++) {
			cc.add(lbl(cap[i], JLabel.LEFT));
			cc.add(txt[i]);
			txt[i].setEnabled(false);
		}

		txt[0].setText(base.get(2) + "");
		txt[1].setText(user.get(1) + "");
		txt[2].setText(farm.get(3) + "");
		txt[3].setText(farm.get(4) + "");
		txt[4].setEnabled(true);

		txt[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txt[4].getText().isEmpty()) {
					txt[5].setText("");
					return;
				}

				if (cint(txt[4].getText()) > cint(txt[3].getText())) {
					emsg("��� �����մϴ�.");
					txt[4].setText("");
					return;
				}

				if (!isNumeric(txt[4].getText()) || cint(txt[4].getText()) < 1) {
					emsg("1�̻��� ���ڷ� �Է��ϼ���.");
					txt[4].setText("");
					return;
				}

				txt[5].setText(cint(txt[4].getText()) * cint(txt[2].getText()) + "");
			}
		});

		sw.add(lbl("��ǰ����", JLabel.LEFT), "North");
		sw.add(sz(area, 50, 0));
		area.setLineWrap(true);
		area.setEditable(false);
		area.setText(base.get(4) + "");
		area.setBorder(new LineBorder(Color.BLACK));
		sc.add(btn("�����ϱ�", a -> {
			if (txt[4].getText().isEmpty()) {
				emsg("������ �Է��ϼ���.");
				return;
			}

			execute("update farm set f_quantity = f_quantity - ? where f_no = ? and u_no = ?", txt[4].getText(),
					farm.get(0), seller.get(0));
			execute("insert purchase values(0,?,?,?,?)", user.get(0), farm.get(0), LocalDate.now().toString(),
					txt[4].getText());
			execute("insert sale values(0,?,?,?,?)", user.get(0), farm.get(0), LocalDate.now().toString(),
					txt[4].getText());
			imsg(base.get(2) + " " + txt[4].getText() + "���� �����Ͽ����ϴ�.");
			new Receipt(Purchase.this).addWindowListener(new before(this));
		}));
		setVisible(true);
	}

}
