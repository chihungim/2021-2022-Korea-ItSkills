package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {

	JPanel main_n, main_c, main_s;
	JLabel logolbl;
	String icon[] = { "<html><center>&#127968<br>메인", "<html><center>&#128269<br>검색",
			"<html><center>&#128100<br>마이페이지" };
	String cap[] = "메인,검색,마이페이지".split(",");
	JButton loginbtn;
	JLabel panels[] = new JLabel[3];

	public MainFrame() {
		super("Fn Mart");
		setSize(1100, 700);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setBackground(Color.WHITE);
		add(BasePage.setB(main_n = new JPanel(new BorderLayout()), new LineBorder(Color.BLACK)), "North");
		add(BasePage.setB(main_c = new JPanel(new GridLayout()), new LineBorder(Color.BLACK)));
		add(BasePage.setB(main_s = new JPanel(new GridLayout(0, 3, 50, 0)), new LineBorder(Color.BLACK)), "South");

		main_n.add(logolbl = BasePage.lbl("FN", JLabel.LEFT, 25), "West");
		main_n.add(BasePage.sz(loginbtn = BasePage.btn("Login", a -> {
			if (a.getActionCommand().equals("Login")) {
				swapPage(new LoginPage());
			} else {
				BasePage.logOut();

			}
		}), 120, 25), "East");
		loginbtn.setBorderPainted(false);

		for (int i = 0; i < cap.length; i++) {
			main_s.add(panels[i] = BasePage.lbl(icon[i], JLabel.CENTER, 20));
			panels[i].setForeground(Color.LIGHT_GRAY);
		}

		panels[0].setForeground(Color.BLACK);

		logolbl.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (BasePage.sno != 0) {
					swapPage(new ManagePage());
				} else {
					swapPage(new MainPage());
				}
				super.mousePressed(e);
			};
		});

		Arrays.stream(panels).forEach(a -> {
			a.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					Arrays.stream(panels).forEach(a -> a.setForeground(Color.LIGHT_GRAY));
					int idx = Arrays.stream(panels).collect(Collectors.toList()).indexOf((JLabel) e.getSource());

					if (idx == 0) {
						if (BasePage.sno == 0) {
							swapPage(new MainPage());
						} else {
							swapPage(new ManagePage());
						}
					} else if (idx == 1) {
						if (BasePage.uno == 0) {
							panels[0].setForeground(Color.BLACK);
							BasePage.eMsg("로그인 하지 않았습니다.");
							return;
						}
						swapPage(new SearchPage());
					} else {
						if (BasePage.uno == 0) {
							panels[0].setForeground(Color.BLACK);
							BasePage.eMsg("로그인 하지 않았습니다.");
							return;
						}
						swapPage(new MyPage());
					}
					a.setForeground(Color.BLACK);
					super.mousePressed(e);
				}

			});
		});

	}

	void menuInit(Boolean user) {

		main_s.removeAll();
		if (user) {
			main_s.setLayout(new GridLayout(0, 3, 30, 50));
			for (int i = 0; i < panels.length; i++) {
				main_s.add(panels[i]);
				panels[i].setForeground(Color.LIGHT_GRAY);
			}
		} else {
			main_s.setLayout(new BorderLayout());
			main_s.add(panels[0]);
		}
		panels[0].setForeground(Color.BLACK);
		repaint();
		revalidate();
	}

	void swapPage(BasePage page) {
		main_c.removeAll();
		main_c.add(page);
		main_c.revalidate();
		main_c.repaint();
	}

	public static void main(String[] args) {
		BasePage.mf.setVisible(true);
		BasePage.mf.swapPage(new MainPage());
	}

}
