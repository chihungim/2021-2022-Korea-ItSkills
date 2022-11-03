package view;

import java.awt.GridLayout;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자", 300, 300);
		setLayout(new GridLayout(0, 1, 10, 10));

		var bcap = "공항등록,항공일정등록,탑승자 분석,로그아웃".split(",");

		for (var b : bcap) {
			add(bbtn(b, a -> {
				if (a.getActionCommand().equals("공항등록")) {
					new Airport().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("항공일정등록")) {
					new Schedule().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("탑승자 분석")) {
					new Chart().addWindowListener(new before(this));
				} else {
					dispose();
				}
			}));
		}
		setVisible(true);
	}
}
