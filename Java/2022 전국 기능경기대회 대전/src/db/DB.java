package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
			base.emsg("셋팅 실패");
			System.exit(0);
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			emsg("셋팅 실패");
			System.exit(0);
		}
	}

	void create(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	String cascade = "on update cascade on delete cascade";

	public DB() {
		execute("drop database if exists 2022전국_2");
		execute("drop user if exists user@localhost");
		execute("create database 2022전국_2 default character set utf8");
		execute("create user user@localhost identified by '1234'");
		execute("set global local_infile = 1");
		execute("grant select, insert, update, delete on 2022전국_2.* to user@localhost");
		execute("use 2022전국_2");

		create("city", "c_no int primary key not null auto_increment, c_x int, c_y int, c_name varchar(15)");
		create("town",
				"t_no int primary key not null auto_increment, c_no int, t_x int, t_y int, t_name varchar(15), foreign key(c_no) references city(c_no)");
		create("user",
				"u_no int primary key not null auto_increment, u_name varchar(15), u_id varchar(15), u_pw int, u_birth varchar(15), division int, t_no int, foreign key(t_no) references town(t_no)");
		create("base",
				"b_no int primary key not null auto_increment, division int, b_name varchar(15), b_temperature int, b_note varchar(100), b_img longblob");
		create("farm",
				"f_no int primary key not null auto_increment, u_no int, b_no int, f_amount int, f_quantity int, foreign key(u_no) references user(u_no), foreign key(b_no) references base(b_no)");
		create("purchase",
				"p_no int primary key not null auto_increment, u_no int, f_no int, p_date varchar(15), p_quantity int, foreign key(f_no) references farm(f_no) "
						+ cascade);
		create("sale",
				"s_no int primary key not null auto_increment, u_no int, f_no int, s_date varchar(15), s_quantity int, foreign key(f_no) references farm(f_no) "
						+ cascade);
		create("weather", "t_day varchar(15), w_temperature int, w_humidity int, w_state int");

		for (var w : new File("./datafiles/농산물").listFiles()) {
			try {
				var pst = con.prepareStatement("update base set b_img =? where b_no = ?");
				pst.setObject(1, new FileInputStream(w));
				pst.setObject(2, w.getName().replace(".jpg", ""));
				pst.execute();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				emsg("셋팅 실패");
				System.exit(0);
			}
		}

		imsg("셋팅 성공");
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}
