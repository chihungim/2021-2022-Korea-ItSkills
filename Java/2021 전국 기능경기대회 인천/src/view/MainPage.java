package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MainPage extends BasePage {
	ArrayList<JPanel> pops = new ArrayList<>();
	ArrayList<JPanel> evnts = new ArrayList<>();

	public MainPage() {
		setLayout(new GridLayout(0, 1));
		try {
			var rs = rs(
					"select pu.p_No, p_Name, count(pu_Price) from product p, purchase pu where p.p_No = pu.p_No group by pu.p_No order by count(pu.p_No) desc, p.p_name limit 15");

			while (rs.next()) {
				var b = new JPanel(new BorderLayout());
				JLabel img;
				b.add(new JLabel(rs.getRow() + "위", JLabel.LEFT), "North");
				b.add(img = (JLabel) sz(new JLabel(getIcon("./datafile/image/" + rs.getString(2) + ".jpg", 80, 100)),
						80, 100));
				setB(img, new LineBorder(Color.BLACK));
				b.add(new JLabel(rs.getString(2), 0), "South");
				b.setOpaque(false);
				setB(b, new EmptyBorder(30, 30, 30, 30));
				pops.add(b);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = rs(
					"select p.p_Name from event e inner join product p on p.p_No=e.p_No where e_Month = month(now())");
			while (rs.next()) {
				var b = new JPanel(new BorderLayout());
				JLabel img;
				b.add(img = (JLabel) sz(new JLabel(getIcon("./datafile/image/" + rs.getString(1) + ".jpg", 80, 80)), 80,
						80));
				setB(img, new LineBorder(Color.BLACK));
				b.add(new JLabel(rs.getString(1), 0), "South");
				b.setOpaque(false);
				setB(b, new EmptyBorder(30, 30, 30, 30));
				evnts.add(b);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		add(new DragPanel(pops, "인기상품"));
		add(new DragPanel(evnts, "이달의 1 + 1 상품"));
		setB(this, new EmptyBorder(20, 20, 20, 20));
	}

	class DragPanel extends JPanel {
		ArrayList<JPanel> itemList;
		JLabel dots[];
		JPanel pages[];
		CardLayout page;
		int pageIdx = 0;
		JPanel mainP;
		JPanel dragP;

		public DragPanel(ArrayList<JPanel> itemList, String title) {
			super(new BorderLayout());
			this.itemList = itemList;
			this.dots = new JLabel[(itemList.size() / 5) + (itemList.size() % 5 > 0 ? 1 : 0)];
			this.pages = new JPanel[dots.length];
			if (dots.length == 0)
				return;
			add(mainP = new JPanel(new BorderLayout()));
			var s = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			mainP.add(dragP = new JPanel(page = new CardLayout()));
			mainP.add(s, "South");
			BasePage.setB(this, new TitledBorder(new LineBorder(Color.BLACK, 2), title, TitledBorder.LEFT,
					TitledBorder.TOP, new Font("맑은 고딕", Font.BOLD, 20)));

			for (int i = 0; i < dots.length; i++) {
				dots[i] = new JLabel("●", JLabel.CENTER);
				dots[i].setForeground(Color.BLACK);
				dots[i].setName(i + "");
				pages[i] = new JPanel(new GridLayout(1, 0, 20, 20));
				dragP.add(pages[i], i + "");
				s.add(dots[i]);
			}

			int pagecnt = 0;
			for (int i = 0; i < itemList.size(); i++) {
				if (pages[pagecnt].getComponents().length == 5)
					pagecnt++;
				pages[pagecnt].add(itemList.get(i));
			}

			dragP.addMouseListener(new MouseAdapter() {
				int x;

				public void mousePressed(java.awt.event.MouseEvent e) {
					x = e.getX();

				};

				@Override
				public void mouseReleased(MouseEvent e) {
					if (x < e.getX()) {
						if (pageIdx == 0)
							return;

						for (int i = 0; i < dots.length; i++) {
							dots[i].setForeground(Color.BLACK);
						}
						page.previous(dragP);
						dots[--pageIdx].setForeground(Color.RED);
					} else if (e.getX() < x) {
						if (pageIdx == dots.length - 1)
							return;
						for (int i = 0; i < dots.length; i++) {
							dots[i].setForeground(Color.BLACK);
						}
						dots[++pageIdx].setForeground(Color.RED);
						page.next(dragP);
					}
				}
			});
			dots[0].setForeground(Color.RED);
		}
	}

}
