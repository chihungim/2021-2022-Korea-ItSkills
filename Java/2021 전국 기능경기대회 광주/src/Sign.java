import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Sign extends Basedialog {
	String str1[] = "전화번호,이메일,비밀번호,이름,주소,배달 수수료".split(",");
	String str2[] = "이 번호의 계정이 이미 있습니다.,이 이메일의 계정이 이미 있습니다.,,,,".split(",");
	String str3[] = {"^[0-9]{3}-[0-9]{4}-[0-9]{4}$", "^(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{3,}@(outlook|daum|naver|gmail).(net|com|kr)$", "^(?=.*[0-9])(?=.*[~`!@#$%^&*()-])(?=.*[a-zA-Z]).{8,12}$", "", "", "^[0-9]+$"};
	
	int level;
	
	public Sign() {
		super("회원가입", 450, 300);
		
		phText = "000-0000-0000;example@example.net;대소문자, 숫자, 특수기호를 포함한 8자 이상;홍길동;클릭하여 주소를 입력하기;000".split(";");
		
		np.setLayout(new FlowLayout(FlowLayout.LEFT));
		size(np, getWidth()-20, 50);
		np.add(jl1 = label("<html><font size=6>시작하기.</font>", 2, 30, getWidth()-20, 50));
		
		cp.setLayout(new GridLayout(3,1));
		cp.add(jl2 = label(str1[0], 2, 13, 0, 0));
		cp.add(jtf1 = textPH(0, 15, 0, 0));
		cp.add(jl3 = label("", 2, 13, 0, 0));
		jl3.setForeground(Color.RED);
		
		sp.setLayout(new GridLayout(2, 1));
		sp.add(jb1=new JButton("다음"));
	
		sp.add(jp1 = new JPanel());
		jp1.add(label("이미 기능배달 회원이십니까?", JLabel.RIGHT, 10, 150, 20));
		jp1.add(jl4=labelB("로그인", JLabel.CENTER, 12));
		
		jl4.setForeground(Color.GREEN.darker().darker());
		jl4.setBorder(new MatteBorder(0, 0, 1, 0, green));
		
		emp((JPanel)getContentPane(), 10, 10, 10, 10);
		emp(cp, 10, 0, 0, 0);
		emp(sp, 30, 0, 0, 0);
		
		//이벤트
		jb1.addActionListener(this);
		jl4.addMouseListener(this);
		
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(jb1)) {
			if(level<6) {
				//빈 칸 또는 플레이스 홀더 기본값 검사
				if (jtf1.getText().equals("") || jtf1.getText().equals(phText[level])) {
					jl3.setText(str1[level] + "을 입력해주세요.");
					return;
				}
				
				//정규식 검사
				if ((level<3 || level==5) && !jtf1.getText().matches(str3[level])) {
					System.out.println();
					jl3.setText(str1[level] + " 형식이 올바르지 않습니다.");
					return;
				}
				
				//전화번호, 이메일 존재 여부 검사
				if(level<2) {
					try {
						ResultSet rs = stmt.executeQuery("Select * from (SELECT email, pw, phone, '구매자' kind FROM `user` Union SELECT email, pw, phone, '판매자' kind FROM seller  Union SELECT email, pw, phone, '라이더' kind FROM rider) as sub where sub.email='" + jtf1.getText() + "' or phone='" + jtf1.getText() + "'");
						if(rs.next()) {
							jl3.setText(str2[level]);
							return;
						}
					} catch (SQLException e1) {
					}
				}
				
				level++;
			}
			
			//다음 단계 또는 로그인 폼으로 이동
			if(level<6) {
				jl2.setText(str1[level]);
				jtf1.setText(phText[level]);
				jtf1.setName(level+"");
				jtf1.setForeground(Color.LIGHT_GRAY);
				
				jl3.setText("");
			} else {
				new Login();
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		new Login();
	}

	public static void main(String[] args) {
		new Sign();
	}
}
