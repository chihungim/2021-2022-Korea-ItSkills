package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Purchase extends BasePage {
	JTextField txt[] = { new JTextField(5), new JTextField(5), new JTextField(5), new JTextField(5), new JTextField(5),
			new JTextField(5), new JTextField(15), new JTextField(15), };
	JComboBox<String> combo = new JComboBox<String>("신한카드,현대카드,삼성카드,KB국민카드,롯데카드,하나카드,BC카드".split(","));

	int mileage = cint(
			getrows("select sum(mi_income - mi_expense) from mileage where m_no = ?", member.get(0)).get(0).get(0));
	double discount = 1;
	int sum;
	JLabel pricelbl;
	JScrollPane pane;
	JButton puzzle;
	SystemTray tray = SystemTray.getSystemTray();
	TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(""));

	public Purchase() {
		mf.setTitle("결제");
		setLayout(new BorderLayout(5, 5));
		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		n.add(lbl("결제하기", JLabel.LEFT, 15), "West");
		n.add(puzzle = bbtn("퍼즐맞추기", a -> {
			new Puzzle(Purchase.this).addWindowListener(new before(mf));
		}), "East");

		add(c = new JPanel(new GridLayout(1, 0, 5, 5)));

		var p1 = new JPanel(new GridLayout(0, 1, 5, 5));
		var p2 = new JPanel(new BorderLayout(5, 5));

		c.add(p1);
		c.add(pane = scrollpane(p2));
		pane.setBorder(null);
		var cap = ("카드|번호|유효기간|비밀번호| |<html>마일리지 사용<br>총" + df.format(mileage)).split("\\|");
		JComponent jc[][] = { { combo }, { txt[0], txt[1], txt[2], txt[3] }, { txt[4], txt[5] }, { txt[6] },
				{ lbl("", 0) }, { txt[7] } };

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 100, 30));
			for (var j : jc[i])
				temp.add(j);
			p1.add(temp);
		}

		for (int i = 0; i < 3; i++) {
			final var j = i + 1;
			txt[i].addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (txt[j - 1].getText().length() == 4) {
						txt[j].requestFocus();
					}
				}
			});
		}

		txt[7].addKeyListener(new KeyAdapter() {
			@Override

			public void keyReleased(KeyEvent e) {
				if (txt[7].getText().isEmpty())
					return;
				if (!isNumeric(txt[7].getText())) {
					emsg("마일리지 금액을 확인해주세요.");
					txt[7].setText("");
				}
				if (cint(txt[7].getText()) > mileage) {
					emsg("마일리지 금액을 확인해주세요.");
					txt[7].setText("");
				}
				if (cint(txt[7].getText()) < 1) {
					emsg("마일리지 금액을 확인해주세요.");
					txt[7].setText("");
				}
				setPricelbl();
			}
		});

		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		s.add(pricelbl = lbl("총n원", JLabel.CENTER));
		s.add(bbtn("결제하기", a -> {
			for (int i = 0; i < txt.length - 1; i++) {
				if (txt[i].getText().isEmpty() || combo.getSelectedIndex() == -1) {
					emsg("빈칸이 존재합니다.");
					return;
				}
			}

			var card = txt[0].getText() + "-" + txt[1].getText() + "-" + txt[2].getText() + "-" + txt[3].getText();
			if (!card.matches("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$")) {
				emsg("카드 번호를 확인해주세요.");
				return;
			}

			String MMyy = txt[4].getText() + "-" + txt[5].getText() + "-1";
			try {
				var d = LocalDate.parse(MMyy, DateTimeFormatter.ofPattern("MM-yy-d"));
			} catch (Exception e1) {
				e1.printStackTrace();
				emsg("유효기간을 확인해주세요.");
				return;
			}

			if (txt[6].getText().length() != 4) {
				emsg("비밀번호를 확인해주세요.");
				return;
			}

			imsg("총 " + df.format(total()) + "원 결제가 완료되었습니다.");

			execute("insert reservation values(0,?,?,?,?)", member.get(0), sno, date, total());
			var rno = getrows("select * from reservation order by r_no desc").get(0).get(0).toString();
			for (var c : companions)
				execute("insert companion values(0,?,?,?,?,?,?)", rno, c[1], c[2], c[3], c[4], c[5]);

			if (txt[7].getText().isEmpty()) {
				execute("insert mileage values(0,?,?,?)", member.get(0), total() * 0.01, 0);
			} else {
				execute("insert mileage values(0,?,?,?)", member.get(0), 0, cint(txt[7].getText()));
			}
			try {
				tray.add(icon);
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			icon.displayMessage("예약이 완료되었습니다.", "", MessageType.INFO);
			mf.page = "";
			mf.swap2("예약조회");
		}));

		c.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

		p1.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

		setPricelbl();
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	int total() {
		var 티켓값 = cint(getrows("select s_price from schedule where s_no = ?", sno).get(0).get(0)) * companions.size();
		var 가방값 = bags.stream().mapToInt(a -> cint(a[0])).sum();

		return (int) ((티켓값 + 가방값 - (txt[7].getText().isEmpty() ? 0 : cint(txt[7].getText()))) * discount);
	}

	void setPricelbl() {
		pricelbl.setText((discount < 1 ? "(10% 할인) " : "") + "총 " + df.format(total()) + "원");
	}
}
