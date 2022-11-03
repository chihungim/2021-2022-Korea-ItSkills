package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import view.Base;

public class User implements Base {
	public Object u_no, u_id, u_pw, u_name, u_birth, u_money, u_ox, u_filter, u_img;

	public User(ArrayList<Object> user) {
		u_no = user.get(0);
		u_id = user.get(1);
		u_pw = user.get(2);
		u_name = user.get(3);
		u_birth = user.get(4);
		u_money = user.get(5);
		u_ox = user.get(6);
		u_filter = user.get(7);
		u_img = user.get(8);
	}

	public List<String> u_filter() {
		return Arrays.asList(u_filter.toString().split(","));
	}

	public ImageIcon u_img(int w, int h) {
		return getBlob(u_img, w, h);
	}

	public int exp() {
		var 게임수 = getrows("select * from library where u_no =?", u_no).size();
		var 황금수 = getrows(
				"select count(distinct s.i_no) cnt from item i, storage s where i.i_no = s.i_no and s.u_no = 1 and s.s_no not in (select s_no from market) and s.u_no = ? group by i.g_no having cnt > 2",
				u_no).size();
		return (게임수 * 3) + (황금수 * 10);
	}

	public int gd() {
		return exp() / 20;
	}
}
