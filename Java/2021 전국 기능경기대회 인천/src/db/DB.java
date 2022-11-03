package db;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DB extends JFrame {
	public static Connection con;
	public static Statement stmt;

	static Icon eIcon = UIManager.getIcon("OptionPane.errorIcon");
	static Icon iIcon = UIManager.getIcon("OptionPane.informationIcon");
	static LocalDateTime sTime;
	DefaultTableModel m = new DefaultTableModel(null, "상태,이름".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};

		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return super.getValueAt(0, columnIndex).getClass();
		};
	};

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (value instanceof JLabel) {
				return (JLabel) value;
			} else
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		};
	};

	JTable t = new JTable(m);
	JScrollPane pane = new JScrollPane(t);
	String cascade = "on delete cascade on update cascade";
	static {
		try {
			con = DriverManager.getConnection(
					"Jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DB() {
		super("db 초기화");
		setSize(500, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		add(pane);
		t.getTableHeader().setReorderingAllowed(false);
		t.setRowHeight(40);
		t.setShowGrid(false);
		t.setOpaque(false);
		t.setDefaultRenderer(JLabel.class, dtcr);
		t.getColumnModel().getColumn(0).setMinWidth(100);
		t.getColumnModel().getColumn(0).setMaxWidth(100);
		pane.setBorder(new TitledBorder("Log"));

		setVisible(true);

		try {
			execute("DB 삭제", "drop database if exists Delivery");
			execute("DB 생성", "create database Delivery default character set utf8");
			execute("user 삭제", "drop user if exists user@localhost");
			execute("user 생성", "create user user@localhost identified by '1234'");
			execute("user 권한설정", "GRANT SELECT, INSERT, UPDATE, DELETE ON DELIVERY.* to user@localhost");

			stmt.execute("set global local_infile = 1");
			stmt.execute("use Delivery");

			createT("point", "po_No int primary key not null auto_increment, po_X int, po_Y int ");
			createT("connect",
					"c_Node1 int , c_Node2 int, foreign key(c_Node1) references point(po_No), foreign key(c_Node2) references point(po_No) "
							+ cascade);
			createT("user",
					"u_No int primary key not null auto_increment, u_Id varchar(20), u_Pattern varchar(10), u_Name varchar(20), u_Addr int, foreign key(u_Addr) references point(po_No) "
							+ cascade);
			createT("seller",
					"s_No int primary key not null auto_increment, s_Id varchar(20), s_Pattern varchar(10), s_Name varchar(20), s_Addr int, foreign key(s_Addr) references point(po_No)");
			createT("category", "c_No int primary key not null auto_increment, c_Name varchar(20)");
			createT("product",
					"p_No int primary key not null auto_increment, c_No int, p_Name varchar(50), p_Price int, foreign key(c_No) references category(c_No) "
							+ cascade);
			createT("stock",
					"st_No int primary key not null auto_increment, s_No int, p_No int, st_Count int, foreign key(s_No) references seller(s_No), foreign key(p_No) references product(p_No) "
							+ cascade);
			createT("event",
					"e_No int primary key not null auto_increment, p_No int, e_Month int ,foreign key(p_No) references product(p_No) "
							+ cascade);

			createT("purchase",
					"pu_No int primary key not null auto_increment, s_No int, p_No int, u_No int, pu_Count int, pu_Price int, pu_Date date, foreign key(s_No) references seller(s_No), foreign key(p_No) references product(p_No), foreign key(u_No) references user(u_No) "
							+ cascade);
			createT("receive",
					"r_No int primary key not null auto_increment, pu_No int not null, r_Time datetime not null, foreign key(pu_No) references purchase(pu_No) "
							+ cascade);
			new Thread(() -> {
				for (int i = 5; i > 0; i--) {
					iLog("<html><b>DB 구성 성공, " + i + "초 후 종료됩니다.", true);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				dispose();
			}).start();
		} catch (SQLException e) {
			new Thread(() -> {
				for (int i = 5; i > 0; i--) {
					iLog("<html><b>DB 구성 실패, " + i + "초 후 종료됩니다.", false);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				dispose();
			}).start();
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new DB();
	}

	void createT(String t, String c) throws SQLException {
		try {
			execute(t + "테이블 생성", "create table " + t + "(" + c + ")");
			execute(t + " 테이블 초기화", "load data local infile './datafile/" + t + ".txt' into table " + t
					+ " lines terminated by '\r\n' ignore 1 lines");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	void execute(String cap, String sql) throws SQLException {
		try {
			stmt.execute(sql);
			iLog(cap, true);
		} catch (SQLException e) {
			System.out.println(sql);
			iLog(cap, false);
			throw e;
		}
	}

	void iLog(String t, boolean b) {
		var clbl = new JLabel(b ? "성공" : "실패", b ? iIcon : eIcon, 0);
		var lbl = new JLabel(t);

		clbl.setFont(new Font("굴림", Font.PLAIN, 11));
		lbl.setFont(new Font("굴림", Font.PLAIN, 11));
		lbl.setForeground(b ? Color.BLUE : Color.RED);
		clbl.setForeground(b ? Color.BLUE : Color.RED);
		m.addRow(new Object[] { clbl, lbl });
		try {
			Thread.sleep(100);
			pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMaximum());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
