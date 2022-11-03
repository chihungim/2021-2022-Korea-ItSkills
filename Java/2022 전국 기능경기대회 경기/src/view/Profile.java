package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Game;
import model.User;

public class Profile extends BasePage {
	User u;

	public Profile(User u) {
		super("����������");
		this.u = u;
		update();
	}

	public static void main(String[] args) {
		�����("1");
	}

	@Override
	void update() {
		removeAll();
		add(c = newJPanel(new BorderLayout()));
		c.add(new JLabel(u.u_img(150, 150)), "West");
		c.add(cc = newJPanel(new GridLayout(0, 1)));

		cc.add(wlbl("�г���:" + u.u_name, JLabel.LEFT));
		cc.add(wlbl("���� �ܾ�:" + u.u_money, JLabel.LEFT))
				.setVisible(BasePage.user.u_no.toString().equals(u.u_no.toString()));
		var l = wlbl("����ġ :" + u.exp() + "(���: " + g_gd[u.gd()] + ")", JLabel.LEFT);
		l.setIcon(getIcon("./datafiles/��޻���/" + u.gd() + ".jpg", 30, 30));
		l.setHorizontalTextPosition(JLabel.LEFT);
		cc.add(l);
		var temp = newJPanel(new FlowLayout(FlowLayout.LEFT));
		cc.add(temp);
		var prg = new JProgressBar(1, Math.min(100, u.gd() * 20 + 20));
		prg.setValue(u.exp());
		prg.setStringPainted(true);
		prg.setString(u.exp() + "/" + Math.min(100, u.gd() * 20 + 20));
		temp.add(prg);

		add(sz(s = newJPanel(new BorderLayout()), 0, 150), "South");
		s.add(wlbl("������ ����", JLabel.LEFT), "North");
		if (!BasePage.user.u_no.toString().equals(u.u_no.toString()) || BasePage.user.u_ox.equals("0")) {
			s.add(wlbl("�����", JLabel.CENTER, 20));
		} else {
			s.add(new JScrollPane(sc = new JPanel(new GridLayout(0, 1))));
			sc.setBackground(back);
			for (var r : getrows("select g.*, l.* from game g, library l where g.g_no = l.g_no and l.u_no = ?",
					u.u_no)) {
				var tmp = newJPanel(new BorderLayout(5, 5));
				var g = new Game(r);
				tmp.add(new JLabel(g.g_img(80, 80)), "West");
				var tmpc = newJPanel(new GridLayout(0, 1));
				tmp.add(tmpc);
				tmpc.add(wlbl("���Ӹ�:" + g.g_name, JLabel.LEFT));
				tmpc.add(wlbl("�帣:" + g.displayGenre(), JLabel.LEFT));
				tmpc.add(wlbl("����:" + g.displayAge(), JLabel.LEFT));
				tmpc.add(wlbl("����:" + g.avg(), JLabel.LEFT));
				tmpc.add(wlbl("����:" + g.displayPrice(), JLabel.LEFT));
				tmp.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
				sc.add(tmp);
				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (BasePage.user.u_no.toString().equals(u.u_no.toString())) {
							var pop = new JPopupMenu();
							var item = new JMenuItem("ȯ��");
							pop.add(item);
							if (e.getButton() == 3)
								pop.show(tmp, e.getX(), e.getY());
							else {
								if (!g.validate(user)) {
									emsg("������ ���� �ʽ��ϴ�.");
								}
								new GamePage(g);
							}
							item.addActionListener(a -> {
								var d = LocalDate.parse(r.get(14) + "", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
								if (d.plusDays(14).isAfter(LocalDate.now())) {
									emsg("ȯ�� ������ �Ⱓ�� �������ϴ�.");
									return;
								}
								execute("update user set u_money = u_money + ? where u_no = ?", r.get(13), u.u_no);
								execute("delete from library where l_no = ?", r.get(10));
								imsg("ȯ���� �Ϸ�Ǿ����ϴ�.");
								mf.updateALL();
							});
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						tmp.setBorder(new CompoundBorder(new LineBorder(Color.red), new EmptyBorder(5, 5, 5, 5)));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						tmp.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
					}
				});
			}
		}
		revalidate();
		repaint();
	}
}
