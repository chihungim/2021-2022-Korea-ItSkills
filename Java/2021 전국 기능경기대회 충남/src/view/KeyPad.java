package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;

import db.DBManager;
import view.BaseFrame.Before;

public class KeyPad extends BaseFrame {
	
	ArrayList<Integer> number = new ArrayList<Integer>();
	{
		for (int i = 0; i < 10; i++) {
			number.add(i);
		}
	}
	JTextField txt = new JTextField();
	JButton btn[] = new JButton[12];
	
	public KeyPad() {
		super("예매번호 확인", 350, 400);
		
		this.add(n = new JPanel(new GridLayout(0, 1, 10, 10)), "North");
		this.add(c = new JPanel(new GridLayout(0, 3, 10, 10)));
		
		n.add(txt);
		n.add(btn("확인", a->{
			if(txt.getText().isEmpty()) {
				eMsg("예매번호를 입력해야 합니다.");
				return;
			}
			try {	
				System.out.println("select * from purchase where user = "+uno+" and serial = '"+txt.getText()+"'");
				var rs = DBManager.rs("select * from purchase where user = "+uno+" and serial = '"+txt.getText()+"'");
				if(rs.next()) {
					new Ticket(rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getString(7), rs.getString(6), rs.getInt(8));
//					new Ticket(rs.getInt(3),rs.getInt(4), LocalDate.parse(rs.getString(7)),LocalTime.parse(rs.getString(6)),rs.getDouble(8),rs.getInt(5)).addWindowListener(new Before(this));
				}else {
					eMsg("예매번호가 일치하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}));
		
		for (int i = 0; i < btn.length; i++) {
			c.add(btn[i] = new JButton());
			btn[i].setForeground(Color.black);
			btn[i].setBackground(Color.white);
			
			btn[i].addActionListener(a->{
				if (a.getActionCommand().contentEquals("←")) {
					try {
						txt.setText(txt.getText(0, txt.getText().length()-1));
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (a.getActionCommand().contentEquals("재배치")) {
					shuffle();
				} else {
					txt.setText(txt.getText()+a.getActionCommand());
				}
			});
			
		}
		txt.setHorizontalAlignment(SwingConstants.CENTER);
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume(); 
			}
		});
		
		btn[9].setText("←");
		btn[11].setText("재배치");
		
		shuffle();
		
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}
		});
		
		setEmpty(c, 10, 0, 0, 0);
		setEmpty((JPanel)getContentPane(), 20, 20, 20, 20);
		this.setVisible(true);
	}

	void shuffle() {
		Collections.shuffle(number);
		for (int i = 0; i < 9; i++) {
			btn[i].setText(number.get(i)+"");
		}
		
		btn[10].setText(number.get(9)+"");
	}
	
	public static void main(String[] args) {
		uno="1";
		new KeyPad();
	}
	
}
