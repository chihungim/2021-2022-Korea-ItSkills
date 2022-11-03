package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Line.Info;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

public class RoomEscape extends BaseFrame {

	JButton btn;
	List<JLabel> lst3;
	Timer t;

	public RoomEscape(String rno) {
		super("방탈출", 400, 400);
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(btn = btn("선택", a -> {
			if (a.getActionCommand().equals("선택")) {
				t.stop();
				setValues("update reservation set r_attend = 1 where r_no = ?", rno);
				btn.setText("게임시작");
			} else {
				setVisible(false);
				new Quiz(lst3.get(0).getName());
			}

		}), "South");

		var lst1 = Arrays.asList(new File("./datafiles/퀴즈").listFiles());

		Collections.shuffle(lst1);

		var lst2 = new ArrayList<JLabel>();

		for (int i = 0; i < 9; i++) {
			var img = new JLabel(getIcon(lst1.get(i).getAbsolutePath(), 150, 150));
			c.add(img);
			lst2.add(img);
			img.setBorder(new LineBorder(Color.black, 2));
			img.setName(lst1.get(i).getName().replace(".jpg", ""));
			img.setEnabled(false);
		}

		Collections.shuffle(lst2);
		lst3 = lst2.subList(0, 5);

		for (var l : lst3) {
			l.setEnabled(true);
		}

		t = new Timer(100, a -> {

			for (var l : lst3)
				l.setBorder(new LineBorder(Color.BLACK, 2));

			Collections.shuffle(lst3);
			lst3.get(0).setBorder(new LineBorder(Color.red, 5));
		});

		t.start();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (btn.getText().equals("게임시작")) {
					int con = JOptionPane.showConfirmDialog(RoomEscape.this, "게임 도중 나갈 시 게임은 다시 진행 할 수 없습니다\n나가시겠습니까?",
							"경고", JOptionPane.YES_NO_OPTION);

					if (con == 0) {
						setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});

		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new RoomEscape("1");
	}

}
