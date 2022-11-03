package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Main extends BaseFrame {

	ArrayList<JLabel> clds = new ArrayList<>();

	JTextField txt[] = { new JTextField(15), new JPasswordField(15) {
		public char getEchoChar() {
			return '●';
		};
	} };

	public Main() {
		super("메인", 600, 600);

		add(sz(n = newJPanel(null), 0, 100), "North");
		add(c = newJPanel(new BorderLayout()));

		c.add(lbl("SKY AIRLINE", JLabel.CENTER, 20), "North");
		c.add(new JLabel(getIcon("./datafiles/비행기.jpg", 300, 300)));
		c.add(cs = newJPanel(new BorderLayout(5, 5)), "South");
		var tempc = newJPanel(new GridLayout(0, 1, 5, 5));
		var temps = newJPanel(new GridLayout(1, 0, 5, 5));

		cs.add(tempc);
		cs.add(temps, "South");

		var cap = "ID,PW".split(",");

		for (int i = 0; i < cap.length; i++) {
			var tmp = newJPanel(new BorderLayout(5, 5));
			tmp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 0), "West");
			tmp.add(txt[i]);
			tempc.add(tmp);
		}

		for (int i = 0; i < 2; i++) {
			var img = new JLabel(getIcon("./datafiles/구름.jpg", 600, 80));
			n.add(img).setBounds(i * 600, 0, 600, 80);
			clds.add(img);
		}

		for (var bc : "회원가입,로그인".split(",")) {
			temps.add(btn(bc, a -> {
				if (a.getActionCommand().equals("로그인")) {
					if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
						emsg("빈칸이 존재합니다.");
						return;
					}

					if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
						imsg("관리자님 환영합니다.");
						new Admin().addWindowListener(new before(this));
					}

					var rs = getrows("select * from member where m_id = ? and m_pw = ?", txt[0].getText(),
							txt[1].getText());

					if (rs.isEmpty()) {
						emsg("일치하는 회원이 없습니다.");
						txt[0].setText("");
						txt[1].setText("");
						return;
					}

					BasePage.member = rs.get(0);
					imsg(BasePage.member.get(3) + "님 환영합니다.");
					new MainFrame().addWindowListener(new before(this));
				} else {
					new Sign().addWindowListener(new before(this));
				}
			}));
		}

		new Thread(() -> {
			while (true) {
				for (int i = 0; i < clds.size(); i++) {
					var x = clds.get(i).getX();
					var y = Math.sin(clds.get(0).getX() / 6) * 20;
					if (x <= -600)
						x = 600;
					clds.get(i).setLocation(x - 5, (int) y);
				}
				try {

					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		c.setBorder(new EmptyBorder(5, 100, 5, 100));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				txt[0].setText("");
				txt[1].setText("");
			}
		});
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
