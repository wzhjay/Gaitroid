package com.shimmerresearch.driver;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;


public class FormatCluster {
	public Vector3d mVector3d;
	public AxisAngle4d mAxisAngle4d;
	public Matrix3d mMatrix3d;
	public Quat4d mQuat4d;
	public String mFormat;
	public String mUnits;
	public double mData;

	public FormatCluster(String format,String units, double data){
		mFormat = format;
		mUnits = units;
		mData = data;
	}
	
	public FormatCluster(String format,String units){
		mFormat = format;
		mUnits = units;
	}
	
	public FormatCluster(String format, String units, Vector3d v3d){
		mFormat = format;
		mUnits = units;
		mVector3d=new Vector3d();
		mVector3d.set(v3d);
	}
	
	public FormatCluster(String format, String units, Matrix3d m3d){
		mFormat = format;
		mUnits = units;
		mMatrix3d=new Matrix3d();
		mMatrix3d.set(m3d);
	}
	
	public FormatCluster(String format, String units, AxisAngle4d aa4d){
		mFormat = format;
		mUnits = units;
		mAxisAngle4d = new AxisAngle4d();
		mAxisAngle4d.set(aa4d);
	}
	
	
}