package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Baggage extends BaseDialog {

	JLabel pricelbl = lbl("", JLabel.RIGHT, 25);
	JButton btn[] = new JButton[3];
	ArrayList<Object[]> tempBag = new ArrayList<>();
	JPanel ccn, ccc;

	public Baggage() {
		super("수하물 구매", 400, 600);
		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");
		c.add(cn = new JPanel(new GridLayout(1, 0)), "North");
		c.add(cc = new JPanel(new BorderLayout()));
		cc.add(ccn = new JPanel(new BorderLayout()), "North");
		cc.add(ccc = new JPanel());
		ccc.setLayout(new BoxLayout(ccc, BoxLayout.Y_AXIS));

		n.add(lbl("수하물 구매", JLabel.CENTER, 20), "North");
		n.add(lbl("<html>사이즈 초과:160cm 이상<br>무게 초과:25kg이상", JLabel.LEFT));
		var header = new JPanel(new GridLayout(1, 0));

		ccn.add(sz(header, getWidth(), 60));
		var plus = lbl("+", 0);
		header.setBorder(new MatteBorder(2, 0, 2, 0, Color.BLACK));
		evt(plus, a -> addBag());

		var bcap = "삭제,초기화,확인".split(",");
		for (int i = 0; i < bcap.length; i++) {
			btn[i] = bbtn(bcap[i], a -> {
				if (a.getActionCommand().equals("삭제")) {
					util().setVisible(true);
				} else if (a.getActionCommand().equals("초기화")) {
					tempBag.clear();
					addBag();
					render();
				} else if (a.getActionCommand().equals("확인")) {
					BasePage.bags = tempBag;
					imsg("수하물 선택이 완료되었습니다.");
					dispose();
				}
			});
		}
		header.add(plus);
		header.add(lbl("사이즈 초과", JLabel.CENTER));
		header.add(lbl("무게 초과", JLabel.CENTER));
		header.add(lbl("요금", JLabel.CENTER));
		header.setBackground(Color.LIGHT_GRAY);
		addBag();
	}

	JDialog util() {
		var d = new JDialog();
		var c = new JPanel(new GridLayout(0, tempBag.size() > 2 ? 3 : tempBag.size()));

		Runnable r = new Runnable() {
			void ui() {
				c.removeAll();
				c.setLayout(new GridLayout(0, tempBag.size() > 2 ? 3 : tempBag.size()));
				for (var t : tempBag) {
					var l = new JLabel(getIcon("./datafiles/수하물.jpg", 150, 80));
					l.setText("bag" + (tempBag.indexOf(t) + 1));
					l.setVerticalTextPosition(JLabel.BOTTOM);
					l.setHorizontalTextPosition(JLabel.CENTER);
					c.add(l);
					evt(l, a -> {
						if (tempBag.size() == 1)
							return;
						if (a.getClickCount() == 2) {
							tempBag.remove(t);
							ui();
							render();
						}
					});
				}
				d.pack();
				d.setLocationRelativeTo(null);
			}

			@Override
			public void run() {
				ui();
			}
		};

		d.add(c);
		r.run();
		d.pack();
		d.setTitle("수하물 삭제");
		d.setLocationRelativeTo(null);
		return d;
	}

	public static void main(String[] args) {
		new Baggage().setVisible(true);
	}

	void render() {
		s.removeAll();
		if (tempBag.size() > 1) {
			for (var b : btn)
				s.add(b);
		} else {
			s.add(btn[2]);
		}

		ccc.removeAll();

		for (var t : tempBag) {
			var temp = new JPanel(new GridLayout(1, 0));
			var l = lbl("bag" + (tempBag.indexOf(t) + 1), JLabel.CENTER);
			var chk = new JCheckBox[] { new JCheckBox("", t[1].equals(true)), new JCheckBox("", t[2].equals(true)) };
			var fee = lbl(df.format(cint(t[0])), JLabel.CENTER);
			temp.add(l);
			temp.add(chk[0]);
			temp.add(chk[1]);
			for (int i = 0; i < chk.length; i++) {
				final var j = i;
				chk[i].addItemListener(x -> {
					var 기준가 = tempBag.indexOf(t) == 0 ? 0 : 50000;
					t[0] = 기준가 + (chk[0].isSelected() ? 30000 : 0) + (chk[1].isSelected() ? 35000 : 0);
					setPrice();
					fee.setText(df.format(cint(t[0])));
				});
			}
			temp.add(fee);
			temp.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
			temp.setMaximumSize(new Dimension(getWidth() - 20, 60));
			ccc.add(temp);
		}
		var p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p.setMaximumSize(new Dimension(600, 60));
		p.add(pricelbl);
		ccc.add(p);
		setPrice();
		revalidate();
		repaint();
	}

	void setPrice() {
		pricelbl.setText("총" + df.format(tempBag.stream().mapToInt(x -> cint(x[0])).sum()) + "원");
	}

	void addBag() {
		if (tempBag.size() == 6) {
			emsg("수하물은 최대 5개까지 구매할 수 있습니다.");
			return;
		}
		tempBag.add(new Object[] { tempBag.size() == 0 ? 0 : 50000, false, false });
		render();
	}

}
