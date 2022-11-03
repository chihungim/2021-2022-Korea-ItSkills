package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicLabelUI;

public class Main extends BaseFrame {

	JLabel lbl[] = new JLabel[6];
	Thread th;
	JLabel loglbl;

	public Main() {
		super("����", 800, 600);
		setLayout(new BorderLayout(5, 5));
		add(n = new JPanel(new BorderLayout()), "North");
		sz(c = new JPanel(null), 0, 300);
		add(s = new JPanel(new GridLayout(0, 3)), "South");

		n.add(f(lbl("����깰�ǸŰ���", JLabel.LEFT, 20), Color.GREEN.darker()), "West");
		n.add(loglbl = lbl("�α����� ���� ���ּ���.", JLabel.RIGHT), "East");

		var bcap = "�α���,ȸ������,�ŷ�����,��깰����,��깰�˻�,�õ����м�".split(",");
		int i = 0;
		for (var b : bcap) {
			var l = new JLabel(b, getIcon("./datafiles/�����̹���/" + b + ".jpg", 50, 50), JLabel.CENTER);
			lbl[i] = l;
			l.setUI(new BasicLabelUI() {
				@Override
				protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
					var g2 = tog2d(g);
					g2.setColor(Color.BLACK);
					g2.drawString(s, textX, textY);
				}
			});
			l.setEnabled(false);
			l.setBorder(new LineBorder(Color.LIGHT_GRAY));
			evt(l, a -> {
				if (!l.isEnabled())
					return;
				if (l.getText().equals("�α���")) {
					new Login().addWindowListener(new before(this));
				} else if (l.getText().equals("�α׾ƿ�")) {
					imsg("�α׾ƿ� �Ǿ����ϴ�.");
					logout();
				} else if (l.getText().equals("ȸ������")) {
					new Sign().addWindowListener(new before(this));
				} else if (l.getText().equals("�ŷ�����")) {
					new TradeLog().addWindowListener(new before(this));
				} else if (l.getText().equals("��깰����")) {
					new BaseManage().addWindowListener(new before(this));
				} else if (l.getText().equals("��깰�˻�")) {
					new Search().addWindowListener(new before(this));
				} else if (l.getText().equals("�õ����м�")) {
					new Map().addWindowListener(new before(this));
				} else if (l.getText().equals("��깰��ϼ���")) {
					new AdminEA().addWindowListener(new before(this));
				} else if (l.getText().equals("��������")) {
					new Cal().addWindowFocusListener(new before(this));
				}
			});
			b(lbl[i], Color.WHITE);
			s.add(sz(l, 0, 100));
			i++;
		}
		logout();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}

	void animation() {
		c.removeAll();
		add(c);
		String sql = "";
		if (user == null) {
			sql = "select b_img, b_name, b_note ,sum(s_quantity) o1 , sum(s_quantity*f_amount) o2 from sale s, farm f , base b where s.f_no = f.f_no and f.b_no = b.b_no group by b.b_no order by o1 desc, o2 desc, b.b_no limit 0,5";
		} else {
			sql = "select b_img, b_name, b_note ,sum(s_quantity) o1 , sum(s_quantity*f_amount) o2 from sale s, farm f , base b where s.f_no = f.f_no and f.b_no = b.b_no and b.division = "
					+ user.get(5) + " group by b.b_no order by o1 desc, o2 desc, b.b_no limit 0,5";
		}

		var rs = getrows(sql);
		var imgList = new ArrayList<JLabel>();
		for (var r : rs) {
			var l = new JLabel(getBlob(r.get(0), 800, 300)) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = tog2d(g);
					var y = Color.YELLOW;
					var cy = Color.CYAN;
					var ny = new Color(y.getRed(), y.getGreen(), y.getBlue(), 50);
					var ncy = new Color(cy.getRed(), cy.getGreen(), cy.getBlue(), 50);
					g2.setPaint(new GradientPaint(0, 0, ny, getWidth(), getHeight(), ncy));
					g2.fillRect(0, 0, getWidth(), getHeight());
					g2.setColor(new Color(255, 255, 255, 100));
					g2.fillRect(0, 0, getWidth(), getHeight());
					g2.setColor(Color.WHITE);
					g2.setFont(lbl("", 0, 20).getFont());
					g2.drawString(r.get(1) + "[" + (rs.indexOf(r) + 1) + "��]", 30, getHeight() - 30);
					g2.setFont(new JLabel().getFont());
					g2.drawString(r.get(2) + "", 30, getHeight() - 10);
				}
			};

			l.setBounds(imgList.size() * 800, 0, 800, 300);
			c.add(l);
			imgList.add(l);
		}

		if (th != null)
			th.stop();

		th = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				boolean flag = false;
				for (var i : imgList) {
					int x = i.getX() - 5;
					if (x <= -800) {
						x = 3200;
						flag = true;
					}
					i.setLocation(x, 0);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (flag) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
		revalidate();
		repaint();
	}

	void user() {
		animation();
		loglbl.setText(user.get(1) + "�� ȯ���մϴ�.");
		lbl[0].setText("�α׾ƿ�");
		lbl[0].setIcon(getIcon("./datafiles/�����̹���/�α׾ƿ�.jpg", 50, 50));
		for (int i = 0; i < 6; i++)
			lbl[i].setEnabled(true);
		lbl[1].setEnabled(false);
		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	void logout() {
		animation();
		loglbl.setText("�α����� ���� ���ּ���.");
		s.removeAll();
		var bcap = "�α���,ȸ������,�ŷ�����,��깰����,��깰�˻�,�õ����м�".split(",");
		for (int i = 0; i < 6; i++) {
			s.add(lbl[i]);
			lbl[i].setEnabled(i < 2);

			lbl[i].setText(bcap[i]);
			lbl[i].setIcon(getIcon("./datafiles/�����̹���/" + bcap[i] + ".jpg", 50, 50));
			lbl[i].setVerticalTextPosition(JLabel.CENTER);
			lbl[i].setHorizontalTextPosition(JLabel.RIGHT);
		}

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	void admin() {
		remove(c);
		setSize(350, 200);
		setLocationRelativeTo(null);
		s.removeAll();

		for (int i = 0; i < 3; i++)
			s.add(lbl[i]);

		var bcap = "�α׾ƿ�,��깰��ϼ���,��������".split(",");

		for (int i = 0; i < 3; i++) {
			lbl[i].setText(bcap[i]);
			lbl[i].setIcon(getIcon("./datafiles/�����̹���/" + bcap[i] + ".jpg", 50, 50));
			lbl[i].setVerticalTextPosition(JLabel.BOTTOM);
			lbl[i].setHorizontalTextPosition(JLabel.CENTER);
			lbl[i].setEnabled(true);
		}
	}
}
