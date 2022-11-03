import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class UserMain extends BaseFrame {
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField() };
	JTextField dates[] = new JTextField[3];

	JLabel imglbl;
	JLabel lbl;

	JPanel imgp[] = new JPanel[5];
	JPanel p = new JPanel(null);
	JPanel popup = new JPanel(new BorderLayout()), in = new JPanel(new GridLayout(1, 0));
	JPanel depart = new JPanel(new GridLayout(0, 1)), arrive = new JPanel(new GridLayout(0, 1));
	JPanel cal = new JPanel(new GridLayout(1, 0));

	JButton btn1, btn2;
	JButton departBtn[] = new JButton[16], arriveBtn[];
	JButton down[] = new JButton[3], up[] = new JButton[3];

	JScrollPane jsc1 = new JScrollPane(depart);
	JScrollPane jsc2 = new JScrollPane(arrive);

	JPopupMenu menu = new JPopupMenu();
	JMenuItem item[] = { new JMenuItem("상세설명"), new JMenuItem("예매") };

	boolean bool;
	int cnt = 0;
	String cap[] = "출발지,도착지,날짜".split(",");
	String name[] = new String[5];

	LocalDate now = LocalDate.now();

	public UserMain() {
		super("버스예매", 1100, 700);

		UI();
		event();
		setVisible(true);
	}

	void event() {
		for (int i = 0; i < txt.length - 1; i++) {
			int j = i;
			txt[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						cnt = j;
						popup.setVisible(true);
						popup.setLocation(txt[j].getX() + 140, txt[j].getY() + 150);
						arrive.removeAll();
						cal.setVisible(false);
						if (bool) {
							txt[2].setText(now + "");
							txt[2].setForeground(Color.BLACK);
						}
						repaint();
						revalidate();
					}
				}
			});
		}

		txt[2].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					popup.setVisible(false);
					bool = true;
					cal.setVisible(true);
					cal.setLocation(txt[2].getX() + 140, txt[2].getY() + 150);
				}
			}
		});

		try {
			var rs1 = stmt.executeQuery("select * from location");
			int i = 0;
			while (rs1.next()) {
				depart.add(departBtn[i] = new JButton(rs1.getString(2)));
				int j = i;
				departBtn[i].addActionListener(a -> {
					arrive.removeAll();
					try {
						ResultSet rs2 = stmt.executeQuery("select * from location2 where location_no = " + (j + 1));
						rs2.last();
						arriveBtn = new JButton[rs2.getRow()];
						rs2.beforeFirst();
						int k = 0;
						while (rs2.next()) {
							arrive.add(arriveBtn[k] = new JButton(rs2.getString(2)));
							arriveBtn[k].addActionListener(a2 -> {
								JButton btn = (JButton) a2.getSource();
								txt[cnt].setText(departBtn[j].getText() + " " + btn.getText());
								txt[cnt].setForeground(Color.BLACK);
							});
							k++;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					repaint();
					revalidate();
				});
				i++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1) {
					popup.setVisible(false);
					cal.setVisible(false);
					if (bool) {
						txt[2].setText(now + "");
						txt[2].setForeground(Color.BLACK);
					}
				}
			}
		});

		for (int i = 0; i < imgp.length; i++) {
			int idx = i;
			imgp[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						menu.show(imgp[idx], e.getX(), e.getY());
					}
				}
			});
		}

		for (int i = 0; i < item.length; i++) {
			int idx = i;
			item[i].addActionListener(e -> {
				if (e.getActionCommand().equals("상세설명")) {
					new Detail(name[idx]);
				} else {
					cnt = 1;
					popup.setVisible(true);
					popup.setLocation(txt[1].getX() + 140, txt[1].getY() + 150);
					arrive.removeAll();
					cal.setVisible(false);
					if (bool) {
						txt[2].setText(now + "");
						txt[2].setForeground(Color.BLACK);
					}

					repaint();
					revalidate();
				}
			});
		}

	}

	void UI() {
		setLayout(null);

		for (int i = 0; i < cap.length; i++) {
			HintText(txt[i], cap[i]);
		}

		for (int i = 0; i < dates.length; i++) {
			var tmp = new JPanel(new GridLayout(0, 1));
			tmp.add(up[i] = new JButton("▲"));
			tmp.add(dates[i] = new JTextField());
			tmp.add(down[i] = new JButton("▼"));

			dates[i].setFocusable(false);
			cal.add(tmp);

			int cnt = i;
			up[i].addActionListener(e -> {
				if (cnt == 1 && dates[1].getText().equals("12"))
					return;

				if (cnt == 1 && toInt(dates[2].getText()) > now.plusMonths(1).lengthOfMonth())
					dates[2].setText(now.plusMonths(1).lengthOfMonth() + "");

				if (cnt == 2 && dates[2].getText().equals(now.lengthOfMonth() + ""))
					return;

				dates[cnt].setText(toInt(dates[cnt].getText()) + 1 + "");
				now = LocalDate.of(toInt(dates[0].getText()), toInt(dates[1].getText()), toInt(dates[2].getText()));
			});

			down[i].addActionListener(e -> {
				if (cnt == 2 && dates[2].getText().equals("1"))
					return;

				if (cnt == 1 && toInt(dates[2].getText()) > now.plusMonths(1).lengthOfMonth())
					dates[2].setText(now.plusMonths(1).lengthOfMonth() + "");

				if (cnt == 1 && dates[1].getText().equals("1"))
					return;

				dates[cnt].setText(toInt(dates[cnt].getText()) - 1 + "");
				now = LocalDate.of(toInt(dates[0].getText()), toInt(dates[1].getText()), toInt(dates[2].getText()));
			});

			dates[i].setHorizontalAlignment(SwingConstants.CENTER);
		}

		dates[0].setText(now.getYear() + "");
		dates[1].setText(now.getMonthValue() + "");
		dates[2].setText(now.getDayOfMonth() + "");

		setBounds(popup, 115, 200, 220, 300);
		setBounds(cal, 115, 200, 220, 100);
		popup.setVisible(false);
		cal.setVisible(false);

		setBounds(imglbl = new JLabel((new ImageIcon(Toolkit.getDefaultToolkit().getImage("지급파일/images/main.jpg")
				.getScaledInstance(1100, 1100, Image.SCALE_SMOOTH)))), 0, 0, 1100, 300);
		setBounds(imglbl, themeButton(), 760, 5, 60, 30);
		setBounds(imglbl, btn("계정", e -> Account()), 830, 5, 60, 30);
		setBounds(imglbl, btn("예매", e -> new Reserve()), 900, 5, 60, 30);
		setBounds(imglbl, btn("로그아웃", e -> dispose()), 970, 5, 100, 30);

		setBounds(imglbl, p, 150, 100, 800, 100);
		setBounds(p, lbl = lbl("예매", 2, 20), 5, 0, 50, 50);
		setBounds(p, txt[0], 5, 50, 200, 30);
		setBounds(p, btn1 = btn("<html>←<br>→", e -> change()), 210, 50, 50, 40);
		setBounds(p, txt[1], 270, 50, 200, 30);
		setBounds(p, txt[2], 480, 50, 200, 30);
		setBounds(p, btn("조회", e -> lookUp()), 700, 50, 80, 40);

		popup.add(in);
		in.add(jsc1);
		in.add(jsc2);

		menu.add(item[0]);
		menu.add(item[1]);

		try {
			var rs = stmt.executeQuery(
					"select r.no, l.name, title, description, img from recommend r, location l, recommend_info ri where r.location_no = l.no and ri.recommend_no = r.no group by l.name order by r.no asc");
			int i = 0;
			while (rs.next()) {
				if (i % 2 == 0) {
					setBounds(imgp[i] = new JPanel(new BorderLayout()), 30 + (i * 210), 400, 180, 180);
					imgp[i].setBorder(new TitledBorder(new LineBorder(Color.BLACK), rs.getString(2)));
					imgp[i].add(
							new JLabel(new ImageIcon(Toolkit
									.getDefaultToolkit().getImage("지급파일/images/recommend/"
											+ hashMap.get(rs.getString(2)) + "/" + rs.getInt(3) + ".jpg")
									.getScaledInstance(450, 180, Image.SCALE_SMOOTH))));
				} else {
					setBounds(imgp[i] = new JPanel(new BorderLayout()), 30 + (i * 210), 440, 180, 180);
					imgp[i].setBorder(new TitledBorder(new LineBorder(Color.BLACK), rs.getString(2)));
					imgp[i].add(
							new JLabel(new ImageIcon(Toolkit
									.getDefaultToolkit().getImage("지급파일/images/recommend/"
											+ hashMap.get(rs.getString(2)) + "/" + rs.getInt(3) + ".jpg")
									.getScaledInstance(450, 180, Image.SCALE_SMOOTH))));
				}
				name[i] = rs.getString(2);
				imgp[i].setOpaque(false);
				i++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		depart.setOpaque(true);
		arrive.setOpaque(true);
		depart.setBackground(whiteColor);
		arrive.setBackground(whiteColor);
		jsc1.setBorder(new LineBorder(Color.BLACK));
		jsc2.setBorder(new LineBorder(Color.BLACK));
		popup.setBorder(new LineBorder(Color.BLACK));
	}

	void change() {
		if (txt[0].getText().equals("출발지") || txt[1].getText().equals("도착지")) {
			return;
		}

		String tmp = txt[1].getText();
		txt[1].setText(txt[0].getText());
		txt[0].setText(tmp);
	}

	void lookUp() {
		if (txt[0].getText().contentEquals("") || txt[0].getText().equals("출발지") || txt[1].getText().contentEquals("")
				|| txt[1].getText().equals("도착지") || txt[2].getText().contentEquals("")
				|| txt[2].getText().equals("날짜")) {
			emsg = new eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
			return;
		}

		try {
			String depart = getOne("select no from a where name = '" + txt[0].getText() + "'");
			String arrive = getOne("select no from a where name = '" + txt[1].getText() + "'");
			var rs = stmt.executeQuery("select * from schedule where departure_location2_no = '" + depart
					+ "' and arrival_location2_no = '" + arrive + "' and date = '" + txt[2].getText() + "'");
			if (rs.next()) {
				new BusReserve(toInt(depart), toInt(arrive), txt[2].getText());
			} else {
				emsg = new eMsg("예매 가능한 일정이 없습니다.");
				return;
			}
		} catch (SQLException e) {
			emsg = new eMsg("예매 가능한 일정이 없습니다.");
		}
	}

	void Account() {
		String yn = JOptionPane.showInputDialog(null, "비밀번호를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);

		try {
			var rs = stmt.executeQuery("select * from user where id = '" + u_id + "' and pwd = '" + yn + "'");
			if (rs.next()) {
				new Account();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new UserMain();
	}
}
