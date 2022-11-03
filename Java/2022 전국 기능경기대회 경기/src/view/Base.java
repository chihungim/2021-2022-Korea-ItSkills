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
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Base {
	static Base base = new Base() {
	};

	Color back = new Color(0, 123, 255).darker().darker().darker().darker();
	String g_genre[] = ",공포,RPG,레이싱,스포츠,시뮬레이션,액션,어드벤쳐,전략,슈팅".split(",");
	String g_age[] = "전체이용,12세 이상,15세 이상,18세 이상".split(",");
	String g_gd[] = "일반,브론즈,실버,골드,플레티넘,다이아".split(",");
	int age[] = { 0, 12, 15, 18 };

	default ArrayList<ArrayList<Object>> getrows(String sql, Object... objds) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			DB.stmt.execute("use 2022전국_3");
			var pst = DB.con.prepareStatement(sql);
			for (int i = 0; i < objds.length; i++)
				pst.setObject(i + 1, objds[i]);
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

	default boolean isNumeric(Object p) {
		try {
			Integer.parseInt(p.toString());
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	default Graphics2D tog2d(Graphics g) {
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2;
	}

	default void execute(String sql, Object... objds) {
		try {
			DB.stmt.execute("use 2022전국_3");
			var pst = DB.con.prepareStatement(sql);
			for (int i = 0; i < objds.length; i++)
				pst.setObject(i + 1, objds[i]);
			System.out.println(pst);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default ImageIcon getBlob(Object p, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().createImage((byte[]) p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	default void b(Color c, JComponent... jc) {
		for (var j : jc) {
			j.setBackground(c);
			j.setOpaque(true);
		}
	}

	default void f(Color c, JComponent... jc) {
		for (var j : jc) {
			j.setForeground(c);
		}
	}

	default JComponent b(JComponent j, Color c) {
		j.setBackground(c);
		j.setOpaque(true);
		return j;
	}

	default JComponent f(JComponent j, Color c) {
		j.setForeground(c);
		return j;
	}

	default String decformat(Object p) {
		return new DecimalFormat("#,##0").format(cint(p));
	}

	default int parsedec(Object p) {
		try {
			return cint(new DecimalFormat("#,##0").parse(p.toString()));
		} catch (ParseException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}

	default double cdbl(Object p) {
		return Double.parseDouble(p.toString());
	}

	default int cint(Object p) {
		return Integer.parseInt(p.toString());
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

	default JLabel imglbl(String cap, String path, int w, int h, Consumer<MouseEvent> evt) {
		try {
			System.out.println(path);
			var f = ImageIO.read(new File(path));
			var l = wlbl(cap, JLabel.CENTER);
			Consumer<Color> color = a -> {
				for (int i = 0; i < f.getWidth(); i++) {
					for (int j = 0; j < f.getHeight(); j++) {
						var raster = f.getRaster().getPixel(i, j, (int[]) null);
						raster[0] = a.getRed();
						raster[1] = a.getGreen();
						raster[2] = a.getBlue();
						f.getRaster().setPixel(i, j, raster);
					}
				}
			};

			color.accept(Color.WHITE);

			l.setIcon(new ImageIcon(f.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
			l.setCursor(new Cursor(Cursor.HAND_CURSOR));

			l.setVerticalTextPosition(JLabel.BOTTOM);
			l.setHorizontalTextPosition(JLabel.CENTER);

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					evt.accept(e);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					color.accept(Color.YELLOW);
					l.setIcon(new ImageIcon(f.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					color.accept(Color.WHITE);
					l.setIcon(new ImageIcon(f.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
				}
			});
			return l;
		} catch (IOException e) {
			return null;
		}
	}

	default JButton bbtn(String cap, ActionListener a) {
		var b = btn(cap, a);
		b.setBackground(new Color(0, 123, 255));
		return b;
	}

	default JLabel lbl(String l, int a, int f, int s) {
		var lb = new JLabel(l, a);
		lb.setFont(new Font("HY헤드라인M", f, s));
		return lb;
	}

	default JLabel lbl(String l, int a, int s) {
		return lbl(l, a, Font.BOLD, s);
	}

	default JLabel lbl(String l, int a) {
		return lbl(l, a, 0, 13);
	}

	default JLabel wlbl(String l, int a, int f, int s) {
		var lbl = lbl(l, a, f, s);
		lbl.setForeground(Color.WHITE);
		return lbl;
	}

	default JLabel wlbl(String l, int a, int s) {
		return wlbl(l, a, Font.BOLD, s);
	}

	default JLabel wlbl(String l, int a) {
		return wlbl(l, a, 0, 13);
	}

	default JPanel newJPanel(LayoutManager m) {
		var p = new JPanel(m);
		p.setOpaque(false);
		return p;
	}

	default JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.addActionListener(a);
		return b;
	}

	default DefaultTableModel model(String[] col) {
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

		var dtcr = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if(value instanceof JComponent)
					return (JComponent)value;
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return t;
	}

	default void addrow(ArrayList<ArrayList<Object>> rs, DefaultTableModel m) {
		m.setRowCount(0);
		for (var r : rs)
			m.addRow(r.toArray());
	}

}
