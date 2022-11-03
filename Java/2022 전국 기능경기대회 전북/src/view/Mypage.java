package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Mypage extends BasePage {
	public Mypage() {
		add(lbl("마이페이지", JLabel.CENTER, 15), "North");
		add(c = new JPanel(new GridLayout(0, 1, 0, 0)));
		Border[] b = { new MatteBorder(2, 0, 0, 0, Color.BLACK), new MatteBorder(2, 0, 2, 0, Color.BLACK), };
		var cap = "정보수정,마일리지 내역".split(",");
		var bcap = "수정하기,내역보기".split(",");
		var icon = "정보수정.png,마일리지.png".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			var tempc = new JPanel(new GridLayout(0, 1));
			temp.add(new JLabel(getIcon("./datafiles/" + icon[i], 80, 80)), "West");
			temp.add(tempc);
			tempc.add(lbl(cap[i], JLabel.LEFT, 15));
			var bt = btn(bcap[i], a -> {
				if (a.getActionCommand().equals("수정하기")) {
					new InfoEdit().setVisible(true);
				} else {
					new Mileage().setVisible(true);
				}
			});
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			tmp.add(bt);
			tempc.add(tmp);
			bt.setBackground(Color.WHITE);

			temp.setBorder(b[i]);
			c.add(temp);
		}

		setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
