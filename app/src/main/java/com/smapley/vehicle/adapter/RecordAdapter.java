package com.smapley.vehicle.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smapley.vehicle.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzhixiong on 2017/5/6.
 */

public class RecordAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Map> list;

    public RecordAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else if(list.size()>10)
            return 10;
        else return list.size();
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
            convertView = inflater.inflate(R.layout.layout_record_item, parent, false);
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

        TextView title;
        TextView detail;
        View numberLayout;
        ImageView number1;
        ImageView number2;
        View outLayout;
        TextView out;

        int[] numberBg = new int[]{R.mipmap.color1, R.mipmap.color2, R.mipmap.color3, R.mipmap.color4, R.mipmap.color5};
        int[] number = new int[]{R.mipmap.number0, R.mipmap.number1, R.mipmap.number2, R.mipmap.number3, R.mipmap.number4, R.mipmap.number5, R.mipmap.number6, R.mipmap.number7, R.mipmap.number8, R.mipmap.number9};
        int[] outBg = new int[]{R.mipmap.out1, R.mipmap.out2, R.mipmap.out3, R.mipmap.out4, R.mipmap.out5};
        int[] color = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5};

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.record_title);
            detail = (TextView) view.findViewById(R.id.record_detail);
            numberLayout = view.findViewById(R.id.record_number_layout);
            number1 = (ImageView) view.findViewById(R.id.record_number1);
            number2 = (ImageView) view.findViewById(R.id.record_number2);
            outLayout = view.findViewById(R.id.record_out_layout);
            out = (TextView) view.findViewById(R.id.record_out);
            view.setTag(this);
        }

        void bindData(Map map, int position) {
            title.setText(map.get("sjname").toString());
            detail.setText(map.get("sjbeizhu").toString());
            out.setText(map.get("sjchukou").toString());
            numberLayout.setBackgroundResource(numberBg[position % 5]);
            outLayout.setBackgroundResource(outBg[position % 5]);
            out.setTextColor(ContextCompat.getColor(context,color[position % 5]));
            if (position < 9) {
                number1.setImageResource(number[position + 1]);
                number2.setImageResource(R.mipmap.icon_null);
                number2.setVisibility(View.GONE);
            } else if (position == 9) {
                number1.setImageResource(number[1]);
                number2.setImageResource(number[0]);
                number2.setVisibility(View.VISIBLE);
            }
        }
    }
}
