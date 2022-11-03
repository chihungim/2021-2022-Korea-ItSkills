

import java.awt.FlowLayout;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Delivery extends Basedialog {
	DefaultTableModel dtm = model("주문번호,주문시각,음식점위치,배달위치".split(","));
	JTable table = new JTable(dtm);
	JScrollPane scr= new JScrollPane(table);
	JPanel s = new JPanel(new FlowLayout(0));
	JButton jb[] = new JButton[2];
	String str[] = "새로운 배달 받기,배달 출발".split(",");
	ArrayList<Integer> del = new ArrayList<Integer>();
	ArrayList<Integer> cho = new ArrayList<Integer>();
	
	public Delivery() {
		super("딜리버리", 700, 500);
		
		add(labelB("내 배달", 2, 15),"North");
		add(scr);
		add(s,"South");
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		
		for (int i = 0; i < jb.length; i++) {
			s.add(jb[i] = btn(str[i], a->{
				if(a.getSource().equals(jb[0])) {
					randirandom();
				}
				if(a.getSource().equals(jb[1])) {
					int rno = toint(table.getValueAt(table.getSelectedRow(), 0).toString());
					Point sp = new Point(toint(table.getValueAt(table.getSelectedRow(), 2).toString().split(",")[0]),toint(table.getValueAt(table.getSelectedRow(), 2).toString().split(",")[1]));
					Point up = new Point(toint(table.getValueAt(table.getSelectedRow(), 3).toString().split(",")[0]),toint(table.getValueAt(table.getSelectedRow(), 3).toString().split(",")[1]));
					Point rp = null;
					
					try {
						ResultSet rs = stmt.executeQuery("select m.x, m.y from map m, rider r where m.no = r.map and r.no = '"+NO+"'");
						if(rs.next()) {
							rp = new Point(rs.getInt(1), rs.getInt(2));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					new Map(this,up,rp,sp,rno).addWindowListener(new be(this));
				}
			}));
		}
		
		set();
		
		setVisible(true);
	}
	
	void set() {
		try {
			ResultSet rs = stmt.executeQuery("select * from delivery d, receipt r where d.receipt = r.no and d.rider = '"+NO+"' and r.status = 2");
			while(rs.next()) {
				del.add(rs.getInt(5));
				cho.add(rs.getInt(5));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(Integer no : del) {
			try {
				ResultSet rs = stmt.executeQuery("select r.no, r.receipt_time, m.x, m.y, u.us, u.uy from receipt r, map m, seller s,(select r.no rno, m.x us, m.y uy from map m, receipt r, user u where m.no = u.map and r.user = u.no) u where r.seller = s.no and m.no = s.map and r.no = u.rno and r.status = 2 and r.no = "+no);
				while(rs.next()) {
					Object row[] = new Object[dtm.getColumnCount()];
					row[0] = rs.getInt(1);
					row[1] = rs.getString(2);
					row[2] = rs.getString(3) + ","+rs.getString(4);
					row[3] = rs.getString(5) + ","+rs.getString(6);
					dtm.addRow(row);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ResultSet rs = stmt.executeQuery("select r.no, r.receipt_time, m.x, m.y, u.us, u.uy from receipt r, map m, seller s,(select r.no rno, m.x us, m.y uy from map m, receipt r, user u where m.no = u.map and r.user = u.no) u where r.seller = s.no and m.no = s.map and r.no = u.rno and r.status = 1");
			while(rs.next()) {
				if(del.contains(rs.getInt(1))) continue;
				del.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void randirandom() {
		if (del.size() == 0) {
			errmsg("배정 가능한 주문건이 없습니다.");
			return;
		}

		Random rand = new Random();
		int no = del.get(rand.nextInt(del.size()));
		while (true) {
			if (cho.contains(no)) {
				no = del.get(rand.nextInt(del.size()));
			} else
				break;

			if (cho.size() == del.size()) {
				errmsg("배정 가능한 주문건이 없습니다.");
				return;
			}
		}

		cho.add(no);

		try {
			var rs = stmt.executeQuery("SELECT \r\n" + "    r.NO, r.RECEIPT_TIME, m.X, m.Y, u.us, u.uy\r\n" + "FROM\r\n"
					+ "    receipt r,\r\n" + "    map m,\r\n" + "    seller s,\r\n" + "    (SELECT \r\n"
					+ "        r.no NO, m.x us, m.y uy\r\n" + "    FROM\r\n" + "        map m, receipt r, user u\r\n"
					+ "    WHERE\r\n" + "        m.NO = u.MAP and r.USER = u.NO) u\r\n" + "WHERE\r\n"
					+ "    r.SELLER = s.NO AND m.NO = s.MAP and r.NO = u.NO and r.STATUS = 1 and r.no=" + no);
			Object row[] = new Object[dtm.getColumnCount()];

			if (rs.next()) {
				row[0] = rs.getInt(1);
				row[1] = rs.getString(2);
				row[2] = rs.getString(3) + "," + rs.getString(4);
				row[3] = rs.getString(5) + "," + rs.getString(6);
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		execute("update receipt set status = 2 where no = " + no);
	}

	public static void main(String[] args) {
		NO = 1;
		new Delivery();
	}

}
