package org.cl.contenu.rest;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;

import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class LatenceBean {

	private ReadWriteLock rrwl = new ReentrantReadWriteLock(true);
	private Lock readLock = rrwl.readLock();
	private Lock writeLock = rrwl.writeLock();

	@Getter
	private volatile int min = 0;
	@Getter
	private volatile int max = 0;

	public void changeLatence(int min, int max) {

		writeLock.lock();

		try {
			this.min = min;
			this.max = max;
		} finally {
			writeLock.unlock();
		}

	}

	public void doLatence() {

		int lmin = 0;
		int lmax = 0;

		readLock.lock();

		try {
			lmin = this.min;
			lmax = this.max;
		} finally {
			readLock.unlock();
		}

		if (lmin != 0 && lmax != 0) {
			Random random = new Random();
			double attente = random.nextDouble() * (double) (lmax - lmin) + (double) lmin;

			try {
				Thread.sleep((long) (attente * 1000));
			} catch (InterruptedException e) {
				Throwables.propagate(e);
			}

		}

	}

}
