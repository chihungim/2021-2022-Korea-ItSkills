package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {

	JLabel img, name;
	JComboBox<String> combo;
	JTextField txt;
	JPanel m, m1, m2;

	public Main() {
		super("����", 450, 600);
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(nc = new JPanel(new FlowLayout(1)));
		n.add(ns = new JPanel(new FlowLayout(1)), "South");

		nc.add(hylbl("�Ƹ�����Ʈ", 0, 30), "North");
		nc.add(img = lbl("", 0));
		nc.add(name = lbl("", 0));

		img.setBorder(new LineBorder(Color.BLACK));

		img.setVisible(false);
		name.setVisible(false);

		ns.add(lbl("����˻�", 0, 15));
		ns.add(txt = new JTextField(15));
		ns.add(btn("�˻�", a -> {
			if (txt.getText().isEmpty()) {
				emsg("�˻��� ������� �Է��ϼ���.");
				return;
			}

			var rs = getValues("select * from company where c_name like ? order by c_no", "%" + txt.getText() + "%");

			if (rs.isEmpty()) {
				emsg("�˻��� ����� �����ϴ�.");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			new Detail(rs.get(0), false).addWindowListener(new before(this));

			famous();
		}));

		c.add(lbl("�α���", JLabel.LEFT, 15), "North");
		c.add(m = new JPanel(new GridLayout(1, 0)));
		m.add(m1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		for (var bc : "�α���,ȸ������,�ݱ�,�ݱ�".split(",")) {
			m2.add(btn(bc, a -> {
				if (a.getActionCommand().equals("�α���")) {
					new Login(this).addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("ȸ������")) {
					new Sign().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("�ݱ�")) {
					System.exit(0);
				} else if (a.getActionCommand().equals("�α׾ƿ�")) {
					logout();
				} else if (a.getActionCommand().equals("ä������")) {
					new Jobs().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("����������")) {
					new Mypage().addWindowListener(new before(this));
				}
			}));
		}

		s.add(sn = new JPanel(new FlowLayout(0)), "North");
		s.add(sz(sc = new JPanel(null), 100, 100));

		sn.add(lbl("����", 0, 15));
		sn.add(combo = new JComboBox<String>(local));

		famous();
		animation();

		combo.addItemListener(i -> {
			if (i.getStateChange() == 1)
				animation();
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);
	}

	void famous() {
		m1.removeAll();

		for (var r : getValues(
				"select row_number() over(), c_name, c_search from company order by c_search desc, c_no asc limit 0,5")) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(sz(lbl(r.get(0) + "", 0), 50, 30), "West");
			temp.add(lbl(r.get(1) + "", JLabel.LEFT));
			temp.add(sz(lbl(r.get(2) + "", JLabel.LEFT), 50, 30), "East");
			m1.add(temp);
		}
		logout();
		repaint();
		revalidate();
	}

	void animation() {
		sc.removeAll();
		var imglist = new ArrayList<JPanel>();
		var rs = getValues("select c_img, c_name from company where left(c_address,2) like ?",
				combo.getSelectedIndex() == 0 ? "%%" : "%" + combo.getSelectedItem() + "%");

		if (rs.isEmpty()) {
			emsg("������ ��������� �����ϴ�.");
			combo.setSelectedIndex(0);
			animation();
			return;
		}

		for (int i = 0; i < rs.size(); i++) {
			var r = rs.get(i);
			var temp = new JPanel(new BorderLayout());
			temp.add(new JLabel(toIcon(r.get(0), 80, 80)));
			temp.add(lbl(r.get(1) + "", 0), "South");
			temp.setBorder(new LineBorder(Color.BLACK));
			sc.add(temp);
			temp.setBounds(i * 100, 0, 100, 100);
			imglist.add(temp);
		}

		var total = imglist.get(imglist.size() - 1).getX();

		var t = new Timer(1, a -> {
			for (var img : imglist) {
				int x = img.getX() - 5;
				if (x <= -100)
					x = total;
				img.setLocation(x, 0);
			}
		});

		t.start();

		revalidate();
		repaint();

	}

	public static void main(String[] args) {
		new Main();
	}

	void login() {
		((JButton) m2.getComponent(0)).setText("�α׾ƿ�");
		name.setVisible(true);
		img.setVisible(true);
		((JButton) m2.getComponent(1)).setText("ä������");
		((JButton) m2.getComponent(2)).setText("����������");
		((JButton) m2.getComponent(3)).setText("�ݱ�");
		((JButton) m2.getComponent(3)).setVisible(true);
	}

	void logout() {
		((JButton) m2.getComponent(0)).setText("�α���");
		name.setVisible(true);
		img.setVisible(true);
		((JButton) m2.getComponent(1)).setText("ȸ������");
		((JButton) m2.getComponent(2)).setText("�ݱ�");
		((JButton) m2.getComponent(3)).setVisible(false);
	}
}
