package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Theme extends Baseframe {
	JPanel n, c;
	JScrollPane scr;
	
	public Theme() {
		super("테마목록", 450, 600);
		
		add(n = new JPanel(new BorderLayout()), "North");
		add(scr = new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		
		n.add(label(cNAME.split(" ")[0] + " 테마목록", 0, 35));
		n.add(label("T H E M E  L I S T", 0, 15), "South");
		
		c.setBackground(Color.BLACK);
		
		String themelist[] = getone("select c_division from cafe where c_name = '"+cNAME+"'").split(",");
		
		for (int i = 0; i < themelist.length; i++) {
			try {
				ResultSet rs = stmt.executeQuery("select t_no, t_name from theme where t_no = '"+themelist[i]+"'");
				if(rs.next()) {
					JPanel temp = new JPanel(new BorderLayout()), s = new JPanel(new FlowLayout(1));
					
					temp.setName(rs.getString(2));
					temp.add(new JLabel(img("테마/"+rs.getInt(1)+".jpg", 320, 400)));
					temp.add(s,"South");
					JLabel jl;
					s.add(jl = label(rs.getString(2), 0, 15));
					jl.setForeground(Color.WHITE);
					String t = rs.getString(2);
					int tno = rs.getInt(1);
					s.add(btn("테마소개", a->{
						tNO = tno;
						tNAME = t;
						new Themeinfo().addWindowListener(new be(this));
					}));
					
					temp.setOpaque(false);
					s.setOpaque(false);
					
					c.add(temp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		setVisible(true);
	}

}
