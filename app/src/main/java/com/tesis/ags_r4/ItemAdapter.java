package com.tesis.ags_r4;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {
	 
    private Context context;
    private List<String> items;
 
    public ItemAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }
 
    @Override
    public int getCount() {
        return this.items.size();
    }
 
    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
    	String cat = (String) getItem(position);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.cat_item,
					null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.cat);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(cat);
		return convertView;
    }

	
 
}