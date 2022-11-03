package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class intro extends Baseframe {
	JPanel n, c, c_n, c_s;
	JLabel jl, img, title;
	
	public intro() {
		super("지점소개", 600, 400);
		
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		
		c.add(c_n = new JPanel(new BorderLayout()), "North");
		c.add(c_s = new JPanel(new GridLayout(1, 0)), "South");
		
		n.add(title = label("지점 소개", 0, 35));
		n.add(label("B R A N C H  O F F I C E", 0, 15), "South");
		
		c_n.add(jl = label("| "+cNAME, JLabel.LEFT,15), "West");
		jl.setForeground(Color.ORANGE);
		c_n.add(btn("테마 보러가기", a->{
			new Theme().addWindowListener(new be(intro.this));
		}), "East");
		
		
		System.out.println(cfNAME);
		c.add(img = new JLabel(img("지점/"+cfNAME+".jpg", 500, 200)));
		
		try {
			ResultSet rs = stmt.executeQuery("select c_address, c_tel from cafe where c_name = '"+cNAME+"'");
			JLabel j, j1;
			if(rs.next()) {
				c_s.add(j = label(rs.getString(1), 2, 13));
				c_s.add(j1 = label(rs.getString(2).replace("-", "."), 0, 13));
				j.setForeground(Color.LIGHT_GRAY);
				j1.setForeground(Color.LIGHT_GRAY);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		emp(c, 5, 5, 5, 5);
		
		c_n.setOpaque(false);
		c_s.setOpaque(false);
		c.setBackground(Color.BLACK);
		
		setVisible(true);
	}

}
