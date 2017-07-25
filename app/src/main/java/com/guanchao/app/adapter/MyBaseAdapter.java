package com.guanchao.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	protected Context context;
	protected LayoutInflater inflater;
	protected List<T> myList=new ArrayList<T>();
	
	public MyBaseAdapter(Context context){
		this.context=context;
		this.inflater=LayoutInflater.from(context);
	}

	/**
	 * 适配器添加数据
	 * */
	public void addDate(T e){
		myList.add(e);
	}

	public List<T>  getAdapterData(){
		return myList;
	}
	//添加数据并判断是否清空
	public void appendData(T t,boolean isClearOld){
		if(t==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.add(t);
	}
	//添加集合数据并判断是否清空�
	public void appendData(List<T> data,boolean isClearOld){
		if(data==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.addAll(data);
	}
	//添加数据到第一个位置并判断是否清空�
	public void appendDataTop(T t,boolean isClearOld){
		if(t==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.add(0, t);
	}
	//添加集合数据到第一个位置并判断是否清空�
	public void appendDataTop(List<T> data,boolean isClearOld){
		if(data==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.addAll(0, data);
	}
	
	public void update(){
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		myList.clear();
	}
	

	public int getCount() {
		if(myList==null)
			return 0;
		return myList.size();
	}

	public T getItem(int position) {
		if(myList==null||myList.size()<=0)
			return null;
		if(position>myList.size())
			return null;
		return myList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return getMyView(position, convertView, parent);
	}
	public abstract View getMyView(int position, View convertView, ViewGroup parent);
	
}
