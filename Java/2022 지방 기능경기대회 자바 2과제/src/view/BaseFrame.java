package view;

import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import db.DB;

public class BaseFrame extends JFrame {
	static Connection con = DB.con;
	static Statement stmt = DB.stmt;
	static GameList gl;
	static {
		try {
			stmt.execute("use 2022지방_1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String uno, uname, uid;

	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;

	ArrayList<ArrayList<Object>> getValues(String sql, Object... objs) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			var pst = con.prepareStatement(sql);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					pst.setObject(i + 1, objs[i]);
			}

			var rs = pst.executeQuery();
			var m = rs.getMetaData();

			while (rs.next()) {
				var row = new ArrayList<Object>();
				for (int i = 0; i < m.getColumnCount(); i++)
					row.add(rs.getObject(i + 1));
				rows.add(row);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}

	void setValues(String sql, Object... objs) {
		try {
			var pst = con.prepareStatement(sql);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					pst.setObject(i + 1, objs[i]);
			}
			pst.execute();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ArrayList<Object> getValue(String sql, Object... objs) {
		if (getValues(sql, objs).isEmpty())
			return null;
		else
			return getValues(sql, objs).get(0);
	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	int toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	void cleartxt(JTextComponent txt) {
		txt.setText("");
		txt.requestFocus();
	}

	ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	ImageIcon getIcon(String path) {
		return new ImageIcon(path);
	}

	JLabel lbl(String lbl, int a) {
		return new JLabel(lbl, a);
	}

	JLabel lbl(String lbl, int a, int s) {
		var l = new JLabel(lbl, a);
		l.setFont(new Font("HY헤드라인M", Font.BOLD, s));
		return l;
	}

	JButton btn(String bc, ActionListener a) {
		var b = new JButton(bc);
		b.addActionListener(a);
		return b;
	}

	DefaultTableModel model(String col[]) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	JTable table(DefaultTableModel m) {
		var t = new JTable(m) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				var jc = (JComponent) super.prepareRenderer(renderer, row, column);
				if (getColumnCount() > 2 && getValueAt(row, 2).toString().equals(uid)) {
					jc.setBackground(Color.BLUE);
					jc.setForeground(Color.WHITE);
				} else {
					jc.setBackground(Color.WHITE);
					jc.setForeground(Color.BLACK);
				}

				if (row == getSelectedRow()) {
					jc.setBackground(new JTable().getSelectionBackground());
					jc.setForeground(Color.BLACK);
				}

				return jc;
			}
		};
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		var dtcr = new DefaultTableCellRenderer();

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	void addRow(DefaultTableModel m, ArrayList<ArrayList<Object>> rs) {
		m.setRowCount(0);
		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}
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
	}
}
