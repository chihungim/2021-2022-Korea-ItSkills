package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends BaseFrame {

	JLabel timelbl, chancelbl, img;
	LocalTime time = LocalTime.of(0, 1, 50);
	JTextField txt;
	int chance = 3;
	String wh = "<html><font color = 'white'>";
	DateTimeFormatter tf = DateTimeFormatter.ofPattern("m:ss");
	Timer t;
	int status = 0;

	public Quiz(String qno) {
		super("Q" + qno, 500, 500);
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(ne = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "East");
		n.add(nw = new JPanel(new FlowLayout(FlowLayout.LEFT)), "West");

		n.add(lbl(wh + "퀴즈번호:" + qno, 0, 30), "North");

		nw.add(chancelbl = lbl(wh + "기회:" + chance + "번", 0, 15));
		ne.add(timelbl = lbl(wh + "남은 시간:" + tf.format(time), 0, 15));

		add(img = new JLabel(getIcon("./datafiles/퀴즈/" + qno + ".jpg", 500, 400)) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3f));
				g2.setColor(Color.RED);
				if (status == 1) {
					g2.drawOval(0, 0, getWidth(), getHeight());
				} else if (status == 2) {
					g2.drawLine(0, 0, getWidth(), getHeight());
					g2.drawLine(getWidth(), 0, 0, getHeight());
				}
			}
		});

		add(s = new JPanel(new FlowLayout(1)), "South");
		s.add(lbl(wh + "답 입력:", 0, 15));
		s.add(txt = new JTextField(15));
		var b = btn("확인", a -> {
			var ans = getValue("select q_answer from quiz where q_no = ?", qno).get(0) + "";
			ans = ans.replaceAll("\\s+", "");
			var input = txt.getText().replaceAll("\\s+", "");

			if (input.equals(ans)) {
				status = 1;
				img.repaint();
				t.stop();
				imsg("Q" + qno + "번 문제를 통과하셨습니다.");
				for (var w : getWindows()) {
					w.setVisible(w == gl);
				}
			} else {
				chance--;
				chancelbl.setText(wh + "기회:" + chance + "번");
				if (chance == 0) {
					status = 2;
					img.repaint();
					t.stop();
					emsg("남은 기회가 없으므로 종료합니다.");
					for (var w : getWindows()) {
						w.setVisible(w == gl);
					}
				}
			}

		});
		b.setBackground(Color.WHITE);
		s.add(b);

		t = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			timelbl.setText(wh + "남은 시간:" + tf.format(time));

			if (time.getMinute() == 0 && time.getSecond() == 0) {
				emsg("제한시간 초과로 종료합니다.");
				t.stop();
				for (var w : getWindows()) {
					w.setVisible(w == gl);
				}
			}

		});

		t.start();

		s.setOpaque(false);
		n.setOpaque(false);
		ne.setOpaque(false);
		nw.setOpaque(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int con = JOptionPane.showConfirmDialog(Quiz.this, "게임 도중 나갈 시 게임은 다시 진행 할 수 없습니다\n나가시겠습니까?", "경고",
						JOptionPane.YES_NO_OPTION);

				if (con == 0) {
					t.stop();
					for (var w : getWindows()) {
						w.setVisible(w == gl);
					}
				} else {
					setDefaultCloseOperation(0);
				}
			}
		});

		((JPanel) getContentPane()).setBackground(Color.BLACK);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Quiz("1");

	}

}
