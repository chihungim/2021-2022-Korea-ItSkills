import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;

public class Shop extends Basedialog {
	JTabbedPane tab = new JTabbedPane();
	JPanel ta = new JPanel(new FlowLayout(1));

	//추가
	String strDim[] = null;
	ArrayList<Object> orderList;

	
	
	public Shop(int _id, String... _strDim) throws SQLException {
		super(_strDim[0], 1200, 700);
		
		//초기화
		shop = this;
		strDim = _strDim;
		orderList = new ArrayList(); 
		
		//UI 배치1
		String _str1 = "<html><p color=\"white\"><font size=6>" + strDim[0] + "</font><br><font size=4>" + strDim[1] + " / " + strDim[2] + "-"  + strDim[3] + " / " + strDim[4] + "</font><br><font size=3>" + strDim[5] + "</font></p><br>";
		np.setLayout(null);
		np.add(jl1=new JLabel(img("./배경/" + _id + ".png", getWidth(), 250)));

		size(np, getWidth(), 250);
		jl1.setBounds(0, 0, getWidth(), 250);
		jl1.setText(_str1);
		jl1.setHorizontalAlignment(JLabel.LEFT);  	//★★★
		jl1.setVerticalTextPosition(JLabel.BOTTOM);	//★★★
		jl1.setIconTextGap(20-getWidth());			//★★★
		
		np.add(jl2=label("뒤로 가기", JLabel.LEFT, 14, 0, 0));
		jl2.setForeground(Color.WHITE);
		jl2.setBounds(20,20,100,20);
		np.setComponentZOrder(jl2, 0);
		
		np.add(jl3=label("주문표 (0)", JLabel.RIGHT, 14, 0, 0));
		jl3.setForeground(Color.WHITE);
		jl3.setBounds(getWidth()-200,20,170,20);
		np.setComponentZOrder(jl3, 0);

		np.add(jl4=label("♡", JLabel.RIGHT, 20, 0, 0));
		jl4.setForeground(Color.RED);
		jl4.setBounds(getWidth()-80,200,50,20);
		np.setComponentZOrder(jl4, 0);
		
		emp(jl1, 0, 0, 0, 0);
		
		//주문표 이벤트
		jl3.addMouseListener(this);
		
		//UI 배치2: 메뉴 탭
		add(tab);
		tab.addTab("메뉴", jsp=new JScrollPane(cp));
		jsp.setAutoscrolls(true);
		cp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		String _str2="", _str3="", _str4="", _str5=""; _str1="";
		String _qry = "select * from seller s, menu m, `type` t where s.no=m.seller and m.type=t.no and s.name='" + _strDim[0] + "' order by m.type;";
		rs = stmt.executeQuery(_qry);
		while(rs.next()) {
			_str2=rs.getString("t.name");
			if(_str1.equals("") || !_str1.equals(_str2)) {
				cp.add(jl5 = label(rs.getString("t.name"), JLabel.LEFT, 20, getWidth(), 30));
				cp.setPreferredSize(new Dimension(getWidth()-50, jl5.getLocation().y + jl5.getHeight() + 50));
			}
			_str3="<font size=5>" + rs.getString("t.name") + "</font>";
			if(rs.getString("m.description").length()>15)
				_str4=rs.getString("m.description").substring(0, 15) + "<br>" + rs.getString("m.description").substring(15) + "<br>";
			else
				_str4=rs.getString("m.description") + "<br><br>";

			_str5="<html>" + _str3 + "<br><font size=3>" + _str4 + "<br><br>" + (new DecimalFormat("#,##0원").format(rs.getInt("m.price"))) + LocalTime.parse(rs.getString("m.cooktime")).getMinute() + "분 소요";  
			cp.add(jl6 = new MyLabel(_str5, getWidth(), 120, "./메뉴/" + rs.getInt("m.no") + ".png", 120, 120, 2, null));
			
			jl6.setName(rs.getString("m.no"));
			jl6.setPreferredSize(new Dimension(getWidth()/3-20, 120));
			jl6.setBorder(new MatteBorder(2, 2, 2, 2, Color.GREEN));
			
			jl6.setHorizontalAlignment(JLabel.RIGHT);
			jl6.setHorizontalTextPosition(JLabel.RIGHT);
			jl6.setVerticalTextPosition(JLabel.TOP);
			jl6.setIconTextGap(-375);
			
			jl6.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(e.getClickCount()==2) {
						String _menuno = ((JLabel)e.getSource()).getName();
						try {
							new OrderAdd_menuOption(Integer.parseInt(_menuno));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			
			setVisible(true);
			cp.setPreferredSize(new Dimension(getWidth()-50, jl6.getLocation().y + jl6.getHeight() + 50));
			
			_str1=_str2;
		}
		
		//UI 배치3: 리뷰 탭
		tab.addTab("리뷰", ta);
		
		setVisible(true);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource().equals(jl3)) {
			new OrderTable_Shoplist();
		}
	}
	
	public static void main(String[] args) {
		try {
			new Shop(24, new String[] {"MC도날드", "1900", "1", "25", "0", "$ - American - Fast Food - Burgers"});
		} catch (SQLException e) {
		}
	}
}