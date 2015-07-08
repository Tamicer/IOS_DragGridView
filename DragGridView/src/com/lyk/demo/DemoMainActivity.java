package com.lyk.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;







import java.util.Random;

import com.example.draggridview.R;
import com.lyk.dragGridView.DragAdapter;
import com.lyk.dragGridView.DragGridView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * 
 * @author lyk
 *
 */
public class DemoMainActivity extends Activity implements OnItemClickListener{
	private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
	
	/**
	 * 一页可见提条目数
	 */
	private static final int VISIBIY_NUMS = 24;
	private DragAdapter mDragAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DragGridView mDragGridView = (DragGridView)findViewById(R.id.dragGridView);
		mDragGridView.setOnItemClickListener(this);
	
		for (int i = 0; i < VISIBIY_NUMS; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			Random random =new Random();
			
			
			if (random.nextInt(3) == 1) {
				itemHashMap.put("item_image",R.drawable.ic_icon);
			}
			
			if (random.nextInt(3) == 0) {
				itemHashMap.put("item_image",R.drawable.icon);
			}
			
			else {
				itemHashMap.put("item_image",R.drawable.icon4);
			}
			itemHashMap.put("item_text", "icon" + Integer.toString(i));
			dataSourceList.add(itemHashMap);
		}
		mDragAdapter = new DragAdapter(this, dataSourceList);
		
		mDragGridView.setAdapter(mDragAdapter);
		//设置需要抖动
		mDragGridView.setNeedShake(true);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Toast.makeText(this, "onClick:" + position,    
                Toast.LENGTH_SHORT).show();   
	}
	

}
