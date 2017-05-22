package com.coolwin.XYT.sortlist;

import com.coolwin.XYT.Entity.Login;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<Login> {

	public int compare(Login o1, Login o2) {
		if (o1.sort.equals("@")
				|| o2.sort.equals("#")
				) {
			return -1;
		} else if (o1.sort.equals("#")
				|| o2.sort.equals("@")) {
			return 1;
		}else if( o1.sort.equals("↑")
				|| o2.sort.equals("☆")){
			return 1;
		}else if( o1.sort.equals("☆")
				|| o2.sort.equals("↑")){
			return 2;
		}
		else {
			return o1.sort.compareTo(o2.sort);
		}
	}

}
