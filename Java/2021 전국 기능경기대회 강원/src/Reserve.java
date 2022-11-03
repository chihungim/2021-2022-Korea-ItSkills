import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseDialog {
	DefaultTableModel m = model("no,출발지,도착지,도착시간,출발날짜,date".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);
	JLabel title;
	JPopupMenu menu = new JPopupMenu();
	JMenuItem item = new JMenuItem("취소");

	public Reserve() {
		super("예매 조회", 650, 550);

		UI();
		event();
		setVisible(true);
	}

	void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					menu.show(t, e.getX(), e.getY());
				}
			}
		});

		item.addActionListener(e -> {
			String date = (String) t.getValueAt(t.getSelectedRow(), 4);
			if (LocalDate.parse(date.split(" ")[0]).toEpochDay() < LocalDate.now().toEpochDay()) {
				emsg = new eMsg("취소 불가능한 일정입니다.");
				return;
			}

			imsg = new iMsg("예매 취소가 완료되었습니다.");
			execute("delete from reservation where no = " + t.getValueAt(t.getSelectedRow(), 5));
			m.setNumRows(0);
			rowData();
		});
	}

	void rowData() {
		try {
			var rs = stmt.executeQuery(
					"select a.name, b.name, date_add(date, interval elapsed_time HOUR_SECOND), date, r.no from reservation r inner join schedule s on r.schedule_no = s.no inner join a on s.departure_location2_no = a.no inner join a as b on s.arrival_location2_no = b.no where user_no = "
							+ u_no + " order by date asc");
			int i = 1;
			while (rs.next()) {
				Object row[] = { i, rs.getString(1), rs.getString(2), rs.getString(3).split(" ")[1], rs.getString(4),
						rs.getString(5) };
				m.addRow(row);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void UI() {
		setLayout(null);

		setBounds(title = lbl("예매조회", 2, 25), 20, 30, 100, 30);
		setBounds(jsc, 20, 70, 600, 400);

		t.getColumnModel().getColumn(0).setMinWidth(80);
		t.getColumnModel().getColumn(0).setMaxWidth(80);
		t.getColumnModel().getColumn(5).setMinWidth(0);
		t.getColumnModel().getColumn(5).setMaxWidth(0);

		rowData();

		menu.add(item);
	}

	public static void main(String[] args) {
		u_no = 1;
		new Reserve();
	}
}
