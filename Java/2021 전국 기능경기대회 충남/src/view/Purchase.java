package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import db.DBManager;
import view.Reserve.ItemPanel;

public class Purchase extends BaseFrame {
	
	JTextField txt[] = {
		new JTextField(8),	
		new JTextField(3),	
		new JTextField(3),	
		new JTextField(3),	
		new JTextField(3),	
		new JTextField(3),	
		new JTextField(5),	
	};
	ArrayList<Integer> list;
	int rand=0, tot = 1200;
	LocalTime start, end;
	JPanel cc = new JPanel(new GridLayout(0, 1));
	int puno;
	
	public Purchase(ItemPanel item, Reserve r) {
		super("결제", 750, 400);
		
		this.add(w = new JPanel(new GridLayout(0, 1, 0, 10)), "West");
		this.add(c = new JPanel(new BorderLayout()));
		
		w.setBackground(blue);
		sz(w, 250, 1);
		c.setBackground(Color.white);
		
		tot = tot < r.tot * 5 ? r.tot * 5 : tot;
		tot = rei(uage) <= 13 ? (int) (tot * 0.9) : tot;
		tot = rei(uage) >= 65 ? (int) (tot * 0.5) : tot;
		
		start =LocalTime.of(item.st/3600, (item.st%3600)/item.st/60, (item.st%60));
		
		String str1 = "<html><p style='color:white'>";
		str1 += "<font size=+3>결제 정보</font><br><br>";
		str1 += "예매 구간:<br>";
		str1 += "<b><font size =+1>" + stNames.get(r.path.get(0))+"<br>"+stNames.get(r.path.size()-1)+"<br>구간</font></b><br><br>";
		str1 += "탑승 시간:<br>";
		str1 += "<b><font size=+1>" + r.date.toString()+"<br>"+start+"</font></b><br><br>";
		str1 += "총 결제 금액:<br>";
		str1 += "<b><font size=+1>"+iFormat(tot)+"원</font></b>";
		str1 += "</p>";
		
		JLabel info = new JLabel(str1);
		info.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		w.add(info);
		setEmpty(w, 10, 10, 10, 10);

		JPanel cn = new JPanel(new GridLayout(0, 1));
		cn.add(new JLabel(img("logo.png", 180, 30), 2));
		cn.add(lbl("Seoul Metro Ticket", 2, 20));
		c.add(cn, "North");
		cn.setOpaque(false);
		c.add(cc);
		
		
		JPanel in = new JPanel(new FlowLayout(0));
		in.add(lbl("안녕하세요, "+uname+"님.", 2, 15));
		cc.add(in);
		
		JPanel in1 = new JPanel(new FlowLayout(0));
		in1.add(lbl("탑승권자 이름은 ", 2, 15));
		in1.add(txt[0]);
		in1.add(lbl("이고,", 2, 15));
		cc.add(in1);
		
		JPanel in2 = new JPanel(new FlowLayout(0));
		in2.add(lbl("카드번호는 ", 2, 15));
		for (int i = 1; i < 5; i++) {
			in2.add(txt[i]);
		}
		in2.add(lbl("이고,", 2, 15));
		cc.add(in2);
		
		JPanel in3 = new JPanel(new FlowLayout(0));
		in3.add(lbl("CVC는 ", 2, 15));
		in3.add(txt[5]);
		in3.add(lbl("카드 비밀번호는 ", 2, 15));
		in3.add(txt[6]);
		in3.add(lbl("입니다.", 2, 15));
		cc.add(in3);
		
		setEmpty(c, 10, 10, 10, 10);
		setEmpty(cc, 20, 0, 20, 0);
		
		setNum();
		
		c.add(btn("결제하기", a->{
			int yn = JOptionPane.showConfirmDialog(null, "구매하시겠습니까?", "메세지", JOptionPane.YES_NO_OPTION);
			if (yn == JOptionPane.YES_OPTION) {
				for (int i = 0; i < txt.length; i++) {
					if (txt[i].getText().isEmpty()) {
						eMsg("모든 항목을 입력해야 합니다.");
						return;
					}
				}
				
				for (int i = 1; i < 5; i++) {
					if (!(txt[i].getText().matches(".*[0-9].*") && txt[i].getText().length()==4)) {
						eMsg("카드 번호는 각 4자리 숫자로 구성해야 합니다.");
						return;
					}
				}
				
				String cv="";
				for (int i = 1; i < 4; i++) {
					cv += txt[i].getText().substring(0, 1);
				}
				
				if (!txt[5].getText().equals(cv)) {
					eMsg("CVC 코드가 일치하지 않습니다.");
					return;
				}
				
				if (!txt[6].getText().equals(ubirth)) {
					eMsg("카드 비밀번호가 일치하지 않습니다.");
					return;
				}
				
//				String code = new DecimalFormat("000000").format(puno);
//				System.out.println("insert into purchase values('"+code+"','"+uno+"','"+r.path.get(0)+"','"+r.path.get(r.path.size()-1)+"','"+tot+"','"+start+"','"+r.date+"','"+r.tot+"')");
				DBManager.execute("insert into purchase values('"+puno+"','"+uno+"','"+r.path.get(0)+"','"+r.path.get(r.path.size()-1)+"','"+tot+"','"+start+"','"+r.date+"','"+r.tot+"')");
				iMsg("결제가 완료되었습니다!\n예매번호:"+puno);
				dispose();
				
			}
		}), "South");
		
		cc.setOpaque(false);
		in.setOpaque(false);
		in1.setOpaque(false);
		in2.setOpaque(false);
		in3.setOpaque(false);
		
		Arrays.stream(txt).forEach(t->{
			t.setHorizontalAlignment(SwingConstants.CENTER);
			t.setFont(new Font("맑은 고딕", Font.BOLD, 15));
			t.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		});
		
		this.setVisible(true);
	}
	
	void setNum() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			var rs = DBManager.rs("select * from purchase");
			while (rs.next()) {
				list.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (true) {
			int ran = (int)(Math.random()*1000000);
			if (list.contains(ran)) {
				continue;
			} else {
				puno = ran;
				return;
			}
		}
	}
	
}
