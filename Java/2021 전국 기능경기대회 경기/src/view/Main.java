package view;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main extends BaseFrame {

	int lorry = 1, lotty = 1;

	static JLabel lbl[] = new JLabel[10];

	JLabel img1, img2;

	public Main() {
		super("메인", 600, 300);
		ui();
		events();
		lblEnable(1);
		setVisible(true);
	}

	void ui() {
		var w = new JPanel(new GridLayout(0, 1));
		var c = new JPanel(new GridLayout(1, 0));
		add(w, "West");
		add(c);

		for (int i = 0; i < lbl.length; i++) {
			var cap = "로그인,회원가입,위치정보,예매,매직패스(0),Mypage,놀이기구 인기순위 TOP5,놀이기구 등록/수정,월별분석,종료 ".split(",");
			lbl[i] = lbl(cap[i], JLabel.CENTER);
			w.add(lbl[i]);
		}

		c.add(img2 = new JLabel(getIcon("datafiles/캐릭터/로티" + lotty + ".jpg", 200, 200)));
		c.add(img1 = new JLabel(getIcon("datafiles/캐릭터/로리" + lorry + ".jpg", 200, 200)));
		c.setOpaque(false);
		w.setOpaque(false);

		Timer t = new Timer(200, a -> {
			if (lorry == 4) {
				lorry = 1;
				lotty = 1;
			}

			Main.this.img1.setIcon(getIcon("datafiles/캐릭터/로티" + lotty + ".jpg", 200, 200));
			Main.this.img2.setIcon(getIcon("datafiles/캐릭터/로리" + lotty + ".jpg", 200, 200));

			lorry++;
			lotty++;
		});
		t.start();

		((JPanel) (getContentPane())).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void events() {
		for (int i = 0; i < lbl.length; i++) {
			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var cap = ((JLabel) e.getSource()).getText();

					if (!((JLabel) e.getSource()).isEnabled()) {
						return;
					}

					switch (cap) {
					case "로그인":
						new Login().addWindowListener(new Before(Main.this));
						break;
					case "로그아웃":
						lblEnable(1);
						break;
					case "회원가입":
						new Register().addWindowListener(new Before(Main.this));
						break;
					case "위치정보":
						new LocationInfo().addWindowListener(new Before(Main.this));
						break;
					case "예매":
						new Reserve().addWindowListener(new Before(Main.this));
						break;
					case "Mypage":
						new MyPage().addWindowListener(new Before(Main.this));
						break;
					case "놀이기구 인기순위 TOP5":
						new TOP5().addWindowListener(new Before(Main.this));
						break;
					case "놀이기구 등록/수정":
						break;
					case "월별 분석":
						break;
					default:
						if (cap.contains("매직패스")) {
							new MagicPass().addWindowListener(new Before(Main.this));
						} else {
							System.exit(0);
						}
						break;
					}
					super.mousePressed(e);
				}
			});
		}
	}

	static void lblEnable(int no) {
		Arrays.stream(lbl).forEach(a -> a.setEnabled(false));

		int e[] = null;
		if (no == 1) {
			lbl[0].setText("로그인");
			e = new int[] { 0, 1, 6, 9 };
			uno = 0;
			uname = "";
			uage = "";
		} else if (no == 2) {
			lbl[0].setText("로그아웃");
			e = new int[] { 0, 2, 3, 5, 9 };
		} else if (no == 3) {
			lbl[0].setText("로그아웃");
			e = new int[] { 0, 6, 7, 8, 9 };
		}

		for (int i = 0; i < e.length; i++) {
			lbl[e[i]].setEnabled(true);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
