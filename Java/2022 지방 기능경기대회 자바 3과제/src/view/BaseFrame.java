package view;

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

import db.DB;

public class BaseFrame extends JFrame {
	static Connection con = DB.con;
	static Statement stmt = DB.stmt;

	static {
		try {
			stmt.execute("use 2022지방_2");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;

	static String uno, uname;

	String apply[] = "심사중,합격,불합격".split(",");
	String category[] = ",편의점,영화관,화장품,음식점,백화점,의류점,커피전문점,은행".split(",");
	String gender[] = ",남자,여자,무관".split(",");
	String graduate[] = "대학교 졸업,고등학교 졸업,중학교 졸업,무관".split(",");
	String local[] = "전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",");

	ArrayList<ArrayList<Object>> getValues(String sql, Object... obs) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			var pst = con.prepareStatement(sql);
			if (obs != null) {
				for (int i = 0; i < obs.length; i++) {
					pst.setObject(i + 1, obs[i]);
				}
			}

			var rs = pst.executeQuery();
			var m = rs.getMetaData();

			while (rs.next()) {
				var row = new ArrayList<Object>();
				for (int i = 0; i < m.getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				rows.add(row);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rows;
	}

	void setValues(String sql, Object... obs) {
		try {
			var pst = con.prepareStatement(sql);
			if (obs != null) {
				for (int i = 0; i < obs.length; i++) {
					pst.setObject(i + 1, obs[i]);
				}
			}
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Object getOne(String sql, Object... obs) {
		var v = getValues(sql, obs);
		if (v.isEmpty()) {
			return null;
		} else {
			return v.get(0).get(0);
		}
	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public static void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	int toInt(Object p) {
		return Integer.parseInt(p.toString());
	}

	ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	ImageIcon toIcon(Object path, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().createImage((byte[]) path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	JLabel lbl(String l, int a) {
		return new JLabel(l, a);
	}

	JLabel lbl(String l, int a, int s) {
		var lbl = lbl(l, a);
		lbl.setFont(new Font("", Font.TYPE1_FONT, s));
		return lbl;
	}

	JLabel hylbl(String l, int a, int s) {
		var lbl = lbl(l, a);
		lbl.setFont(new Font("HY헤드라인M", Font.BOLD, s));
		return lbl;
	}

	JButton btn(String cap, ActionListener a) {
		var n = new JButton(cap);
		n.addActionListener(a);
		return n;
	}

	DefaultTableModel model(String[] col) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		var dtcr = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JLabel)
					return (JLabel) value;
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	void addRow(DefaultTableModel m, ArrayList<ArrayList<Object>> rs) {
		m.setRowCount(0);

		for (var r : rs)
			m.addRow(r.toArray());
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
