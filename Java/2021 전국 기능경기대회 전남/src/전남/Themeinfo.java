package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Themeinfo extends Baseframe {
	JPanel mainp = new JPanel(new BorderLayout());
	JPanel n = new JPanel(new BorderLayout()), c = new JPanel(), e = new JPanel(new BorderLayout());
	JPanel en = new JPanel(new BorderLayout()), es =new JPanel(new GridLayout(0, 1)), ec = new JPanel(new BorderLayout());
	
	JLabel name;
	JTextArea intro;
	JLabel lbl[] = { label("", 0, 20), label("Theme Introduction", 0, 10) };
	JLabel imglbl = new JLabel();
	JLabel idx[] = { new JLabel("제한시간 : ", 2), new JLabel("난이도 : ", 2), new JLabel("추천 인원 : ", 2)};
	JButton btn = new JButton("예약하기");
	
	public Themeinfo() {
		super("테마 소개", 600, 500);
		
		String star = "";
		try {
			ResultSet rs = stmt.executeQuery("select t_time, t_difficulty, t_personnel from theme where t_name = '"+tNAME+"'");
			if(rs.next()) {
				idx[0].setText("제한시간 : "+rs.getInt(1)+"분");
				for (int i = 0; i < rs.getInt(2); i++) {
					star+="★";
				}
				idx[1].setText("난이도 : "+star);
				idx[2].setText("추천 인원 : "+rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		add(mainp);
		
		mainp.add(imglbl = new JLabel(img("테마/"+tNO+".jpg", 290, 400)),"West");
		mainp.add(n,"North");
		mainp.add(e);
		
		e.add(en,"North");
		e.add(ec);
		e.add(es,"South");
		
		en.add(name = label(tNAME, 2, 20));
		name.setForeground(Color.WHITE);
		en.add(btn,"East");
		
		for (int i = 0; i < idx.length; i++) {
			es.add(idx[i]);
			idx[i].setForeground(Color.WHITE);
		}
		
		ec.add(intro = new JTextArea());
		
		intro.setLineWrap(true);
		intro.setOpaque(false);
		intro.setText(getone("select t_explan from theme where t_no = "+tNO).replace(". ", ".\n\n"));
		intro.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		intro.setForeground(Color.WHITE);
		
		btn.addActionListener(a->{
			new Reserve().addWindowListener(new be(Themeinfo.this));
		});
		
		n.add(label("테마소개", 0, 30));
		n.add(label("Theme Introduction", 0,15),"South");
		
		e.setBackground(Color.BLACK);
		es.setOpaque(false);
		ec.setOpaque(false);
		en.setOpaque(false);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		tNAME = "404호 살인사건";
		tNO = 1;
		NO = 1;
		new Themeinfo();
	}

}
