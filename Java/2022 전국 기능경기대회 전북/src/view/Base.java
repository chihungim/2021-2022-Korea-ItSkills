package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Base {
	DecimalFormat df = new DecimalFormat("#,##0");

	String c_division[] = ",성인,소아,유아".split(",");

	default ArrayList<ArrayList<Object>> getrows(String sql, Object... objs) {
		try {
			DB.stmt.execute("use airline");
			var pst = DB.con.prepareStatement(sql);
			var rows = new ArrayList<ArrayList<Object>>();

			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			System.out.println(pst);

			var rs = pst.executeQuery();
			var m = rs.getMetaData();

			while (rs.next()) {
				var row = new ArrayList<>();
				for (int i = 0; i < m.getColumnCount(); i++)
					row.add(rs.getObject(i + 1));
				rows.add(row);
			}

			return rows;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	static Base b = new Base() {
	};

	default JScrollPane scrollpane(JComponent jc) {
		var j = new JScrollPane(jc);
		var basb = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = Color.GRAY;
//				trackColor = Color.LIGHT_GRAY;
			}
		};
		var basb2 = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = Color.GRAY;
//				trackColor = Color.LIGHT_GRAY;
			}
		};

		j.getHorizontalScrollBar().setUI(basb);
		j.getVerticalScrollBar().setUI(basb);
		return j;
	}

	static void 디버그(String no) {
		BasePage.member = b.getrows("select * from member where m_no =?", no).get(0);
		new MainFrame();
	}

	default Graphics2D tog2d(Graphics g) {
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2;
	}

	default void execute(String sql, Object... objs) {
		try {
			DB.stmt.execute("use airline");
			var pst = DB.con.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	default JComponent f(JComponent jc, Color p) {
		jc.setForeground(p);
		return jc;
	}

	default JComponent evt(JComponent jc, Consumer<MouseEvent> evt) {
		jc.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				evt.accept(e);
			}
		});
		return jc;
	}

	default double cdbl(Object p) {
		return Double.parseDouble(p.toString());
	}

	default double distance(String a1, String a2) {
		return cdbl(getrows(
				"select  round(st_distance_sphere(point(a1.a_longtitude, a1.a_latitude), point(a2.a_longtitude, a2.a_latitude))/1000/600 ,1)*60 from airport a1, airport a2 where a1.a_no = ? and a2.a_no = ?",
				a1, a2).get(0).get(0));
	}

	default JComponent b(JComponent jc, Color p) {
		jc.setOpaque(true);
		jc.setBackground(p);
		return jc;
	}

	default void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
//		jc.setPreferredSize(new Dimension(w == 0 ? (int) jc.getMaximumSize().getWidth() : w,
//				(int) (h == 0 ? jc.getMaximumSize().getWidth() : h)));
		return jc;
	}

	default int cint(Object p) {
		return Integer.parseInt(p.toString());
	}

	default boolean isNumeric(Object p) {
		try {
			Integer.parseInt(p.toString());
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	default JLabel lbl(String p, int a, int s, int f) {
		var l = new JLabel(p, a);
		l.setFont(new Font("맑은 고딕", f, s));
		return l;
	}

	default JLabel lbl(String p, int a, int s) {
		var l = lbl(p, a, s, Font.BOLD);
		return l;
	}

	default JLabel lbl(String p, int a) {
		return lbl(p, a, 13, new JLabel().getFont().getStyle());
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		var dtcr = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JComponent) {
					return (JComponent) value;
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return t;
	}

	default DefaultTableModel model(String[] col) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	default JPanel newJPanel(LayoutManager m) {
		var p = new JPanel(m);
		p.setOpaque(false);
		return p;
	}

	default void addrow(ArrayList<ArrayList<Object>> objs, DefaultTableModel m) {
		m.setRowCount(0);
		for (var r : objs)
			m.addRow(r.toArray());
	}

	default JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.addActionListener(a);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return b;
	}

	default JButton bbtn(String cap, ActionListener a) {
		var b = btn(cap, a);
		b(b, Color.blue);
		f(b, Color.WHITE);
		return b;
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default ImageIcon getBlob(Object p, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().createImage((byte[]) p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
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

}
