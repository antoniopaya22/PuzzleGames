package com.puzzlesmentales.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;


/**
 * Clase que hace de temporizador para los juegos
 */
public abstract class Temporizador extends Handler {

	private long tickInterval = 0;
	private boolean isRunning = false;
	private int tickCount;
	private long nextTime;
	private long accumTime;
	private long lastLogTime;

	/**
	 * Constructor principal de la clase
	 * @param ival
	 */
	public Temporizador(long ival) {
		tickInterval = ival;
		isRunning = false;
		accumTime = 0;
	}


	// Control Temporizador

	public void start() {
		if (isRunning)
			return;

		isRunning = true;
		long now = SystemClock.uptimeMillis();
		lastLogTime = now;
		nextTime = now;
		postAtTime(runner, nextTime);
	}

	public void stop() {
		if (isRunning) {
			isRunning = false;
			long now = SystemClock.uptimeMillis();
			accumTime += now - lastLogTime;
			lastLogTime = now;
		}
	}

	public final void reset() {
		stop();
		tickCount = 0;
		accumTime = 0;
	}

	public final boolean isRunning() {
		return isRunning;
	}
	public final long getTime() {
		return accumTime;
	}
	protected abstract boolean step(int count, long time);
	protected void done() {}

	private final Runnable runner = new Runnable() {

		public final void run() {
			if (isRunning) {
				long now = SystemClock.uptimeMillis();

				accumTime += now - lastLogTime;
				lastLogTime = now;

				if (!step(tickCount++, accumTime)) {

					nextTime += tickInterval;
					if (nextTime <= now)
						nextTime += tickInterval;
					postAtTime(runner, nextTime);
				} else {
					isRunning = false;
					done();
				}
			}
		}

	};

	public void saveState(Bundle outState) {
		if (isRunning) {
			long now = SystemClock.uptimeMillis();
			accumTime += now - lastLogTime;
			lastLogTime = now;
		}

		outState.putLong("tickInterval", tickInterval);
		outState.putBoolean("isRunning", isRunning);
		outState.putInt("tickCount", tickCount);
		outState.putLong("accumTime", accumTime);
	}

	boolean restoreState(Bundle map) {
		return restoreState(map, true);
	}

	boolean restoreState(Bundle map, boolean run) {
		tickInterval = map.getLong("tickInterval");
		isRunning = map.getBoolean("isRunning");
		tickCount = map.getInt("tickCount");
		accumTime = map.getLong("accumTime");
		lastLogTime = SystemClock.uptimeMillis();

		if (isRunning) {
			if (run)
				start();
			else
				isRunning = false;
		}

		return true;
	}



}

