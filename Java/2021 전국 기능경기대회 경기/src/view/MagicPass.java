package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MagicPass extends BaseFrame {
	int cnt;
	int boom;
	Random r = new Random();
	ArrayList<Integer> nums = new ArrayList<Integer>();
	ArrayList<JLabel> lbls = new ArrayList<JLabel>();

	Timer 스피이이이ㅣ인;

	public MagicPass() {
		super("매직패스", 480, 580);
		data();
		ui();
		events();
		setVisible(true);
	}

	void ui() {
		boom = (r.nextInt(5) + 1);

		var c = new JPanel(new GridLayout(0, 4, 5, 5));
		var s = new JPanel();
		add(lbl("환상의 매직패스", 0, 20), "North");
		add(c);
		add(s, "South");

		for (int i = 0; i < boom; i++) {
			JLabel lbl;
			lbls.add(lbl = new JLabel(getIcon("./datafiles/이미지/꽝.jpg", 100, 100)));
			lbl.setName("0");
		}

		for (int i = boom; i < 16; i++) {
			int n = r.nextInt(cnt) + 1;
			JLabel lbl;
			lbls.add(lbl = new JLabel(getIcon(
					"./datafiles/이미지/" + getOne("select r_name from ride where r_no =" + n) + ".jpg", 105, 105)));
			lbl.setName(n + "");
		}

		Collections.shuffle(lbls);
		for (var l : lbls) {
			l.setBorder(new LineBorder(Color.BLACK));
			c.add(l);
		}

		for (var l : lbls) {
			l.setBorder(new LineBorder(Color.BLACK));
			c.add(l);
		}

		s.add(sz(btn("Stop", a -> btn_event()), 120, 25));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		s.setOpaque(false);
		c.setOpaque(false);

	}

	void events() {
		스피이이이ㅣ인 = new Timer(50, new ActionListener() {

			int idx = -1;

			@Override
			public void actionPerformed(ActionEvent e) {
				for (var l : lbls)
					l.setEnabled(false);
				if (idx >= lbls.size() - 1) {
					idx = 0;
				} else
					idx++;
				lbls.get(idx).setEnabled(true);
			}
		});
		스피이이이ㅣ인.start();
	}

	void btn_event() {
		스피이이이ㅣ인.stop();
		JLabel img = null;
		for (var l : lbls) {
			if (l.isEnabled()) {
				img = l;
				break;
			}

		}


		String no = img.getName();
		if (no.contentEquals("0")) {
			eMsg("아쉽네요~ 다음 기회에 다시 도전해주세요.");

			Main.lbl[4].setText("매직패스(" + (toInt(Main.lbl[4].getText())) + ")");
			if ((toInt(Main.lbl[4].getText()) == 0)) {
				Main.lbl[4].setEnabled(false);
			}
			dispose();
			return;
		} else {
			if (toInt(getOne("SELECT count(*) FROM ticket where t_date = curdate() and r_no=" + no)) >= toInt(
					getOne("select r_max from ride where r_no=" + no))) {
				eMsg(getOne("select r_name from ride where r_no=" + no) + "은(는) 만석입니다. 다시 한번 도전해주세요.");
				스피이이이ㅣ인.start();
			} else {
				iMsg(getOne("select r_name from ride where r_no=" + no) + " 매직패스에 당첨되셨습니다.");
				execute("insert into ticket values(0, '" + uno + "',curdate(),'" + no + "',1)");
				Main.lbl[4].setText("매직패스(" + (toInt(Main.lbl[4].getText()) - 1) + ")");
				if ((toInt(Main.lbl[4].getText()) == 0)) {
					Main.lbl[4].setEnabled(false);
				}
				dispose();
			}
		}
	}

	void data() {
		cnt = toInt(getOne("select count(*) from ride"));
	}

}
