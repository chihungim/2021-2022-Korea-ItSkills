package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
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

	DefaultTableModel m1 = model("번호,회원번호,영화제목,영화관,등급,포맷,인원수,좌석번호,상영날짜,상영시간".split(","));
	DefaultTableModel m2 = model("번호,회원번호,상품이름,상품정보,상품가격,구매날짜".split(","));

	JTable t = table(m1);

	JComboBox<String> combo = new JComboBox<String>("예매,스토어".split(","));

	JLabel lbl;
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();

	public MyPage() {
		super("마이페이지", 800, 400);
		setLayout(new BorderLayout(5, 5));
		add(n = newJPanel(new BorderLayout(5, 5)), "North");
		n.add(lbl = lbl("예매 내역", JLabel.LEFT, 20), "West");
		n.add(combo, "East");
		add(new JScrollPane(t));

		combo.addItemListener(i -> {
			if (combo.getSelectedIndex() == 0) {
				lbl.setText("예매 내역");
				t.setModel(m1);
			} else {
				t.setModel(m2);
				lbl.setText("스토어");
			}

			for (int j = 0; j < t.getColumnCount(); j++)
				t.getColumnModel().getColumn(j).setCellRenderer(dtcr);
		});

		evt(t, a -> {
			if (a.getButton() == 3) {
				var pop = new JPopupMenu();
				JMenuItem item = null;
				if (combo.getSelectedIndex() == 0) {
					item = new JMenuItem();
					var dt = LocalDateTime.parse(
							t.getValueAt(t.getSelectedRow(), 8) + " "
									+ t.getValueAt(t.getSelectedRow(), 9).toString().replaceAll("[\r\n]", ""),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
					if (dt.isAfter(LocalDateTime.now())) {
						item.setText("삭제");
					} else {
						item.setText("취소");
					}
					item.addActionListener(x -> {
						execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						data();
					});
				} else {
					item = new JMenuItem("삭제");
					item.addActionListener(x -> {
						execute("delete from orderlist where o_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						data();
					});
				}
				pop.add(item);
				if (t.getSelectedRow() == -1)
					return;

				pop.show(t, a.getX(), a.getY());
			}
		});

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		data();
		setVisible(true);
		combo.setBackground(Color.WHITE);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		mno = "1";
		new MyPage();
	}

	void data() {
		var sql1 = "SELECT \r\n" + "    r_no,\r\n" + "    u_no,\r\n" + "    m_name,\r\n" + "    t_name,\r\n"
				+ "    m_rating,\r\n" + "    p_name,\r\n" + "    r_people,\r\n" + "   r.r_seat, sc_date,\r\n"
				+ "    sc_time\r\n" + "FROM\r\n" + "    reservation r,\r\n" + "    schedule sc,\r\n"
				+ "    movie m,\r\n" + "    pomaes p,\r\n" + "    theater t\r\n" + "WHERE\r\n"
				+ "    sc.sc_no = r.sc_no AND m.m_no = sc.m_no\r\n" + "        AND sc.p_no = p.p_no\r\n"
				+ "        AND sc.t_no = t.t_no\r\n" + "        and r.u_no = ?\r\n"
				+ "order by sc_date asc, time(sc_time) asc ";
		addrow(getrows(sql1, mno), m1);
		var sql2 = "select o_no, u_no, s_name, s_explanation, s_price, o_date from orderlist o, store s where s.s_no = o.s_no and o.u_no = ? order by o_date asc";
		addrow(getrows(sql2, mno), m2);
	}
}
