package org.cl.contenu.rest;

import java.util.Random;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
@Data
public class LatenceBean {

	private int min = 0;
	private int max = 0;

	public void changeLatence(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public void doLatence() {

		if (min != 0 && max != 0) {
			Random random = new Random();
			double attente = random.nextDouble() * (double) (max - min) + (double) min;

			try {
				Thread.sleep((long) (attente * 1000));
			} catch (InterruptedException e) {
				Throwables.propagate(e);
			}

		}

	}

}
