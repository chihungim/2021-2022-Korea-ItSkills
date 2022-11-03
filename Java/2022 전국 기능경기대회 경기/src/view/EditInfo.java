package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class EditInfo extends BasePage {

	JTextField txt[] = { new JTextField(15), new JTextField(15) };
	JRadioButton radio[] = { new JRadioButton("공개"), new JRadioButton("비공개"), };
	ButtonGroup bg = new ButtonGroup();
	JLabel pricelbl;

	public EditInfo() {
		super("정보수정");
		add(c = new JPanel(new BorderLayout()));
		add(sz(e = new JPanel(new FlowLayout()), 300, 0), "East");
		e.add(ec = new JPanel(new BorderLayout()));
		c.add(cc = new JPanel());
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));
		c.add(cs = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		ec.add(en = new JPanel(new BorderLayout()), "North");
		var ecc = new JPanel(new GridLayout(0, 1));
		ec.add(ecc);

		var cap = "프로필사진,닉네임,PW,프로필 공개 여부,보유 잔액".split(",");

		JComponent jc[][] = { { new JLabel(user.u_img(150, 150)) }, { txt[0] }, { txt[1] }, { radio[0], radio[1] },
				{ pricelbl = lbl(decformat(user.u_money) + "원", JLabel.LEFT, 15),
						btn("충전하기", a -> new Charge().setVisible(true)) } };
		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT, 20), 180, 30));
			for (var j : jc[i])
				temp.add(j);
			cc.add(temp);
		}

		for (var r : radio)
			bg.add(r);
		radio[cint(user.u_ox)].setSelected(true);

		en.add(lbl("검색 제외", JLabel.CENTER, 15), "North");
		var age = lbl("연령제한", JLabel.CENTER, 13);
		en.add(age);
		age.setForeground(!user.u_filter().contains("12") ? Color.LIGHT_GRAY : Color.BLACK);

		var plus = new JLabel(getIcon("./datafiles/기본사진/10.png", 30, 30));

		for (var l : user.u_filter()) {
			if (l.equals("0") || l.equals("12"))
				continue;
			ecc.add(wlbl(l, JLabel.CENTER));
		}

		cs.add(btn("수정하기", a -> {
			var lst = new ArrayList<String>();
			if (age.getForeground().equals(Color.BLACK)) {
				lst.add("12");
			}
			for (var ec : ecc.getComponents()) {
				var l = (JLabel) ec;
				lst.add(Arrays.asList(g_genre).indexOf(l.getText()) + "");
			}

			if (lst.size() == 0)
				lst.add("0");

			imsg("수정이 완료되었습니다.");
			execute("update user set u_name = ?, u_pw= ? ,u_ox = ?, u_filter= ? where u_no = ?", txt[0].getText(),
					txt[1].getText(), radio[0].isSelected() ? 0 : 1, String.join(",", lst), user.u_no);
			mf.updateALL();
		}));

		ec.add(plus, "South");
		evt(age, a -> {
			if (age.getForeground().equals(Color.black)) {
				age.setForeground(Color.LIGHT_GRAY);
			} else {
				age.setForeground(Color.BLACK);
			}
		});
		evt(plus, a -> {
			new Genre(ecc, true).setVisible(true);
		});
		txt[0].setText(BasePage.user.u_name + "");
		txt[1].setText(BasePage.user.u_pw + "");
		setBackground(null);
	}

	public static void main(String[] args) {
		디버그("1");
		new EditInfo();
		mf.setVisible(true);
	}

	@Override
	void update() {
		pricelbl.setText(decformat(user.u_money) + "원");
	}
}
