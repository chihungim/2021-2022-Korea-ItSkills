package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

public class Map extends JDialog {
	HashMap<Integer, Point> locMap = new HashMap<Integer, Point>();
	HashMap<Integer, Integer> uMap = new HashMap<Integer, Integer>();
	HashMap<Integer, Object[]> dMap = new HashMap<Integer, Object[]>();

	final int INF = Short.MAX_VALUE;
	ArrayList<Integer> beeLine = new ArrayList<Integer>();
	ArrayList<LocalDateTime> times[];
	ArrayList<LocalDateTime> time;
	ArrayList<JRadioButton> addrs = new ArrayList<JRadioButton>();
	ArrayList<ArrayList<Integer>> beeLineList;
	int adjDim[][], pathDim[][];
	static String kind;

	int bwSize = 70, bhSize = 50, hOff = -35, vOff = -15;

	JPanel center;

	public Map(String kind) {
		super(BasePage.mf, "배송 현황", true);
		setSize(1200, 750);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);

		add(BasePage.lbl("[" + BasePage.sname + "] 물품배달", 0, 25), "North");
		add(center = new JPanel(null));
		Map.kind = kind;

		try {
			var rs = BasePage.rs("select * from point");
			while (rs.next()) {
				locMap.put(rs.getInt(1), new Point(rs.getInt(2), rs.getInt(3)));
				addrs.add(new JRadioButton(rs.getInt(2) + "-" + rs.getInt(3)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = BasePage.rs("select * from user");
			while (rs.next()) {
				uMap.put(rs.getInt(1), rs.getInt(5));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = BasePage.rs(
					"select p.u_No, p.pu_No, r.r_Time from purchase as p, receive as r where p.pu_No = r.pu_No and p.s_No = "
							+ BasePage.sno + " and date(r.r_Time) = date(now())");
			while (rs.next()) {
				dMap.put(rs.getInt(1), new Object[] { rs.getInt(2), rs.getString(3) });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adjDim = new int[locMap.size() + 1][locMap.size() + 1];

		for (int i = 1; i <= locMap.size(); i++) {
			for (int j = i + 1; j <= locMap.size(); j++) {
				adjDim[i][j] = adjDim[j][i] = INF;
			}
		}

		try {
			var rs = BasePage.rs("select * from connect");
			while (rs.next()) {
				var r = rs.getInt(1);
				var c = rs.getInt(2);
				int x1 = locMap.get(r).x, x2 = locMap.get(c).x;
				int y1 = locMap.get(r).y, y2 = locMap.get(c).y;
				adjDim[r][c] = adjDim[c][r] = (int) Point.distance(x1, y1, x2, y2);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < addrs.size(); i++) {
			Point p = locMap.get(i + 1);
			var me = addrs.get(i);
			me.setName(i + 1 + "");
			me.setBounds(p.x + hOff, p.y + vOff, bwSize, bhSize);
			me.setVerticalAlignment(0);
			me.setHorizontalAlignment(0);
			me.setHorizontalTextPosition(0);
			me.setVerticalTextPosition(3);
			me.setEnabled(false);
			me.setOpaque(false);
			center.add(me);
			center.setComponentZOrder(me, 0);
		}
		var line = new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(2.5f));
				g2.setColor(Color.LIGHT_GRAY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for (var r : locMap.keySet()) {
					for (int c = 1; c <= locMap.size(); c++) {
						if (adjDim[r][c] > 0 && adjDim[r][c] < INF) {
							int x1 = locMap.get(r).x;
							int x2 = locMap.get(c).x;
							int y1 = locMap.get(r).y;
							int y2 = locMap.get(c).y;
							g2.drawLine(x1, y1, x2, y2);
						}
					}
				}

				if (beeLineList != null) {
					beeLineList.forEach(a -> {
						a.forEach(e -> {
							addrs.get(e - 1).setSelected(true);
							addrs.get(e - 1).setEnabled(true);
						});
					});

					for (int i = 0; i < beeLineList.size(); i++) {
						var beeline = beeLineList.get(i);
						var time = times[i];
						final int xx[] = new int[beeline.size()], yy[] = new int[beeline.size()];
						for (int j = 0; j < beeline.size(); j++) {
							xx[j] = locMap.get(beeline.get(j)).x;
							yy[j] = locMap.get(beeline.get(j)).y;
							g2.setColor(Color.BLACK);
							if (j == 0)
								g2.drawString("출발", xx[j] - 10, yy[j] - 15);
							else if (j == beeline.size() - 1)
								g2.drawString("도착", xx[j] - 10, yy[j] - 15);

							if (time.get(j).isAfter(LocalDateTime.now()))
								g2.setColor(Color.RED);
							else if (time.get(j).isBefore(LocalDateTime.now()))
								g2.setColor(Color.BLUE);
							else
								g2.setColor(LocalDateTime.now().getSecond() % 2 == 0 ? Color.RED : Color.blue);
						}
						g2.drawPolyline(xx, yy, xx.length);
					}
				}

				if (beeLine != null) {
					for (int i = 0; i < beeLine.size(); i++) {
						addrs.get(beeLine.get(i) - 1).setSelected(true);
						addrs.get(beeLine.get(i) - 1).setEnabled(true);
					}

					for (int i = 0; i < beeLine.size() - 1; i++) {
						int x1 = locMap.get(beeLine.get(i)).x;
						int x2 = locMap.get(beeLine.get(i + 1)).x;
						int y1 = locMap.get(beeLine.get(i)).y;
						int y2 = locMap.get(beeLine.get(i + 1)).y;

						g2.setColor(Color.BLACK);

						if (i == 0)
							g2.drawString("출발", x1 - 10, y1 - 15);
						else if (i == beeLine.size() - 2)
							g2.drawString("도착", x2 - 10, y2 - 15);

						if (time.get(i).isAfter(LocalDateTime.now()))
							g2.setColor(Color.red);
						else if (time.get(i + 1).isBefore(LocalDateTime.now()))
							g2.setColor(Color.blue);
						else {
							var blink = LocalDateTime.now().getSecond() % 2 == 0;
							g2.setColor(blink ? Color.red : Color.BLUE);
						}

						g2.drawLine(x1, y1, x2, y2);
					}

				}
			}

		};

		Timer p = new Timer(1, a -> {
			line.repaint();
			line.revalidate();
		});

		p.start();
		p.setCoalesce(true);

		center.add(line);
		line.setBounds(0, 0, 1000, 700);
		center.setSize(100, 700);
		dijkstra(BasePage.sAddr);

		if (kind.contentEquals("seller")) {
			beeLineList = new ArrayList<ArrayList<Integer>>();

			for (var v : dMap.keySet()) {
				beeLineList.add(getBeePath(v));
			}

			var sTimes = new LocalDateTime[beeLineList.size()];
			var idx = 0;
			for (var v : dMap.keySet()) {
				sTimes[idx] = LocalDateTime.parse(dMap.get(v)[1].toString(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				idx++;
			}

			times = new ArrayList[beeLineList.size()];

			for (int i = 0; i < beeLineList.size(); i++) {
				var time = times[i] = new ArrayList<LocalDateTime>();
				var beeline = beeLineList.get(i);
				for (int j = 0; j < beeline.size(); j++) {
					time.add(sTimes[i].plusSeconds(pathDim[1][beeline.get(j)] / 10));
				}
			}

		} else {
			beeLine = getBeePath(uMap.get(BasePage.uno));
			time = new ArrayList<LocalDateTime>();
			var sTime = BasePage.pdtime;
			for (int i = 0; i < beeLine.size(); i++) {
				time.add(sTime.plusSeconds(pathDim[1][beeLine.get(i)] / 10));
			}

			showState();
		}
	}

	public static void main(String[] args) {
		BasePage.sAddr = 1;
		BasePage.sno = 1;
		new Map("seller").setVisible(true);
	}

	void showState() {
		int eidx = beeLine.size() - 1;

		// 배달 상황 기록
		int totalSec = pathDim[1][beeLine.get(eidx)] / 10;
		String stag = "<font color=\"blue\">", etag = "</font>";
		JLabel lbl = new JLabel();
		lbl.setFont(new Font("굴림체", Font.LAYOUT_RIGHT_TO_LEFT, 16));
		lbl.setText("<html><br> ○ 출발지점 " + stag + addrs.get(beeLine.get(0) - 1).getText() + etag + "<br><br> ○ 도착지점 "
				+ stag + addrs.get(beeLine.get(eidx) - 1).getText() + etag + "<br><br> ○ 최단거리 " + stag
				+ pathDim[1][beeLine.get(eidx)] + "m" + etag + "<br><br> ○ 소요시간 " + stag
				+ String.format("%02d", totalSec / 3600) + ":" + String.format("%02d", (totalSec % 3600) / 60) + ":"
				+ String.format("%02d", totalSec % 60) + etag + "<br><br> ○ 배송시간 " + stag
				+ DateTimeFormatter.ofPattern("HH:mm:ss").format(time.get(0)).toString() + etag + "<br><br> ○ 도착시간 "
				+ stag + DateTimeFormatter.ofPattern("HH:mm:ss").format(time.get(eidx)).toString() + etag + "</html>");
		center.add(lbl);
		lbl.setBounds(950, 50, 190, 250);
		lbl.setBackground(new Color(255, 255, 0));
		lbl.setOpaque(true);
		lbl.setVerticalAlignment(JLabel.TOP);
		lbl.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	ArrayList<Integer> getBeePath(int arrv) {
		ArrayList<Integer> path = new ArrayList<>();

		path.add(arrv);
		while (pathDim[3][arrv] != -1) {
			path.add(pathDim[3][arrv]);
			arrv = pathDim[3][arrv];

		}

		Collections.reverse(path);
		return path;
	}

	void dijkstra(int dep) {
		pathDim = new int[4][locMap.size() + 1];

		for (int i = 1; i <= locMap.size(); i++) {
			pathDim[1][i] = adjDim[dep][i];
			pathDim[2][i] = 0;
			if (adjDim[i][dep] < INF)
				pathDim[3][i] = dep;

		}

		pathDim[2][dep] = 1;
		pathDim[3][dep] = -1;

		for (int i = 1; i < locMap.size(); i++) {
			int minDist = 2147483647, idx = 0;
			for (int j = 1; j <= locMap.size(); j++) {
				if (pathDim[2][j] == 0 && minDist > pathDim[1][j]) {
					minDist = pathDim[1][j];
					idx = j;
				}
			}
			pathDim[2][idx] = 1;

			int from = idx, to;
			for (to = 1; to <= locMap.size(); to++) {
				if (pathDim[2][to] == 0 && pathDim[1][to] > pathDim[1][from] + adjDim[from][to]) {
					pathDim[1][to] = pathDim[1][from] + adjDim[from][to];
					pathDim[3][to] = from;
				}
			}
		}
	}

}
