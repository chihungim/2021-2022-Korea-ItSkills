package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MainFrame extends BaseFrame {

	String page = "";

	JButton btn[] = new JButton[4];

	SystemTray tray = SystemTray.getSystemTray();

	TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/비행기.jpg"));
	{
		icon.setImageAutoSize(true);
		var pop = new PopupMenu();
		var item1 = new MenuItem("일정보기");
		var item2 = new MenuItem("닫기");

		pop.add(item1);
		pop.add(item2);

		item2.addActionListener(a -> {
			tray.remove(icon);
		});

		item1.addActionListener(a -> {
			swap2("예약하기");
		});

		icon.setPopupMenu(pop);

		try {
			tray.add(icon);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		var rs = getrows(
				"select a1.a_name , a2.a_name, r_date, s_time from schedule  s, airport a1, airport a2, reservation r where a1.a_no = s.s_depart and a2.a_no = s.s_arrival and r.s_no = s.s_no and r.m_no = ? and concat(r_date, ' ', s_time) >= now() order by date(r_date), time(s_time)",
				BasePage.member.get(0));

		if (!rs.isEmpty()) {
			icon.displayMessage(rs.get(0).get(0) + " → " + rs.get(0).get(1), rs.get(0).get(2) + " " + rs.get(0).get(1),
					MessageType.INFO);
		}

	}

	public MainFrame() {
		super("", 800, 600);
		var cap = "예약하기,예약조회,마이페이지,로그아웃".split(",");
		BasePage.mf = this;
		var tool = new JToolBar();
		add(tool, "North");
		for (int i = 0; i < btn.length; i++) {
			btn[i] = btn(cap[i], a -> {
				swap2(a.getActionCommand());
			});
			tool.add(btn[i]);
		}

		add(c = new JPanel(new BorderLayout()));
		swap2("예약하기");

		setVisible(true);
	}

	public static void main(String[] args) {
		Base.디버그("1");
	}

	void swap2(String page) {
		if (this.page.equals("예약하기") && !page.equals("예약하기")) {
			if (JOptionPane.showConfirmDialog(this, "예약을 종료하시겠습니까?", "메시지", JOptionPane.YES_NO_OPTION) != 0) {
				return;
			}
		}

		this.page = page;

		for (int i = 0; i < 4; i++) {
			btn[i].setForeground(null);
			btn[i].setBackground(null);
			if (btn[i].getText().equals(page)) {
				btn[i].setForeground(Color.WHITE);
				btn[i].setBackground(Color.BLUE);
			}
		}

		if (page.equals("예약하기")) {
			swap(new Reserve());
		} else if (page.equals("예약조회")) {
			swap(new Reservation());
		} else if (page.equals("마이페이지")) {
			swap(new Mypage());
		} else {
			imsg("로그아웃이 완료되었습니다.");
			dispose();
		}
	}

	void swap(JPanel p) {
		c.removeAll();
		c.add(p);
		revalidate();
		repaint();
	}
}
