
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Sign extends BaseFrame {

	JLabel img;

	String cap[] = "이름:,ID:,PW:".split(",");
	String bcap[] = "회원가입,취소".split(",");
	String fpath;
	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15) };

	public Sign() {
		super("회원가입", 430, 250);

		var c_c = new JPanel(new GridLayout(0, 1));
		var c_s = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		add(sz(w = new JPanel(new BorderLayout(5, 5)), 150, 100), "West");
		add(c = new JPanel(new BorderLayout()));
		c.add(c_c);
		c.add(c_s, "South");

		w.add(img = new JLabel());
		w.add(btn("사진 등록", a -> {
			JFileChooser jfc = new JFileChooser("./Datafiles/회원사진");
			int r = jfc.showOpenDialog(this);
			jfc.setMultiSelectionEnabled(false);
			if (r == jfc.OPEN_DIALOG) {
				fpath = jfc.getSelectedFile().toPath().toString();
				img.setIcon(new ImageIcon(
						Toolkit.getDefaultToolkit().getImage(fpath).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
			}
		}), "South");

		for (int i = 0; i < txt.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			tmp.add(sz(lbl(cap[i], JLabel.LEFT), 30, 20));
			tmp.add(txt[i]);
			c_c.add(tmp);
		}

		for (String bc : bcap) {
			c_s.add(btn(bc, a -> {
				if (a.getActionCommand().equals("회원가입")) {
					for (int i = 0; i < txt.length; i++) {
						if (txt[i].getText().equals("")) {
							eMsg("빈칸이 존재합니다.");
							return;
						}
					}

					try {
						var rs = stmt.executeQuery("select * from user where u_name ='" + txt[0].getText() + "'");
						if (rs.next()) {
							eMsg("이미 존재하는 아이디입니다.");
							txt[0].setText("");
							txt[1].setText("");
							txt[2].setText("");
							return;
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (img.getIcon() == null) {
						eMsg("사진을 등록해주세요.");
						return;
					}
					if (!(txt[1].getText().matches(".*[0-9].*") && txt[1].getText().matches(".*[a-zA-Z].*")
							&& txt[1].getText().matches(".*[\\W].*")) || txt[1].getText().length() < 4) {
						eMsg("비밀번호를 확인해주세요.");
						return;

					}
					try {
						PreparedStatement ps = con.prepareStatement("insert user values(0,?,?,?,?)");
						ps.setString(1, txt[0].getText());
						ps.setString(2, txt[1].getText());
						ps.setString(3, txt[2].getText());
						FileInputStream fin = null;
						try {
							fin = new FileInputStream(fpath);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ps.setBinaryStream(4, fin);
						ps.execute();

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					iMsg("회원가입이 완료되었습니다.");
					dispose();
				} else {
					dispose();
				}
			}));
		}

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
			}
		});

		img.setBorder(new LineBorder(Color.BLACK));

		((JPanel) (getContentPane())).setBorder(new EmptyBorder(20, 20, 20, 20));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}