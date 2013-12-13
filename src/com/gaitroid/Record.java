package com.gaitroid;

import java.util.List;

public class Record {
    private String _date;
    private List<String> _items;

    public Record(String date, List<String> items) {
        _date = date;
        _items = items;
    }

    public String getDate() {
        return _date;
    }

    public List<String> getItems() {
        return _items;
    }
}
