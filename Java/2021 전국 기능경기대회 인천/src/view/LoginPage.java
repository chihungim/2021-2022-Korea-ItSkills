package view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPage extends BasePage {
	String bcap[] = "구매자,판매자".split(",");
	String tcap[] = "user,seller".split(",");
	JTextField txt[] = { new JTextField(), new JTextField() };
	JPanel page;
	JLabel lbl;
	String cap[] = "아이디,비밀빈호".split(",");
	int login;
	JLabel sign;
	int cnt;

	public LoginPage() {
		setLayout(new BorderLayout());
		add(page = new JPanel(new GridBagLayout()));
		var grid = new JPanel(new GridLayout(1, 0, 10, 5));
		page.add(grid);

		for (int i = 0; i < cap.length; i++) {
			int idx = i;
			grid.add(sz(btn(bcap[i] + "로 로그인", a -> {
				login = idx;
				loginField();
			}), 150, 150));
		}
	}

	void loginField() {
		page.removeAll();
		page.add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(s = new JPanel(new GridLayout(1, 0, 10, 10)), "South");
		var cc = new JPanel(new GridLayout(0, 1, 10, 10));
		c.add(cc);
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();
			tmp.add(sz(new JLabel(cap[i]), 60, 25));
			tmp.add(sz(txt[i], 230, 35));
			cc.add(tmp);
			tmp.setOpaque(false);
		}

		txt[1].addKeyListener(new KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent e) {
				e.consume();
			};
		});

		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new Pattern(txt[1]).setVisible(true);
				super.mouseClicked(e);
			}
		});

		s.add(sz(btn(bcap[login] + "로 로그인", a -> {
			var id = txt[0].getText();
			var pw = txt[1].getText();
			if (id.isEmpty() || pw.isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			if (pw.length() < 3) {
				eMsg("패턴은 3자리 이상으로 입력해주세요.");
				return;
			}

			String sql = (login == 0) ? "select * from user where u_Id = '" + id + "' and u_Pattern = '" + pw + "'"
					: "select * from seller where s_Id = '" + id + "' and s_Pattern = '" + pw + "'";

			try {
				var rs = rs(sql);
				if (rs.next()) {
					if (login == 0) {
						uno = rs.getInt(1);
						uname = rs.getString(4);
						uAddr = rs.getInt(5);
						iMsg(uname + "회원님, 환영합니다.");
						mf.menuInit(true);
						mf.loginbtn.setText("Logout");
						mf.swapPage(new MainPage());
					} else {
						sno = rs.getInt(1);
						sname = rs.getString(4);
						sAddr = rs.getInt(5);
						iMsg(sname + "판매자님, 환영합니다.");
						mf.menuInit(false);
						mf.loginbtn.setText("Logout");
						mf.swapPage(new ManagePage());
					}
				} else {
					eMsg("아이디나 패턴을 다시 확인해주세요.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}), 1, 30));

		revalidate();
		repaint();
	}
}
