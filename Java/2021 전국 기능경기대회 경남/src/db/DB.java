package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection con;
	public static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&allowPublicKeyRetrieval=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	void createTable(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './지급자료/" + t + ".txt' into table " + t
				+ " lines terminated by '\r\n' ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists albajava");
		execute("create database albajava default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("set global local_infile=1");
		execute("use albajava");

		createTable("user",
				"u_no int primary key not null auto_increment, id varchar(10), pw varchar(15), name varchar(10), email varchar(30), phone varchar(13), birth varchar(10), address varchar(100), gender varchar(1), agree int");
		createTable("type", "t_no int primary key not null auto_increment, name varchar(10)");
		createTable("mail",
				"ma_no int primary key not null auto_increment, division int, recipient int, sender int, title varchar(200), detail varchar(250), `date` date, `read` int");
		createTable("details",
				"d_no int primary key not null auto_increment, name varchar(10), t_no int, foreign key(t_no) references type(t_no)");
		createTable("employment", "e_no int primary key not null auto_increment, name varchar(5)");
		createTable("benefit", "b_no int primary key not null auto_increment, name varchar(10) ");
		createTable("search", "s_no int primary key not null auto_increment, word varchar(30), time datetime");
		createTable("company",
				"c_no int primary key not null auto_increment, number varchar(10), name varchar(30), d_no int, entrepreneur varchar(30), phone varchar(13), detail varchar(10), address varchar(100), foreign key(d_no) references details(d_no)");
		createTable("manager",
				"m_no int not null, id varchar(10), pw varchar(16), name varchar(10), email varchar(30), phone varchar(13), foreign key(m_no) references company(c_no)");

		createTable("recruitment",
				"r_no int primary key not null auto_increment, c_no int, title varchar(200), period varchar(5), week varchar(30), time varchar(11), standard varchar(2), salary int, date date, d_no int, e_no int, benefit varchar(8), deadline date, hits int, foreign key(c_no) references company(c_no), foreign key(d_no) references details(d_no), foreign key(e_no) references employment(e_no)");
		createTable("information",
				"i_no int primary key not null auto_increment, u_no int, title varchar(200), level varchar(16), d_no int, e_no int, period varchar(10), anonymous varchar(3), foreign key(u_no) references user(u_no), foreign key(d_no) references details(d_no), foreign key(e_no) references employment(e_no)");

		for (int i = 1; i <= 30; i++) {
			execute("update user set pw = 'user" + String.format("%02d", i) + "#$' where u_no =" + i);
		}
		for (int i = 1; i <= 112; i++) {
			execute("update manager set pw = 'manager" + String.format("%02d", i) + "#$' where m_no =" + i);
		}

	}

	public static void main(String[] args) {
		new DB();
	}

}
