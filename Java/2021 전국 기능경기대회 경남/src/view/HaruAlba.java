
package view;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HaruAlba extends BaseFrame {

	DefaultTableModel m = new DefaultTableModel(null, ",접수일자,기업명,모집제목,급여,마감일".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	JTable t = new JTable(m);
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();

	public HaruAlba() {
		super("하루알바", 800, 500);

		this.add(n = new JPanel(), "North");
		this.add(c = new JPanel(new BorderLayout()));

		n.add(lbl(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(LocalDate.now()), 0, 25));

		c.add(new JScrollPane(t));

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		int widths[] = { 0, 50, 80, 200, 70, 100 };
		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		t.setRowHeight(30);
		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		try {
			var rs = stmt.executeQuery(
					"SELECT r.r_no, r.date, c.name, r.title, r.salary, r.deadline FROM recruitment r, company c where r.c_no=c.c_no and date = curdate()");
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}

			if (m.getRowCount() == 0) {
				eMsg("오늘 하루알바는 없습니다.");
				this.dispose();
				return;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setEmpty((JPanel) getContentPane(), 10, 10, 0, 10);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new HaruAlba();
	}

}