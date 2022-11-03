package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import db.DB;

public interface Base {
	Color red = new Color(237, 58, 59);
	Color yellow = new Color(246, 213, 92);
	Color olive = new Color(42, 72, 88);

	String genre[] = (",액션," + "애니메이션," + "드라마," + "범죄," + "코미디," + "로맨스," + "스릴러," + "공포," + "SF," + "미스터리," + "판타지,"
			+ "어드벤처," + "가족," + "사극," + "전쟁," + "다큐멘터리," + "뮤지컬").split(",");
	String grade[] = (",일반," + "VIP," + "RVIP," + "VVIP," + "SVIP").split(",");
	int dage[] = { 0, 0, 12, 15 };

	default ArrayList<ArrayList<Object>> getrows(String sql, Object... objs) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			DB.stmt.execute("use 2022전국");
			var pst = DB.con.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			System.out.println(pst);
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

	default ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default void execute(String sql, Object... objs) {
		try {
			DB.stmt.execute("use 2022전국");
			var pst = DB.con.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++)
				pst.setObject(i + 1, objs[i]);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	default JLabel ageLimit(String age) {
		var a = cint(age);
		var l = new JLabel(",ALL,12,15".split(",")[a], JLabel.CENTER) {
			protected void paintComponent(Graphics g) {
				var g2 = tog2d(g);
				g2.setColor(new Color[] { null, Color.green, new Color(0, 123, 255), Color.ORANGE }[a]);
				g2.fillOval(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			};
		};
		l.setForeground(Color.WHITE);
		return (JLabel) sz(l, 30, 30);
	}

	default JComponent border(JComponent jc, Border b) {
		jc.setBorder(b);
		return jc;
	}

	default JComponent f(JComponent jc, Color col) {
		jc.setForeground(col);
		return jc;
	}

	default JComponent b(JComponent jc, Color col) {
		jc.setOpaque(true);
		jc.setBackground(col);
		return jc;
	}

	default JFileChooser jfc() {
		var jfc = new JFileChooser("./지급자료/image/user");
		jfc.setFileFilter(new FileNameExtensionFilter("JPG Images", "JPG"));
		return jfc;
	}

	default int msg() {
		return JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시겠습니까?", "질문", JOptionPane.YES_NO_OPTION);
	}

	default void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default Graphics2D tog2d(Graphics g) {
		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2;
	}

	default ImageIcon rndImg(String path, int w, int h) {
		try {
			var img = ImageIO.read(new File(path));
			var buff = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			var g2 = tog2d(buff.getGraphics());
			g2.setPaint(new TexturePaint(img, new Rectangle2D.Double(0, 0, w, h)));
			g2.fillOval(0, 0, w, h);
			return new ImageIcon(buff);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	default JLabel lbl(String t, int a, int f, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("맑은 고딕", f, s));
		return l;
	}

	default JLabel lbl(String t, int a, int s) {
		return lbl(t, a, Font.BOLD, s);
	}

	default JLabel lbl(String t, int a) {
		return lbl(t, a, 0, new JLabel().getFont().getSize());
	}

	default int cint(Object p) {
		return Integer.parseInt(p.toString());
	}

	default double cdbl(Object p) {
		return Double.parseDouble(p.toString());
	}

	default JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));

		int neww1 = (int) (w == 0 ? jc.getMaximumSize().getWidth() : w);
		int newh1 = (int) (h == 0 ? jc.getMaximumSize().getHeight() : h);
		int neww2 = (int) (w == 0 ? jc.getMinimumSize().getWidth() : w);
		int newh2 = (int) (h == 0 ? jc.getMinimumSize().getHeight() : h);
		jc.setMaximumSize(new Dimension(neww1, newh1));
		jc.setMinimumSize(new Dimension(neww2, newh2));
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

	default JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.addActionListener(a);
		b.setBackground(red);
		b.setForeground(Color.WHITE);
		return b;
	}

	default JButton rndBtn(String cap, ActionListener a) {
		var b = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = tog2d(g);
				g2.setColor(red);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				super.paintComponent(g);
			}
		};
		b.setForeground(Color.WHITE);
		b.addActionListener(a);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setRolloverEnabled(false);
		b.setOpaque(false);
		b.setText(cap);
		return b;
	}

	default JScrollPane scrollPane(JComponent jc) {
		JScrollPane pane = new JScrollPane(jc);
		var h = pane.getHorizontalScrollBar();
		var v = pane.getVerticalScrollBar();
		pane.setBorder(null);
		var b1 = new BasicScrollBarUI() {

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return (JButton) sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return (JButton) sz(new JButton(), 0, 0);
			}

			@Override
			protected void configureScrollBarColors() {
				thumbColor = Color.LIGHT_GRAY;
				trackColor = Color.WHITE;
			}
		};
		var b2 = new BasicScrollBarUI() {

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return (JButton) sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return (JButton) sz(new JButton(), 0, 0);
			}

			@Override
			protected void configureScrollBarColors() {
				thumbColor = Color.LIGHT_GRAY;
				trackColor = Color.WHITE;
			}
		};
		h.setUI(b1);
		v.setUI(b2);
		return pane;
	}

	class Hint {
		String hint;

		public Hint(String hint) {
			this.hint = hint;
		}

		void paintHint(Graphics g, JTextComponent jtc) {
			if (jtc.getText().isEmpty()) {
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.LIGHT_GRAY);
				var ins = jtc.getInsets();
				g2.drawString(hint, ins.left, g2.getFontMetrics().getHeight() - ins.bottom);
			}
		}

		JTextField txt(int col) {
			return new JTextField(col) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintHint(g, this);
				}
			};
		}

		JPasswordField pw(int col, char echo) {
			return new JPasswordField(col) {
				@Override
				public char getEchoChar() {
					return echo;
				}

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintHint(g, this);
				}
			};
		}

		JTextArea area(int r, int c) {
			return new JTextArea(r, c) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					paintHint(g, this);
				}
			};
		}

	}

	default JPanel newJPanel(LayoutManager m) {
		var p = new JPanel(m);
		p.setOpaque(false);
		return p;
	}

	default void addrow(ArrayList<ArrayList<Object>> rs, DefaultTableModel m) {
		m.setRowCount(0);
		for (var r : rs)
			m.addRow(r.toArray());
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.getTableHeader().setBackground(red);
		t.getTableHeader().setForeground(Color.WHITE);
		var dtcr = new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JComponent)
					return (JComponent) value;
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++)
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
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
