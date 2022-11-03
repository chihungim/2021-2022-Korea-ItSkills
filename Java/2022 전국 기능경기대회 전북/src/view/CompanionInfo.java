package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class CompanionInfo extends BasePage {

	JPanel cont, m;
	ArrayList<Item> itemList = new ArrayList<>();
	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15) };

	public CompanionInfo() {
		add(scrollpane(cont = new JPanel(new FlowLayout(FlowLayout.CENTER))));
		cont.add(m = new JPanel());
		m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));

		for (var comp : companions)
			m.add(new Item(comp));

		m.add(c = new JPanel(new GridLayout(0, 1)));
		var cap = "이메일,전화번호,비밀번호,생년월일".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 80, 30), "West");
			temp.add(txt[i]);
			c.add(temp);
		}

		c.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.BLACK), "예약자정보",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.ABOVE_TOP), new EmptyBorder(5, 5, 5, 5)));

		m.add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)));
		s.add(btn("확인", a -> {
			for (var i : itemList) {
				for (var t : i.txt) {
					if (t.getText().isEmpty() || i.combo.getSelectedIndex() == -1) {
						emsg("빈칸이 존재합니다.");
						return;
					}
				}

				if (i.txt[0].getText().matches(".*[^a-zA-Z].*") || i.txt[1].getText().matches(".*[^a-zA-Z].*")) {
					emsg("이름은 영문으로만 입력해주세요.");
					return;
				}

				try {
					var birth = LocalDate.parse(i.txt[2].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					var now = LocalDate.now();
					var temp = LocalDate.of(now.getYear(), birth.getMonthValue(), birth.getDayOfMonth());

					int age = now.getYear() - temp.getYear() + (temp.isBefore(now) ? 0 : -1);

					int div = cint(i.objs[5]);
					if (div == 1 && age < 12) {
						emsg("생년월일을 확인해주세요.");
						return;
					} else if (div == 2 && (age > 2 || age >= 12)) {
						emsg("생년월일을 확인해주세요.");
						return;
					} else if (div == 3 && age >= 2) {
						emsg("생년월일을 확인해주세요.");
						return;
					}
				} catch (Exception e) {
					emsg("생년월일을 확인해주세요.");
					e.printStackTrace();
					return;
				}

				companions.get(itemList.indexOf(i))[1] = i.combo.getSelectedIndex();
				companions.get(itemList.indexOf(i))[2] = i.txt[0].getText() + " " + i.txt[1].getText();
				companions.get(itemList.indexOf(i))[3] = i.txt[2].getText();
			}

			int col[] = { 7, 6, 2, 5 };

			for (int i = 0; i < col.length; i++) {
				if (!member.get(col[i]).toString().equals(txt[i].getText())) {
					emsg("예약자정보가 올바르지 않습니다.");
					return;
				}
			}

			imsg("입력이 완료되었습니다.");
			mf.swap(new Option());
		}));

	}

	class Item extends JPanel {
		Object[] objs;
		JTextField txt[] = { new JTextField(10), new JTextField(15), new JTextField(15) };
		JComboBox<String> combo = new JComboBox<String>("남,여".split(","));

		public Item(Object[] objs) {
			setLayout(new GridLayout(0, 1));
			this.objs = objs;
			var cap = "성별,이름(영문),생년월일".split(",");

			for (int i = 0; i < cap.length; i++) {
				var temp = new JPanel(new BorderLayout());
				var tempc = new JPanel(new BorderLayout());
				temp.add(sz(lbl(cap[i], JLabel.LEFT), 70, 0), "West");
				temp.add(tempc);
				if (i == 0) {
					tempc.add(combo);
				} else if (i == 1) {
					tempc.add(txt[0], "West");
					tempc.add(txt[1]);
				} else {
					tempc.add(txt[2]);
				}
				add(temp);
			}
			txt[0].setToolTipText("성을 입력하세요.");
			txt[1].setToolTipText("이름을 입력하세요.");

			setBorder(
					new CompoundBorder(
							new TitledBorder(new LineBorder(Color.BLACK), c_division[cint(objs[5])] + " " + objs[0],
									TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.ABOVE_TOP),
							new EmptyBorder(5, 5, 5, 5)));
			itemList.add(this);
		}
	}

	public static void main(String[] args) {
		Base.디버그("1");
		companions = new ArrayList<>();
//		companions.add(new Object[] { 1, null, null, null, null, 1 });
//		companions.add(new Object[] { 1, null, null, null, null, 1 });
//		companions.add(new Object[] { 1, null, null, null, null, 1 });
//		companions.add(new Object[] { 1, null, null, null, null, 2 });
		companions.add(new Object[] { 1, null, null, null, null, 3 });
		mf.swap(new CompanionInfo());
	}
}
