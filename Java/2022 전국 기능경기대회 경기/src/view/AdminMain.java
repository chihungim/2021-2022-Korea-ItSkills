package view;

import java.awt.GridLayout;

public class AdminMain extends BasePage {
	public AdminMain() {
		super("관리자메인");
		add(c = newJPanel(new GridLayout(1, 0)));

		var bcap = "게임관리,거래내역,차트".split(",");

		for (int i = 0; i < bcap.length; i++) {
			var cap = bcap[i];
			c.add(imglbl(bcap[i], "./datafiles/기본사진/" + (i + 5) + ".png", 150, 150, a -> {
				if (cap.equals("게임관리")) {
					new Search();
				} else if (cap.equals("거래내역")) {
					new TradeLog();
				} else {
					new Chart();
				}
			}));
		}
	}

	public static void main(String[] args) {
		new MainFrame();
		new AdminMain();
		mf.setVisible(true);
	}
}
