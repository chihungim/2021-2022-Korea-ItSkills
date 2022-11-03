import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class AdminMain extends BaseFrame {
	JTabbedPane pane = new JTabbedPane();
	String cap[] = "사용자 관리,추천 여행지 관리,일정 관리,예매 관리".split(",");
	JPanel p[] = { new UserManage(), new Recommend(), new ScheduleManage(), new ReserveManage() };

	public AdminMain() {
		super("관리자", 1100, 700);

		UI();
		setVisible(true);
	}

	void UI() {
		add(pane);
		pane.setTabPlacement(2);

		for (int i = 0; i < cap.length; i++) {
			pane.add(p[i]);
			pane.setTitleAt(i, cap[i]);
		}

		pane.add("테마", null);
		pane.add("로그아웃", null);

		pane.addChangeListener(e -> {
			if (pane.getSelectedIndex() == 4) {
				whiteColor = (whiteColor.equals(Color.WHITE)) ? Color.DARK_GRAY : Color.WHITE;
				blackColor = (blackColor.equals(Color.WHITE)) ? Color.BLACK : Color.WHITE;
				BaseDialog.whiteColor = (whiteColor.equals(Color.WHITE)) ? Color.WHITE : Color.DARK_GRAY;
				BaseDialog.blackColor = (blackColor.equals(Color.WHITE)) ? Color.WHITE : Color.BLACK;
				((JPanel) getContentPane()).setBackground(whiteColor);
				for (int i = 0; i < 4; i++) {
					p[i].setBackground(whiteColor);
				}
				repaint();
				revalidate();
			}
			if (pane.getSelectedIndex() == 5) {
				dispose();
				new UserMain();
			}
		});

		pane.setBackgroundAt(4, (whiteColor.equals(Color.WHITE)) ? Color.DARK_GRAY : Color.WHITE);
		pane.setBackgroundAt(4, (blackColor.equals(Color.WHITE)) ? Color.BLACK : Color.WHITE);
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}
