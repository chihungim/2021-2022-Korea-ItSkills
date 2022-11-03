package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {

	int pos[][] = { { 10, 11, 415, 79 }, { 484, 11, 186, 80 }, { 13, 105, 342, 158 }, { 420, 105, 179, 158 },
			{ 15, 271, 250, 42 }, { 504, 271, 170, 40 }, { 19, 330, 653, 312 } };

	JComponent jc[] = { new JPanel(new BorderLayout()), new JPanel(new GridLayout(0, 1, 5, 5)),a 
			new JPanel(new BorderLayout()), new Popular(), new JPanel(), new JPanel(),
			new JPanel(new GridLayout(0, 5)) };
	Color col[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
	JLabel lblLocate[] = new JLabel[18];
	JTextField txt = new JTextField(15);
	JLabel bd;

	public Main() {
		super("메인", 700, 700);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ui();
		setVisible(true);
	}

	void ui() {
		setLayout(null);
		for (int i = 0; i < jc.length; i++) {
			add(jc[i]);
			jc[i].setBounds(pos[i][0], pos[i][1], pos[i][2], pos[i][3]);
		}

		// Northern
		{
			var sp = jc[0];
			var mp = jc[1];
			var sp_n = new JPanel(new FlowLayout());
			var sp_c = new JPanel(new GridLayout(1, 0));
			sp.add(sp_n, "North");
			sp.add(sp_c);

			JLabel lbl = lbl("알바자바", 0);
			lbl.setFont(new Font("휴먼모음T", Font.BOLD, 30));

			sp_n.add(lbl);
			sp_n.add(txt);
			sp_n.add(btn("검색", a -> {

			}));

			for (var bcap : "채용정보,하루알바,인재정보,공고등록".split(",")) {
				sp_c.add(btn(bcap, a -> {

				}));
			}

			var mp_n = new JPanel();

			mp_n.setLayout(new BoxLayout(mp_n, BoxLayout.X_AXIS));

			mp.add(mp_n, "North");

			JButton b;

			mp_n.add((b = btn("로그인", a -> {

			})));

			MailPanel m = new MailPanel();

			mp_n.add(m);
			b.setMaximumSize(new Dimension(150, 50));
			m.setMaximumSize(new Dimension(40, 50));
			mp.add(btn("기업수정", a -> {

			}));
		}

		// center
		{
			String[] locate = "서울,경기,인천,부산,대구,대전,경남,전남,충남,광주,울산,경북,전북,충북,강원,제주,세종,전국".split(",");
			var lp = (JPanel) jc[2];
			var lp_c = new JPanel(new GridLayout(0, 6));
			lp.add(lbl("지역별 알바", JLabel.CENTER, 20), "North");
			lp.add(lp_c);

			for (int i = 0; i < locate.length; i++) {
				lp_c.add(lblLocate[i] = lbl(locate[i], JLabel.CENTER));
				lblLocate[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {

						Arrays.stream(lblLocate).forEach(a -> a.setBorder(BorderFactory.createEmptyBorder()));

						var me = (JLabel) e.getSource();
						me.setBorder(new LineBorder(Color.RED));

						super.mouseEntered(e);
					}
				});
			}

			var tp = (JPanel) jc[4];
			tp.setLayout(new FlowLayout(FlowLayout.LEFT));
			tp.add(lbl("브랜드 알바", JLabel.LEFT, 20));
			tp.add(btn("더보기+", a -> {

			}));

			var dp = (JPanel) jc[5];

			dp.add(bd = lbl(dateTimeFormat(LocalDateTime.now(), "yyyy.MM.dd hh:mm:ss") + " 기준", JLabel.LEFT));
		}

		// brandalba

		{

			var bp = (JPanel) jc[6];

			ArrayList<String> cno = new ArrayList<String>();
			String path = "./지급자료/브랜드/", mname;
			try {
				for (var file : Files.list(Paths.get(path)).toArray(Path[]::new)) {
					mname = file.getName(file.getNameCount() - 1).toString();
					int pos = mname.lastIndexOf(".");
					mname = mname.substring(0, pos);
					cno.add(getOne("select c_no from company where name ='" + mname + "'"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				var rs = stmt.executeQuery(
						"SELECT c.c_no, c.name, sum(hits) FROM recruitment r, company c where r.c_no=c.c_no and c.c_no in ("
								+ String.join(",", cno) + ")" + " group by c.c_no order by hits desc limit 15");
				while (rs.next()) {
					String no = rs.getString(1);
					JPanel jp = new JPanel(new BorderLayout());
					setLine(jp);
					jp.add(new JLabel(img("./지급자료/브랜드/" + rs.getString(2) + ".jpg", 130, 120)));
					bp.add(jp);
					jp.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							new Inform(no);
						}
					});
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class MailPanel extends JPanel {

		JLabel img;
		JLabel l;

		public MailPanel() {
			super(new BorderLayout());
			this.setOpaque(false);

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!isLogin) {
						eMsg("로그인해주세요.");
						new Login();
					} else {
						new Mail();
					}
				}
			});

			this.add(img = new JLabel(img("./지급자료/메일.png", 50, 50)));
			l = new JLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g;
					if (isLogin) {

						int cnt = 0;
						try {
							System.out.println(mailSql("send"));
							var rs = stmt.executeQuery(mailSql("send"));
							while (rs.next()) {
								cnt++;
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (cnt > 0) {
							g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2.setColor(Color.yellow);
							g2.fillOval(24, 0, 24, 25);
							g2.setColor(Color.black);
							g2.drawOval(24, 0, 24, 25);
							g2.drawString(cnt + "", 34, 18);
						}
					}

				}
			};
			l.setBounds(0, 0, 50, 25);
			img.add(l);

		}
	}

	class Popular extends JPanel {
		Item items[] = new Item[5];
		ArrayList<String> beforeWord = new ArrayList<>(); // 원래 순위
		ArrayList<String> afterWord = new ArrayList<>(); // 후 순위

		public Popular() {
			super(new BorderLayout());

			var c = new JPanel(new GridLayout(0, 1, 5, 5));
			add(lbl("인기검색어", JLabel.CENTER, 20), "North");
			add(c);

			try {
				var rs = stmt.executeQuery(
						"SELECT word, count(*) cnt FROM albajava.search where time < now() group by word order by cnt desc, word asc limit 0 ,5");
				while (rs.next()) {
					beforeWord.add(rs.getString(1));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			beforeWord.forEach(a -> {
				c.add(items[beforeWord.indexOf(a)] = new Item(beforeWord.indexOf(a) + 1, a));
			});

			new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					bd.setText(dateTimeFormat(LocalDateTime.now(), "yyyy.MM.dd hh:mm:ss") + " 기준");

					try {
						var rs = stmt.executeQuery(
								"SELECT word, count(*) cnt FROM albajava.search where time < now() group by word order by cnt desc, word asc limit 0 ,5");
						afterWord.clear();

						while (rs.next()) {
							afterWord.add(rs.getString(1));
						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (String str : afterWord) {
						int aidx = afterWord.indexOf(str);
						if (beforeWord.contains(str)) { // 순위에 이미 존재할 경우 ㅇㅇ
							int bidx = beforeWord.indexOf(str);
							if (bidx - aidx != 0 || aidx - bidx != 0) {
								if (aidx > bidx) { // 순위가 낮은경우 ()
									items[aidx].setTweak(aidx - bidx, false);
								} else { // 순위가 높은 경우 ?
									items[aidx].setTweak(bidx - aidx, true);
								}
							}
						} else { // new
							items[aidx].newTweak();
						}
					}

					afterWord.forEach(a -> {
						items[afterWord.indexOf(a)].set(afterWord.indexOf(a) + 1, a);
					});

					Collections.copy(beforeWord, afterWord);

					repaint();
					revalidate();
				}

			}).start();

		}

		class Item extends JPanel {

			String text;
			JLabel rankLabel, textLabel, tweakLabel;

			public Item(int rank, String text) {
				super(new BorderLayout(5, 5));
				this.text = text;
				add(sz(rankLabel = new JLabel(rank + "", JLabel.CENTER), 20, 20), "West");
				add(textLabel = new JLabel(text + "", JLabel.LEFT));
				add(tweakLabel = lbl("-", JLabel.CENTER, 20), "East");

				rankLabel.setOpaque(true);
				rankLabel.setForeground(Color.WHITE);
				rankLabel.setBackground(Color.RED);
				tweakLabel.setOpaque(true);

				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						new Recruitment();
						super.mousePressed(e);
					}
				});
			}

			void set(int rank, String text) {
				this.text = text;
				rankLabel.setText(rank + "");
				textLabel.setText(text);
			}

			void setTweak(int tweak, boolean isIncrease) {
				if (isIncrease) {
					tweakLabel.setForeground(Color.RED);
					tweakLabel.setText("↑ " + tweak);
				} else {
					tweakLabel.setForeground(Color.BLUE);
					tweakLabel.setText("↓ " + tweak);
				}
			}

			void newTweak() {
				tweakLabel.setForeground(Color.GREEN);
				tweakLabel.setText("NEW");
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
