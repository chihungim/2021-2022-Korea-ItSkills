package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Charge extends BaseDialog {

	JRadioButton radio[] = new JRadioButton[6];
	ButtonGroup bg = new ButtonGroup();
	int idx = 0;

	public Charge() {
		super("충전하기", 300, 400);
		add(wlbl("충전 금액 선택", JLabel.LEFT, 20), "North");

		add(c = newJPanel(new GridLayout(0, 1)));
		add(s = newJPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		int pr[] = { 5000, 10000, 20000, 25000, 50000, 100000 };

		for (int i = 0; i < pr.length; i++) {
			radio[i] = new JRadioButton(decformat(pr[i]));
			radio[i].setFont(lbl("", 0, 15).getFont());
			radio[i].setForeground(Color.WHITE);
			int j = i;
			radio[i].setOpaque(false);
			radio[i].addActionListener(a -> idx = j);
			bg.add(radio[i]);
			c.add(radio[i]);
		}

		radio[0].setSelected(true);

		s.add(bbtn("충전", a -> {
			execute("update user set u_money = u_money + ? where u_no = ?", pr[idx], BasePage.user.u_no);
			BasePage.mf.updateALL();
			imsg("충전이 완료되었습니다\n보유 잔액:" + decformat(BasePage.user.u_money) + "원");
		}));

	}
}
