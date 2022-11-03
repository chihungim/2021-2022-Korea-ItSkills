package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseFrame {
	DefaultTableModel m = model("pno,날짜,여유좌석,d".split(","));
	JTable t = new JTable(m);
	JLabel lbl;
	String date;
	String pno;

	public static void main(String[] args) {
		u_no = 1;
		new Reserve("1");
	}

	public Reserve(String pno) {
		super("예매", 600, 300);
		this.pno = pno;
		try {
			System.out.println("select * from perform where p_no=" + pno);
			var rs = stmt.executeQuery("select * from perform where p_no=" + pno);
			if (rs.next()) {
				for (int i = 0; i < pinfo.length; i++) {
					pinfo[i] = rs.getString(i + 1);
					System.out.println(rs.getString(i + 1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		date = pinfo[6];
		addRow("select p.p_no, date_format(p.p_date, '%m. %d.'), 60 - count(*), p.p_date from perform p, ticket t where p.p_no=t.p_no and p.p_name like '%"
				+ pinfo[2] + "' group by p.p_no", m);
		add(lbl(pinfo[2], JLabel.LEFT, 20), "North");
		var c = new JPanel(new BorderLayout(20, 20));
		var e = new JPanel(new BorderLayout());
		var s = new JPanel(new FlowLayout(2));

		add(c);
		add(sz(e, 200, 1), "East");
		add(s, "South");

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);
		t.getColumnModel().getColumn(3).setMinWidth(0);
		t.getColumnModel().getColumn(3).setMaxWidth(0);

		c.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		c.add(new JLabel(getIcon("./Datafiles/공연사진/" + pinfo[1] + ".jpg", 120, 150)), "West");
		c.add(lbl = new JLabel("<html><left>장소 : " + pinfo[3] + "<br><br>출연 : " + pinfo[5] + "<br><br>가격 : "
				+ df.format(toInt(pinfo[4])) + "<br><br>날짜 : " + date));

		e.add(setB(new JScrollPane(t), new LineBorder(Color.BLACK)));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				try {
					var rs = stmt
							.executeQuery("select * from perform where p_no=" + t.getValueAt(t.getSelectedRow(), 0));
					if (rs.next()) {
						for (int i = 0; i < pinfo.length; i++) {
							pinfo[i] = rs.getString(i + 1);
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				date = t.getValueAt(t.getSelectedRow(), 3).toString();
				lbl.setText("<html><left>장소 : " + pinfo[3] + "<br><br>출연 : " + pinfo[5] + "<br><br>가격 : "
						+ df.format(toInt(pinfo[4])) + "<br><br>날짜 : " + date);
				repaint();
				revalidate();

				super.mousePressed(e);
			}
		});

		s.add(sz(btn("예매하기", a -> {
			if (u_no == 0) {
				int yn = JOptionPane.showConfirmDialog(null, "회원만이 가능한 서비스 입니다. \n 로그인 하시겠습니까?", "로그인",
						JOptionPane.YES_NO_OPTION);
				if (yn == JOptionPane.YES_OPTION) {
					new Login().addWindowListener(new before(this));
					return;
				}
			}

			new Stage().addWindowListener(new before(this));
		}), 150, 30));
		setVisible(true);
	}
}
