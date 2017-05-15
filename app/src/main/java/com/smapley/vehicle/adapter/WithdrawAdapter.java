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

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzhixiong on 2017/5/6.
 */

public class WithdrawAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Map> list;

    public WithdrawAdapter(Context context, List list) {
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
        if(convertView == null ){
            convertView = inflater.inflate(R.layout.layout_withdraw_item,parent,false);
            holder = new ViewHolder(convertView);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(holder!=null){
            holder.bindData(getItem(position));
        }

        return convertView;
    }

    class ViewHolder{

        TextView name;
        TextView card;
        TextView date;
        TextView money;
        TextView state;
        ImageView bank;
        ImageView stateimg;

        ViewHolder(View view){
            name =(TextView) view.findViewById(R.id.withdraw_item_name);
            card =(TextView)view.findViewById(R.id.withdraw_item_card);
            date = (TextView)view.findViewById(R.id.withdraw_item_date);
            money = (TextView)view.findViewById(R.id.withdraw_item_money);
            state =(TextView)view.findViewById(R.id.withdraw_item_state);
            bank = (ImageView) view.findViewById(R.id.withdraw_item_bank);
            stateimg = (ImageView) view.findViewById(R.id.withdraw_item_state_ico);
            view.setTag(this);
        }

        void bindData(Map map){
            name.setText(map.get("name").toString());
            card.setText("("+map.get("kahao").toString()+")");
            date.setText(map.get("tm").toString());
            money.setText(map.get("jine").toString());
            String stateStr = map.get("zt").toString();
            if(!StringUtils.isEmpty(stateStr)){
                int stateInt = Integer.parseInt(stateStr);
                switch (stateInt){
                    case 0:
                        stateimg.setImageResource(R.mipmap.withdraw_ing);
                        state.setText("等待处理");
                        state.setTextColor(ContextCompat.getColor(context,R.color.green4));
                        break;
                    case 1:
                        stateimg.setImageResource(R.mipmap.withdraw_success);
                        state.setText("处理完成");
                        state.setTextColor(ContextCompat.getColor(context,R.color.blue4));
                        break;
                    case 9:
                        stateimg.setImageResource(R.mipmap.withdraw_ing);
                        state.setText("处理失败");
                        state.setTextColor(ContextCompat.getColor(context,R.color.red4));
                        break;
                }
            }
            bank.setImageResource(map.get("bank").toString().equals(context.getResources().getStringArray(R.array.bank)[1])?R.mipmap.withdraw_alipay:R.mipmap.withdraw_bank);
        }
    }
}
