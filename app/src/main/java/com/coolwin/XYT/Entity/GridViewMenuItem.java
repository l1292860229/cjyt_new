package com.coolwin.XYT.Entity;

import com.coolwin.XYT.global.FeatureFunction;

import java.io.Serializable;


/**
 * 底部九宫格弹出框子项
 * @author dongli
 *
 */
public class GridViewMenuItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public int resource_id;
	public String menu_name;
	public GridViewMenuItem(int id, String resource_name, String menu_name) {
		super();
		this.id = id;
		if(resource_name!=null && !resource_name.equals("")){
			this.resource_id = FeatureFunction.getSourceIdByName(resource_name);
		}
		this.menu_name = menu_name;
	}
	public GridViewMenuItem() {
		super();
	}
	
}
