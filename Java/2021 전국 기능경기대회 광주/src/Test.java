

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;

public class Test extends Basedialog {
	DefaultTableModel dtm = new DefaultTableModel(null, "asd,das,qwe,eqw".split(","));
	JTable table = new JTable(dtm) {
		public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
			
		};
	};
	DefaultTableModel dtm2 = new DefaultTableModel(null, "ㅁㄴㅇ,ㅁㄴㅇ,ㅁㄴㅇ,ㅁㄴㅇ,ㅁㄴㅇ,ㅁㄴㅇ,".split(","));
	JScrollPane scr = new JScrollPane(table);
	
	public Test() {
		super("실험", 1000, 1000);
		
		add(new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				
				g2.setClip(new Ellipse2D.Float(300, 0, 300, 300));
				g2.drawImage(img("배경/1.png").getImage(), 0, 0, 1000, 300, this);
			}
		});
		add(scr,"South");
		
		table.getTableHeader().getColumnModel().getColumn(0).setIdentifier("컬럼1");
		
		Object row[] = "asd,asd,asd,asd".split(",");
		Object row1[] = "asd1,asd1,asd1,asd1".split(",");
		Object row2[] = "asd2,asd2,asd2,asd2".split(",");
		Object row3[] = "asd3,asd3,asd3,asd3".split(",");
		Object row4[] = "asd4,asd4,asd4,asd4".split(",");
		dtm.addRow(row);
		dtm.addRow(row1);
		dtm.addRow(row2);
		dtm.addRow(row3);
		dtm.addRow(row4);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(table.getTableHeader().getColumnModel().getColumn(table.getSelectedColumn()).getIdentifier());
			}
		});
		
		ActionMap map = new ActionMap();
		
		map.put("asd", new Action() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("asd");
			}
			
			@Override
			public void setEnabled(boolean b) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void putValue(String key, Object value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object getValue(String key) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		table.setActionMap(map);
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
//				table.getColumnModel().getColumn(table.columnAtPoint(e.getPoint())).setHeaderValue(table.getTableHeader().getColumnModel().getColumn(table.columnAtPoint(e.getPoint())).getIdentifier()+"↑");
				table.setGridColor(Color.MAGENTA);
				table.setActionMap(null);
				table.setAutoCreateColumnsFromModel(true);
				table.setModel(dtm2);
			}
		});
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Test();
	}

}
