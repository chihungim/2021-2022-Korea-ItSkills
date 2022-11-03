package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class Map extends BaseFrame {

	Image img = img("metro.png").getImage();

	JScrollPane scr;
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item[] = { new JMenuItem("출발으로 설정"), new JMenuItem("도착으로 설정"), new JMenuItem("해당 역 정보 보기") };

	int n[][] = { {0,0}, { 1110, 81 }, { 1192, 81 }, { 1268, 82 }, { 1354, 78 }, { 1442, 83 }, { 1504, 80 }, { 1570, 81 },
			{ 1631, 82 }, { 1696, 80 }, { 1760, 81 }, { 1839, 81 }, { 1890, 131 }, { 1922, 159 }, { 1956, 193 },
			{ 1958, 230 }, { 1956, 254 }, { 1956, 287 }, { 1956, 324 }, { 1956, 356 }, { 1956, 392 }, { 1954, 418 },
			{ 1956, 476 }, { 1956, 511 }, { 1955, 548 }, { 1883, 545 }, { 1818, 546 }, { 1763, 545 }, { 1683, 547 },
			{ 1557, 544 }, { 1482, 545 }, { 1429, 545 }, { 1313, 549 }, { 1311, 639 }, { 1312, 825 }, { 1311, 901 },
			{ 1311, 1084 }, { 1217, 1081 }, { 1184, 1084 }, { 1142, 1083 }, { 1055, 1081 }, { 989, 1082 },
			{ 826, 1081 }, { 771, 1083 }, { 711, 1082 }, { 643, 1083 }, { 586, 1081 }, { 538, 1081 }, { 463, 1081 },
			{ 432, 1053 }, { 401, 1020 }, { 369, 993 }, { 340, 962 }, { 319, 943 }, { 273, 942 }, { 220, 943 },
			{ 169, 942 }, { 120, 946 }, { 116, 995 }, { 116, 1035 }, { 116, 1076 }, { 118, 1111 }, { 115, 1150 },
			{ 828, 1161 }, { 825, 1249 }, { 827, 1295 }, { 771, 1294 }, { 825, 1338 }, { 829, 1398 }, { 843, 1423 },
			{ 872, 1453 }, { 908, 1491 }, { 910, 1534 }, { 909, 1596 }, { 909, 1661 }, { 910, 1723 }, { 909, 1788 },
			{ 910, 1843 }, { 988, 1844 }, { 1066, 1844 }, { 1129, 1779 }, { 1144, 1841 }, { 1228, 1845 },
			{ 1310, 1846 }, { 1387, 1842 }, { 1463, 1842 }, { 1543, 1845 }, { 1615, 1845 }, { 1688, 1843 },
			{ 1761, 1845 }, { 1833, 1844 }, { 1905, 1844 }, { 1979, 1845 }, { 2053, 1845 }, { 2151, 1844 },
			{ 2260, 1843 }, { 2405, 1843 }, { 2507, 1843 }, { 2507, 1723 }, { 683, 965 }, { 686, 1007 }, { 814, 1011 },
			{ 915, 1010 }, { 988, 1161 }, { 990, 1230 }, { 989, 1319 }, { 1073, 1319 }, { 1137, 1319 }, { 1219, 1321 },
			{ 1311, 1318 }, { 1402, 1319 }, { 1452, 1318 }, { 1498, 1318 }, { 1540, 1319 }, { 1650, 1320 },
			{ 1694, 1318 }, { 1740, 1318 }, { 1807, 1257 }, { 1876, 1191 }, { 1915, 1152 }, { 1916, 1075 },
			{ 1914, 1004 }, { 1914, 942 }, { 1913, 905 }, { 1914, 864 }, { 1914, 822 }, { 1883, 791 }, { 1861, 766 },
			{ 1821, 726 }, { 1730, 642 }, { 1684, 639 }, { 1559, 636 }, { 1515, 636 }, { 1429, 641 }, { 1369, 640 },
			{ 1222, 640 }, { 1172, 685 }, { 1128, 730 }, { 1083, 772 }, { 1039, 811 }, { 992, 857 }, { 988, 906 },
			{ 988, 961 }, { 990, 1015 }, { 1914, 783 }, { 1915, 697 }, { 1819, 597 }, { 301, 368 }, { 356, 370 },
			{ 422, 368 }, { 489, 369 }, { 543, 369 }, { 628, 368 }, { 675, 371 }, { 722, 369 }, { 773, 368 },
			{ 822, 370 }, { 870, 370 }, { 924, 369 }, { 1031, 368 }, { 1107, 370 }, { 1199, 370 }, { 1258, 370 },
			{ 1324, 370 }, { 1426, 370 }, { 1431, 419 }, { 1429, 474 }, { 1480, 728 }, { 1540, 786 }, { 1582, 826 },
			{ 1619, 865 }, { 1620, 931 }, { 1618, 1055 }, { 1593, 1083 }, { 1539, 1139 }, { 1540, 1240 },
			{ 1544, 1397 }, { 1651, 1396 }, { 1696, 1395 }, { 1738, 1395 }, { 1779, 1359 }, { 1814, 1328 },
			{ 1887, 1320 }, { 1935, 1320 }, { 1972, 1321 }, { 1974, 1211 }, { 2104, 1212 }, { 2218, 1214 },
			{ 266, 1668 }, { 324, 1665 }, { 394, 1664 }, { 458, 1666 }, { 512, 1664 }, { 585, 1663 }, { 627, 1629 },
			{ 662, 1596 }, { 687, 1567 }, { 720, 1536 }, { 743, 1512 }, { 766, 1493 }, { 841, 1489 }, { 978, 1490 },
			{ 1027, 1491 }, { 1082, 1490 }, { 1163, 1491 }, { 1241, 1491 }, { 1294, 1489 }, { 1399, 1489 },
			{ 1404, 1428 }, { 1404, 1388 }, { 1403, 1239 }, { 1404, 1163 }, { 1404, 1082 }, { 1402, 1005 },
			{ 1402, 920 }, { 1368, 885 }, { 1358, 781 }, { 1412, 728 }, { 1558, 479 }, { 1559, 423 }, { 1558, 366 },
			{ 1558, 292 }, { 1675, 288 }, { 1753, 288 }, { 1809, 288 }, { 1872, 288 }, { 2073, 288 }, { 2169, 287 },
			{ 2251, 288 }, { 527, 557 }, { 571, 600 }, { 625, 654 }, { 682, 714 }, { 682, 770 }, { 682, 817 },
			{ 682, 862 }, { 681, 905 }, { 776, 964 }, { 829, 963 }, { 890, 964 }, { 951, 962 }, { 1059, 1023 },
			{ 1142, 983 }, { 1142, 920 }, { 1143, 870 }, { 1224, 781 }, { 1226, 705 }, { 1226, 484 }, { 1365, 483 },
			{ 1610, 726 }, { 1666, 727 }, { 1748, 729 }, { 1973, 729 }, { 2007, 762 }, { 2043, 797 }, { 2071, 831 },
			{ 2073, 880 }, { 2072, 943 }, { 2074, 1014 }, { 2219, 1018 }, { 2218, 975 }, { 2218, 931 }, { 2219, 895 },
			{ 2219, 852 }, { 2218, 815 }, { 2218, 775 }, { 2221, 738 }, { 2329, 738 }, { 2328, 774 }, { 2327, 813 },
			{ 2218, 1050 }, { 2219, 1084 }, { 2218, 1153 }, { 2216, 1288 }, { 2235, 1313 }, { 2270, 1347 } };
	JLabel lblImg, lblPath, lbls[] = new JLabel[n.length];
	ArrayList<String> list = new ArrayList<String>();
	int dep, arrv;
	int pathDim[][];
	ArrayList<Integer> cpath;
	
	public Map() {
		super("지도", 1600, 900);

		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new GridLayout()), "South");

		c.add(scr = new JScrollPane(lblImg = new JLabel(new ImageIcon(img)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.red);

				for (int i = 1; i < adjDim.length; i++) {
					for (int j = i+1; j < adjDim.length; j++) {
						if (adjDim[i][j] != 0 && adjDim[i][j]!=INF) {
							int x1 = n[i][0], x2 = n[j][0];
							int y1 = n[i][1], y2 = n[j][1];
							g2.drawString(adjDim[i][j]/5+"", (x1+x2)/2-5, ((y1+y2)/2)+5);
						}
					}
				}
				
				
//				for (int i = 0; i < list.size(); i++) {
//					var p = list.get(i).substring(list.get(i).indexOf("{") + 1, list.get(i).indexOf("}")).split(",");
//					g2.drawRect(rei(p[0]) - 10, rei(p[1]) - 10, 20, 20);
//				}
			}
		}));

		s.add(btn("결정", a -> {
			if (cpath == null) {
				eMsg("출발역과 도착역을 모두 선택해야 합니다.");
				return;
			}
			new Reserve(cpath).addWindowListener(new Before(this));
		}));

		for (int i = 0; i < item.length; i++) {
			pop.add(item[i]);
			item[i].addActionListener(a -> {
				if (a.getSource().equals(item[0])) {
					dep = rei(sno);
					iMsg("출발역을 "+stNames.get(dep)+"으로 설정하였습니다.");
					
					if (dep != 0 && arrv != 0) {
						dijkstra();
						showPath(arrv);
					}
				} else if (a.getSource().equals(item[1])) {
					arrv = rei(sno);
					iMsg("도착역을 "+stNames.get(arrv)+"으로 설정하였습니다.");
					if (dep != 0 && arrv != 0) {
						dijkstra();
						showPath(arrv);
					}
				} else {
					new Station().addWindowListener(new Before(this));
				}
			});
		}
		
		
		for (int i = 0; i < n.length; i++) {
			lblImg.add(lbls[i] = new JLabel());
			lbls[i].setBounds(n[i][0]-10, n[i][1]-10, 20, 20);
			int k = i;
			lbls[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton()==3) {
						pop.setName(k+"");
						sno = k+"";
						var p = getMousePosition();
						pop.show(Map.this, p.x, p.y);
					}
				}
			});
		}

