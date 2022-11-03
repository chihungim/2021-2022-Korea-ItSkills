

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Review extends Basedialog {
	DefaultTableModel dtm = model("주문번호,주문일시,주문금액,음식점,리뷰,rno".split(","));
	JTable table = new JTable(dtm);
	JScrollPane scr = new JScrollPane(table);
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem("리뷰수정");

	public Review() {
		super("리뷰", 700, 400);

		add(scr);

		pop.add(item);

		try {
			ResultSet rs = stmt.executeQuery(
					"SELECT r.no,RECEIPT_TIME, format(price,'#,##0'),s.name,ifnull(ri.title,''), s.no FROM receipt r left join review ri on r.no = ri.RECEIPT, seller s where r.seller= s.no and status = 3 and r.user = "+NO);
			while (rs.next()) {
				Object row[] = new Object[5];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		
		table.getColumnModel().getColumn(5).setMinWidth(0);
		table.getColumnModel().getColumn(5).setMaxWidth(0);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					int row = table.rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(row, row);
					if(table.getValueAt(table.getSelectedRow(), 4).equals("")) {
						item.setText("리뷰등록");
					}else{
						item.setText("리뷰수정");
					}
					pop.show(table, e.getX(), e.getY());
				}
			}
		});
		
		item.addActionListener(a->{
			if(item.getText().equals("리뷰등록")) {
				new Write("등록",toint(table.getValueAt(table.getSelectedRow(), 0)),toint(table.getValueAt(table.getSelectedRow(), 5))).addWindowListener(new be(this));
			}else {
				new Write("수정",toint(table.getValueAt(table.getSelectedRow(), 0)),toint(table.getValueAt(table.getSelectedRow(), 5))).addWindowListener(new be(this));
			}
		});
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		NO = 1;
		new Review();
	}

}
