package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class BaseFrame extends JFrame {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;
	static String uage, uname;
	static int uheight, uold, udisable, uno;
	static String age[] = "성인,청소년,어린이,노인".split(",");
	static {
		try {
			stmt.execute("use adventure");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String getOne(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class IconLabel extends JLabel {
		String title;

		public IconLabel(String title, boolean enb) {
			super("", BaseFrame.this.getIcon("./datafiles/아이콘.png", 25, 30), 0);
			this.title = title;
			setEnabled(enb);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (!enb) {
						eMsg("회원님이 이용하실 수 없는 등급입니다.");
						return;
					}
					super.mousePressed(e);
					new Detail(title).addWindowListener(new Before(BaseFrame.this));
				}
			});
		}

		public IconLabel(String title, String floor) {
			super("", BaseFrame.this.getIcon("./datafiles/아이콘.png", 25, 30), 0);
			this.title = title;
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
				}
			});
		}
	}

	DefaultTableModel model(String col[]) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		return m;
	}

	void addRow(DefaultTableModel m, String sql) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int toInt(Object path) {
		System.out.println(path);
		path = path.toString().replaceAll("[^\\d]", "");
		System.out.println(path);
		if (path.equals(""))
			return 0;
		return Integer.parseInt(path.toString());
	}

	static int getAge(LocalDate l) {
		var t = LocalDate.now();
		var y = t.getYear() - l.getYear();
		if (t.getMonthValue() > l.getMonthValue()) {
			y--;
		}

		if (y <= 12) {
			return 3;
		} else if (y >= 13 && y <= 19) {
			return 2;
		} else if (y >= 20 && y < 65) {
			return 1;
		}
		return 4;
	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		((JPanel) getContentPane()).setBackground(Color.WHITE);
	}

	JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
			super.windowClosed(e);
		}

	}

	JLabel lbl(String t, int a) {
		return new JLabel(t, a);
	}

	JLabel lbl(String t, int a, int s) {
		JLabel l = new JLabel(t, a);
		l.setFont(new Font("", Font.BOLD, s));
		return l;
	}

	void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "오류", JOptionPane.ERROR_MESSAGE);
	}

	ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	ImageIcon getIcon(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
	}

	JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.addActionListener(a);
		return b;
	}

}
