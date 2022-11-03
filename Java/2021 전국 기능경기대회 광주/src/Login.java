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

public class Login extends Basedialog {
	String str1[] = "이메일 주소 또는 휴대폰번호로 로그인하세요.,비밀번호를 입력하고 로그인하세요.".split(",");
	String str2[] = "이 이메일 또는 휴대폰을 찾을 수 없습니다.".split(",");
	
	int level;
	
	public Login() {
		super("로그인", 450, 300);
		
		phText = "이메일 주소 또는 휴대폰번호;대소문자, 숫자, 특수문자를 포함한 8자 이상".split(";");
		
		np.setLayout(new FlowLayout(FlowLayout.LEFT));
		size(np, getWidth()-20, 50);
		np.add(jl1 = label("<html><font size=6>돌아오신것을 환영합니다.</font>", 2, 30, getWidth()-20, 50));
		
		cp.setLayout(new GridLayout(3,1));
		cp.add(jl2 = label(str1[0], 2, 13, 0, 0));
		cp.add(jtf1 = textPH(0, 15, 0, 0));
		cp.add(jl3 = label("", 2, 13, 0, 0));
		jl3.setForeground(Color.RED);
		
		sp.setLayout(new GridLayout(2, 1));
		sp.add(jb1=new JButton("로그인"));
	
		sp.add(jp1 = new JPanel());
		jp1.add(label("기능배달이 처음이십니까?", JLabel.RIGHT, 10, 150, 20));
		jp1.add(jl4=labelB("계정 만들기", JLabel.CENTER, 12));
		
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
			if(level<1) {
				try {
					ResultSet rs = stmt.executeQuery("Select * from (SELECT email, pw, phone, '구매자' kind FROM `user` Union SELECT email, pw, phone, '판매자' kind FROM seller  Union SELECT email, pw, phone, '라이더' kind FROM rider) as sub where sub.email='" + jtf1.getText() + "' or phone='" + jtf1.getText() + "'");
					if(rs.next()) {
						jl2.setText(str1[1]);
						jtf1.setText(phText[1]);
						jtf1.setName(1+"");
						jtf1.setForeground(Color.LIGHT_GRAY);
					}
					else {
						jl3.setText(str2[level]);
					}
				} catch (SQLException e1) {
				}
			} else {
				try {
					ResultSet rs = stmt.executeQuery("Select * from (SELECT email, pw, phone, '구매자' kind FROM `user` Union SELECT email, pw, phone, '판매자' kind FROM seller Union SELECT email, pw, phone, '라이더' kind FROM rider) as sub where sub.pw='" + jtf1.getText() + "'");
					if(rs.next()) {
						if(rs.getString(3).equals("구매자")) {
							new Mainf().addWindowListener(new be(Login.this));
						}else if(rs.getString(3).equals("판매자")) {
							new Seller().addWindowListener(new be(Login.this));
						}else {
							new Delivery().addWindowListener(new be(Login.this));
						}
					}else {
						errmsg("입력한 비밀번호가 올바르지 않습니다.");
						return;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		new Sign().addWindowListener(new be(Login.this));
	}

	public static void main(String[] args) {
		new Login();
	}
}
