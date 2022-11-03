

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableCellRenderer;

public class Baseframe extends JFrame {
	static Statement stmt;
	static Connection con;
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&allowPublicKeyRetrieval=true","user","1234");
			stmt = con.createStatement();
			stmt.execute("use eats");
			dtcr.setHorizontalAlignment(dtcr.CENTER);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void errmsg(String str) {
		JOptionPane.showMessageDialog(null, str, "¸Þ½ÃÁö", JOptionPane.ERROR_MESSAGE);
	}
	
	void msg(String str) {
		JOptionPane.showMessageDialog(null, str, "¸Þ½ÃÁö", JOptionPane.INFORMATION_MESSAGE);
	}
	
	JLabel label(String str, int alig) {
		JLabel jl = new JLabel(str, alig);
		return jl;
	}
	
	JLabel label(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, size));
		return jl;
	}
	
	JLabel labelP(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, size));
		return jl;
	}
	
	JComponent addCom(JComponent c, int x, int y, int w, int h) {
		c.setBounds(x, y, w, h);
		return c;
	}
	
	JComponent addCom(JPanel p, JComponent c, int x, int y, int w, int h) {
		p.add(c);
		c.setBounds(x, y, w, h);
		return c;
	}
	
	static <T extends JComponent>T size(T c, int w, int h){
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}
	
	JButton btn(String str, ActionListener a) {
		JButton jb = new JButton(str);
		jb.addActionListener(a);
		jb.setBackground(Color.GREEN);
		jb.setForeground(Color.WHITE);
		return jb;
	}
	
	public Baseframe(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
	}

}
