package com.smapley.vehicle.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smapley.vehicle.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzhixiong on 2017/5/6.
 */

public class DetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Map> list;

    public DetailAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    @Override
    public Map getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_detail_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder != null) {
            holder.bindData(getItem(position), position);
        }

        return convertView;
    }

    class ViewHolder {

        int[] outBg = new int[]{R.mipmap.out1, R.mipmap.out2, R.mipmap.out3, R.mipmap.out4, R.mipmap.out5};
        int[] color = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5};

        TextView title;
        TextView detail;
        TextView gold;
        View outLayout;
        TextView out;
        TextView date;

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.detail_title);
            detail = (TextView) view.findViewById(R.id.detail_detail);
            gold = (TextView) view.findViewById(R.id.detail_gold);
            out = (TextView) view.findViewById(R.id.detail_out);
            outLayout = view.findViewById(R.id.detail_out_layout);
            date = (TextView) view.findViewById(R.id.detail_date);
            view.setTag(this);
        }

        void bindData(Map map, int position) {
            title.setText(map.get("sjname").toString());
            detail.setText(map.get("sjbeizhu").toString());
            try{
                gold.setText(((Double)map.get("jine")).intValue()+"");
            }catch(Exception e){
                e.printStackTrace();
            }
            out.setText(map.get("sjchukou").toString());
            date.setText(map.get("tm").toString());
            outLayout.setBackgroundResource(outBg[position % 5]);
            out.setTextColor(ContextCompat.getColor(context, color[position % 5]));
            gold.setTextColor(ContextCompat.getColor(context, color[position % 5]));

        }
    }
}
