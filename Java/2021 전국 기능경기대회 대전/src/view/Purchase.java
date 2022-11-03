package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Purchase extends BaseFrame {
	String cap_w[] = "공연명,장소,날짜,좌석,총금액".split(",");
	String cap_e[] = { pinfo[2], pinfo[3], pinfo[6], "", df.format(Stage.sum) };
	String bcap[] = "결제하기,취소".split(",");
	JLabel check;

	public Purchase(String seats, String discounts) {
		super("결제", 300, 320);

		var c = new JPanel();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		var c_s = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var s = new JPanel(new GridLayout(1, 0, 5, 5));

		add(setB(lbl("결제", JLabel.CENTER, 25), new MatteBorder(0, 0, 2, 0, Color.BLACK)), "North");
		add(c);
		add(s, "South");

		for (int i = 0; i < cap_w.length; i++) {
			if (i != 3) {
				var tmp = new JPanel(new BorderLayout());
				tmp.add(lbl(cap_w[i], JLabel.LEFT));
				tmp.add(lbl(cap_e[i], JLabel.RIGHT), "East");
				c.add(tmp);
			} else {
				var tmp = new JPanel(new BorderLayout());
				var tmp_c = new JPanel(new GridLayout(0, 1));
				tmp.add(lbl(cap_w[i], JLabel.LEFT), "West");
				tmp.add(tmp_c);
				for (int j = 0; j < seats.split("#").length; j++) {
					tmp_c.add(lbl(seats.split("#")[j], JLabel.RIGHT));
				}
				c.add(tmp);
				tmp.setBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
			}
		}
		c.add(c_s);

		c_s.add(btn("본인인증", a -> {
			String result = JOptionPane.showInputDialog("비밀번호를 입력해주세요.");
			System.out.println(result);
			if (result == null)
				return;
			try {
				var rs = stmt
						.executeQuery("select * from user where u_no = '" + u_no + "' and u_pw = '" + result + "'");
				if (!rs.next()) {
					eMsg("비밀번호가 일치하지 않습니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			iMsg("본인 인증이 완료되었습니다.");
			((JButton) a.getSource()).setEnabled(false);
			check.setText("V");
		}));

		c_s.add(check = lbl("", JLabel.CENTER));

		for (var bcap : "결제하기,취소".split(",")) {
			s.add(btn(bcap, a -> {
				if (a.getActionCommand().equals("결제하기")) {
					if (!check.getText().equals("V")) {
						eMsg("본인인증을 해주세요.");
						return;
					}

					iMsg("결제가 완료되었습니다.");
					String seat = "";

					for (int i = 0; i < seats.split("#").length; i++) {
						seat += seat.equals("")
								? seats.split("#")[i].replaceAll("[^A-Z]", "")
										+ seats.split("#")[i].replaceAll("[^0-9]", "").substring(2)
								: "," + seats.split("#")[i].replaceAll("[^A-Z]", "")
										+ seats.split("#")[i].replaceAll("[^0-9]", "").substring(2);

					}

					try {
						execute("insert ticket values(0," + u_no + "," + pinfo[0] + ",'" + seat + "','" + discounts
								+ "')");
					} catch (Exception e) {
						e.printStackTrace();
					}

					for (var f : getWindows()) {
						if (f instanceof Main) {
							f.setVisible(true);
							f.addWindowListener(new before(this));
						}
					}
				} else {
					dispose();
				}
			}));
		}

		check.setForeground(Color.GREEN);
		c_s.setBorder(new EmptyBorder(5, 5, 5, 5));
		((JPanel) (getContentPane())).setBorder(new EmptyBorder(5, 5, 5, 5));
		pack();
		setVisible(true);
	}

}
