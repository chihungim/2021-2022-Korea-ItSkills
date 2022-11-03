import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class ReserveManage extends JPanel {
	JPanel n, c, s;
	JPanel n_s;
	JPanel Chart;

	DefaultTableModel m = BaseFrame.model("순번,예매자,출발지,도착지,출발날짜,도착시간".split(","));
	JTable t = BaseFrame.table(m);
	JScrollPane jsc = new JScrollPane(t);

	JComboBox<String> com = new JComboBox<String>();

	JPanel p1 = new JPanel(new GridLayout(0, 1)), p2 = new JPanel(new GridLayout(0, 1)),
			menuP = new JPanel(new GridLayout(1, 0));
	JPopupMenu menu = new JPopupMenu();
	JScrollPane menuJsc = new JScrollPane(p1);
	JScrollPane menuJsc2 = new JScrollPane(p2);
	JButton btn1[] = new JButton[16], btn2[];

	boolean bool;

	int num[] = new int[6];
	int count = 0;

	public ReserveManage() {

		UI();
		data();
		event();
	}

	void data() {
		try {
			var rs = BaseFrame.stmt.executeQuery("select * from location");
			int i = 0;
			while (rs.next()) {
				p1.add(btn1[i] = new JButton(rs.getString("name")));
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3 && (t.getSelectedColumn() == 1 || t.getSelectedColumn() == 2)) {
					count = t.getSelectedColumn();
					menu.show(t, e.getX(), e.getY());
				}
			}
		});

		for (int i = 0; i < btn1.length; i++) {
			int j = i;
			btn1[i].addActionListener(a -> {
				p2.removeAll();
				try {
					ResultSet rs1 = BaseFrame.stmt
							.executeQuery("select * from location2 where location_no = " + (j + 1));
					rs1.last();
					btn2 = new JButton[rs1.getRow()];
					rs1.beforeFirst();
					int k = 0;
					while (rs1.next()) {
						p2.add(btn2[k] = new JButton(rs1.getString(2)));
						btn2[k].addActionListener(a2 -> {
							JButton jj = (JButton) a2.getSource();
							String aa = btn1[j].getText() + " " + jj.getText();
							t.setValueAt(aa, t.getSelectedRow(), count);
							bool = true;
						});
						k++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				repaint();
				revalidate();
			});

			for (Component c : getComponents()) {
				if (c instanceof JPanel)
					remove(c);
			}

			areaChart();

			com.addItemListener(e -> {
				if (com.getSelectedIndex() == 0) {
					for (Component c : getComponents()) {
						if (c instanceof JPanel)
							remove(c);
					}
					areaChart();
					repaint();
					revalidate();
				} else {
					for (Component c : getComponents()) {
						if (c instanceof JPanel)
							remove(c);
					}

					radialChart();
					repaint();
					revalidate();
				}
			});

			rowData();
		}
	}

	void rowData() {
		m.setRowCount(0);

		try {
			ResultSet rs = BaseFrame.stmt.executeQuery(
					"SELECT u.name, a.name, b.name, date, date_add(date, interval elapsed_time HOUR_SECOND) FROM busticketbooking.reservation r inner join user u on r.user_no = u.no inner join schedule s on r.schedule_no = s.no inner join a on s.departure_location2_no = a.no inner join a b on s.arrival_location2_no = b.no order by r.no asc");
			int i = 1;
			while (rs.next()) {
				Object row[] = { i, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5).split(" ")[1] };
				m.addRow(row);
				i++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	void UI() {
		setLayout(null);

		menu.add(menuP);
		menuP.add(menuJsc);
		menuP.add(menuJsc2);

		BaseDialog.size(menu, 240, 280);

		add(BaseFrame.lbl("예매 관리", 2, 20)).setBounds(10, 20, 130, 30);
		add(com).setBounds(110, 25, 100, 25);
		add(BaseFrame.lbl("<가장 예매가 많은 일정 TOP 6>", 0, 13)).setBounds(370, 20, 200, 30);

		com.addItem("2차원 영역형");
		com.addItem("방사형");

		add(jsc).setBounds(10, 420, 950, 180);
		add(BaseFrame.btn("저장", e -> save())).setBounds(750, 600, 90, 30);
		add(BaseFrame.btn("삭제", e -> delete())).setBounds(850, 600, 90, 30);
	}

	void delete() {
		BaseFrame.imsg = new BaseFrame.iMsg("삭제를 완료하였습니다.");
		BaseFrame.execute("delete from schedule where no = " + BaseFrame.toInt(t.getValueAt(t.getSelectedRow(), 0)));
	}

	void save() {
		if (bool) {
			BaseFrame.imsg = new BaseFrame.iMsg("수정내용을 저장 완료하였습니다.");
			for (int i = 0; i < t.getRowCount(); i++) {
				int depart = BaseFrame
						.toInt(BaseFrame.getOne("select no from location2 where name = '" + t.getValueAt(i, 1) + "'"));
				int arrive = BaseFrame
						.toInt(BaseFrame.getOne("select no from location2 where name = '" + t.getValueAt(i, 2) + "'"));
				BaseFrame.execute("update schedule set departure_location2_no = " + depart + ", arrival_location2_no = "
						+ arrive + " where no = " + BaseFrame.toInt(t.getValueAt(i, 0)));
			}
		}
	}

	void areaChart() {
		Chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.drawLine(50, 20, 50, 260);
				for (int i = 0; i < 5; i++) {
					g2d.drawLine(50, 20 + (i * 60), 600, 20 + (i * 60));
				}
				Polygon p = new Polygon();

				try {
					var rs = BaseFrame.stmt.executeQuery(
							"select *, count(schedule_no) as cnt from reservation group by schedule_no order by cnt desc, schedule_no asc limit 6");
					int i = 0, max = 0;
					while (rs.next()) {
						if (i == 0)
							max = rs.getInt(4);

						if (i < 5)
							g2d.drawString(max - (i * 2) + "", 30, 25 + (i * 60));

						g2d.drawString(rs.getString(3), 50 + (i * 105), 290);
						p.addPoint(50 + (i * 110), 20 + ((max - rs.getInt(4)) * 30));
						i++;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				p.addPoint(600, 260);
				p.addPoint(50, 260);
				g2d.setColor(new Color(0, 123, 255));
				g2d.fillPolygon(p);
			}
		};

		add(Chart).setBounds(15, 100, 900, 300);
		Chart.setOpaque(true);
		repaint();
		revalidate();
	}

	void radialChart() {
		Chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(Color.BLACK);

				int po[][] = new int[6][2];
				Polygon p[] = { new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(),
						new Polygon() };
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 6; j++) {
						p[i].addPoint((int) (380 + (100 - i * 20) * Math.cos(j * Math.PI / 3 + (Math.PI / 6))),
								(int) (150 + (100 - i * 20) * Math.sin(j * Math.PI / 3 + (Math.PI / 6))));
					}

					g2.drawPolygon(p[i]);
				}

				for (int i = 0; i < 6; i++) {
					po[i][0] = (int) (375 + (110) * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
					po[i][1] = (int) (150 + (110) * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
				}

				int basex = 0, basey = 0;
				try {
					ResultSet rs = BaseFrame.stmt.executeQuery(
							"SELECT *, count(schedule_no) as cnt FROM busticketbooking.reservation group by schedule_no order by cnt desc, schedule_no asc limit 6");
					int i = 0, max = 0;
					while (rs.next()) {
						if (i == 0) {
							max = rs.getInt(4);
							basex = (int) (po[0][0] / max);
							basey = (int) (po[0][1] / max);
						}
						g2.setColor(Color.BLACK);
						if (i < 5)
							g2.drawString(max - 2 * i + "", 370, 55 + (i * 20));
						g2.drawString(rs.getString(3), po[i][0], po[i][1]);
						num[i] = rs.getInt(4);
						i++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				g2.setColor(new Color(0, 123, 255));
				Polygon poly = new Polygon();
				for (int i = 0; i < num.length; i++) {
					poly.addPoint(
							(int) (380
									+ (100 - (num[0] - num[i]) / 2.0 * 20) * Math.cos(i * Math.PI / 3 - (Math.PI / 2))),
							(int) (150 + (100 - (num[0] - num[i]) / 2.0 * 20)
									* Math.sin(i * Math.PI / 3 - (Math.PI / 2))));
				}
				g2.drawPolygon(poly);
			}
		};

		Chart.setOpaque(true);
		add(Chart).setBounds(100, 100, 500, 300);
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}
