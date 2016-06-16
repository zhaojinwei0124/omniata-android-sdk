package com.omniata.android.sdk;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

class OmniataEventLogger implements Runnable {
	private static final String TAG = "OmniataEventLogger";
	
	private BlockingQueue<JSONObject>			eventBuffer;
	private PersistentBlockingQueue<JSONObject> eventLog;
	private Thread								worker;
	private boolean								isRunning;
	private boolean								isStarted;

	public OmniataEventLogger(BlockingQueue<JSONObject> eventBuffer, PersistentBlockingQueue<JSONObject> eventLog) {
		this.eventBuffer = eventBuffer;
		this.eventLog	 = eventLog;
		this.worker      = new Thread(this);
	}
	
	public void start() {
		if (!isStarted) {
			this.worker.start();
			isStarted = true;
		}
	}
	
	@Override
	public void run() {
		OmniataLog.i(TAG, "Thread begin");
		isRunning = true;
		try {
			while(isRunning) {
				OmniataLog.v(TAG, "Thread running: " + Thread.currentThread().getId());
				if ( ! eventLog.offer(eventBuffer.take()) ){
					OmniataLog.e(TAG, "No space currently available, event is ignored");
				}
			}
		} catch (InterruptedException e) {
			OmniataLog.e(TAG, "Thread interrupted");
			Thread.currentThread().interrupt();
		}
		OmniataLog.i(TAG, "Thread done");
	}
}