//		lblImg.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				if (e.getButton() == 3) {
//					list.remove(list.size() - 1);
//					lblImg.repaint();
//					lblImg.revalidate();
//					System.out.println("좌표목록 : " + String.join(",", list));
//				} else {
//					list.add("{" + e.getX() + "," + e.getY() + "}");
//					lblImg.repaint();
//					lblImg.revalidate();
//					System.out.println("좌표목록 : " + String.join(",", list));
//				}
//			}
//		});

		this.setVisible(true);
	}
	
	void showPath(int idx){
		cpath = new ArrayList<Integer>();
		cpath.add(idx);
		while (pathDim[3][idx] != -1) {
			cpath.add(pathDim[3][idx]);
			idx = pathDim[3][idx];
		}
		
		Collections.reverse(cpath);
		
		dep = 0;
		arrv = 0;
		
		try {
			lblImg.remove(lblPath);
		} catch (Exception e) {
		}
		
		lblImg.add(lblPath = new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int xx[] = new int[cpath.size()], yy[] = new int[cpath.size()];
				for (int i = 0; i < yy.length; i++) {
					xx[i] = n[cpath.get(i)][0];
					yy[i] = n[cpath.get(i)][1];
				}
				
				g2.setStroke(new BasicStroke(5));
				g2.setColor(Color.white);
				g2.drawPolyline(xx, yy, cpath.size());
				setOpaque(false);
			}
		});
		lblPath.setBounds(0, 0, img.getWidth(null), img.getHeight(null));
		
		repaint();
		revalidate();
		
	}
	
	void dijkstra() {
		pathDim = new int[4][n.length];
		for (int i = 0; i < n.length; i++) {
			pathDim[1][i] = adjDim[dep][i];
			pathDim[2][i] = 0;
			if (adjDim[i][dep] < INF) {
				pathDim[3][i] = dep;
			}
		}
		
		pathDim[2][dep] = 1;
		pathDim[3][dep] = -1;
		
		for (int i = 0; i < n.length-1; i++) {
			int min = 21000000, idx = 0;
			for (int j = 1; j < n.length; j++) {
				if (pathDim[2][j] == 0 && pathDim[1][j] < min) {
					min = pathDim[1][j];
					idx = j;
				}
			}
			pathDim[2][idx] = 1;
			int from = idx;
			for (int to = 0; to < n.length; to++) {
				if (pathDim[2][to] == 0 && pathDim[1][to] > adjDim[from][to] + pathDim[1][from]) {
					pathDim[1][to] = adjDim[from][to] + pathDim[1][from];
					pathDim[3][to] = from;
				}
			}
		}
		
	}

}
