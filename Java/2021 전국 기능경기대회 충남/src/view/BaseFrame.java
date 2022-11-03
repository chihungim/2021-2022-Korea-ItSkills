package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DBManager;

public class BaseFrame extends JFrame {
	
	JPanel c,s,e,w,n;
	static String uno="", uname, ubirth, uage, sno, sname;
	static ArrayList<String> metroNames = new ArrayList<String>();
	static ArrayList<String> stNames = new ArrayList<String>();
	static Object metroStInfo[][] = new Object[31][4];
	static int adjDim[][] = new int[276][276];
	static int lineDim[][] = new int[276][276];
	static int metroTime[][][] = new int[31][100][300];
	static LocalTime now = LocalTime.now();
	static Color blue = new Color(50, 100, 255);
	final int INF = 10000000;
	static Home home = new Home();
	ResultSet rs;
	
	public BaseFrame(String tit, int w, int h) {
		super("¼­¿ï¸ÞÆ®·Î - "+tit);
		this.setSize(w, h);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(2);
	}
	
	void dataInit() throws SQLException {
		rs = DBManager.rs("select * from metro");
		metroNames.add("");
		while (rs.next()) {
			metroNames.add(rs.getString(2));
		}

		rs = DBManager.rs("select * from station");
		stNames.add("");
		while (rs.next()) {
			stNames.add(rs.getString(2));
		}

		for (int i = 1; i < 31; i++) {
			metroStInfo[i][0] = new ArrayList<String>();
			((ArrayList) metroStInfo[i][0]).add("");

			rs = DBManager.rs(
					"select s.name from route r, station s where r.metro = " + i + " and r.station = s.serial");
			while(rs.next()) {
				((ArrayList)metroStInfo[i][0]).add(rs.getString(1));
			}
			rs = DBManager.rs("select * from metro where serial = "+i);
			rs.next();
			metroStInfo[i][1] = rs.getString(3);
			metroStInfo[i][2] = rs.getString(4);
			metroStInfo[i][3] = rs.getString(5);
		}
		
		for (int i = 1; i <= 30; i++) {
			ArrayList al = (ArrayList) metroStInfo[i][0];
			for (int j = 1; j <= al.size() -2; j++) {
				lineDim[stNames.indexOf(al.get(j))][stNames.indexOf(al.get(j+1))] = i;
			}
		}
		
		for (int i = 1; i < 276; i++) {
			for (int j = i + 1; j < 276; j++) {
				adjDim[i][j] = adjDim[j][i] = INF;
			}
		}
		rs = DBManager.rs("select * from path");
		while(rs.next()) {
			adjDim[rs.getInt(2)][rs.getInt(3)] = rs.getInt(4) * 5; 
		}
		
		for (int i = 1; i <= 30; i++) {
			for (int j = 1; j < 100; j++) {
				ArrayList m = (ArrayList)metroStInfo[i][0];
				if(j >= m.size()) break;
				
				int start = LocalTime.parse((CharSequence) metroStInfo[i][1]).toSecondOfDay();
				int end = LocalTime.parse((CharSequence) metroStInfo[i][2]).toSecondOfDay();
				int interval = LocalTime.parse((CharSequence) metroStInfo[i][3]).toSecondOfDay();
				
				metroTime[i][j][0] = j == 1?start : metroTime[i][j - 1][0] + adjDim[stNames.indexOf(m.get(j - 1))][stNames.indexOf(m.get(j))];
				for (int k = 1; k < 300; k++) {
					if(j > 1 && metroTime[i][j-1][k] == 0) break;
					metroTime[i][j][k] = metroTime[i][j][k - 1] + interval;
				}
			}
		}
	}
	
	static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "¸Þ¼¼Áö", JOptionPane.INFORMATION_MESSAGE);
	}
	
	static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "¸Þ¼¼Áö", JOptionPane.ERROR_MESSAGE);
	}
	
	static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}
	
	static JLabel lbl(String text, int alig) {
		JLabel l = new JLabel(text, alig);
		return l;
	}
	
	static JLabel lbl(String text, int alig, int size) {
		JLabel l = new JLabel(text, alig);
		l.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, size));
		return l;
	}
	static JLabel lbl(String text, int alig, int size, Color fore) {
		JLabel l = new JLabel(text, alig);
		l.setForeground(fore);
		l.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, size));
		return l;
	}
	
	static JLabel lblP(String text, int alig, int size) {
		JLabel l = new JLabel(text, alig);
		l.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, size));
		return l;
	}
	
	static JButton btn(String text, ActionListener a) {
		JButton b = new JButton(text);
		b.addActionListener(a);
		b.setBackground(blue);
		b.setForeground(Color.white);
		return b;
	}
	
	static String tFormat(LocalTime t, String regex) {
		return DateTimeFormatter.ofPattern(regex).format(t);
	}
	static String tFormat(LocalDate t, String regex) {
		return DateTimeFormatter.ofPattern(regex).format(t);
	}
	
	static int rei(Object s) {
		return Integer.parseInt(s.toString().replaceAll("[^0-9]", "").isEmpty() ? "0" : s.toString().replaceAll("[^0-9]", ""));
	}
	
	static String iFormat(int s) {
		return new DecimalFormat("#,##0").format(s);
	}
	
	static ImageIcon img(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Áö±ÞÀÚ·á/images/"+path));
	}
	
	static ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Áö±ÞÀÚ·á/images/"+path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	static void setLine(JComponent c) {
		c.setBorder(new LineBorder(Color.black));
	}
	
	static void setEmpty(JComponent c, int t, int l, int b, int r) {
		c.setBorder(new EmptyBorder(t, l, b, r));
	}
	
	DefaultTableModel model(String str) {
		return new DefaultTableModel(null, str.split(",")) {
			public boolean isCellEditable(int row, int column) { return false; };
		};
	}
	
	JTable table(DefaultTableModel m) {
		JTable t = new JTable(m);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return t;
	}
	
	class Before extends WindowAdapter {
		BaseFrame b;
		public Before(BaseFrame b) {
			this.b=b;
			b.setVisible(false);
		}
		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
	
	void holder(JTextField txt, String hol) {
		txt.setText(hol);
		txt.setForeground(Color.LIGHT_GRAY);
		
		txt.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (txt.getText().trim().isEmpty()) {
					txt.setText(hol);
					txt.setForeground(Color.LIGHT_GRAY);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (txt.getText().equals(hol)) {
					txt.setText("");
					txt.setForeground(Color.black);
				}
			}
		});
	}


	
}
