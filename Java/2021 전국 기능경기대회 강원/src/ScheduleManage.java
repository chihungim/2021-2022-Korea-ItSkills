import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ScheduleManage extends JPanel {
	DefaultTableModel m = BaseFrame.model("순번,출발지,도착지,출발날짜,이동시간".split(","));
	JTable t = BaseFrame.table(m);
	JScrollPane jsc = new JScrollPane(t);

	JPanel p1 = new JPanel(new GridLayout(0, 1)), p2 = new JPanel(new GridLayout(0, 1)),
			menuP = new JPanel(new GridLayout(1, 0));
	JPopupMenu menu = new JPopupMenu();
	JScrollPane menuJsc = new JScrollPane(p1);
	JScrollPane menuJsc2 = new JScrollPane(p2);
	JButton btn[] = new JButton[16], btn2[];

	int count = -1;
	boolean bool;

	public ScheduleManage() {

		UI();
		data();
		event();
	}

	void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3 && (t.getSelectedColumn() == 1 || t.getSelectedColumn() == 2)) {
					count = t.getSelectedColumn();
					menu.show(t, e.getX(), e.getY());
				}
			}
		});

		for (int i = 0; i < btn.length; i++) {
			int j = i;
			btn[i].addActionListener(a -> {
				p2.removeAll();
				try {
					ResultSet rs1 = BaseFrame.stmt
							.executeQuery("select * from location2 where location_no = " + (j + 1));
					rs1.last();
					btn2 = new JButton[rs1.getRow()];
					rs1.beforeFirst();
					int k = 0;
					while (rs1.next()) {
						p2.add(btn2[k] = new JButton(rs1.getString(2)));
						btn2[k].addActionListener(a2 -> {
							JButton jj = (JButton) a2.getSource();
							String aa = btn[j].getText() + " " + jj.getText();
							t.setValueAt(aa, t.getSelectedRow(), count);
							bool = true;
						});
						k++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				repaint();
				revalidate();
			});
		}

		rowData();
	}

	void rowData() {
		m.setRowCount(0);

		try {
			var rs = BaseFrame.stmt.executeQuery(
					"SELECT s.no, a.name, b.name, date, elapsed_time FROM busticketbooking.schedule s inner join a on s.departure_location2_no = a.no inner join a b on s.arrival_location2_no = b.no");
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void data() {
		try {
			var rs = BaseFrame.stmt.executeQuery("select * from location");
			int i = 0;
			while (rs.next()) {
				p1.add(btn[i] = new JButton(rs.getString("name")));
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void UI() {
		setLayout(null);

		menu.add(menuP);
		menuP.add(menuJsc);
		menuP.add(menuJsc2);

		BaseDialog.size(menu, 240, 280);

		add(BaseFrame.lbl("일정 관리", 2, 20)).setBounds(10, 20, 130, 30);
		add(jsc).setBounds(10, 60, 940, 430);
		add(BaseFrame.btn("저장", e -> save())).setBounds(670, 500, 130, 30);
		add(BaseFrame.btn("삭제", e -> delete())).setBounds(820, 500, 130, 30);
	}

	void delete() { 
		BaseFrame.imsg = new BaseFrame.iMsg("삭제를 완료하였습니다.");
		BaseFrame.execute("delete from schedule where no = " + BaseFrame.toInt(t.getValueAt(t.getSelectedRow(), 0)));
	}

	void save() {
		if (bool) {
			BaseFrame.imsg = new BaseFrame.iMsg("수정내용을 저장 완료하였습니다.");
			for (int i = 0; i < t.getRowCount(); i++) {
				int depart = BaseFrame
						.toInt(BaseFrame.getOne("select no from location2 where name = '" + t.getValueAt(i, 1) + "'"));
				int arrive = BaseFrame
						.toInt(BaseFrame.getOne("select no from location2 where name = '" + t.getValueAt(i, 2) + "'"));
				BaseFrame.execute("update schedule set departure_location2_no = " + depart + ", arrival_location2_no = "
						+ arrive + " where no = " + BaseFrame.toInt(t.getValueAt(i, 0)));
			}
		}
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}
