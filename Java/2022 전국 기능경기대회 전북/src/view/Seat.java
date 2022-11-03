package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Seat extends BaseDialog {
	HashMap<String, JLabel> seatmap = new HashMap<>();

	Border dissel = new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5));
	Border sel = new CompoundBorder(new LineBorder(Color.blue), new EmptyBorder(5, 5, 5, 5));
	JPanel ecc;
	JLabel sellbl;

	public Seat() {
		super("좌석배정", 1200, 600);
		add(scrollpane(c = new JPanel(new GridLayout(0, 11, 5, 5))));

		var cap = "A,B,,C,D,E,,F,G".split(",");

		var reserved = getrows(
				"select c_seat from companion c , reservation r , schedule s where r.r_no = c.r_no and s.s_no = r.s_no and r.r_date = ? and s.s_no = ?",
				BasePage.date, BasePage.sno).stream().map(a -> a.get(0) + "").collect(Collectors.toList());
		System.out.println(reserved);
		for (int i = 0; i < 21; i++) {
			c.add(lbl(i == 0 ? "" : i + "", 0));
			for (int j = 0; j < cap.length; j++) {
				if (i == 0) {
					c.add(lbl(cap[j], JLabel.CENTER));
				} else if (!cap[j].isEmpty()) {
					var code = cap[j] + i;
					var s = lbl(code, JLabel.CENTER);
					b(s, Color.WHITE);
					seatmap.put(code, s);
					var flag = reserved.contains(code.toLowerCase());
					if (flag)
						b(s, Color.GRAY);

					evt(s, a -> {
						if (flag) {
							emsg(code + "좌석은 선택이 불가능합니다.");
							return;
						}

						String name = sellbl.getText().split("-")[0].trim();
						String seat = sellbl.getText().split("-")[1].trim();

						if (!seat.isEmpty()) {
							if (JOptionPane.showConfirmDialog(this, "이미 좌석을 선택하셨습니다. 해당 좌석을 선택하시겠습니까?", "경고",
									JOptionPane.YES_NO_OPTION) != 0) {
								return;
							}
						}

						seat = s.getText();

						sellbl.setText(name + " - " + seat);

						var selSeats = Arrays.stream(ecc.getComponents()).map(x -> ((JLabel) x))
								.map(x -> x.getText().split("-")[1].trim()).collect(Collectors.toList());
						s.setBackground(Color.RED);

						revalidate();
						repaint();
						System.out.println(selSeats);
						for (var ss : seatmap.values())
							if (!ss.getBackground().equals(Color.gray))
								ss.setBackground(Color.WHITE);

						for (var sss : selSeats)
							if (seatmap.get(sss) != null)
								seatmap.get(sss).setBackground(Color.red);
					});
					s.setBorder(new LineBorder(Color.LIGHT_GRAY));
					c.add(sz(s, 60, 60));
				} else {
					c.add(lbl("", 0));
				}
			}
			c.add(lbl(i == 0 ? "" : i + "", 0));
		}

		add(e = new JPanel(new BorderLayout()), "East");
		sz(e, 280, 0);
		e.add(lbl("총 " + BasePage.companions.size() + "명", JLabel.LEFT), "North");
		e.add(ec = new JPanel(new FlowLayout(FlowLayout.CENTER)));
		ec.add(ecc = new JPanel(new GridLayout(0, 1, 5, 5)));

		for (var c : BasePage.companions) {
			var l = lbl(c[2] + " - ", JLabel.LEFT);
			l.setBorder(dissel);
			evt(l, a -> {
				for (var w : ecc.getComponents())
					((JLabel) w).setBorder(dissel);
				l.setBorder(sel);
				sellbl = l;
			});

			ecc.add(sz(l, 250, 30));
		}

		((JComponent) ecc.getComponent(0)).setBorder(sel);
		sellbl = ((JLabel) ecc.getComponent(0));
		e.add(bbtn("선택완료", a -> {
			var i = 0;
			for (var e : ecc.getComponents()) {
				if (((JLabel) e).getText().split(" - ").length < 2
						|| ((JLabel) e).getText().split(" - ")[1].trim().isEmpty()) {
					emsg("좌석을 모두 선택해주세요.");
					return;
				}
				BasePage.companions.get(i)[4] = ((JLabel) e).getText().split(" - ")[1].trim().toLowerCase();
				i++;
			}

			dispose();

		}), "South");

		e.setBorder(new EmptyBorder(5, 5, 5, 5));

	}
}
