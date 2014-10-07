package com.lazooz.lbm.businessClasses;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;



public class TelephonyDataTracker {

	private OnTelephonyDataListener mOnTelephonyDataListener;
	private Timer mTimer;
	private TelephonyManager mTelephonyManager;
	private int mLastCid = 0;

	public TelephonyDataTracker(Context context){
		mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	
	public interface OnTelephonyDataListener {
        public void onCellChanged(int newCellID);
    }
	
	
	public OnTelephonyDataListener getOnTelephonyDataListener() {
		return mOnTelephonyDataListener;
	}

	public void setOnTelephonyDataListener(OnTelephonyDataListener onTelephonyDataListener) {
		this.mOnTelephonyDataListener = onTelephonyDataListener;
	}
	
	
	
	public void requestCellUpdates(OnTelephonyDataListener onTelephonyDataListener){
		this.mOnTelephonyDataListener = onTelephonyDataListener;
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		
		mTimer = new Timer();
		TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					checkEveryPeriod();				
				}
			};
			mTimer.scheduleAtFixedRate(timerTask, 1000, 10*1000);

	}
	
	
	
	protected void checkEveryPeriod() {
		
		GsmCellLocation cellLocation = (GsmCellLocation)mTelephonyManager.getCellLocation();
		
		if (cellLocation != null){
			int cellId = cellLocation.getCid();
			if(mLastCid  != cellId){
				mLastCid = cellId;
				 Message msg = handler.obtainMessage();
			     handler.sendMessage(msg);
			       
				
			}
			
		}
	}

	 private final Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
            	  try {
            		  mOnTelephonyDataListener.onCellChanged(mLastCid);
            	  }catch (Exception e) {
            		  e.printStackTrace();
            	  }
	        }
	 };
	
	public void removeUpdates(){
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}
}