package view;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class ManagePage extends BasePage {
	JPanel page;
	String bcap[] = "배송관리,배송현황,통계".split(",");

	public ManagePage() {
		add(page = new JPanel(new GridBagLayout()));

		var grid = new JPanel(new GridLayout(1, 0, 10, 5));

		for (int i = 0; i < bcap.length; i++) {
			grid.add(sz(btn(bcap[i], a -> {
				if (a.getActionCommand().equals(bcap[0])) {
					mf.swapPage(new DeliveryPage());
				} else if (a.getActionCommand().equals(bcap[1])) {
					new Map("seller").setVisible(true);
				} else {
					mf.swapPage(new ChartPage());
				}
			}), 150, 150));
		}
		page.add(grid);
	}
}
