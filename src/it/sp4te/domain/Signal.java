/**
 * Classe che modella i segnali nel discreto
 * @author A.Goggia & J.Longo
 */
package it.sp4te.domain;

import java.util.Arrays;

public class Signal {

	public int length;
	public double[] reale;
	public double[] immaginaria;

	public Signal() {}

	public Signal(double[] reale, double[] immaginaria, int lenght){
		Signal signal = new Signal();
		signal.setLength(lenght);
		signal.setImmaginaria(immaginaria);
		signal.setReale(reale);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public double[] getReale() {
		return reale;
	}

	public void setReale(double[] reale) {
		this.reale = reale;
	}

	public double[] getImmaginaria() {
		return immaginaria;
	}

	public void setImmaginaria(double[] immaginaria) {
		this.immaginaria = immaginaria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(immaginaria);
		result = prime * result + length;
		result = prime * result + Arrays.hashCode(reale);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Signal other = (Signal) obj;
		if (!Arrays.equals(immaginaria, other.immaginaria))
			return false;
		if (length != other.length)
			return false;
		if (!Arrays.equals(reale, other.reale))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Signal [length=" + length + ", reale=" + Arrays.toString(reale)
				+ ", immaginaria=" + Arrays.toString(immaginaria) + "]";
	}
}