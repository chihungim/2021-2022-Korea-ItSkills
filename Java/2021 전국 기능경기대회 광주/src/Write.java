

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Write extends Basedialog {
	PlaceH tt = new PlaceH(30);
	PlaceTA ta = new PlaceTA();
	JPanel s = new JPanel(new BorderLayout()), sw = new JPanel(new FlowLayout(1));
	JLabel star[] = {
			labelB("☆", 0,15),
			labelB("☆", 0,15),
			labelB("☆", 0,15),
			labelB("☆", 0,15),
			labelB("☆", 0,15)
	};
	int sel;
	JButton jb;
	
	public Write(String mode, int no, int sno) {
		super(mode.equals("등록")?"리뷰 등록":"리뷰 수정", 400, 500);
		tt.setPlace("제목");
		ta.setPlace("내용");
		
		add(tt,"North");
		add(ta);
		add(s,"South");
		
		s.add(sw,"West");
		s.add(jb = btn("등록", a->{
			if(tt.getText().isEmpty() || ta.getText().isEmpty()) {
				errmsg("내용을 입력해주세요.");
				return;
			}
			if(sel == 0) {
				errmsg("평점을 선택해주세요.");
				return;
			}
			if(a.getActionCommand().equals("등록")) {
				msg("리뷰를 등록했습니다.");
				execute("insert into review values(0,'"+tt.getText()+"','"+ta.getText()+"','"+sel+"','"+NO+"','"+sno+"','"+no+"')");
			}else {
				msg("리뷰를 수정했습니다.");
				execute("update review set title = '"+tt.getText()+"', content = '"+ta.getText()+"', rate = "+sel+" where receipt = "+no);
			}
		}),"East");
		
		emp((JPanel)getContentPane(), 10, 10, 10, 10);
		
		ta.setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		for (int i = 0; i < star.length; i++) {
			sw.add(star[i]);
			star[i].setForeground(Color.ORANGE);
			int k = i;
			star[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < star.length; j++) {
						if(j <= k) {
							star[j].setText("★");
						}else {
							star[j].setText("☆");
						}
						sel = k + 1;
					}
				}
			});
		}
		
		if(mode.equals("수정")) {
			jb.setText("수정");
			tt.setText(getone("select title from review where receipt = "+no));
			ta.setText(getone("select content from review where receipt = "+no));
			sel = toint(getone("select rate from review where receipt = "+no));
			for (int i = 0; i < sel; i++) {
				star[i].setText("★");
			}
		}
		
		sw.setOpaque(false);
		s.setOpaque(false);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		NO = 1;
		new Write("수정", 66,3);
		
	}

}
