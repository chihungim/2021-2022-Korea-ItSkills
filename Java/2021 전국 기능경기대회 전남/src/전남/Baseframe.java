package 전남;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Baseframe extends JFrame {
	static Statement stmt;
	static Connection con;
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	static int NO, tNO;
	static String NAME, cNAME, cfNAME, ID, nDATE, tNAME;
	boolean chk;
	
	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
					"user", "1234");
			stmt = con.createStatement();
			stmt.execute("use roomescape");
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

	static void errmsg(String message) {
		JOptionPane.showMessageDialog(null, message, "메시지", JOptionPane.ERROR_MESSAGE);
	}

	static void msg(String message) {
		JOptionPane.showMessageDialog(null, message, "메시지", JOptionPane.INFORMATION_MESSAGE);
	}

	int toint(Object str) {
		return Integer.parseInt(str.toString());
	}

	static JButton btn(String str, ActionListener a) {
		JButton jb = new JButton(str);
		jb.addActionListener(a);
		return jb;
	}

	static JLabel label(String str, int alig) {
		JLabel jl = new JLabel(str, alig);
		return jl;
	}

	static JLabel label(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("", Font.BOLD, size));
		return jl;
	}

	static JLabel labelP(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("", Font.PLAIN, size));
		return jl;
	}

	int getLast(String sql) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			return rs.getRow();
		} catch (SQLException e) {
			return 0;
		}
	}

	static ImageIcon img(String path, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	static Image img(String path) {
		return Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path);
	}

	JTable table(DefaultTableModel dtm) {
		JTable table = new JTable(dtm);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return table;
	}

	void addCom(JComponent c, int x, int y, int w, int h) {
		c.setBounds(x, y, w, h);
	}

	DefaultTableModel model(String str[]) {
		DefaultTableModel dtm = new DefaultTableModel(null, str) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		return dtm;
	}
	
	String getone(String sql) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	static <T extends JComponent> T size(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	void addRow(String sql, DefaultTableModel dtm, int cnt) {
		dtm.setRowCount(cnt);
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object row[] = new Object[dtm.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	class be extends WindowAdapter {
		Baseframe b;

		public be(Baseframe bef) {
			b = bef;
			bef.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}

	static JComponent line(JComponent comp) {
		comp.setBorder(new LineBorder(Color.BLACK));
		return comp;
	}

	static JComponent emp(JComponent c, int t, int l, int b, int r) {
		c.setBorder(new EmptyBorder(t, l, b, r));
		return c;
	}

	public Baseframe(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
	}
}
