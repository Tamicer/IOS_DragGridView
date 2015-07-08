package com.lyk.dragGridView;

import android.view.View;

public interface DragGridListener {
	/**
	 * 重新排列数据
	 * @param oldPosition
	 * @param newPosition
	 */
	public void reorderItems(int oldPosition, int newPosition);
	
	
	/**
	 * 设置某个item隐藏
	 * @param hidePosition
	 */
	public void setHideItem(int hidePosition);
	
	
	/**
	 * 删除某个item
	 * @param hidePosition
	 */
	public void removeItem(int hidePosition);
	

}
