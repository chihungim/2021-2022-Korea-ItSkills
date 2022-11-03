package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public class BaseFrame extends JFrame {
	static Connection con = DB.con;
	static Statement stmt = db.DB.stmt;
	static int u_no, t_no;
	static String pinfo[] = new String[7];
	static String u_name;
	static DecimalFormat df = new DecimalFormat("#,##0");
	JPanel n, c, s, w, e;

	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();

	static {
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		try {
			stmt.execute("use 2021Àü±¹");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./Datafiles/¿À·»Áö.jpg"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	JLabel lbl(String title, int al) {
		return new JLabel(title, al);
	}

	JComponent setB(JComponent jc, Border b) {
		jc.setBorder(b);
		return jc;
	}

	void addRow(String sql, DefaultTableModel m) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);

			Object[] row = new Object[m.getColumnCount()];

			while (rs.next()) {
				for (int i = 0; i < m.getColumnCount(); i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static DefaultTableModel model(String[] col) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		return m;
	}

	String getOne(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {
			return null;
		}
	}

	JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "°æ°í", JOptionPane.ERROR_MESSAGE);
	}

	void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Á¤º¸", JOptionPane.INFORMATION_MESSAGE);
	}

	static JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	class before extends WindowAdapter {

		BaseFrame b;

		public before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
			super.windowClosed(e);
		}

	}

	void setLogin(String no) {
		try {
			var rs = stmt.executeQuery("select * from user where u_no = " + no);
			if (rs.next()) {
				u_name = rs.getString("u_name");
				u_no = rs.getInt("u_no");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	JLabel lbl(String title, int a, int s) {
		JLabel lbl = new JLabel(title, a);
		lbl.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, s));
		return lbl;
	}

	JButton btn(String cap, ActionListener a) {
		var btn = new JButton(cap);
		btn.addActionListener(a);
		return btn;
	}

	ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	ImageIcon getIcon(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
	}

}
