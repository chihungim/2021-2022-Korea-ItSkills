package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db.DBManager;

public class UserManage extends BaseFrame {
	
	JTextField txt = new JTextField(20);
	DefaultTableModel model = new DefaultTableModel(null, "번호,아이디,비밀번호,이름,생일,휴대전화".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return !(column==0 || column==1);
		};
	};
	JTable t = table(model);
	HashMap<Integer, String> unos = new HashMap<Integer, String>();
	
	public UserManage() {
		super("회원 관리", 700, 400);
		
		this.add(n = new JPanel(), "North");
		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new FlowLayout(2)), "South");
		
		n.add(txt);
		n.add(btn("검색", a->{
			addRow();
		}));
		
		c.add(new JScrollPane(t));
		
		s.add(btn("저장", a->{
			if (unos.size()==0) {
				eMsg("변경사항이 없습니다.");
				return;
			}
			
			iMsg("변경사항이 수정되었습니다.");
			for (var n : unos.keySet()) {
				try (var pst = DBManager.con.prepareStatement("update user set pw = ?, name = ?, birth = ?, phone = ? where serial=?")) {
					for (int i = 1; i <= 4; i++) {
						pst.setObject(i, t.getValueAt(n, i+1));
					}
					pst.setObject(5, unos.get(n));
					pst.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			
		}));
		
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var c = t.columnAtPoint(e.getPoint());
				if ((c==0 || c==1) && e.getClickCount()==2) {
					uno = t.getValueAt(t.getSelectedRow(), 0).toString();
					new PurchaseList().addWindowListener(new Before(UserManage.this));
				}
			}
		});
		
		t.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				unos.put(t.getSelectedRow(), t.getValueAt(t.getSelectedRow(), 0)+"");
			}
		});
		addRow();
		
		this.setVisible(true);
	}
	
//	"update user set ? where uno=?"
	
	void addRow() {
		model.setRowCount(0);
		try {
			var rs = DBManager.rs("select * from user where name like '%"+txt.getText()+"%'");
			while (rs.next()) {
				Object row[] = new Object[model.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i+1);
				}
				model.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
