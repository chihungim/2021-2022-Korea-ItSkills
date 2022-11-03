package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.HashSet;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SearchPage extends BasePage {

	JScrollPane pane1, pane2;
	JPanel order, scrp = new JPanel();
	JTextField txt;
	JCheckBox evntChk = new JCheckBox();
	JComboBox<String> cate = new JComboBox<String>(), sell = new JComboBox<String>();
	String cap[] = "영업점명,카테고리,행사 여부".split(",");
	HashSet<Integer> idSet = new HashSet<Integer>();
	int cnt;

	public SearchPage() {
		setLayout(new BorderLayout(5, 5));
		add(n = new JPanel(new GridLayout(0, 1, 0, 0)), "North");
		var nc = new JPanel(new BorderLayout());
		var ns = new JPanel(new GridLayout(1, 0, 5, 5));
		n.add(nc);
		n.add(ns);

		nc.add(txt = new JTextField());
		nc.add(sz(btn("<html>&#128269", a -> {
			if (sell.getSelectedIndex() == -1)
				return;
			c.removeAll();
			search();
		}), 40, 1), "East");

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();
			tmp.add(sz(new JLabel(cap[i]), 60, 25));
			if (i == 0) {
				tmp.add(sell);
			} else if (i == 1) {
				tmp.add(cate);
			} else {
				tmp.add(evntChk);
			}
			ns.add(tmp);
		}

		add(pane1 = new JScrollPane(c = new JPanel(new GridLayout(0, 3, 5, 5))));
		add(sz(scrp = new JPanel(new BorderLayout()), 250, 1), "East");
		scrp.add(lbl("장바구니", 0, 20), "North");
		scrp.add(pane2 = new JScrollPane(order = new JPanel(new GridLayout(0, 1, 5, 5))));
		scrp.add(btn("구매하기", a -> {
			if (idSet.size() == 0) {
				eMsg("장바구니가 비어있습니다.");
				return;
			}

			mf.swapPage(new PurchasePage(sell.getSelectedIndex() + 1, order));
		}), "South");

		cate.addItem("전체");
		try {
			var rs = rs("select c_Name from category");
			while (rs.next()) {
				cate.addItem(rs.getString(1));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			var rs = rs("select s_Name from seller");
			while (rs.next()) {
				sell.addItem(rs.getString(1));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		cate.setSelectedIndex(0);

		sell.setSelectedIndex(-1);
		sell.addItemListener(a -> {
			cnt = 0;
			idSet.clear();
			txt.setText("");
			cate.setSelectedIndex(0);
			evntChk.setSelected(false);
			order.removeAll();
			c.removeAll();
			search();
			repaint();
			revalidate();
		});

		c.removeAll();

		cate.setSelectedIndex(-1);
		var scr = pane1.getVerticalScrollBar();
		scr.addAdjustmentListener(a -> {
			if (scr.getValue() != 0 && (scr.getValue() + scr.getHeight() == scr.getMaximum())) {
				cnt += 9;
				search();
			}
		});

		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		setB(this, new EmptyBorder(5, 5, 5, 5));
	}

	void search() {
		String sql = "SELECT p.p_No, c.c_Name, p.p_Name, p.p_Price, if(e.e_Month = month(now()), e.e_Month, 0) state, st.st_Count FROM product p inner join category c on c.c_No=p.c_No left join event e on e.p_No=p.p_No inner join stock st on st.p_No=p.p_No where p_Name like '%"
				+ txt.getText() + "%' and st.s_No='" + (sell.getSelectedIndex() + 1) + "'";
		if (cate.getSelectedIndex() != 0) {
			sql += " and c.c_No = "
					+ toInt(getOne("select c_No from category where c_Name ='" + cate.getSelectedItem() + "'"));
		}

		if (evntChk.isSelected())
			sql += " and if(e.e_Month = month(now()), e.e_Month, 0) <> 0";

		sql += " order by p.p_No limit " + cnt + ",9";
		try {
			var rs = rs(sql);
			while (rs.next()) {
				int no = rs.getInt(1);
				String name = rs.getString(3);
				int price = rs.getInt(4);
				int sale = rs.getInt(5);
				int stock = rs.getInt(6);
				var b = new JPanel(new BorderLayout());
				var c = new JPanel(new BorderLayout());
				var s = new JPanel(new FlowLayout(2));
				b.add(c);
				b.add(s, "South");

				c.add(setB(new JLabel(getIcon("./datafile/image/" + name + ".jpg", 100, 100)),
						new LineBorder(Color.BLACK)), "West");
				var con = ((sale != 0) ? "O" : "X");
				c.add(new JLabel(
						"<html><left>" + name + "<br>가격 :" + price + "원<br>재고 : " + stock + "개<br>행사상품 " + con));
				s.add(setB(btn("장바구니에 추가", a -> {
					if (idSet.contains(no)) {
						eMsg("이미 추가된 상품입니다.");
						return;
					}

					order.add(new OrderItem(no, name, price, stock, sale));
					idSet.add(no);
					order.repaint();
					order.revalidate();
				}), new LineBorder(Color.BLACK)));

				b.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

				SearchPage.this.c.add(sz(b, 1, 200));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
		revalidate();

	}

	class OrderItem extends JPanel {
		int no, price, stock, state;
		String name;

		public OrderItem(int no, String name, int price, int stock, int state) {
			this.no = no;
			this.name = name;
			this.price = price;
			this.stock = stock;
			this.state = state;
			setLayout(new BorderLayout());
			setB(this, new LineBorder(Color.BLACK));
			var c = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
			String txt = name.length() <= 10 ? name : name.substring(0, 10) + "...";
			c.add(sz(BasePage.lbl(txt, JLabel.LEFT), 150, 20));
			add(setB(sz(new JLabel(getIcon("./Datafile/image/" + name + ".jpg", 30, 30)), 30, 2),
					new LineBorder(Color.BLACK)), "West");
			add(sz(btn("x", a -> {
				idSet.remove(this.no);
				order.remove(OrderItem.this);
				order.repaint();
				order.revalidate();
			}), 30, 30), "East");
			add(c);
			repaint();
			revalidate();
		}
	}
}
