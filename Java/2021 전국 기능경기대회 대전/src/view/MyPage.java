package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {

	static DefaultTableModel m = model("tno,pno,날짜,공연명,좌석,금액".split(","));
	JTable t = new JTable(m);
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item[] = { new JMenuItem("취소"), new JMenuItem("수정") };

	public MyPage() {
		super("마이페이지", 600, 500);

		this.add(lbl("회원 : " + u_name, 2, 25), "North");
		this.add(new JScrollPane(t));

		set("select t_no, p.p_no, p_date, p_name, t_seat, t_discount, p_price from perform p, ticket t where p.p_no=t.p_no and t.u_no="
				+ u_no + " order by p_date", m);
		int widt[] = { 0, 0, 100, 200, 100, 100 };
		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
			t.getColumnModel().getColumn(i).setPreferredWidth(widt[i]);
		}

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);
		t.getColumnModel().getColumn(1).setMinWidth(0);
		t.getColumnModel().getColumn(1).setMaxWidth(0);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					var c = t.columnAtPoint(e.getPoint());
					var r = t.rowAtPoint(e.getPoint());

					t.changeSelection(r, c, false, false);
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		for (int i = 0; i < item.length; i++) {
			pop.add(item[i]);
			item[i].addActionListener(a -> {
				int r = t.getSelectedRow();
				if (a.getActionCommand().equals("취소")) {
					iMsg("취소되었습니다.");
					execute("delete from ticket where t_no ='" + t.getValueAt(r, 0) + "'");
					set("select t_no, p.p_no, p_date, p_name, t_seat, t_discount, p_price from perform p, ticket t where p.p_no=t.p_no and t.u_no="
							+ u_no + " order by p_date", m);
					repaint();
					revalidate();
				} else {
					if (LocalDate.parse(t.getValueAt(r, 2).toString()).isBefore(LocalDate.now())) {
						eMsg("수정이 불가합니다.");
						return;
					}
					try {
						var rs = stmt.executeQuery(
								"select * from perform where p_no=" + t.getValueAt(t.getSelectedRow(), 1));
						if (rs.next()) {
							for (int j = 0; j < pinfo.length; j++) {
								pinfo[j] = rs.getString(j + 1);
							}
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new Stage(t.getValueAt(r, 0).toString()).addWindowListener(new before(this));
				}

			});
		}

		setB((JPanel) getContentPane(), new EmptyBorder(10, 10, 10, 10));
		this.setVisible(true);
	}

	static void refresh() {
		set("select t_no, p.p_no, p_date, p_name, t_seat, t_discount, p_price from perform p, ticket t where p.p_no=t.p_no and t.u_no="
				+ u_no + " order by p_date", m);
	}

	static void set(String sql, DefaultTableModel m) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int p = rs.getInt(7);
				var dis = rs.getString(6).split(",");
				int sum = 0;
				for (int i = 0; i < dis.length; i++) {
					double d = (dis[i].equals("0") ? 1 : dis[i].equals("1") ? 0.8 : dis[i].equals("2") ? 0.6 : 0.5);
					sum += (p * d);
				}
				m.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), df.format(sum) });
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		u_no = 1;
		u_name = "강찬석";
		new MyPage();
	}

}
