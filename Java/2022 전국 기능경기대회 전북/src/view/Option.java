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

public class Option extends BasePage {
	public Option() {
		add(lbl("옵션선택", JLabel.CENTER, 15), "North");
		add(c = new JPanel(new GridLayout(0, 1, 0, 0)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");
		Border[] b = { new MatteBorder(2, 0, 0, 0, Color.BLACK), new MatteBorder(2, 0, 2, 0, Color.BLACK), };
		var cap = "좌석배정,수하물 사전 구매".split(",");
		var descrip = "좌석배정을 통해 여정에 편안함을 더하세요.,부치는 짐이 있으시다면 수하물을 미리 구매하세요.".split(",");
		var bcap = "배정하기,구매하기".split(",");
		var icon = "좌석.png,수하물.png".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			var tempc = new JPanel(new GridLayout(0, 1));
			temp.add(new JLabel(getIcon("./datafiles/" + icon[i], 80, 80)), "West");
			temp.add(tempc);
			tempc.add(lbl(cap[i], JLabel.LEFT, 15));
			tempc.add(lbl(descrip[i], JLabel.LEFT, 13));
			var bt = btn(bcap[i], a -> {
				if (a.getActionCommand().equals("배정하기")) {
					new Seat().setVisible(true);
				} else {
					bags = new ArrayList<Object[]>();
					new Baggage().setVisible(true);
				}
			});
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			tmp.add(bt);
			tempc.add(tmp);
			bt.setBackground(Color.WHITE);

			temp.setBorder(b[i]);
			c.add(temp);
		}

		s.add(btn("확인", a -> {
			for (var c : companions) {
				if (c[5] == null) {
					emsg("좌석을 배정해주세요.");
					return;
				}
			}

			if (bags.isEmpty() || bags == null) {
				emsg("수하물을 구매해주세요.");
				return;
			}
			mf.swap(new Purchase());
		}));
		setBorder(new EmptyBorder(20, 20, 20, 20));

	}

	public static void main(String[] args) {
		Base.디버그("1");
		sno = "1";
		date = "2022-08-31";
		depart = "1";
		arrival = "4";
		companions = new ArrayList<>();
		bags = new ArrayList<>();
		bags.add(new Object[] { 0, false, false });
		companions.add(new Object[] { 1, null, "이름", null, "A2", 3 });
		companions.add(new Object[] { 1, null, "이름", null, "B2", 3 });
		mf.swap(new Option());
	}
}
