import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	static Statement stmt;
	static Connection con;
	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
					"root", "1234");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	};

	public DB() {
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void createDB() {
		execute("drop database if exists eats");
		execute("create database eats");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("use eats");
		execute("grant update, delete, insert, select on eats.* to user@localhost");
		execute("set global local_infile=1");
	}

	void createTable(String n, String c) {
		execute("create table " + n + "(" + c + ")");
		execute("load data local infile './지급자료/" + n + ".txt' into table " + n
				+ " lines terminated by '\r\n' ignore 1 lines");
	}

	public static void main(String[] args) {
		DB db = new DB();
		db.createDB();
		db.createTable("Category", "NO int primary key not null auto_increment, NAME varchar(20)");
		db.createTable("Map", "NO int primary key not null auto_increment, X int, Y int, TYPE int");
		db.createTable("User",
				"NO int primary key not null auto_increment, EMAIL varchar(50), PHONE varchar(13), PW varchar(20), NAME varchar(40), MAP int, foreign key(MAP) references Map(NO) on delete cascade on update cascade");
		db.createTable("Seller",
				"NO int primary key not null auto_increment, EMAIL varchar(50), PHONE varchar(13), PW varchar(20), NAME varchar(40), ABOUT text, CATEGORY int, DELIVERYFEE int, MAP int, foreign key(CATEGORY) references Category(NO) on delete cascade on update cascade, foreign key(MAP) references Map(NO) on delete cascade on update cascade");
		db.createTable("Rider",
				"NO int primary key not null auto_increment, EMAIL varchar(50), PHONE varchar(13), PW varchar(20), NAME varchar(40), MAP int, foreign key(MAP) references Map(NO) on delete cascade on update cascade");
		db.createTable("Payment",
				"NO int primary key not null auto_increment, ISSUER varchar(40), CARD varchar(16), CVV varchar(4), PW varchar(6), USER int, foreign key(USER) references User(NO) on delete cascade on update cascade");
		db.createTable("Type",
				"NO int primary key not null auto_increment, NAME varchar(40), SELLER int, foreign key(SELLER) references Seller(NO) on delete cascade on update cascade");
		db.createTable("Menu",
				"NO int primary key not null auto_increment, NAME varchar(100), DESCRIPTION text, PRICE int, COOKTIME time, SELLER int, TYPE int, foreign key(SELLER) references Seller(NO) on delete cascade on update cascade, foreign key(TYPE) references Type(NO) on delete cascade on update cascade");
		db.createTable("Options",
				"NO int primary key not null auto_increment, TITLE varchar(50), NAME varchar(50), PRICE int, MENU int, foreign key(MENU) references Menu(NO) on delete cascade on update cascade");
		db.createTable("Favorite",
				"NO int primary key not null auto_increment, SELLER int, USER int, foreign key(SELLER) references Seller(NO) on delete cascade on update cascade, foreign key(USER) references User(NO) on delete cascade on update cascade");
		db.createTable("Receipt",
				"NO int primary key not null auto_increment, PRICE int, RECEIPT_TIME datetime, SELLER int, USER int, PAYMENT int, STATUS int, foreign key(SELLER) references Seller(NO) on delete cascade on update cascade, foreign key(USER) references User(NO) on delete cascade on update cascade, foreign key(PAYMENT) references Payment(NO) on delete cascade on update cascade");
		db.createTable("Receipt_Detail",
				"NO int primary key not null auto_increment, MENU int, COUNT int, PRICE int, RECEIPT int, foreign key(MENU) references Menu(NO) on delete cascade on update cascade, foreign key(RECEIPT) references Receipt(NO) on delete cascade on update cascade");
		db.createTable("Receipt_Options",
				"NO int primary key not null auto_increment, OPTIONS int, PRICE int, RECEIPT_DETAIL int, foreign key(OPTIONS) references Options(NO) on delete cascade on update cascade, foreign key(RECEIPT_DETAIL) references Receipt_Detail(NO) on delete cascade on update cascade");
		db.createTable("Delivery",
				"NO int primary key not null auto_increment, RIDER int, RECEIPT int, START_TIME datetime, foreign key(RIDER) references Rider(NO) on delete cascade on update cascade, foreign key(RECEIPT) references Receipt(NO) on delete cascade on update cascade");
		db.createTable("Review",
				"NO int primary key not null auto_increment, TITLE varchar(30), CONTENT text, RATE int, USER int, SELLER int, RECEIPT int, foreign key(USER) references User(NO) on delete cascade on update cascade, foreign key(SELLER) references Seller(NO) on delete cascade on update cascade, foreign key(RECEIPT) references Receipt(NO) on delete cascade on update cascade");
	}

}
