package com.gaitroid;

import com.gaitroid.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends BaseExpandableListAdapter {
    private List<Record> _records;
    private LayoutInflater inflater;

    public RecordAdapter(Context context, List<Record> records) {
        this._records = records;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return _records.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _records.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _records.get(groupPosition).getDate();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _records.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.record_group, parent, false);
        }

        ((TextView) convertView).setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.record_child, parent, false);
        }

        ((TextView)convertView).setText(getChild(groupPosition,childPosition).toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }
}
