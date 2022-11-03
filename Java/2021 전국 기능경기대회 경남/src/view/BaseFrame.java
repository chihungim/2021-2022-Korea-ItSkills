package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import db.*;

public class BaseFrame extends JFrame {

	JPanel n, c, s, w, e;

	static Connection con = DB.con;
	static Statement stmt = DB.stmt;
	

	static int uno, mno;
	static boolean isLogin;
	static String type;
	String addr[] = "서울,경기,인천,부산,대구,대전,경남,전남,충남,광주,울산,경북,충북,강원,제주,세종".split(",");

	static {
		try {
			stmt.executeQuery("use albajava");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean checkIsExists(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return true;
			} else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	String getOne(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BaseFrame(String tit, int w, int h) {
		super(tit);
		this.setSize(w, h);
		this.setDefaultCloseOperation(2);
		this.setLocationRelativeTo(null);

	}

	String isPath(String cno) {
		String path = "./지급자료/이미지없음.png";
		if (Files.exists(Paths.get("./지급자료/브랜드/" + cno + ".jpg"))) {
			return "./지급자료/브랜드/" + cno + ".jpg";
		} else if (Files.exists(Paths.get("./지급자료/기업/" + cno + ".jpg"))) {
			return "./지급자료/브랜드/" + cno + ".jpg";
		}
		return path;
	}

	String mailSql(String division) {
		String sql = "";
		if (type.contentEquals("user")) { // 현재 로그인 타입 user
			if (division.contentEquals("send")) { // 받는메일
				sql = "select ma.title, u.email, ma.date, ma.ma_no, ma.detail, ma.read from mail ma, user u where division=2 and sender="
						+ uno + " and ma.recipient=u.u_no";
			} else {
				sql = "select ma.title, u.email, ma.date, ma.ma_no, ma.detail, ma.read from mail ma, user u where division=1 and recipient="
						+ uno + " and ma.recipient=u.u_no";
			}
		} else {
			if (division.contentEquals("send")) { // 받는메일
				sql = "select ma.title, m.email, ma.date, ma.ma_no, ma.detail, ma.read from mail ma, manager m where division=1 and sender="
						+ mno + " and ma.recipient=m.m_no";
			} else {
				sql = "select ma.title, m.email, ma.date, ma.ma_no, ma.detail, ma.read from mail ma, manager m where division=2 and recipient="
						+ mno + " and ma.recipient=m.m_no";
			}
		}
		return sql;
	}

	static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	static JLabel lbl(String text, int alig) {
		JLabel l = new JLabel(text, alig);
		return l;
	}

	static JLabel imglbl(String fpath, int w, int h) {
		return new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(fpath).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	static JLabel lbl(String text, int alig, int size) {
		JLabel l = new JLabel(text, alig);
		l.setFont(new Font("", Font.BOLD, size));
		return l;
	}

	static JLabel lbl(String text, int alig, int size, Color col) {
		JLabel l = new JLabel(text, alig);
		l.setFont(new Font("", Font.BOLD, size));
		l.setForeground(col);
		return l;
	}

	static JLabel lbl(String text, int alig, int size, Color col, int font) {
		JLabel l = new JLabel(text, alig);
		l.setFont(new Font("", font, size));
		l.setForeground(col);
		return l;
	}

	static JButton btn(String text, ActionListener a) {
		JButton b = new JButton(text);
		b.addActionListener(a);
		return b;
	}

	void setLine(JComponent c) {
		c.setBorder(new LineBorder(Color.black));
	}

	static void setEmpty(JComponent c, int t, int l, int b, int r) {
		c.setBorder(new EmptyBorder(t, l, b, r));
	}

	ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	static int rei(Object obj) {
		return Integer.parseInt(obj.toString());
	}

	boolean isNumeric(Object obj) {
		try {
			Integer.parseInt(obj.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	String dateTimeFormat(LocalDateTime lTime, String pattern) {
		return lTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	void addItem(JComboBox<String> combo, String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				combo.addItem(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setNumeric(JTextField txt) {
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!isNumeric(e.getKeyChar())) {
					e.consume();
				}
			}
		});
	}

	public class MyPanel extends JPanel {

		ArrayList<String> list = new ArrayList<String>();

		int sx = 0, sy = 0, ex = 0, ey = 0, tmpex = 0, tmpey = 0;
		public Vector<Point> sv = new Vector<Point>();
		public Vector<Point> ev = new Vector<Point>();

		public MyPanel() {
			super(null);
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					sx = e.getX();
					sy = e.getY();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					ex = e.getX();
					ey = e.getY();

					// 현재 박스 좌표 기록, 전체 박스 좌표 출력
					list.add("{" + sx + "," + sy + "," + (ex - sx) + "," + (ey - sy) + "}");
					System.out.println("좌표목록:" + String.join(",", list));

					sv.add(new Point(sx, sy));
					ev.add(new Point(ex, ey));
					repaint();
				}
			});

			this.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					tmpex = e.getX();
					tmpey = e.getY();
					repaint();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.BLACK);

			Point cs, ce;
			// 기록된 박스들 모두 표시
			for (int i = 0; i < sv.size(); i++) {
				cs = sv.get(i);
				ce = ev.get(i);
				g.drawRect(cs.x, cs.y, ce.x - cs.x, ce.y - cs.y);
			}

			// 드래그 자취 표시
			if (sx > 0 || sy > 0 || tmpex > 0 || tmpey > 0)
				g.drawRect(sx, sy, tmpex - sx, tmpey - sy);
		}
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}

}
