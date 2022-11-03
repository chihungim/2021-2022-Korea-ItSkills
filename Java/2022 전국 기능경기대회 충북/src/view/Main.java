package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	JLabel lbl[] = new JLabel[4];
	JScrollPane pane;
	JLabel prev, next;
	boolean 한칸 = true;

	public Main() {
		super("GGV", 1040, 800);
		setLayout(new BorderLayout(5, 5));
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafile/Logo.png"));

		var l = lbl("<html><font face = 'HY헤드라인M' size = '15'>GGV</font><font size = '6'>MOVIE", JLabel.CENTER);
		l.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(l, "North");
		add(sz(c = newJPanel(new BorderLayout(5, 5)), 0, 300));
		add(sz(s = newJPanel(new BorderLayout(5, 5)), 0, 300), "South");

		var cap = "로그인,회원가입,마이페이지,통계".split(",");
		var icon = "Lock,Join,People,Analytics".split(",");

		for (int i = 0; i < cap.length; i++) {
			lbl[i] = lbl(cap[i], JLabel.CENTER);
			lbl[i].setIcon(getIcon("./datafile/아이콘/" + icon[i] + ".png", 15, 15));
			lbl[i].setVerticalTextPosition(JLabel.BOTTOM);
			lbl[i].setHorizontalTextPosition(JLabel.CENTER);
			l.add(lbl[i]);
			evt(lbl[i], a -> {
				var me = (JLabel) a.getSource();
				if (me.getText().equals("로그인")) {
					new Login().addWindowListener(new before(this));
				} else if (me.getText().equals("회원가입")) {
					new Sign().addWindowListener(new before(this));
				} else if (me.getText().equals("마이페이지")) {
					if (uno == null) {
						emsg("로그인을 먼저 해주세요.");
						new Login().addWindowListener(new before(this));
						return;
					}
					new MyPage().addWindowListener(new before(this));
				} else if (me.getText().equals("통계")) {
					new Chart().addWindowListener(new before(this));
				} else if (me.getText().equals("로그아웃")) {
					uno = null;
					login();
				}
			});
		}

		c.add(cn = newJPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		c.add(cc = newJPanel(null));

		for (var bcap : "예매,영화,영화관,스토어".split(",")) {
			var l2 = hylbl(bcap, JLabel.CENTER, 15);
			l2.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (l2.getText().equals("예매")) {
						new Reservation().addWindowListener(new before(Main.this));
					} else if (l2.getText().equals("영화")) {
						new MovieList().addWindowListener(new before(Main.this));
					} else if (l2.getText().equals("영화관")) {
						new Theater().addWindowListener(new before(Main.this));
					} else if (l2.getText().equals("스토어")) {
						new Store().addWindowListener(new before(Main.this));
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					l2.setBorder(new MatteBorder(0, 0, 1, 0, Color.RED));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					l2.setBorder(null);
				}
			});

			cn.add(sz(l2, 80, 30));
			if (!bcap.equals("스토어"))
				cn.add(mkBar(13)).setForeground(Color.LIGHT_GRAY);
		}
		l.setFont(new Font(Font.SANS_SERIF, 0, 13));

		var imgList = new ArrayList<JLabel>();

		for (var f : new File("./datafile/배경").listFiles()) {
			var img = new JLabel(getIcon(f.getPath(), 1045, 280));
			cc.add(img).setBounds(imgList.size() * 1045, 0, 1045, 280);
			imgList.add(img);
		}

		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (true) {
				boolean flag = false;
				for (var i : imgList) {
					int x = i.getX() - 5;
					if (x <= -1045) {
						x = 2090;
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
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		s.add(prev = new JLabel(getIcon("./datafile/아이콘/Left.png", 50, 50)), "West");
		s.add(pane = new JScrollPane(sc = new JPanel(new GridLayout(1, 0, 5, 5))));
		s.add(next = new JLabel(getIcon("./datafile/아이콘/Right.png", 50, 50)), "East");

		var data = getrows(
				"select m.*,count(*) cnt from reservation r , schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by cnt desc limit 0 ,25");

		for (var d : data) {
			var temp = newJPanel(new BorderLayout());

			var poster = new JLabel(getIcon("./datafile/영화/" + d.get(5) + ".jpg", 180, 250));
			temp.add(poster);
			sc.add(sz(temp, 180, 280));
			temp.add(lbl(d.get(5) + "", JLabel.CENTER), "South");
			evt(poster, e -> {
				if (JOptionPane.showConfirmDialog(null, "예매하시겠습니까?", "정보", JOptionPane.INFORMATION_MESSAGE) == 0) {
					new Reservation().addWindowListener(new before(this));
				} else {
					new MovieInfo(d).addWindowListener(new before(this));
				}
			});
		}

		evt(prev, a -> {
			if (prev.isEnabled()) {
				move(-1);
			}
		});

		evt(next, a -> {
			if (next.isEnabled()) {
				move(1);
			}
		});
		sc.setBackground(Color.WHITE);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setBorder(null);
		prev.setEnabled(false);
		setVisible(true);
	}

	void move(int dir) {
		if (!한칸)
			return;
		new Thread(() -> {
			int step = 0;
			var h = pane.getHorizontalScrollBar();
			한칸 = false;
			while (true) {
				int v = h.getValue() + (dir * 5);
				h.setValue(v);

				prev.setEnabled(h.getValue() != 0);
				next.setEnabled(h.getValue() <= 3600);
				step += Math.abs(dir * 5);
				System.out.println(step);
				if (step >= 185)
					break;
				repaint();
				revalidate();
				sc.repaint();
			}
			한칸 = true;
		}).start();

	}

	void login() {
		if (uno == null) {
			lbl[0].setVisible(true);
			lbl[1].setText("회원가입");
			lbl[1].setIcon(getIcon("./datafile/아이콘/Join.png", 15, 15));
		} else {
			lbl[0].setVisible(false);
			lbl[1].setText("로그아웃");
			lbl[1].setIcon(getIcon("./datafile/아이콘/Unlock.png", 15, 15));
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
