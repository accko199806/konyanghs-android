package net.accko.konyanghs.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.accko.konyanghs.R;

import java.util.ArrayList;

public class BBSListAdapter extends BaseAdapter {

    private Context mContext = null;

    public ArrayList<ListData> mListData = new ArrayList<>();

    public BBSListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_listview_style, null);

            holder.mTitle = convertView.findViewById(R.id.item_title);
            holder.mWriter = convertView.findViewById(R.id.item_writer);
            holder.mDate = convertView.findViewById(R.id.item_date);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListData mData = mListData.get(position);

        holder.mTitle.setText(mData.mTitle);

        holder.mWriter.setText(mData.mWriter + " 선생님"); //선생님을 붙힘
        holder.mDate.setText(mData.mDate);

        return convertView;
    }
}