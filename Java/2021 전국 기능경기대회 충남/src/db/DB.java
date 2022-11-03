package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	
	static Connection con;
	static Statement stmt;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&allowPublicKeyRetrieval=true", "root", "1234");
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
		execute("create table "+t+"("+c+")");
		execute("load data local infile './지급자료/"+t+".txt' into table "+t+" lines terminated by '\r\n' ignore 1 lines");
	}
	
	public DB() {
		execute("drop database if exists metro");
		execute("create database metro default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select,insert,update,delete on metro.* to user@localhost");
		execute("set global local_infile=1");
		execute("use metro");
		
		createTable("Station", "serial int primary key not null auto_increment, name varchar(40)");
		createTable("Metro", "serial int primary key not null auto_increment, name varchar(60), start Time, end Time, `interval` Time");
		createTable("route", "serial int primary key not null auto_increment, station int, metro int, foreign key(station) references station(serial), foreign key(metro) references metro(serial)");
		createTable("Path", "serial int primary key not null auto_increment, departure int, arrive int, cost int, foreign key(departure) references station(serial), foreign key(arrive) references station(serial)");
		createTable("User", "serial int primary key not null auto_increment, id varchar(20), pw varchar(50), name varchar(30), birth date, phone varchar(14)");
		createTable("Purchase", "serial int primary key not null auto_increment, user int, departure int, arrive int, price int, `time` time, `date` date, distance int, foreign key(user) references user(serial), foreign key(departure) references station(serial), foreign key(arrive) references station(serial)");
	}
	
	public static void main(String[] args) {
		new DB();
	}
	
}
