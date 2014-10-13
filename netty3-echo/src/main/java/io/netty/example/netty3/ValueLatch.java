package io.netty.example.netty3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
 
/**
 * The Class ValueLatch.
 *
 * @param <T> the generic type
 */
public class ValueLatch<T> {

	/** The value. */
	private T value = null;

	/** The done. */
	private final CountDownLatch done = new CountDownLatch(1);

	/**
	 * Checks if is sets the.
	 *
	 * @return true, if is sets the
	 */
	public boolean isSet() {
		return (done.getCount() == 0);
	}

	/**
	 * Sets the value.
	 *
	 * @param newValue the new value
	 */
	public synchronized void setValue(T newValue) {
		if (!isSet()) {
			value = newValue;
			done.countDown();
		}
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 * @throws InterruptedException the interrupted exception
	 */
	public T getValue() throws InterruptedException {
		done.await();
		synchronized (this) {
			return value;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param timeout the timeout
	 * @param unit the unit
	 * @return the value
	 * @throws InterruptedException the interrupted exception
	 */
	public T getValue(long timeout, TimeUnit unit) throws InterruptedException {
		done.await(timeout, unit);
		synchronized (this) {
			return value;
		}
	}
}
