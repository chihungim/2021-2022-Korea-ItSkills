package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {
	public static Connection con;
	public static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "셋팅 실패", "경고", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "셋팅 실패", "경고", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	void create(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022지방_1");
		execute("create database 2022지방_1 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, update, delete on 2022지방_1.* to user@localhost");
		execute("set global local_infile = true");
		execute("use 2022지방_1");

		create("user",
				"u_no int primary key not null auto_increment, u_id varchar(10), u_pw varchar(10), u_name varchar(10), u_date date");
		create("area", "a_no int primary key not null auto_increment, a_name varchar(2)");
		create("cafe",
				"c_no varchar(10) primary key, c_name varchar(20), t_no varchar(100), c_tel varchar(15), a_no int, c_address varchar(50), c_price int, foreign key(a_no) references area(a_no)");
		create("genre", "g_no int primary key not null auto_increment, g_name varchar(10)");
		create("theme",
				"t_no int primary key not null auto_increment, t_name varchar(30), g_no int, t_explan varchar(200), t_personnel int, t_time int, foreign key(g_no) references genre(g_no)");
		create("reservation",
				"r_no int primary key not null auto_increment, u_no int, c_no varchar(10), t_no int, r_date date, r_time varchar(20), r_people int, r_attend int, foreign key(u_no) references user(u_no), foreign key(c_no) references cafe(c_no), foreign key(t_no) references theme(t_no)");
		create("map", "a_no int, m_x int, m_y int, foreign key(a_no) references area(a_no)");
		create("ping", "a_no int, p_x int, p_y int, foreign key(a_no) references area(a_no)");
		create("notice",
				"n_no int primary key not null auto_increment, u_no int, n_date date, n_title varchar(20), n_content varchar(150), n_viewcount int, n_open int, foreign key(u_no) references user(u_no)");
		create("quiz", "q_no int primary key not null auto_increment, q_name varchar(10)");
		JOptionPane.showMessageDialog(null, "셋팅 성공", "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		new DB();
	}
}
