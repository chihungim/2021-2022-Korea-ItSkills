package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Announcement extends BaseFrame {

	JTextField txt[] = new JTextField[4];
	JComboBox combo[] = new JComboBox[11];
	JRadioButton rbtn[] = { new JRadioButton("무관"), new JRadioButton("남"), new JRadioButton("여") };
	JCheckBox chk[] = { new JCheckBox("월"), new JCheckBox("화"), new JCheckBox("수"), new JCheckBox("목"),
			new JCheckBox("금"), new JCheckBox("토"), new JCheckBox("일") };
	JPanel item = new JPanel(new GridLayout(0, 1));
	boolean state;
	ArrayList<String> benefits = new ArrayList<String>();

	public Announcement() {
		super("채용공고등록", 480, 700);

		this.add(n = new JPanel(new GridLayout(0, 1)), "North");
		this.add(c = new JPanel(new GridLayout(0, 1)));

		n.setBorder(new TitledBorder(new LineBorder(Color.black), "모집내용", TitledBorder.LEFT, TitledBorder.TOP));
		c.setBorder(new TitledBorder(new LineBorder(Color.black), "근무조건", TitledBorder.LEFT, TitledBorder.TOP));

		this.add(s = new JPanel(new FlowLayout(2)), "South");
		s.add(sz(btn("등록", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}
			if (!(chk[0].isSelected() || chk[1].isSelected() || chk[2].isSelected() || chk[3].isSelected()
					|| chk[4].isSelected() || chk[5].isSelected() || chk[6].isSelected())) {
				eMsg("빈칸이 있습니다.");
				return;
			}
			if (!(rbtn[0].isSelected() || rbtn[1].isSelected() || rbtn[2].isSelected())) {
				eMsg("빈칸이 있습니다.");
				return;
			}
			for (int i = 0; i < combo.length; i++) {
				if (combo[i].getSelectedIndex() == -1) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}
			if (!state) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			String title = txt[0].getText();
			String deadline = combo[0].getSelectedItem() + "-" + combo[1].getSelectedItem() + "-"
					+ combo[2].getSelectedItem();
			String period = txt[2].getText() + "" + combo[4].getSelectedItem();
			String week = "";
			for (int i = 0; i < chk.length; i++)
				if (chk[i].isSelected())
					week += chk[i].getText() + ",";
			week = week.substring(0, week.length() - 1);

			String time = combo[5].getSelectedItem() + ":" + combo[6].getSelectedItem() + "~"
					+ combo[7].getSelectedItem() + ":" + combo[8].getSelectedItem();
			String standard = "시급";
			String salary = txt[1].getText();
			String date = "curdate()";
			String d_no = getOne("select * from details where name ='" + txt[3].getText() + "'");
			String e_no = (combo[9].getSelectedIndex() + 1) + "";
			String benefit = "";
			for (int i = 0; i < benefits.size(); i++) {
				benefit += getOne("select * from benefit where name ='" + benefits.get(i) + "'") + ",";
			}
			benefit = benefit.substring(0, benefit.length() - 1);

			iMsg("공고가 등록되었습니다.");
			execute("insert into recruitment values(0, '" + mno + "','" + title + "','" + period + "','" + week + "','"
					+ time + "','" + standard + "','" + salary + "',curdate(),'" + d_no + "','" + e_no + "','" + benefit
					+ "','" + deadline + "',0)");
			this.dispose();

		}), 100, 30));

		drawNorth();

		drawCenter();

		componentAction();

		for (int i = 0; i < combo.length; i++) {
			combo[i].setSelectedIndex(-1);
		}
		sz(n, 1, 200);

		setEmpty((JPanel) getContentPane(), 10, 10, 10, 10);
		this.setVisible(true);
	}

	void componentAction() {
		combo[0].addItem("무관");
		for (int i = 1; i <= 5; i++) {
			combo[0].addItem(i + "");
		}

		combo[1].addItem(LocalDate.now().getYear());
		combo[1].addItem(LocalDate.now().getYear() + 1);
		combo[1].addActionListener(a -> {
			if (combo[1].getSelectedIndex() == -1)
				return;
			combo[2].removeAllItems();
			combo[3].removeAllItems();
			LocalDate l = LocalDate.now();
			if (combo[1].getSelectedItem().toString().contentEquals(LocalDate.now().getYear() + "")) {
				for (int i = l.getMonthValue(); i <= 12; i++) {
					combo[2].addItem(i + "");
				}
			} else {
				for (int j = 1; j <= 12; j++) {
					combo[2].addItem(j + "");
				}
			}
		});
		combo[2].addActionListener(a -> {
			if (combo[1].getSelectedIndex() == -1 || combo[2].getSelectedIndex() == -1)
				return;
			combo[3].removeAllItems();
			int day = 1;
			if (LocalDate.now().getMonthValue() == rei(combo[2].getSelectedItem()))
				day = LocalDate.now().getDayOfMonth();

			LocalDate l = LocalDate.of(rei(combo[1].getSelectedItem()), rei(combo[2].getSelectedItem()), day);
			while (l.getMonthValue() == LocalDate
					.of(rei(combo[1].getSelectedItem()), rei(combo[2].getSelectedItem()), day).getMonthValue()) {
				combo[3].addItem(l.getDayOfMonth());
				l = l.plusDays(1);
			}

		});

		setNumeric(txt[1]);

		String combo4[] = "년,개월,주,일,기간무관".split(",");
		for (int i = 0; i < combo4.length; i++)
			combo[4].addItem(combo4[i]);

		combo[4].addItemListener(a -> {
			txt[2].setText("");
			txt[2].setEnabled(combo[4].getSelectedIndex() != 4);
		});

		for (int i = 0; i < 24; i++) {
			combo[5].addItem(String.format("%02d", i));
			combo[7].addItem(String.format("%02d", i));
		}

		for (int i = 0; i < 60; i += 10) {
			combo[6].addItem(String.format("%02d", i));
			combo[8].addItem(String.format("%02d", i));
		}

		txt[3].addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				new Choice(Announcement.this, txt[3]);
			}
		});

		addItem(combo[9], "select name from employment");
		addItem(combo[10], "select name from benefit");

	}

	void drawNorth() {
		String cap[] = "제목,인원,성별,마감일".split(",");
		JPanel p[] = new JPanel[cap.length];
		for (int i = 0; i < p.length; i++) {
			n.add(p[i] = new JPanel(new FlowLayout(0)));
			p[i].add(sz(new JLabel(cap[i]), 80, 25));
		}
		p[0].add(sz(txt[0] = new JTextField(), 300, 25));
		p[1].add(sz(combo[0] = new JComboBox(), 300, 25));

		ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < rbtn.length; i++) {
			bg.add(rbtn[i]);
			p[2].add(sz(rbtn[i], 80, 25));
		}

		for (int i = 0; i < 3; i++) {
			p[3].add(sz(combo[i + 1] = new JComboBox(), 90, 25));
			if (i != 2)
				p[3].add(new JLabel("-"));
		}

	}

	void drawCenter() {
		String cap[] = "급여,근무기간,근무요일,근무시간,직종,고용형태,복리후생".split(",");
		JPanel p[] = new JPanel[cap.length];
		for (int i = 0; i < p.length; i++) {
			c.add(p[i] = new JPanel(new FlowLayout(0)));
			p[i].add(sz(new JLabel(cap[i]), 80, 25));
		}

		p[0].add(sz(txt[1] = new JTextField(), 200, 25));
		p[0].add(new JLabel("원", JLabel.RIGHT));
		p[0].add(btn("급여계산기", a -> {
//			new Calc();
		}));

		p[1].add(sz(combo[4] = new JComboBox(), 150, 25));
		p[1].add(sz(new JLabel(), 10, 25));
		p[1].add(sz(txt[2] = new JTextField(), 150, 25));

		for (int i = 0; i < chk.length; i++)
			p[2].add(sz(chk[i], 40, 25));

		p[3].add(sz(combo[5] = new JComboBox(), 70, 25));
		p[3].add(new JLabel(":"));
		p[3].add(sz(combo[6] = new JComboBox(), 70, 25));
		p[3].add(new JLabel("~"));
		p[3].add(sz(combo[7] = new JComboBox(), 70, 25));
		p[3].add(new JLabel(":"));
		p[3].add(sz(combo[8] = new JComboBox(), 70, 25));

		p[4].add(sz(txt[3] = new JTextField(), 320, 25));

		p[5].add(sz(combo[9] = new JComboBox(), 320, 25));

		p[6].add(sz(combo[10] = new JComboBox(), 200, 25));
		p[6].add(sz(new JLabel(), 10, 25));
		p[6].add(sz(btn("추가", a -> {

			if (combo[10].getSelectedIndex() == -1)
				return;
			if (item.getComponentCount() >= 3) {
				eMsg("복리후생은 최대 3개까지만 가능합니다.");
				return;
			}
			for (var i : item.getComponents()) {
				var l = ((JLabel) i);
				if (combo[10].getSelectedItem().toString().contentEquals(l.getText())) {
					return;
				}
			}

			if (!state) {
				state = true;
				c.add(item);
				item.setBorder(new EmptyBorder(0, 80, 0, 0));
			}

			JLabel lbl = new JLabel(combo[10].getSelectedItem().toString());
			item.add(lbl);
			benefits.add(lbl.getText());
			repaint();
			revalidate();

			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						System.out.println("asdf");
						item.remove(((JLabel) e.getSource()));
						benefits.remove(((JLabel) e.getSource()).getText());

						if (item.getComponentCount() == 0) {
							c.remove(item);
							state = false;
						}
						repaint();
						revalidate();

					}
				}
			});

		}), 100, 25));

	}

	public static void main(String[] args) {
		new Announcement();
	}

}
