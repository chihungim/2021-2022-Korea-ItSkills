package view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import db.DB;

public interface Base {
	default ArrayList<ArrayList<Object>> getrows(String sql, Object... objs) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			DB.stmt.execute("use movie");
			var pst = DB.con.prepareStatement(sql);

			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			var rs = pst.executeQuery();
			var m = rs.getMetaData();

			while (rs.next()) {
				var row = new ArrayList<>();
				for (int i = 0; i < m.getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				rows.add(row);
			}

			return rows;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	DecimalFormat df = new DecimalFormat("#,##0");

	default JButton rbtn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.setForeground(Color.WHITE);
		b.setBackground(Color.RED);
		b.addActionListener(a);
		return b;
	}

	default JButton bbtn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.setBackground(Color.BLACK);
		b.setForeground(Color.WHITE);
		b.addActionListener(a);
		return b;
	}

	default JPanel newJPanel(LayoutManager m) {
		var p = new JPanel(m);
		p.setOpaque(false);
		return p;
	}

	default void execute(String sql, Object... objs) {
		try {
			DB.stmt.execute("use movie");
			var pst = DB.con.prepareStatement(sql);

			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public default void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	public default void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default int cint(Object p) {
		return Integer.parseInt(p.toString());
	}

	default double cdbl(Object p) {
		return Double.parseDouble(p.toString());
	}

	default JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	default void b(Color c, JComponent... jc) {
		for (var j : jc) {
			j.setOpaque(true);
			j.setBackground(c);
		}
	}

	default void f(Color c, JComponent... jc) {
		for (var j : jc) {
			j.setForeground(c);
		}
	}

	default JComponent evt(JComponent jc, Consumer<MouseEvent> con) {
		jc.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				con.accept(e);
			}
		});
		return jc;
	}

	default JLabel lbl(String cap, int a, String fs, int f, int s) {
		var l = new JLabel(cap, a);
		l.setFont(new Font(fs, f, s));
		return l;
	}

	default JLabel lbl(String cap, int a, int s) {
		return lbl(cap, a, "맑은 고딕", Font.BOLD, s);
	}

	default JLabel lbl(String cap, int a) {
		return lbl(cap, a, "맑은 고딕", 0, new JLabel().getFont().getSize());
	}

	default JLabel hylbl(String cap, int a, int s) {
		return lbl(cap, a, "HY헤드라인M", Font.BOLD, s);
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	class before extends WindowAdapter {

		JFrame f;

		public before(JFrame f) {
			this.f = f;
			f.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			f.setVisible(true);
		}
	}

	default JLabel mkBar(int h) {
		return lbl("|", JLabel.CENTER, "", 0, h);
	}

	default void addrow(ArrayList<ArrayList<Object>> data, DefaultTableModel m) {
		m.setRowCount(0);
		for (var d : data)
			m.addRow(d.toArray());
	}

	default DefaultTableModel model(String col[]) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		var dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return t;

	}

	default Graphics2D tog2d(Graphics g) {
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2;
	}

	class Hint {
		String hint;

		public Hint(String hint) {
			this.hint = hint;
		}

		void paintHint(JTextComponent jtc, Graphics g) {
			if (jtc.getText().isEmpty()) {
				g.setColor(Color.LIGHT_GRAY);
				var f = g.getFontMetrics();
				var ins = jtc.getInsets();
				g.drawString(hint, ins.left, f.getHeight());
			}
		}

		public JTextField txt(int col) {
			return new JTextField(col) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintHint(this, g);
				}
			};
		}

		public JPasswordField pw(int col, char echo) {
			return new JPasswordField(col) {
				@Override
				public char getEchoChar() {
					return echo;
				}

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintHint(this, g);
				}
			};
		}

	}

	class CSpinner extends JPanel implements Base {
		int value, max, min;
		JLabel p, m, v;

		public CSpinner(int value, int max, int min) {
			setLayout(new BorderLayout());
			this.value = value;
			this.max = max;
			this.min = min;
			add(p = lbl("+", JLabel.CENTER, 15), "East");
			add(v = lbl(value + "", JLabel.CENTER, 15));
			add(m = lbl("-", JLabel.CENTER, 15), "West");
			m.setBackground(Color.LIGHT_GRAY);
			p.setBackground(Color.LIGHT_GRAY);
			evt(p, a -> cValue(1));
			evt(m, a -> cValue(-1));
			setBackground(Color.WHITE);
			setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
		}

		void cValue(int dir) {
			if (ValueRange.of(min, max).isValidIntValue(value + dir)) {
				value += dir;
				v.setText(value + "");
			}
		}

	}

}
