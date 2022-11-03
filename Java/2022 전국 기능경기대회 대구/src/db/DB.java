package db;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DB extends JFrame {
	public static Connection con;
	public static Statement stmt;

	JLabel loglbl;
	int arc = 0;

	boolean failed;
	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql, String log, int w) throws SQLException {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			stmt.execute(sql);
			loglbl.setText(log);
			arc += w;
		} catch (SQLException e) {
			failed = true;
			loglbl.setText(log + "Failed");
			loglbl.setForeground(Color.RED);
			throw e;
		}
	}

	void create(String t, String c) throws SQLException {
		try {
			execute("create table " + t + "(" + c + ")", t + " table 생성", 10);
		} catch (SQLException e) {
			throw e;
		}
	}

	void load(String t) throws SQLException {
		try {
			execute("load data local infile './지급자료/" + t + ".csv' into table " + t
					+ " fields terminated by ',' lines terminated by '\\r\\n' ignore 1 lines", t + " Data Insert", 10);
		} catch (SQLException e) {
			throw e;
		}
	}

	public DB() {
		setTitle("Database Setting");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(360, 400);
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
		add(new JPanel() {
			@Override
			public Color getBackground() {
				return Color.white;
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(failed ? Color.DARK_GRAY : new Color(237, 85, 59));
				g2.fill(new Arc2D.Double(0, 0, getWidth(), getHeight(), 90, -arc, Arc2D.PIE));
				int w = 40;
				g2.setColor(Color.WHITE);
				g2.fillOval(w, w, getWidth() - w * 2, getHeight() - w * 2);
			}
		});

		getContentPane().setBackground(Color.WHITE);

		add(loglbl = new JLabel("", JLabel.CENTER), "South");

		new Thread(() -> {
			while (arc <= 365 && !failed) {
				arc += 3;
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				repaint();
			}

			if (!failed) {
				loglbl.setText("Setting Complete!");
			}
		}).start();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		setVisible(true);
		sql();
	}

	public static void main(String[] args) {
		new DB();
	}

	void sql() {
		try {
			execute("drop database if exists 2022전국", "Database 삭제", 5);
			execute("drop user if exists user@localhost", "User 삭제", 5);
			execute("create database 2022전국 default character set utf8", "Database 생성", 5);
			execute("create user user@localhost identified by '1234'", "User 생성", 5);
			execute("grant select, insert, update, delete on 2022전국.* to user@localhost", "User 권한 지정", 5);
			stmt.execute("set global local_infile = 1");
			stmt.execute("use 2022전국");

			create("grade",
					"gr_no int primary key not null auto_increment, gr_name varchar(10) not null, gr_criteria int not null");
			create("user",
					"u_no int primary key not null auto_increment, u_id varchar(20) not null, u_pw varchar(30) not null, u_name varchar(30) not null, u_birth date not null, u_gender int not null, gr_no int not null, foreign key(gr_no) references grade(gr_no)");
			create("area", "a_no int primary key not null auto_increment, a_name varchar(15) not null");
			create("theater",
					"t_no int primary key not null auto_increment, t_name varchar(30) not null, a_no int not null, m_no varchar(200) not null, foreign key(a_no) references area(a_no)");
			create("genre", "g_no int primary key not null auto_increment, g_name varchar(10) not null");
			create("movie",
					"m_no int primary key not null auto_increment, m_title varchar(50) not null, m_synopsis text not null, g_no varchar(100) not null, m_time int not null,m_open int not null, m_directer varchar(20) not null");
			create("comment",
					"c_no int primary key not null auto_increment, u_no int not null, m_no int not null, c_text text not null, c_rate int not null, foreign key(m_no) references movie(m_no), foreign key(u_no) references user(u_no)");
			create("reservation",
					"r_no int primary key not null auto_increment, u_no int not null, m_no int not null, t_no int not null, r_date date not null, r_time varchar(10) not null, r_seat varchar(200) not null, r_price int not null, foreign key(u_no) references user(u_no), foreign key(m_no) references movie(m_no), foreign key(t_no) references theater(t_no)");

			load("grade");
			load("user");
			load("area");
			load("theater");
			load("genre");
			load("movie");
			load("comment");
			load("reservation");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
