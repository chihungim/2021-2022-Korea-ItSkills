package 전남;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

public class Search extends Baseframe {
	JPanel n, n_s, c, c_w, c_c;
	DefaultTableModel m = new DefaultTableModel(null, "지역".split(",")) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};;
	JTable t;
	JScrollPane scr1;
	JScrollPane scr2;
	JTextField txt;
	String[] bcap = "평점 높은 순,리뷰 많은 순,1인 가능,난이도 5".split(",");
	ArrayList<String> nam = new ArrayList<String>();
	JToggleButton tog[] = new JToggleButton[bcap.length];
	ButtonGroup bg = new ButtonGroup();

	public Search() {
		super("검색", 750, 450);
		setLayout(new BorderLayout(5, 5));
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(c_w = new JPanel(new BorderLayout()), "West");
		c.add(scr1 = new JScrollPane(c_c = new JPanel(new GridLayout(0, 3, 5, 5))));
		n.add(label("방탈출 카페 검색", JLabel.LEFT, 30));
		n.add(n_s = new JPanel(new FlowLayout(FlowLayout.LEFT)), "South");
		c_w.add(size(scr2 = new JScrollPane(t = new JTable(m)), 80, 400), "West");

		for (int i = 0; i < bcap.length; i++) {
			n_s.add(tog[i] = new JToggleButton(bcap[i]));
			bg.add(tog[i]);
			tog[i].addActionListener(a->{
				String sql ="";
				String str = (t.getValueAt(t.getSelectedRow(), 0).equals("전국")?"":t.getValueAt(t.getSelectedRow(), 0).toString());
				if(a.getSource().equals(tog[0])) {
					sql = "select c.c_name from evaluation e, cafe c where e.c_no = c.c_no and c_name like '%"+txt.getText()+"%' and left(c_address,2) like '%"+str+"%' group by c.c_no order by e.e_score desc";
				}
				if(a.getSource().equals(tog[1])) {
					sql = "select c_name from notice n, cafe c, reservation r where c.c_no = r.c_no and n.u_no = r.u_no and r.r_date <= n.n_date and c_name like '%"+txt.getText()+"%' and left(c_address,2) like '%"+str+"%' group by c.c_no";
				}
				if(a.getSource().equals(tog[2])) {
					sql = "select c_name from notice n, cafe c, reservation r where c.c_no = r.c_no and n.u_no =r.u_no and r.r_people = 1 and c_name like '%"+txt.getText()+"%' and left(c_address,2) like '%"+str+"%' group by c.c_no";
				}
				if(a.getSource().equals(tog[3])) {
					sql ="select c_name from theme t, cafe c, reservation r where c.c_no = r.c_no and t.t_no = r.t_no and t.t_difficulty = 5 and c_name like '%"+txt.getText()+"%' and left(c_address,2) like '%"+str+"%' group by c.c_no"; 
				}
				System.out.println(sql);
				search(sql);
			});
		}
		
		tog[0].setSelected(true);

		n_s.add(size(new JLabel(" "), 150, 20));
		n_s.add(txt = new JTextField(10));
		n_s.add(btn("검색", a -> {
			search(txt.getText(), 0);
		}));
		
		m.addRow(new Object[] { "전국", });
		addRow("select left(c_address, 2) from cafe group by left(c_address, 2)", m, 1);
		
		t.setRowSelectionInterval(0, 0);

		emp(((JPanel) getContentPane()), 5, 5, 5, 5);

		search(txt.getText(), 0);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				search(t.getValueAt(t.getSelectedRow(), 0) + "", 1);
			}
		});

		setVisible(true);
	}

	private void search(String str, int i) {
		c_c.removeAll();
		str = str.equals("전국")?"":str;
		
		try {
			ResultSet rs = stmt.executeQuery("select c_name from cafe where" + (i==0?" c_name like '%"+str+"%'":" left(c_address,2) like '%"+str+"%'"));
			while(rs.next()) {
				String name = rs.getString(1);
				JPanel temp = new JPanel(new BorderLayout());
				temp.add(new JLabel(img("지점/"+rs.getString(1).split(" ")[0]+".jpg", 180, 180)));
				temp.add(label(rs.getString(1), 0),"South");
				line(temp);
				temp.setName(rs.getString(1));
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							cNAME  = ((JPanel)e.getSource()).getName();
							cfNAME = name.split(" ")[0];
							new intro().addWindowListener(new be(Search.this));
						}
					}
				});
				c_c.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		repaint();
		revalidate();
	}
	private void search(String sql) {
		c_c.removeAll();
		
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String name = rs.getString(1);
				JPanel temp = new JPanel(new BorderLayout());
				temp.add(new JLabel(img("지점/"+rs.getString(1).split(" ")[0]+".jpg", 180, 180)));
				temp.add(label(rs.getString(1), 0),"South");
				line(temp);
				temp.setName(rs.getString(1));
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							cNAME  = ((JPanel)e.getSource()).getName();
							cfNAME = name.split(" ")[0];
							new intro().addWindowListener(new be(Search.this));
						}
					}
				});
				c_c.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		repaint();
		revalidate();
	}
	
	public static void main(String[] args) {
		NO = 1;
		new Search();
	}

}
