package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import view.BaseFrame;

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
			BaseFrame.emsg("���� ����");
			System.exit(0);
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			BaseFrame.emsg("���� ����");
			System.exit(0);
		}
	}

	void create(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022����_2");
		execute("create database 2022����_2 default character set utf8");
		execute("drop user if exists user@localhost ");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, update, delete on 2022����_2.* to user@localhost");
		execute("set global local_infile = 1");
		execute("use 2022����_2");

		create("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(10), u_pw varchar(15), u_birth varchar(15), u_email varchar(30), u_gender int, u_graduate int, u_address varchar(100), u_img longblob");
		create("company",
				"c_no int primary key not null auto_increment, c_name varchar(10), c_ceo varchar(10), c_address varchar(100), c_category varchar(15), c_employee int, c_img longblob, c_search int");
		create("employment",
				"e_no int primary key not null auto_increment, c_no int, e_title varchar(30), e_pay int, e_people int, e_gender int, e_graduate int, foreign key(c_no) references company(c_no)");
		create("applicant",
				"a_no int primary key not null auto_increment, e_no int, u_no  int , a_apply int, foreign key(e_no) references employment(e_no), foreign key(u_no) references user(u_no)");

		try {
			var rs = stmt.executeQuery("select * from user");
			while (rs.next()) {
				var pst = con.prepareStatement("update user set u_img = ? where u_no = ?");
				pst.setBinaryStream(1, new FileInputStream("./datafiles/ȸ������/" + rs.getInt(1) + ".jpg"));
				pst.setObject(2, rs.getString(1));
				pst.execute();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			BaseFrame.emsg("���� ����");
			System.exit(0);
		}

		try {
			var rs = stmt.executeQuery("select * from company");
			while (rs.next()) {
				var pst = con.prepareStatement("update company set c_img = ? where c_no = ?");
				pst.setBinaryStream(1, new FileInputStream("./datafiles/���/" + rs.getString(2) + "1.jpg"));
				pst.setObject(2, rs.getString(1));
				pst.execute();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			BaseFrame.emsg("���� ����");
			System.exit(0);
		}

		BaseFrame.imsg("���� ����");
	}

	public static void main(String[] args) {
		new DB();
	}
}
