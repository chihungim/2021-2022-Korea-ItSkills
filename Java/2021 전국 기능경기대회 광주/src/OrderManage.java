

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class OrderManage extends Basedialog {
	DefaultTableModel dtm = model("주문번호,주문일시,주문금액,주문상태".split(",")), dtm2 = model("번호,메뉴명,수량,옵션".split(","));
	JTable table = new JTable(dtm), table2 = new JTable(dtm2);
	JScrollPane scr = new JScrollPane(table), scr2 = new JScrollPane(table2);
	JPanel s = new JPanel(new BorderLayout());

	public OrderManage() {
		super("주문관리", 800, 600);
		
		add(scr);
		add(s,"South");
		
		s.add(scr2);
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
			table2.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(table.getValueAt(table.getSelectedRow(), 3).equals("결제완료")) {
					if(e.getClickCount() == 2) {
						int yn = JOptionPane.showConfirmDialog(null, "조리를 완료하셨습니까?","메시지",JOptionPane.YES_NO_OPTION);
						
						if(yn == JOptionPane.YES_OPTION) {
							execute("update receipt set status = 1 where no = '"+table.getValueAt(table.getSelectedRow(), 0)+"'");
						}
					}
				}
				setT(toint(table.getValueAt(table.getSelectedRow(), 0)));
			}
		});
		
		setTable();
		
		size(s, 0, 280);
		
		emp(s, 10, 0, 0, 0);
		emp((JPanel)getContentPane(), 10, 10, 10, 10);
		
		s.setOpaque(false);

		setVisible(true);
	}
	
	private void setTable() {
		dtm.setRowCount(0);
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT no, RECEIPT_TIME, format(price,'#,##0'), if(status=0,'결제완료',if(status=1,'조리완료',if(status=2,'배달중','배달완료'))) as stat FROM eats.receipt where seller = "+NO);
			while(rs.next()) {
				Object row[] = new Object[4];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i+1);
				}
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void setT(int no) {
		dtm2.setRowCount(0);
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT rd.no, m.name, rd.count, o.name FROM eats.receipt_options ro, receipt_detail rd, menu m, options o where ro.RECEIPT_DETAIL = rd.no and rd.menu = m.no and ro.OPTIONS = o.no and RECEIPT = "+no+" group by rd.no");
			while(rs.next()) {
				Object row[] = new Object[4];
				row[0] = rs.getInt(1);
				row[1] = rs.getString(2);
				row[2] = rs.getInt(3);
				row[3] = "";
				dtm2.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < table2.getRowCount(); i++) {
			try {
				System.out.println("select name from receipt_options r, options o where r.options = o.no and r.receipt_detail = '"+table2.getValueAt(i, 0)+"'");
				ResultSet rs = stmt.executeQuery("select name from receipt_options r, options o where r.options = o.no and r.receipt_detail = '"+table2.getValueAt(i, 0)+"'");
				String a = "";
				while(rs.next()) {
					a += ","+rs.getString(1);
				}
				table2.setValueAt(a.substring(1, a.length()), i, 3);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		NO = 3;
		new OrderManage();
	}

}
