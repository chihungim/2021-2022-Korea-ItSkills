package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import model.Game;

public class UserMain extends BasePage {

	JLabel lbl[] = new JLabel[5];
	int idx = 0;

	ArrayList<JPanel> games = new ArrayList<>();
	boolean moved;
	JPanel ccc;
	Animation animation;

	public UserMain() {
		super("사용자메인");
		add(c = newJPanel(new FlowLayout(FlowLayout.CENTER)));
		add(s = newJPanel(new FlowLayout(FlowLayout.CENTER)), "South");
		c.add(bbtn("◀", a -> {
			idx = idx - 1 >= 0 ? idx - 1 : 4;
			move();
		}));
		c.add(cc = newJPanel(new BorderLayout()));
		c.add(bbtn("▶", a -> {
			idx = idx + 1 <= 4 ? idx + 1 : 0;
			move();
		}));

		ccc = newJPanel(new BorderLayout());
		var ccs = newJPanel(new FlowLayout(FlowLayout.CENTER));

		cc.add(sz(ccc, 400, 400));
		cc.add(ccs, "South");

		var bcap = "검색,장터,종료".split(",");

		for (int i = 0; i < bcap.length; i++) {
			var l = bcap[i];
			var img = imglbl(bcap[i], "./datafiles/기본사진/" + (i + 2) + ".png", 80, 80, a -> {
				if (l.equals("검색")) {
					new Search();
				} else if (l.equals("장터")) {
					new Storage(false);
				} else if (l.equals("종료")) {
					System.exit(0);
				}
			});
			s.add(img);
		}
		var rs = getrows("select * from game order by rand() limit 0,5");
		for (var r : rs) {
			var g = new Game(r);
			var temp = newJPanel(new BorderLayout());
			var temps = newJPanel(new GridLayout(0, 1));
			var img = new JLabel(g.g_img(400, 300));
			temp.add(img);
			temp.add(temps, "South");
			temps.add(wlbl("게임명:" + g.g_name, JLabel.LEFT));
			temps.add(wlbl("평점:" + g.avg(), JLabel.LEFT));
			temps.add(wlbl("가격:" + g.displayPrice(), JLabel.LEFT));
			games.add(temp);
			ccs.add(lbl[rs.indexOf(r)] = lbl("■", 0))
					.setForeground(rs.indexOf(r) == 0 ? Color.WHITE : Color.LIGHT_GRAY);
			evt(lbl[rs.indexOf(r)], a -> {
				idx = rs.indexOf(r);
				move();
			});
			evt(img, a -> {
				if (!g.validate(user)) {
					emsg("연령이 맞지 않습니다.");
				}
				new GamePage(g);
			});
			temp.setBorder(new LineBorder(Color.BLACK));
		}
		ccc.add(games.get(0));
		animation = new Animation();
		animation.start();
	}

	public static void main(String[] args) {
		디버그("1");
		new UserMain();
		mf.setVisible(true);
	}

	class Animation extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					ccc.removeAll();
					ccc.add(games.get(idx));
					revalidate();
					repaint();
					Stream.of(lbl).forEach(a -> a.setForeground(Color.LIGHT_GRAY));
					lbl[idx].setForeground(Color.WHITE);
					Thread.sleep(1000);
					idx = (idx + 1) % 5;
				}
			} catch (Exception e) {
			}
		}
	}

	public void move() {
		animation.interrupt();
		animation = null;
		animation = new Animation();
		animation.start();
	}
}
