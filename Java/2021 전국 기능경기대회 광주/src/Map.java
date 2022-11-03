

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;

public class Map extends Basedialog {
	map map[][] = new map[15][15];
	Point sp, rp, up;
	ArrayList<map> p1;
	ArrayList<map> p2;
	int rno;
	boolean mode;
	int dx[] = { -1, 0, 1, 0 }, dy[] = { 0, -1, 0, 1 };
	Sign s;

	public Map(Basedialog b) {
		super("지도", 800, 800);
		setLayout(new GridLayout(15, 15));
		setModal(true);

		if (b instanceof Sign) {
			mode = true;
			s = (Sign) b;
		}

		init();

		setVisible(true);
	}

	public Map(Basedialog b, Point up, Point rp, Point sp, int rno) {
		super("지도", 800, 800);
		setLayout(new GridLayout(15, 15));

		init();

		this.up = up;
		this.sp = sp;
		this.rp = rp;
		this.rno = rno;

		R2S();

		setVisible(true);
	}

	void R2S() {
		map[sp.y - 1][sp.x - 1].setBackground(Color.RED);
		map[rp.y - 1][rp.x - 1].setBackground(Color.MAGENTA);
		bfs(new Point(sp.x - 1, sp.y - 1), new Point(rp.x - 1, rp.y - 1));
		showAnim();
	}

	void R2U() {
		map[up.y - 1][up.x - 1].setBackground(Color.RED);
		map[sp.y - 1][sp.x - 1].setBackground(Color.MAGENTA);
		bfs(new Point(up.x - 1, up.y - 1), new Point(sp.x - 1, sp.y - 1));
	}

	private void showAnim() {
		new Thread(() -> {
			for (int i = 0; i < p1.size(); i++) {
				p1.get(i).setBackground(Color.LIGHT_GRAY);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			msg("음식점에 도착했습니다!");
			reset();
			R2U();
			for (int i = 0; i < p2.size(); i++) {
				p2.get(i).setBackground(Color.LIGHT_GRAY);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			msg("배달을 완료했습니다!");
			execute("update receipt set status = 3 where  no = " + rno);
			dispose();
		}).start();
	}

	private void reset() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j].type == 0) {
					map[i][j].setBackground(Color.WHITE);
				} else if (map[i][j].type == 1) {
					map[i][j].setBackground(Color.ORANGE);
				} else if (map[i][j].type == 2) {
					map[i][j].setBackground(Color.PINK);
					String name = getone("select name from seller where map = " + map[i][j].no);
					map[i][j].setToolTipText(name);
					map[i][j].setText(name);
				} else if (map[i][j].type == 3) {
					map[i][j].setBackground(Color.BLUE);
				} else if (map[i][j].type == 4) {
					map[i][j].setBackground(Color.GREEN.darker());
				}
			}
		}
	}

	private void bfs(Point cur, Point arrv) {
		ArrayList<map> r = new ArrayList<map>();
//		Queue<mjl> q = new LinkedList<mjl>();
		ArrayList<Integer> rcnt = new ArrayList<Integer>();
		ArrayList<Integer> startP = new ArrayList<Integer>();
		ArrayList<map> sp = new ArrayList<map>();

		for (int i = 0; i < dx.length; i++) {
			int x = cur.x + dx[i];
			int y = cur.y + dy[i];
			if ((x > -1 && x < map.length) && (y > -1 && y < map.length)) {
				if (map[y][x].type == 1) {
					r.add(map[y][x]);
					rcnt.add(1);
					startP.add(-1);
				}
			}
		}

		int didx = -1;
		

		while (true) {
			didx++;

			boolean stop = false;
			var cu = r.get(didx);
			int pcnt = rcnt.get(didx);

			for (int i = 0; i < dx.length; i++) {
				int x = cu.x + dx[i];
				int y = cu.y + dy[i];
				if (x > -1 && x < map.length && y > -1 && y < map.length) {
					if (y == arrv.y && x == arrv.x) {
						stop = true;
						break;
					}
				}
			}

			if (stop)
				break;

			for (int i = 0; i < dx.length; i++) {
				int x = cu.x + dx[i];
				int y = cu.y + dy[i];
				if (x > -1 && x < map.length && y > -1 && y < map.length) {
					if (!(r.contains(map[y][x])) && map[y][x].type == 1 && !map[y][x].visit) {
						r.add(map[y][x]);
						pcnt++;
						rcnt.add(pcnt);
						startP.add(didx);
					}
				}
			}
			if (p1 == null) {
				p1 = sp;
			} else {
				p2 = sp;
			}
		}
		int idx = didx;
		while (true) {
			sp.add(r.get(idx));
			idx = startP.get(idx);
			if (idx == -1)
				break;
		}
	}

	private void init() {
		try {
			ResultSet rs = stmt.executeQuery("select * from map");
			while (rs.next()) {
				map[rs.getInt(3) - 1][rs.getInt(2) - 1] = new map(rs.getInt(4), rs.getInt(1), rs.getInt(2),
						rs.getInt(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				add(map[i][j]);
				if (mode) {
					map[i][j].addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if (e.getClickCount() == 2) {
								map m = (map) e.getSource();
								if (m.type == 1) {
									errmsg("길은 선택할 수 없습니다.");
									return;
								}
								if (m.type != 0) {
									errmsg("이미 사용중인 위치입니다.");
									return;
								}
								s.tt.setText(getone("select no from map where x = " + m.x + " and y = " + m.y));
								s.jb.setEnabled(true);
								dispose();
							}
						}
					});
				}

				if (map[i][j].type == 0) {
					map[i][j].setBackground(Color.WHITE);
				} else if (map[i][j].type == 1) {
					map[i][j].setBackground(Color.ORANGE);
				} else if (map[i][j].type == 2) {
					map[i][j].setBackground(Color.PINK);
					String name = getone("select name from seller where map = " + map[i][j].no);
					map[i][j].setToolTipText(name);
					map[i][j].setText(name);
				} else if (map[i][j].type == 3) {
					map[i][j].setBackground(Color.BLUE);
				} else if (map[i][j].type == 4) {
					map[i][j].setBackground(Color.GREEN.darker());
				}
			}
		}
	}

	class map extends JLabel {
		int type, no, x, y;
		boolean visit;

		public map(int type, int no, int x, int y) {
			setOpaque(true);
			this.type = type;
			this.no = no;
			this.x = x - 1;
			this.y = y - 1;
		}
	}

	public static void main(String[] args) {
		NO = 3;
		new Delivery();
	}

}
