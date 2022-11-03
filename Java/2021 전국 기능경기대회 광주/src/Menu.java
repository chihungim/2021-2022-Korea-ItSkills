

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Menu extends Basedialog {
	DefaultTableModel dtm = model("번호,이름,설명,가격,조리시간".split(","));
	JTable table = new JTable(dtm);
	JScrollPane scr = new JScrollPane(table);
	JPanel n = new JPanel(new BorderLayout()), c = new JPanel(new BorderLayout()), s = new JPanel(new FlowLayout(2));
	String str[] = "추가,수정".split(",");
	PlaceH tt = new PlaceH(20);
	JButton jb[] = new JButton[2];
	
	public Menu() {
		super("메뉴관리", 800, 450);
		
		add(n,"North");
		add(c);
		add(s,"South");
		
		n.add(tt);
		tt.setPlace("검색할 이름명을 입력해주세요.");
		n.add(btn("검색", a->{
			search();
		}),"East");
		
		c.add(scr);
		
		for (int i = 0; i < jb.length; i++) {
			s.add(jb[i] = btn(str[i], a->{
				if(a.getSource().equals(jb[0])) {
					new Modify().addWindowListener(new be(this));
				}
				if(a.getSource().equals(jb[1])) {
					if(table.getSelectedRow() == -1) {
						errmsg("수정할 메뉴를 선택해야 합니다.");
						return;
					}
					new Modify(toint(table.getValueAt(table.getSelectedRow(), 0))).addWindowListener(new be(this));
				}
			}));
		}
		
		search();
		
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.getColumnModel().getColumn(j).setCellRenderer(dtcr);
		}
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		
		emp((JPanel)getContentPane(), 10, 10, 10, 10);
		emp(c, 10, 0, 20, 0);
		
		c.setOpaque(false);
		s.setOpaque(false);
		n.setOpaque(false);
		
		setVisible(true);
	}
	
	private void search() {
		dtm.setRowCount(0);
		
		try {
			ResultSet rs = stmt.executeQuery("select no, name, description, format(price,'#,##0'),cooktime from menu where seller = "+NO+" and name like '%"+tt.getText()+"%'");
			while(rs.next()) {
				Object row[] = new Object[5];
				for (int j = 0; j < row.length; j++) {
					row[j] = rs.getString(j+1);
				}
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NO = 1;
		new Menu();
	}

}
