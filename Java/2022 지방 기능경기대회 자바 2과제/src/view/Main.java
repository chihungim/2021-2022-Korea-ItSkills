package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main extends BaseFrame {

	ArrayList[] values = { getValues(
			"select left(c_name, instr(c_name, ' ')-1), c_name, count(r.r_no) cnt from cafe c, reservation r where c.c_no = r.c_no and r.r_date <= now() group by c.c_no order by cnt desc, c.c_no asc limit 5"),
			getValues(
					"select t.t_no, t_name, count(r.r_no) cnt from theme t, reservation r where t.t_no = r.t_no and r.r_date <= now() group by t.t_no order by cnt desc limit 5") };

	JComboBox<String> combo;

	public Main() {
		super("메인", 600, 500);

		add(n = new JPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new FlowLayout(1)), "South");

		n.add(lbl("예약TOP5", 0, 15));
		n.add(combo = new JComboBox<String>("지점,테마".split(",")));

		animation();

		combo.addItemListener(i -> {
			if (i.getStateChange() == 1)
				animation();
		});

		for (var bc : "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",")) {
			s.add(btn(bc, a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login(this).addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					status(false);
				} else if (a.getActionCommand().equals("검색")) {
					new Search().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("게시판")) {
					new Notice().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("방탈출게임")) {
					gl = new GameList();
					gl.addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("예약현황")) {
					new Chart().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new Mypage().addWindowListener(new before(this));
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		status(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}

	void animation() {
		c.removeAll();

		var rs = (ArrayList<ArrayList<Object>>) values[combo.getSelectedIndex()];
		var imgList = new ArrayList<JPanel>();
		String path = "지점,테마".split(",")[combo.getSelectedIndex()];

		for (int i = 0; i < rs.size(); i++) {
			var r = rs.get(i);
			var temp = new JPanel(new BorderLayout());
			var img = new JLabel(getIcon("./Datafiles/" + path + "/" + r.get(0) + ".jpg", 600, 300));
			temp.add(img);
			temp.add(lbl(r.get(1) + "", 0, 20), "South");
			temp.setBounds(0, i * 420, 600, 400);
			c.add(temp);
			imgList.add(temp);
		}

		var total = imgList.get(imgList.size() - 1).getY();

		var t = new Timer(2, a -> {
			for (var img : imgList) {
				var y = img.getY() - 5;
				if (y <= -400) {
					y = total;
				}
				img.setLocation(0, y);
			}
		});
		t.setCoalesce(true);
		t.start();
		revalidate();
		repaint();
	}

	void status(boolean b) {
		((JButton) s.getComponent(0)).setText(b ? "로그아웃" : "로그인");

		for (int i = 1; i <= 4; i++) {
			((JButton) s.getComponent(i)).setEnabled(b);
		}
	}
}
