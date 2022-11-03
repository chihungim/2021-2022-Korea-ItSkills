import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DB {
	static Connection con;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
					"root", "1234");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
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

	void createTable(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './지급파일/" + t + ".csv' into table " + t
				+ " fields terminated by ',' lines terminated by '\r\n' ignore 1 lines ");
	}

	public DB() {
		execute("drop database if exists busticketbooking");
		execute("create database busticketbooking default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("use busticketbooking");
		execute("grant update, insert, select, delete on busticketbooking.* to user@localhost");
		execute("set global local_infile = 1");

		createTable("user",
				"no int primary key not null auto_increment, id varchar(50) not null, pwd varchar(50) not null, name varchar(50) not null, email varchar(50) not null, point int not null");
		createTable("location", "no int primary key not null auto_increment, name varchar(50) not null");
		createTable("location2",
				"no int primary key not null auto_increment, name varchar(50) not null, location_no int not null, foreign key(location_no) references location(no)");
		createTable("schedule",
				"no int primary key not null auto_increment, departure_location2_no int not null, arrival_location2_no int not null, date datetime not null, elapsed_time time not null, foreign key(departure_location2_no) references location2(no), foreign key(arrival_location2_no) references location2(no)");
		createTable("reservation",
				"no int primary key not null auto_increment, user_no int not null, schedule_no int not null, foreign key(user_no) references user(no), foreign key(schedule_no) references schedule(no)");
		createTable("recommend",
				"no int primary key not null auto_increment, location_no int not null, foreign key(location_no) references location(no)");
		createTable("recommend_info",
				"recommend_no int not null auto_increment, title varchar(50) not null, description varchar(200), img longblob not null, primary key(recommend_no, title), foreign key(recommend_no) references recommend(no) on delete cascade on update cascade");

		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("update recommend_info set img = ? where title = ? and recommend_no = ?");
			ResultSet rs = stmt.executeQuery(
					"select ri.recommend_no, l.name, ri.title, description, ri.img from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and r.location_no = l.no order by recommend_no asc");

			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("부산", "busan");
			hashMap.put("강원도", "gangwondo");
			hashMap.put("광주", "gyeongju");
			hashMap.put("전라남도", "Jeollanam-do");
			hashMap.put("서울", "seoul");

			while (rs.next()) {
				preparedStatement.setObject(1, new FileInputStream(new File(
						"지급파일/images/recommend/" + hashMap.get(rs.getString(2)) + "/" + rs.getString(3) + ".jpg")));
				preparedStatement.setObject(2, rs.getString(3));
				preparedStatement.setObject(3, rs.getString(1));
				preparedStatement.execute();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		execute("create view a as select l2.no, concat(l.name, ' ', l2.name) as name from location2 l2, location l where l2.location_no = l.no");
	}

	public static void main(String[] args) {
		new DB();
	}

}
