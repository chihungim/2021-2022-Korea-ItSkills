package view;

import java.awt.GridLayout;

public class AdminMain extends BasePage {
	public AdminMain() {
		super("�����ڸ���");
		add(c = newJPanel(new GridLayout(1, 0)));

		var bcap = "���Ӱ���,�ŷ�����,��Ʈ".split(",");

		for (int i = 0; i < bcap.length; i++) {
			var cap = bcap[i];
			c.add(imglbl(bcap[i], "./datafiles/�⺻����/" + (i + 5) + ".png", 150, 150, a -> {
				if (cap.equals("���Ӱ���")) {
					new Search();
				} else if (cap.equals("�ŷ�����")) {
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
