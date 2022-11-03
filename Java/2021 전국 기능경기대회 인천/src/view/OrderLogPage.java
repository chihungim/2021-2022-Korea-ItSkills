package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import view.OrderLogPage.ProductItem;

public class OrderLogPage extends BasePage {
	JScrollPane pane;
	JPanel p;
	int cnt;

	ArrayList<ProductItem> items = new ArrayList<>();

	public OrderLogPage() {
		try {
			var rs = rs("SELECT \r\n" + "    p.p_Name,\r\n" + "    s.s_Name,\r\n" + "    pu.pu_Count,\r\n"
					+ "    pu.pu_Price,\r\n" + "	ifNull(r.r_No, 0),\r\n" + "    pu.pu_Date,\r\n"
					+ "    r.r_Time,\r\n" + "    s.s_Addr,\r\n" + "    pu.pu_No\r\n" + "FROM\r\n"
					+ "    purchase pu\r\n" + "        LEFT OUTER JOIN\r\n" + "    receive r ON r.pu_No = pu.pu_No\r\n"
					+ "        INNER JOIN\r\n" + "    product p ON pu.p_No = p.p_No\r\n" + "        INNER JOIN\r\n"
					+ "    seller s ON s.s_No = pu.s_No\r\n" + "where\r\n" + "	pu.u_No = '" + uno + "'\r\n"
					+ "order by pu.pu_date asc");
			while (rs.next()) {
				items.add(new ProductItem(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5),
						rs.getString(6),

						rs.getString(7) == null ? null
								: LocalDateTime.parse(rs.getString(7),
										DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
						rs.getInt(8), rs.getInt(9)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		add(lbl(uname + "님의 구매목록", JLabel.CENTER, 25), "North");
		add(pane = new JScrollPane(p = new JPanel(new GridLayout(0, 3, 5, 5))));
		pane.getViewport().setBackground(Color.WHITE);
		setLogs();
		setB(this, new EmptyBorder(50, 50, 50, 50));

		var scr = pane.getVerticalScrollBar();
		scr.addAdjustmentListener(a -> {
			if (scr.getValue() != 0 && scr.getValue() + scr.getHeight() == scr.getMaximum()) {
				cnt += 6;
				setLogs();
			}
		});
	}

	void setLogs() {
		DecimalFormat df = new DecimalFormat("#,##0");
		for (var cnt = this.cnt; cnt < this.cnt + 6; cnt++) {
			if (cnt >= items.size())
				return;
			var item = items.get(cnt);
			var b = setB(sz(new JPanel(new BorderLayout()), 300, 300),
					new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
			var w = new JPanel(new BorderLayout());
			var c = setB(new JPanel(new FlowLayout(FlowLayout.LEFT)), new EmptyBorder(5, 5, 5, 5));
			var s = new JPanel(new BorderLayout());

			b.add(w, "West");
			b.add(c);
			b.add(s, "South");

			w.add(setB(sz(new JLabel(getIcon("./datafile/image/" + item.name + ".jpg", 130, 130)), 130, 130),
					new LineBorder(Color.BLACK)));
			c.add(new JLabel("<html>" + item.name + "<br><br>판매자 : " + item.seller + "<br><br>개수 : " + item.stock
					+ "개<br><br>가격 : " + df.format(item.price) + "원<br><br>", JLabel.LEFT));
			String state = "배달 " + (item.receive == 0 ? "준비" : "현황");
			JButton bu;
			s.add(bu = btn(state, a -> {
				sname = item.seller;
				sAddr = item.saddr;
				pdtime = item.pdtime;
				new Map("user").setVisible(true);
			}), "East");

			bu.setEnabled(item.receive != 0);
			p.add(b);
		}
	}

	public static void main(String[] args) {
		uno = 1;
		mf.swapPage(new OrderLogPage());
		mf.setVisible(true);
	}

	class ProductItem {
		String name, seller, date;
		int stock, price, receive, saddr, puno;
		LocalDateTime pdtime;

		public ProductItem(String name, String seller, int stock, int price, int receive, String date,
				LocalDateTime pdtime, int saddr, int puno) {
			this.name = name;
			this.seller = seller;
			this.stock = stock;
			this.price = price;
			this.receive = receive;
			this.date = date;
			this.pdtime = pdtime;
			this.saddr = saddr;
			this.puno = puno;
		}
	}
}
