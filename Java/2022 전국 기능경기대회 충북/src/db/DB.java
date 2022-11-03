package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import view.Base;

public class DB implements Base {
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
			System.out.println(sql);
			emsg("셋팅 실패");
			e.printStackTrace();
			System.exit(0);
		}
	}

	void create(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafile/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists movie");
		execute("create database movie default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, update, delete on movie.* to user@localhost");
		execute("set global local_infile = 1");
		execute("use movie");

		create("area", "a_no int primary key not null auto_increment, a_name varchar(15)");
		create("user",
				"u_no int primary key not null auto_increment, u_id varchar(10), u_pw varchar(10), u_name varchar(10), u_phone varchar(50), u_email varchar(100), u_date date, u_gender int");
		create("genre", "g_no int primary key not null auto_increment, g_name varchar(10)");
		create("store",
				"s_no int primary key not null auto_increment, s_name varchar(30), s_explanation varchar(130), s_price int");
		create("theater",
				"t_no int primary key not null auto_increment, a_no int, t_name varchar(30), foreign key(a_no) references area(a_no)");
		create("movie",
				"m_no int primary key not null auto_increment, g_no int, m_rating varchar(10), m_show int, m_grade double, m_name varchar(50), m_content varchar(500), foreign key(g_no) references genre(g_no)");
		create("orderlist",
				"o_no int primary key not null auto_increment, u_no int, s_no int, o_count int, o_date date, foreign key(u_no) references user(u_no), foreign key(s_no) references store(s_no)");
		create("pomaes", "p_no int primary key not null auto_increment, p_name varchar(20)");
		create("schedule",
				"sc_no int primary key not null auto_increment, t_no int, m_no int, p_no int, sc_theater varchar(5), sc_date date, sc_time varchar(10), foreign key(t_no) references theater(t_no), foreign key(m_no) references movie(m_no), foreign key(p_no) references pomaes(p_no)");
		create("reservation",
				"r_no int primary key not null auto_increment, u_no int, r_division varchar(50), sc_no int, r_people int, r_seat varchar(50), r_date date, r_time varchar(15), foreign key(u_no) references user(u_no), foreign key(sc_no) references schedule(sc_no)");
		imsg("셋팅 성공");
	}

	public static void main(String[] args) {
		new DB();
	}
}
