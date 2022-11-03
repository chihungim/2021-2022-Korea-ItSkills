package Àü³²;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	static Statement stmt;
	static Connection con;

	public DB() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&&allowPublicKeyRetreival=true","root","1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void createDB() {
		execute("drop database if exists RoomEscape");
		execute("create database RoomEscape");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("use RoomEscape");
		execute("grant update, select, delete, insert on RoomEscape.* to user@localhost");
		execute("set global local_infile = 1");
	}

	void createTable(String n, String c) {
		execute("create table " + n + "(" + c + ")");
		execute("load data local infile './Datafiles/" + n + ".txt' into table " + n
				+ " lines terminated by '\r\n' ignore 1 lines");
	}

	public static void main(String[] args) {
		DB db = new DB();
		db.createDB();
		db.createTable("user", "u_no int primary key not null auto_increment, u_id varchar(10), u_pw varchar(10), u_name varchar(10), u_date date, u_address varchar(50)");
		db.createTable("cafe", "c_no varchar(10) primary key not null, c_name varchar(20), c_division varchar(20), c_tel varchar(15), c_address varchar(50), c_price int");
		db.createTable("genre", "g_no int primary key not null auto_increment, g_name varchar(10)");
		db.createTable("theme", "t_no int primary key not null auto_increment, t_name varchar(30), g_no int, t_explan varchar(200), t_personnel int, t_time int, t_difficulty int, foreign key(g_no) references genre(g_no) on delete cascade on update cascade");
		db.createTable("reservation", "r_no int primary key not null auto_increment, u_no int, c_no varchar(10), t_no int, r_date date, r_time varchar(20), r_people int, r_attend int, foreign key(u_no) references user(u_no) on delete cascade on update cascade, foreign key(c_no) references cafe(c_no) on delete cascade on update cascade, foreign key(t_no) references theme(t_no) on delete cascade on update cascade");
		db.createTable("notice", "n_no int primary key not null auto_increment, n_date date, u_no int, n_title varchar(20), n_content varchar(150), n_viewcount int, n_open varchar(1), foreign key(u_no) references user(u_no) on delete cascade on update cascade");
		db.createTable("evaluation", "e_no int primary key not null auto_increment, u_no int, c_no varchar(10), e_score int, foreign key(u_no) references user(u_no) on delete cascade on update cascade, foreign key(c_no) references cafe(c_no) on delete cascade on update cascade");
		db.createTable("test", "t_no int primary key not null auto_increment, t_answer varchar(10)");
	}

}
