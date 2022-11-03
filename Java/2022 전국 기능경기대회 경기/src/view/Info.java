package view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Game;

public class Info extends BaseDialog {

	JScrollPane pane;

	public Info(ArrayList<Game> lst) {
		super("정보", 300, 200);
		add(sz(pane = new JScrollPane(c = new JPanel(new GridLayout(0, 1))), 250, 100), "North");
		c.setBackground(back);
		for (var l : lst) {
			if (cint(l.g_ox) == 1) {
				var items = getrows("select * from item where g_no = ?", l.g_no);
				Collections.shuffle(items);
				var item = wlbl(items.get(2) + "", JLabel.LEFT, 13);
				item.setIcon(getBlob(items.get(0).get(3), 80, 100));
				execute("insert storage values(0,?,?)", BasePage.user.u_no, items.get(0).get(0));
				pane.add(item);
			}
		}

		pane.setVisible(c.getComponentCount() > 0);
		add(wlbl("결제가 완료되었습니다.", JLabel.CENTER, 20));
		add(wlbl("보유 잔액:" + decformat(BasePage.user.u_money) + "원", JLabel.CENTER), "South");
	}

	String ino;

	public Info(String ino) {
		this(new ArrayList<Game>());
		var items = getrows("select * from item where i_no = ?", ino).get(0);
		var item = wlbl("아이템명 : " + items.get(2) + "", JLabel.LEFT, 13);
		item.setIcon(getBlob(items.get(3), 80, 100));
		c.add(item);
		pane.setVisible(true);

	}

}
