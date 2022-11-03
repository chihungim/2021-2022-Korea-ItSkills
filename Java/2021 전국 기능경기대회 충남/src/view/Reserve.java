package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

public class Reserve extends BaseFrame {
	
	ArrayList<Integer> path, ho = new ArrayList<Integer>(), dist = new ArrayList<Integer>();
	String ssta, esta;
	LocalDate date = LocalDate.now();
	LocalTime time = LocalTime.now();
	JLabel lblTime, lblRead;
	JPanel cc, ccc, cn = new JPanel(new FlowLayout(0));
	int tot, tcnt, inter;
	LocalTime TTWT = LocalTime.of(0, 0, 0), start, end;
	boolean bp;
	JLabel lbl;
	
	int timetable[][], eidx;
	
	
	public Reserve(ArrayList<Integer> cpath) {
		super("예매", 500, 600);
		
		this.path = cpath;
		for (int i = 0; i < path.size()-1; i++) {
			tot += adjDim[path.get(i)][path.get(i+1)];
		}
		
		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));
		
		ssta = stNames.get(path.get(0));
		esta = stNames.get(path.get(path.size()-1));
		
		n.add(lbl=lbl(ssta + " → " + esta, 0, 25));
		lbl.setForeground(blue);
		
		c.add(cn, "North");
		
		cn.add(btn("시간 변경", a->{
			new TimePicker(Reserve.this);
		}));
		
		cn.add(lblTime = lbl(date+" "+tFormat(time, "HH:mm:ss")+" 탑승", 2));
		
		c.add(new JScrollPane(cc = new JPanel(new BorderLayout())));
		
		JPanel ccn = new JPanel(new BorderLayout());
		cc.add(ccc = new JPanel(new GridLayout(0, 1, 5, 5)));
		
		mkTimeTable();
		
		cc.add(ccn, "North");
		JLabel l;
		ccn.add(l = lbl(path.size()+"개역 정차 "+tcnt+"회 환승", 2, 15), "West");
		l.setForeground(blue);
		ccn.add(lblRead = lbl("자세히보기", 4, 15), "East");
		lblRead.setForeground(Color.red);
		
		lblRead.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new WayPoint(Reserve.this);
			}
		});
		
		setEmpty(ccc, 5, 5, 5, 5);
		setTime();
		
		setEmpty((JPanel)getContentPane(), 10, 10, 10, 10);
		this.setVisible(true);
	}
	
	void setTime() {
		lblTime.setText(date +" "+tFormat(time, "HH:mm:ss")+" 탑승");
		
		ccc.removeAll();
		for (int i = 4; i < 300; i++) {
			if (timetable[eidx][i]==0) break;
			ccc.add(new ItemPanel(i));
		}
		
		repaint();
		revalidate();	
	}
	
	class ItemPanel extends JPanel {
		String mname;
		JLabel lblSt, lblEt;
		JPanel ce, cw, c, cc;
		JLabel lblT;
		int st, et;
		
		public ItemPanel(int idx) {
			super(new BorderLayout(5, 5));
			st = timetable[0][idx];
			et = timetable[eidx][idx];
			
			this.add(c = new JPanel(new BorderLayout()));
			c.add(cc = new JPanel(new BorderLayout()));
			c.add(ce = new JPanel(new BorderLayout()), "East");
			c.add(cw = new JPanel(new BorderLayout()), "West");
			
			cw.add(lblSt = lbl(ssta, 2, 15), "South");
			ce.add(lblEt = lbl(esta, 4, 15), "South");
			
			lblSt.setForeground(Color.LIGHT_GRAY);
			lblEt.setForeground(Color.LIGHT_GRAY);
		
			c.setBackground(Color.white);
			
			cw.add(lbl(String.format("%02d:%02d", st/3600, (st%3600)/60), 0, 35));
			this.add(btn("선택", a->{
				if (uno.isEmpty()) {
					eMsg("로그인 후 예매 가능합니다.");
					return;
				}
				
				int yn = JOptionPane.showConfirmDialog(null, String.format("%02d:%02d", st/3600, (st%3600)/60)+"시간 지하철을 예매할까요?", "메시지", JOptionPane.YES_NO_OPTION);
				if (yn == JOptionPane.YES_OPTION) {
					new Purchase(ItemPanel.this, Reserve.this).addWindowListener(new Before(Reserve.this));
				}
				
			}), "East");
			
			int tot = et - st;
			
			cc.add(lblT = new JLabel((tot/60)+"분 "+(tot%60)+"초 소요", 0), "South");
			ce.add(lbl(String.format("%02d:%02d", et/3600, (et%3600)/60), 0, 35));
			
			lblT.setBorder(new MatteBorder(1, 0, 0, 0, Color.black));
			lblT.setForeground(blue);
			sz(lblT, 1, 40);
			
			cc.setOpaque(false);
			ce.setOpaque(false);
			cw.setOpaque(false);
			
		}
		
	}
	
	void mkTimeTable() {
		int lineIdx, innerStIdx;
		
		timetable = new int[path.size()][300];
		eidx = path.size()-1;
		
		for (int i = 0; i < eidx; i++) {
			timetable[i][0] = path.get(i);
			timetable[i][1] = lineDim[path.get(i)][path.get(i+1)];
			if (i==0) {
				timetable[i][3]=0;
			} else {
				timetable[i][3]=adjDim[timetable[i-1][0]][timetable[i][0]];
				if (timetable[i-1][1]!=timetable[i][1]) {
					timetable[i][2]=1;
					tcnt++;
				}
			}
		}
		
		timetable[eidx][0] = path.get(eidx);
		timetable[eidx][3] = adjDim[timetable[eidx-1][0]][timetable[eidx][0]];
		
		int col=4;
		lineIdx = timetable[0][1];
		innerStIdx = ((ArrayList)metroStInfo[lineIdx][0]).indexOf(stNames.get(path.get(0)));
		for (int i = 0; i < 300; i++) {
			if (metroTime[lineIdx][innerStIdx][i] == 0) break;
			if (time.toSecondOfDay() <= metroTime[lineIdx][innerStIdx][i]) {
				timetable[0][col++] = metroTime[lineIdx][innerStIdx][i];
//				System.out.println("timetable[0]["+(col-1)+"] = "+metroTime[lineIdx][innerStIdx][i]);
				
			}
		}
		
		for (int i = 1; i < eidx; i++) {
			lineIdx = timetable[i][1];
			innerStIdx = ((ArrayList)metroStInfo[lineIdx][0]).indexOf(stNames.get(path.get(i)));
			
			for (int j = 4; j < 300; j++) {
				if (timetable[i-1][j] == 0) break;
				
				for (int k = 0; k < 300; k++) {
					if (metroTime[lineIdx][innerStIdx][k] == 0) break;
					if (timetable[i-1][j] + timetable[i][3] <= metroTime[lineIdx][innerStIdx][k]) {
						timetable[i][j] = metroTime[lineIdx][innerStIdx][k];
						break;
					}
				}
			}
		}
		
		
		for (int i = 4; i < 300; i++) {
			if (timetable[eidx-1][i] == 0) break;
			timetable[eidx][i] = timetable[eidx-1][i] + timetable[eidx][3];
		}
		
		
	}
	
	public static void main(String[] args) {
		int n[] = { 158,159,160,161,162,163,164,165,166,31,248,247,135 };
		ArrayList<Integer> cpath = new ArrayList<Integer>();
		for (var s : n) {
			cpath.add(s);
		}
		uno ="1";
		uname ="윤팡열";
		uage ="8";
		ubirth = "2013";
		new Reserve(cpath);
	}
	
}
