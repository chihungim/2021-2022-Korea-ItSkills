

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Orderlist extends Basedialog {
	DefaultTableModel dtm = new DefaultTableModel(null, "주문번호,주문일시,주문금액,음식점,결제수단,주문상태".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	JTable table = new JTable(dtm);
	JScrollPane scr = new JScrollPane(table);
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem("주문 취소");
	
	public Orderlist() {
		super("주문내역", 800, 400);
		
		add(scr);
		
		pop.add(item);
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					int row = table.rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(row, row);
					pop.show(table, e.getX(), e.getY());
				}
			}
		});
		
		item.addActionListener(a->{
			if(table.getValueAt(table.getSelectedRow(), 5).equals("결제완료")) {
				msg("주문이 취소되었습니다.");
				execute("delete from receipt where no = "+toint(table.getValueAt(table.getSelectedRow(), 0)));
				setTable();
			}else {
				errmsg("결제 이후의 상태에서는 취소할 수 없습니다.");
				return;
			}
		});
		
		setTable();
		
		setVisible(true);
	}
	
	private void setTable() {
		dtm.setRowCount(0);
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT r.no, r.RECEIPT_TIME, format(r.price,'#,##0'), s.name, p.issuer, if(status=0,'결제완료',if(status=1,'조리완료',if(status=2,'배달중','배달완료'))) as stat FROM receipt r, seller s, payment p where r.user = "+NO+" and r.PAYMENT = p.no and r.seller = s.no");
			while(rs.next()) {
				Object row[] = new Object[rs.getMetaData().getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i+1);
				}
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NO = 1;
		new Orderlist();
	}

}
