package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import model.Game;
import model.User;

public class GamePage extends BasePage {

	Game g;

	public GamePage(Game g) {
		super("����������");
		this.g = g;
		update();
	}

	int rate = 1;

	@Override
	void update() {
		removeAll();
		var p = new JPanel(new BorderLayout());
		var pane = new JScrollPane(p);
		add(pane);
		p.setBackground(back);
		rate = 1;
		p.add(c = newJPanel(new BorderLayout()));
		c.add(cn = newJPanel(new BorderLayout()), "North");
		c.add(cc = newJPanel(new FlowLayout(FlowLayout.RIGHT)));
		c.add(cs = newJPanel(new GridLayout(0, 1)), "South");
		var tmp = newJPanel(new BorderLayout(5, 5));
		tmp.add(new JLabel(g.g_img(200, 200)), "West");
		var tmpc = newJPanel(new GridLayout(0, 1));
		tmp.add(tmpc);
		tmpc.add(wlbl("���Ӹ�:" + g.g_name, JLabel.LEFT));
		tmpc.add(wlbl("�帣:" + g.displayGenre(), JLabel.LEFT));
		tmpc.add(wlbl("����:" + g.displayAge(), JLabel.LEFT));
		tmpc.add(wlbl("����:" + g.avg(), JLabel.LEFT));
		tmpc.add(wlbl("����:" + g.displayPrice(), JLabel.LEFT));

		cn.add(tmp, "North");
		var descript = wlbl("<html>" + g.g_explan + "", JLabel.LEFT);
		descript.setVerticalTextPosition(JLabel.TOP);
		cn.add(sz(descript, 700, 300));
		JTextField txt = new JTextField(15);
		JLabel star[] = new JLabel[5];
		JButton btn = btn("���", a -> {
			if (txt.getText().isEmpty()) {
				emsg("��ĭ�� �ֽ��ϴ�.");
				return;
			}
			if (a.getActionCommand().equals("���")) {
				execute("insert review values(0,?,?,?,?)", user.u_no, g.g_no, rate, txt.getText());
			} else {
				execute("update review set r_content = ? , r_score= ? where u_no = ? and g_no = ?", txt.getText(), rate,
						user.u_no, g.g_no);
			}
			imsg("���� " + a.getActionCommand() + "�� �Ϸ�Ǿ����ϴ�.");
			mf.updateALL();
		});
		if (getrows("select * from library where u_no = ? and g_no = ?", user.u_no, g.g_no).isEmpty()) {
			cc.setLayout(new FlowLayout(FlowLayout.RIGHT));
			cc.add(btn("�����ϱ�", a -> {
				var rp = g.ownPrice(user);

				if (rp > cint(user.u_money)) {
					emsg("�ݾ��� �����մϴ�.");
					new Charge().setVisible(true);
					return;
				}
				var lst = new ArrayList<Game>();
				lst.add(g);
				execute("update user set u_money = u_money - ? where u_no = ?", rp, user.u_no);
				execute("insert library values(0,?,?,?,?)", user.u_no, g.g_no, rp, LocalDate.now().toString());
				mf.updateALL();
				new Info(lst).setVisible(true);
			}));

			cc.add(btn("��ٱ��Ͽ� �߰�", a -> {
				if (!getrows("select * from cart where g_no =? and u_no = ?", g.g_no, user.u_no).isEmpty()) {
					emsg("�̹� ��ٱ��Ͽ� �ִ� �����Դϴ�.");
					return;
				}
				execute("insert cart values(0,?,?)", user.u_no, g.g_no);
				imsg("��ٱ��Ͽ� �߰��� �Ϸ�Ǿ����ϴ�.");
				mf.history = mf.history.subList(0, 1);
				new Cart();
				mf.updateALL();
			})).setVisible(!g.displayPrice().equals("����"));
		} else {
			cc.setLayout(new FlowLayout(FlowLayout.CENTER));
			cc.add(wlbl("����", JLabel.LEFT));
			cc.add(txt);
			for (int i = 0; i < star.length; i++) {
				cc.add(star[i] = new JLabel(getIcon("./datafiles/�⺻����/��.jpg", 30, 30)));
				final int j = i;
				evt(star[i], a -> {
					rate = j + 1;
					for (int k = 0; k < 5; k++)
						star[k].setIcon(getIcon("./datafiles/�⺻����/��.jpg", 30, 30));
					for (int k = 0; k < rate; k++) {
						star[k].setIcon(getIcon("./datafiles/�⺻����/��.jpg", 30, 30));
					}
				});
			}
			cc.add(btn);
			star[0].setIcon(getIcon("./datafiles/�⺻����/��.jpg", 30, 30));
		}

		for (var r : getrows("SELECT u.*,r.* FROM review r, user u where r.u_no = u.u_no and r.g_no = ?", g.g_no)) {
			var user = new User(r);
			var item = new JPanel(new BorderLayout());
			item.add(new JLabel(user.u_img(50, 50)), "West");
			var itemc = newJPanel(new GridLayout(0, 1));
			var flag = false;
			if (BasePage.user.u_no.toString().equals(user.u_no.toString())) {
				flag = true;
				btn.setText("����");
				rate = cint(r.get(12));
				for (int i = 0; i < rate; i++)
					star[i].setIcon(getIcon("./datafiles/�⺻����/��.jpg", 30, 30));
				txt.setText(r.get(13) + "");
			}
			itemc.add(wlbl("" + "������:" + user.u_name, JLabel.LEFT)).setForeground(flag ? Color.YELLOW : Color.WHITE);
			itemc.add(wlbl("����:" + r.get(12) + "��", JLabel.LEFT)).setForeground(flag ? Color.YELLOW : Color.WHITE);
			itemc.add(wlbl(r.get(13) + "", JLabel.LEFT)).setForeground(flag ? Color.YELLOW : Color.WHITE);

			item.add(itemc);
			if (cint(r.get(12)) <= 2) {
				item.add(new JLabel(getIcon("./datafiles/�⺻����/�Ⱦ��.jpg", 50, 50)), "East");
			} else {
				item.add(new JLabel(getIcon("./datafiles/�⺻����/���ƿ�.jpg", 50, 50)), "East");
			}
			evt(item, a -> {
				mf.history = mf.history.subList(0, 1);
				new Profile(user);
			});
			item.setBorder(new LineBorder(Color.white));
			cs.add(item);
			item.setBackground(Color.BLACK);
		}

		revalidate();
		repaint();
	}

}
