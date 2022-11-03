package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Notice extends Baseframe {
	JPanel mainp = new JPanel(new BorderLayout());
	JPanel n = new JPanel(new BorderLayout()), c = new JPanel(new BorderLayout());
	JPanel n_w = new JPanel(new FlowLayout(0)), n_e = new JPanel(new FlowLayout(2)), c_w = new JPanel(new GridLayout()),
			c_e = new JPanel();
	JLabel lbl[] = { new JLabel("페이지 정보 : "), new JLabel("1"), new JLabel(" / "), new JLabel("5"), new JLabel("검색 :") };
	JButton btn[] = { new JButton("◀"), new JButton("▶"), new JButton("게시물 작성") };
	JComboBox com = new JComboBox("제목,아이디,내용".split(","));
	JTextField txt = new JTextField(15);
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = new JTable(m);
	JScrollPane jsc = new JScrollPane(t);
	int cnt = 0;
	String sql = " and n_title like '%" + txt.getText() + "%' ";

	public Notice() {
		super("알림", 750, 400);

		add(mainp);
		mainp.add(n, "North");
		mainp.add(c, "Center");
		n.add(n_w, "West");
		n.add(n_e, "East");
		c.add(c_e, "East");
		c.add(c_w, "Center");

		for (int i = 0; i < 4; i++) {
			n_w.add(lbl[i]);
		}
		n_w.add(btn[0]);
		n_w.add(btn[1]);
		n_e.add(lbl[4]);
		n_e.add(com);
		n_e.add(txt);
		n_e.add(btn[2]);
		c_w.add(jsc);
		c.setBorder(new LineBorder(Color.BLACK));

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		t.getColumnModel().getColumn(1).setMinWidth(350);
		t.getColumnModel().getColumn(1).setMaxWidth(350);
		t.getColumnModel().getColumn(0).setMinWidth(60);
		t.getColumnModel().getColumn(0).setMaxWidth(60);
		t.getColumnModel().getColumn(4).setMinWidth(60);
		t.getColumnModel().getColumn(4).setMaxWidth(60);

		t.setRowHeight(25);
		
		line(jsc);

		emp(c, 5, 5, 5, 0);

		for (int i = 0; i < btn.length; i++) {
			btn[i].addActionListener(a -> {
				t.removeAll();
				t.getColumnModel().getColumn(1).setMinWidth(350);
				t.getColumnModel().getColumn(1).setMaxWidth(350);
				c_e.removeAll();
				if (a.getSource().equals(btn[0])) {
					if (cnt != 0)
						cnt--;
					lbl[1].setText(cnt + 1 + "");
					addRow();
				}
				if (a.getSource().equals(btn[1])) {
					if (cnt+1 != toint(lbl[3].getText())) 
						cnt++;
					lbl[1].setText(cnt + 1 + "");
					addRow();
				}
				if (a.getSource().equals(btn[2])) {
					com.setSelectedIndex(0);
					txt.setText("");
					sql = " and n_title like '%" + txt.getText() + "%' ";
					addRow();
					new AddNotice().addWindowListener(new be(Notice.this));
				}
				repaint();
				revalidate();
			});
		}
		
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == e.VK_ENTER) {
					addRow();
				}
			}
		});
		t.addMouseListener(new MouseAdapter() {
			boolean bool = false;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				c_e.removeAll();
				
				if(e.getClickCount() == 2) {
					t.getColumnModel().getColumn(1).setMinWidth(200);
					t.getColumnModel().getColumn(1).setMaxWidth(200);
					try {
						if(!bool) {
							ResultSet rs = stmt.executeQuery("select n_content from notice where n_title = '"+t.getValueAt(t.getSelectedRow(), 1)+"'");
							while(rs.next()) {
								Accumulate ac = new Accumulate(t.getValueAt(t.getSelectedRow(), 1), rs.getString(1));
								size(ac, 250, 300);
								c_e.add(ac);
								repaint();
								revalidate();
							}
						}else {
							c_e.removeAll();
							repaint();
							revalidate();
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					if(t.getValueAt(t.getSelectedRow(), 2).equals(ID)) {
						t.setSelectionForeground(Color.BLUE);
					}else {
						t.setSelectionForeground(Color.BLACK);
						execute("update notice set n_viewcount = n_viewcount+1 where n_title = '"
								+ t.getValueAt(t.getSelectedRow(), 1) + "'");
					}
					repaint();
					revalidate();
				}
			}
		});
		
		addRow();

		setVisible(true);
	}
	
	class Accumulate extends JPanel {
		JPanel mainp = new JPanel(new BorderLayout());
		JPanel n = new JPanel(new FlowLayout(0)), c = new JPanel(new GridLayout()), s = new JPanel(new FlowLayout(2));
		JLabel lbl[] = { label("제목 : ", 2, 12), new JLabel(), label("작성일", 4, 10), new JLabel(LocalDate.now() + "") };
		JTextArea txt = new JTextArea();
		Object 제목;
		String 설명;
		
		public Accumulate(Object 제목, String 설명) {
			setLayout(new BorderLayout());
			this.제목 = 제목;
			this.설명 = 설명;

			setUI();
		}
		
		void setUI() {
			add(mainp);
			mainp.add(n, "North");
			mainp.add(c, "Center");
			mainp.add(s, "South");

			n.add(lbl[0]);
			n.add(lbl[1]);
			c.add(txt);
			s.add(lbl[2]);
			s.add(lbl[3]);

			lbl[1].setText(제목 + "");
			txt.setText(설명);
			txt.setLineWrap(true);
			txt.setBorder(new LineBorder(Color.black));
			txt.setEditable(false);
		}
	}

	void addRow() {
		m.setRowCount(0);
		
		if (com.getSelectedIndex() == 0) {
			sql = " and n_title like '%" + txt.getText() + "%' ";
		} else if (com.getSelectedIndex() == 1) {
			sql = " and u_id like '%" + txt.getText() + "%' ";
		} else {
			sql = " and n_content like '%" + txt.getText() + "%' ";
		}
		
		try {
			ResultSet rs = stmt.executeQuery(
					"SELECT n_title, u_id, n_date, n_viewcount FROM notice n, user u where n.u_no = u.u_no and n_open = 'O'"
							+ sql + "order by n_date asc limit " + (cnt * 10) + ",10");
			int incre = 1;
			while (rs.next()) {
				Object row[] = new Object[5];
				row[0] = (cnt * 10) + incre;
				for (int j = 1; j < row.length; j++) {
					row[j] = rs.getString(j);
				}
				m.addRow(row);
				incre++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("SELECT n.n_no, n_title, u_id, n_date, n_viewcount FROM notice n, user u where n.u_no = u.u_no and n_open = 'O'"
				+ sql + "order by n_date asc");
		String cnt = (getLast("SELECT n.n_no, n_title, u_id, n_date, n_viewcount FROM notice n, user u where n.u_no = u.u_no and n_open = 'O'"
				+ sql + "order by n_date asc") / 10==0?1:getLast("SELECT n.n_no, n_title, u_id, n_date, n_viewcount FROM notice n, user u where n.u_no = u.u_no and n_open = 'O'"
						+ sql + "order by n_date asc") / 10+1)+"";
		lbl[3].setText(cnt);
	}

	public static void main(String[] args) {
		NO = 1;
		ID = "room1";
		new Notice();
	}

}
