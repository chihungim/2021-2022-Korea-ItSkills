package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Main extends BaseFrame {

	String titles[] = "인기공연(뮤지컬),인기공연(오페라),인기공연(콘서트)".split(","), type[] = "M,C,O".split(",");
	JLabel lbls[] = new JLabel[5];
	JPanel[] pop = new JPanel[3];
	static JLabel uImg, loginlbl;
	CardLayout page;
	JPanel viewP;
	int pageIdx;

	public Main() {
		super("메인", 620, 300);
		setLayout(new BorderLayout(0, 20));

		var n_w = new JPanel(new FlowLayout(0, 10, 0));
		var n_e = new JPanel(new FlowLayout(2, 10, 0));

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(n_w, "West");
		n.add(n_e, "East");
		add(viewP = new JPanel(page = new CardLayout()));
		for (int i = 0; i < lbls.length; i++) {
			lbls[i] = lbl("TICKETING,MONTH SCHEDULE,CHART,LOGIN,MYPAGE".split(",")[i], JLabel.CENTER, 15);
			lbls[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					String name = ((JLabel) e.getSource()).getText();
					switch (name) {
					case "TICKETING":
						new Search().addWindowListener(new before(Main.this));
						break;
					case "MONTH SCHEDULE":
						new Schedule().addWindowListener(new before(Main.this));
						break;
					case "CHART":
						new Chart().addWindowListener(new before(Main.this));
						break;
					case "LOGIN":
						new Login().addWindowListener(new before(Main.this));
						break;
					case "LOGOUT":
						eMsg("로그아웃 되었습니다.");
						uImg.setIcon(null);
						uImg.setBorder(null);
						loginlbl.setText("LOGIN");
						break;
					default:
						if (loginlbl.getText().equals("LOGIN")) {
							eMsg("로그인을 하세요.");
							return;
						}
						new MyPage().addWindowListener(new before(Main.this));
						break;
					}
					super.mousePressed(e);
				}
			});
			if (i < 3)
				n_w.add(lbls[i]);
			else {
				if (i == 3) {
					n_e.add(uImg = new JLabel());
					loginlbl = lbls[i];
				}
				n_e.add(lbls[i]);
			}
		}

		for (int i = 0; i < pop.length; i++) {
			viewP.add(setB(pop[i] = new JPanel(new BorderLayout(0, 1)),
					new TitledBorder(new LineBorder(Color.BLACK), titles[i])), i + "");
			var c = new JPanel(new GridLayout(1, 0));
			var s = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pop[i].add(c);
			pop[i].add(s, "South");

			try {
				var rs = stmt.executeQuery(
						"select p_name, pf_no, count(*) from perform p, ticket t where p.p_no = t.p_no and left(p.pf_no,1) = '"
								+ type[i] + "' group by p.p_name order by count(*) desc limit 0,5");
				while (rs.next()) {
					var item = new JPanel(new BorderLayout());
					item.add(lbl(rs.getRow() + "위", JLabel.LEFT, 15), "North");
					item.add(new JLabel(getIcon("./Datafiles/공연사진/" + rs.getString(2) + ".jpg", 100, 100)));
					item.add(lbl(rs.getString(1), 0), "South");
					c.add(item);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int j = 0; j < "1,2,3".split(",").length; j++) {
				var l = lbl("1,2,3".split(",")[j], 10);
				if (i == j) {
					l.setForeground(Color.RED);
				}
				s.add(l);
			}
		}

		setB((JComponent) getContentPane(), new EmptyBorder(5, 5, 5, 5));
		Timer animeT = new Timer(2000, a -> {
			pageIdx = pageIdx == 3 ? 0 : pageIdx;
			page.show(viewP, pageIdx + "");
			pageIdx++;
		});
		animeT.start();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
