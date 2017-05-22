package com.coolwin.XYT.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.KindAdapter;
import com.coolwin.XYT.adapter.KindGirdAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.coolwin.XYT.global.GlobalParam.MSG_GET_TYPE_DATA;

/**
 * Created by dell on 2017/5/2.
 */

public class KindFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private GridView gridView;
    private List<String> list = new ArrayList<String>();
    private KindAdapter listAdapter;
    private KindGirdAdapter girdAdapter;
    private ArrayList<String> map  = new ArrayList<String>();
    //用于保存选中的列表索引值
    private int listSelect = -1;
    private Handler handler;
    public String checktype;
    private int showtype=0;
    public static final KindFragment newInstance(ArrayList<String> map, Handler handler, String checktype){
        KindFragment c = new KindFragment();
        Bundle bdl = new Bundle();
        bdl.putStringArrayList("map", map);
        bdl.putString("checktype", checktype);
        c.setArguments(bdl);
        c.setHandler(handler);
        return c;
    }
    public static final KindFragment newInstance(ArrayList<String> map, Handler handler, String checktype, int showtype){
        KindFragment c = new KindFragment();
        Bundle bdl = new Bundle();
        bdl.putStringArrayList("map", map);
        bdl.putString("checktype", checktype);
        bdl.putInt("showtype",showtype);
        c.setArguments(bdl);
        c.setHandler(handler);
        return c;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_kind, null);
        Bundle bundle = getArguments();
        map = bundle.getStringArrayList("map");
        checktype = bundle.getString("checktype");
        showtype = bundle.getInt("showtype");
        initData();
        init(view);
        return view;
    }
    /**
     * 初始化数据
     */
    private void initData() {
        // TODO Auto-generated method stub
        list = map;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(checktype)) {
                listSelect = i;
                break;
            }
        }
    }
    /**
     * 初始化控件
     *
     * @param view
     */
    private void init(View view) {
        // TODO Auto-generated method stub
        listView = (ListView) view.findViewById(R.id.kind_list);
        gridView = (GridView) view.findViewById(R.id.kind_grid);
        if(showtype==0){
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            listAdapter = new KindAdapter(getActivity());
            listAdapter.setList(list);
            listAdapter.setClickItem(listSelect);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(this);
        }else if(showtype==1){
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            girdAdapter = new KindGirdAdapter(getActivity());
            girdAdapter.setList(list);
            girdAdapter.setClickItem(listSelect);
            gridView.setAdapter(girdAdapter);
            gridView.setOnItemClickListener(this);
        }
    }

    /**
     * ListView点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        handler.obtainMessage(MSG_GET_TYPE_DATA, list.get(position)).sendToTarget();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

}
