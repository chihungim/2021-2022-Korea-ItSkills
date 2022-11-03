package view;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class MyPage extends BasePage {
	JPanel page;
	String bcap[] = "구매내역,정보수정".split(",");

	public MyPage() {
		add(page = new JPanel(new GridBagLayout()));
		page.add(c = new JPanel(new GridLayout(1, 0, 10, 5)));
		for (int i = 0; i < bcap.length; i++) {
			c.add(btn(bcap[i], a -> {
				if (a.getActionCommand().equals("구매내역")) {
					mf.swapPage(new OrderLogPage());
				} else {
					mf.swapPage(new EditProfilePage());
				}
			})); 
		}
	}
}
