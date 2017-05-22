package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coolwin.XYT.R;

import java.util.List;


/**
 * Created by dell on 2017/5/2.
 */

public class KindGirdAdapter extends BaseAdapter {
    private List<String> list;
    private LayoutInflater inflater;
    public KindGirdAdapter(Context context) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }
    public List<String> getList() {
        return list;
    }
    public void setList(List<String> list) {
        this.list = list;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list == null ? null : list.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return list == null ? -1 : position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater
                    .inflate(R.layout.adapter_fragment_grid, null);
            holder.text = (TextView) convertView
                    .findViewById(R.id.adapter_kind_text);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if (position != clickItem) {
            holder.text.setSelected(false);
        } else {
            holder.text.setSelected(true);
        }
        holder.text.setText(list.get(position));
        return convertView;
    }
    private int clickItem = -1;
    public void setClickItem(int clickItem) {
        this.clickItem = clickItem;
    }

    class ViewHolder {
        TextView text;
    }
}
