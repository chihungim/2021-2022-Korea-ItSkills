package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.concurrent.Flow;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Game;

public class Cart extends BasePage {

	ArrayList<Item> lst;
	JCheckBox all;
	JLabel sum;
	int summ = 0;

	public Cart() {
		super("장바구니");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		add(n = newJPanel(new FlowLayout(FlowLayout.RIGHT)), "North");
		n.add(all = new JCheckBox("모두선택"));
		all.setOpaque(false);
		all.setForeground(Color.WHITE);
		all.addActionListener(a -> {
			lst.stream().forEach(b -> b.box.setSelected(all.isSelected()));
			setPrice();
		});
		c.setBackground(back);

		add(s = new JPanel(new BorderLayout()), "South");
		s.add(sum = lbl("총 금액 0원", JLabel.LEFT, 20), "West");
		s.add(sc = new JPanel(new FlowLayout(FlowLayout.RIGHT)));
		c.setBackground(back);
		data();

		sc.add(bbtn("장바구니 삭제", a -> {
			if (lst.stream().allMatch(x -> !x.box.isSelected())) {
				emsg("삭제할 게임을 선택하세요.");
				return;
			}
			for (var l : lst) {
				if (l.box.isSelected())
					execute("delete from cart where c_no = ?", l.cno);
			}
			imsg("삭제가 완료되었습니다.");
			data();
			mf.updateALL();
		}));
		sc.add(bbtn("구매하기", a -> {
			if (summ > cint(user.u_money)) {
				emsg("금액이 부족합니다.");
				new Charge().setVisible(true);
				return;
			}

			ArrayList<Game> games = new ArrayList<>();

			for (var l : lst) {
				if (l.box.isSelected()) {
					games.add(l.g);
					execute("update user set u_money = u_money - ? where u_no = ?", l.g.ownPrice(user), user.u_no);
					execute("delete from cart where c_no = ?", l.cno);
				}
			}

			new Info(games).setVisible(true);
			data();
			mf.updateALL();
		}));
	}

	public static void main(String[] args) {
		디버그("1");
		new Cart();
		mf.setVisible(true);
	}

	void data() {
		summ = 0;
		c.removeAll();
		lst = new ArrayList<>();
		for (var r : getrows("select g.*,c.* from cart c, game g where c.g_no = g.g_no and c.u_no =?", user.u_no)) {
			var g = new Game(r);
			var item = new Item(r.get(10) + "", g);
			c.add(item);
		}

		while (c.getComponentCount() < 3)
			c.add(new JLabel());
		revalidate();
		repaint();
	}

	class Item extends JPanel {
		JCheckBox box;
		Game g;
		Border sel = new CompoundBorder(new LineBorder(Color.RED), new EmptyBorder(5, 5, 5, 5));
		Border dissel = new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5));
		String cno;

		public Item(String cno, Game g) {
			this.g = g;
			this.cno = cno;
			setLayout(new BorderLayout());
			var c = newJPanel(new BorderLayout());
			add(new JLabel(g.g_img(100, 100)), "West");
			add(c);
			c.add(wlbl("게임명:" + g.g_name, JLabel.LEFT), "North");
			c.add(wlbl("평점:" + g.avg() + "점", JLabel.LEFT));
			c.add(wlbl("가격:" + g.displayPrice(), JLabel.LEFT), "South");
			add(box = new JCheckBox(), "East");
			box.addItemListener(i -> {
				setBorder(box.isSelected() ? sel : dissel);
			});
			box.addActionListener(a -> {
				setPrice();
				all.setSelected(lst.stream().allMatch(x -> x.box.isSelected()));
			});
			box.setBackground(back);
			setBackground(back);
			setBorder(dissel);
			lst.add(this);
		}
	}

	void setPrice() {
		summ = lst.stream().filter(a -> a.box.isSelected()).mapToInt(a -> a.g.ownPrice(user)).sum();
		sum.setText("총 금액:"
				+ decformat(lst.stream().filter(a -> a.box.isSelected()).mapToInt(a -> a.g.ownPrice(user)).sum())
				+ "원");
	}
}
