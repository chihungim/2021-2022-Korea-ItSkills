package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BasePage extends JPanel {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;
	static MainFrame mf = new MainFrame();
	static int sno, uno, sAddr, uAddr;
	JPanel n, c, e, w, s;
	static String uname, sname;
	static LocalDateTime pdtime;
	static {
		try {
			stmt.execute("use delivery");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ResultSet rs(String sql) throws SQLException {
		var rs = stmt.executeQuery(sql);
		return rs;
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "°æ°í", JOptionPane.ERROR_MESSAGE);
	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Á¤º¸ ", JOptionPane.INFORMATION_MESSAGE);
	}

	public static ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getIcon(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
	}

	static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BasePage() {
		super(new BorderLayout());
	}

	public static void logOut() {
		iMsg("·Î±×¾Æ¿ô ÇÏ¼Ì½À´Ï´Ù.");
		BasePage.uno = 0;
		BasePage.uname = "";
		BasePage.sno = 0;
		BasePage.sname = "";
		mf.swapPage(new MainPage());
		mf.loginbtn.setText("Login");
		mf.menuInit(true);
	}

	public static JLabel lbl(String t, int al, int s) {
		var l = new JLabel(t, al);
		l.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, s));
		return l;
	}

	public static String getOne(String sql) {
		try {
			var rs = rs(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {

			return null;
		}
	}

	public static Integer toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	public static JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	public static JLabel lbl(String t, int al) {
		return new JLabel(t, al);
	}

	public static JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.setBackground(Color.WHITE);
		b.setOpaque(false);
		b.addActionListener(a);
		return b;
	}

	public static JComponent setB(JComponent jc, Border b) {
		jc.setBorder(b);
		return jc;
	}
}
