package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Mypage extends BasePage {
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;

	DefaultTableModel m = model("포스터,제목,일시,인원,가격,리뷰,c_no,m_no,r_no,r_seat".split(","));
	JTable t = table(m);

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			var jc = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 3)
				jc.setToolTipText(m.getValueAt(row, 9).toString());
			return jc;
		};
	};

	public Mypage() {
		super("Mypage");
		setLayout(new BorderLayout(0, 20));
		add(n = new JPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		n.add(new JLabel(rndImg("./지급자료/image/user/" + BaseFrame.uno + ".jpg", 100, 100)));
		n.add(nc = new JPanel(new GridLayout(0, 1, 5, 5)));
		var user = getrows("select * from user where u_no = ?", BaseFrame.uno).get(0);
		nc.add(lbl(user.get(3) + "", JLabel.LEFT, 20));
		nc.add(lbl(user.get(4) + " " + "남,여".split(",")[cint(user.get(5)) - 1], JLabel.LEFT, 15));

		nc.add(lbl(grade[cint(user.get(6))], JLabel.LEFT));

		add(scrollPane(t));
		t.setRowHeight(80);

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		t.getColumn("인원").setCellRenderer(dtcr);

		String col[] = "포스터,인원,가격,c_no,r_no,m_no,r_seat".split(",");
		int sz[] = { 60, 60, 120, 0, 0, 0, 0 };
		t.getTableHeader().setBackground(red);
		t.getTableHeader().setForeground(Color.WHITE);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int r = t.getSelectedRow();
				if (r == -1)
					return;
				var pop = new JPopupMenu();
				var item1 = new JMenuItem("리뷰 작성");
				var item2 = new JMenuItem("삭제");

				item1.addActionListener(a -> new Comment(t.getValueAt(r, 7).toString(), Mypage.this).setVisible(true));
				item2.addActionListener(a -> {
					var ld = LocalDateTime.parse(t.getValueAt(r, 2).toString(),
							DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
					if (ld.isBefore(LocalDateTime.now())) {
						emsg("이미 상영된 영화 입니다.");
						return;
					}

					execute("delete from reservation where r_no = ?", t.getValueAt(r, 8));
					execute("delete from comment where c_no = ?", t.getValueAt(r, 6));
					data();
				});

				if (e.getButton() == 3) {
					if (t.getValueAt(t.getSelectedRow(), 5).toString().equals("-"))
						pop.add(item1);
					pop.add(item2);
					pop.show(t, e.getX(), e.getY());
				}
				pop.setBorder(new LineBorder(Color.LIGHT_GRAY));
			}
		});

		for (int i = 0; i < col.length; i++) {
			t.getColumn(col[i]).setMinWidth(sz[i]);
			t.getColumn(col[i]).setMaxWidth(sz[i]);
		}
		data();
		setBorder(new EmptyBorder(50, 50, 50, 50));
	}

	void data() {
		var rs = getrows(
				"select m.m_no, m.m_title, r_date, r_time, r_seat, r_price, ifnull(c.c_text, '-'), c.c_no, m.m_no, r.r_no, r.r_seat from movie m, reservation r left outer join comment c on r.u_no =c.u_no and r.m_no = c.m_no where r.m_no = m.m_no and r.u_no = ? order by r.r_date, r.r_time ",
				BaseFrame.uno);

		for (var r : rs) {
			r.set(0, new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 45, 65)));
			r.set(2, LocalDateTime
					.of(LocalDate.parse(r.get(2) + "", DateTimeFormatter.ofPattern("yyyy-MM-dd")),
							LocalTime.parse(r.get(3) + "", DateTimeFormatter.ofPattern("H:mm")))
					.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")));
			r.remove(3);
			r.set(3, r.get(3).toString().split("\\.").length + "명");
		}

		addrow(rs, m);

	}
}
