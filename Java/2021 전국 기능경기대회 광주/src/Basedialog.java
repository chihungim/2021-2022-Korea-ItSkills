

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Basedialog extends JDialog implements ActionListener, MouseListener, KeyListener {
	static Statement stmt;
	static Connection con;
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	static int NO, sNO;
	static String NAME, TYPE;
	Color green = Color.GREEN.darker().darker();
	DecimalFormat format = new DecimalFormat("#,##0");
	static ArrayList<ArrayList<Object>> order = new ArrayList<ArrayList<Object>>();
	static boolean isPur;
	
	JPanel ns = new JPanel(new FlowLayout(1));

	//추가 
	static Shop shop = null;
	JPanel cp = new JPanel(new BorderLayout()), np = new JPanel(new BorderLayout()), wp = new JPanel(new BorderLayout()), ep = new JPanel(new BorderLayout()), sp = new JPanel(new BorderLayout());
	JPanel jp1, jp2, jp3;
	JScrollPane jsp;
	JTextField jtf1, jtf2, jtf3;
	JButton jb1, jb2;
	JLabel jl1, jl2, jl3, jl4, jl5, jl6, jl7, jl8, jlDim[];
	JRadioButton jrb1, jrb2, jrb3;
	
	static ArrayList<String> catList = new ArrayList<String>();
	static ArrayList<String> selList = new ArrayList<String>();
	static Object menuInfoDim[][], optionDim[][];
	ResultSet rs;
	
	String phText[];

	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?allowLoadLocalInfile=true&serverTimezone=UTC&allowPublicKeyRetrieval=true","user","1234");
			stmt = con.createStatement();
			stmt.execute("use eats");
			dtcr.setHorizontalAlignment(dtcr.CENTER);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Basedialog(String _title, int _w, int _h) {
		setTitle(_title);
		setSize(_w, _h);
		setModal(false);
		setIconImage(img("logo.png").getImage());
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		
		//추가
		add(cp); add(np, "North"); add(wp, "West"); add(ep, "East"); add(sp, "South");
		cp.setBackground(Color.WHITE);	np.setBackground(Color.WHITE);	wp.setBackground(Color.WHITE);	ep.setBackground(Color.WHITE);	sp.setBackground(Color.WHITE);
//		setLayout(new BorderLayout());
	}
	
	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void errmsg(String str) {
		JOptionPane.showMessageDialog(null, str, "메시지", JOptionPane.ERROR_MESSAGE);
	}
	
	void msg(String str) {
		JOptionPane.showMessageDialog(null, str, "메시지", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	DefaultTableModel model(String str[]) {
		DefaultTableModel dtm = new DefaultTableModel(null, str) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		return dtm;
	}
	
	
	JLabel label(String str, int alig) {
		JLabel jl = new JLabel(str, alig);
		return jl;
	}
	
	
	JLabel labelB(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("맑은 고딕", Font.BOLD, size));
		return jl;
	}
	
	JLabel labelP(String str, int alig, int size) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("맑은 고딕", Font.PLAIN, size));
		return jl;
	}
	
	JButton button(String _str, int _w, int _h, Color c) {
		JButton _jb = new JButton(_str);
		_jb.setPreferredSize(new Dimension(_w, _h));
		_jb.setBackground(c);
		
		return _jb;
	}
	
	//추가
	JLabel label(String str, int alig, int size, int w, int h) {
		JLabel jl = new JLabel(str, alig);
		jl.setFont(new Font("맑은 고딕", Font.PLAIN, size));
		jl.setPreferredSize(new Dimension(w, h));
		return jl;
	}
	
	void dataInit()  throws SQLException {
		//카테고리 리스트
		catList.clear();
		rs = stmt.executeQuery("select name from category");
		catList.add("");
		while(rs.next()) {
			catList.add(rs.getString(1));
		}
		
		//판매자 리스트
		selList.clear();
		rs = stmt.executeQuery("SELECT name FROM seller");
		selList.add("");
		while(rs.next()) {
			selList.add(rs.getString(1));
		}
		
		//옵션 리스트: 옵션ID, 타이틀, 이름, 가격, 메뉴ID
		rs = stmt.executeQuery("SELECT * FROM options");
		rs.last();
		optionDim = new Object[rs.getRow()+1][5];
		rs.beforeFirst();

		int _idx=1;
		while(rs.next()) {
			optionDim[_idx++]= new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5)};
		}
		
		//메뉴 정보: 메뉴ID, 메뉴명, 타입, 셀러ID, 음식점, 가격, 설명, 조리시간
		rs = stmt.executeQuery("Select * from seller s, menu m, `type` t where s.no=m.seller and m.type=t.no");
		rs.last();
		menuInfoDim = new Object[rs.getRow()+1][8];
		rs.beforeFirst();
		
		_idx=1;
		while(rs.next()) {
			menuInfoDim[_idx++]= new Object[]{rs.getString("m.no"), rs.getString("m.name"), rs.getString("t.name"), rs.getString("s.no"), rs.getString("s.name"), rs.getInt("m.price"), rs.getString("m.description"), rs.getString("m.cooktime")};
		}
	}
	
	JTextField textPH(int _idx, int _col, int _w, int _h) {
		JTextField _jtf = new JTextField(phText[_idx], _col);
		_jtf.setName(_idx + "");
		_jtf.setPreferredSize(new Dimension(_w, _h));
		_jtf.setForeground(Color.LIGHT_GRAY);
		
		_jtf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(_jtf.getText().equals(phText[toint(_jtf.getName())])) _jtf.setText("");
				_jtf.setForeground(Color.BLACK);
			}
		});
		
		_jtf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(!_jtf.getText().equals("") && _jtf.getText().substring(1).equals(phText[toint(_jtf.getName())]))
					_jtf.setText(e.getKeyChar() + "");
				
				if(!_jtf.getText().equals("") && !_jtf.getText().equals(phText[toint(_jtf.getName())]))
					_jtf.setForeground(Color.BLACK);
			}
		});
		
		_jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(_jtf.getText().equals(phText[Integer.parseInt(_jtf.getName())]) || _jtf.getText().equals("")) {
					_jtf.setText(phText[Integer.parseInt(_jtf.getName())]);
					_jtf.setForeground(Color.LIGHT_GRAY);
				}
			}
		});

		return _jtf;
	}
	
	JComponent addCom(JComponent c, int x, int y, int w, int h) {
		c.setBounds(x, y, w, h);
		return c;
	}
	
	JComponent addCom(JPanel p, JComponent c, int x, int y, int w, int h) {
		p.add(c);
		c.setBounds(x, y, w, h);
		return c;
	}
	
	static <T extends JComponent>T size(T c, int w, int h){
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}
	
	JButton btn(String str, ActionListener a) {
		JButton jb = new JButton(str);
		jb.addActionListener(a);
		jb.setBackground(Color.GREEN.darker());
		jb.setForeground(Color.WHITE);
		return jb;
	}
	
	int toint(Object str) {
		return Integer.parseInt(str.toString());
	}
	
	ImageIcon img(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/"+path));
	}
	
	ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/"+path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	ImageIcon imgP(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	static class PlaceH extends JTextField {
		String place;
		
		public PlaceH(int col) {
			super(col);
		}
		
		void setPlace(String str) {
			place = str;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D)g;
			if(place == null || place.length() == 0 || PlaceH.this.getText().length() > 0) {
				return;
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.LIGHT_GRAY);
			g2.setFont(new Font("맑은 고딕", Font.BOLD, 13));
			g2.drawString(place, getInsets().left, (getHeight() / 2)+5);
		}
	}
	
	static class PlaceTA extends JTextArea {
		String place;
		
		public PlaceTA() {
			setLineWrap(true);
		}
		
		void setPlace(String str) {
			place = str;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D)g;
			if(place == null || place.length() == 0 || PlaceTA.this.getText().length() > 0) {
				return;
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.LIGHT_GRAY);
			g2.setFont(new Font("맑은 고딕", Font.BOLD, 13));
			g2.drawString(place, getInsets().left, 20);
		}
	}
	
	String getone(String sql) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				return rs.getString(1);
			}else {
				return "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	JComponent emp(JComponent c, int t, int l, int b, int r) {
		c.setBorder(new EmptyBorder(t, l, b, r));
		return c;
	}
	
	JComponent line(JComponent c) {
		c.setBorder(new LineBorder(Color.BLACK));
		return c;
	}
	
	
	class be extends WindowAdapter {
		Basedialog b;
		
		public be(Basedialog bef) {
			b = bef;
			bef.setVisible(false);
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

class MyLabel extends JLabel {
	public MyLabel(String _txt, int _w, int _h, String _path, int _iw, int _ih, int _st, Color _col) {
		Image _img = Toolkit.getDefaultToolkit().getImage("./지급자료/"+_path).getScaledInstance(_iw, _ih, Image.SCALE_SMOOTH);
		
		//이건 왜 필요할까? 있어야 이미지가 나타난다.
		ImageIcon _icon = new ImageIcon(_img);

		BufferedImage _bi = new BufferedImage(_iw+_st*2, _ih+_st*2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = _bi.createGraphics();
		g2.drawImage(_img, 0, 0, _bi.getWidth(), _bi.getHeight(), null);

		if(_col!=null) {
			g2.setColor(_col);
			g2.setStroke(new BasicStroke(4));
			g2.drawRect(_st/2, 4+_st, _bi.getWidth()-_st, _bi.getHeight()-4*2-_st*2);
		}
		g2.dispose();
		
		this.setIcon(new ImageIcon(_bi));
		this.setText(_txt);
	}
}