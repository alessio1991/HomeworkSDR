/**
 * Classe per il calcolo della soglia e della percentuale di detection
 * @author A.Goggia & J.Longo
 */
package it.sp4te.signalprocessing;

import it.sp4te.domain.Noise;
import it.sp4te.domain.Signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class SignalProcessor {

	/** Metodo per la determinazione dell’energia del segnale
	 * (sommatoria modulo quadro dei campioni)
	 * 
	 * @param complex
	 * @return energia
	 */
	public static double energia(double[] reale, double[] immaginaria){
		double zr = 0;
		double zi = 0;
		for(double r : reale)
			zr = zr +	Math.pow(r,2);
		for(double i : immaginaria)
			zi =zi + Math.pow(i,2);
		return zr + zi;
	}

	/**Metodo per il calcolo della soglia
	 * 
	 * @param snr
	 * @return soglia
	 * @throws Exception
	 */
	public static double calcoloSoglia(double snr) {
		double soglia = 0;
		double[] vettoreRumore = new double[1000];
		int i = 0;
		while(i < vettoreRumore.length){
			Noise noise = new Noise(snr, 1000, 1);
			double e = energia(noise.getParteReale(), noise.getParteImmaginaria());
			vettoreRumore[i] = e;
			i++;
		}			
		try {
			soglia = valorMedio(vettoreRumore) + (2 * Math.sqrt(varianza(vettoreRumore))* InvErf(1-2*Math.pow(10, -3)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return soglia;
	}

	public static double valorMedio(double[] array){
		double somma = 0;
		for(double i : array)
			somma = somma + i;
		return (somma/ array.length);
	}

	public static double varianza(double[] array){
		double medio = valorMedio(array);
		double temp = 0;
		for(double a :array)
			temp += (medio-a)*(medio-a);
		return temp/array.length;
	}

	/**
	 * Metodo per il calcolo della funzione di errore inversa
	 * @param d
	 * @return funzione
	 * @throws Exception
	 */
	public static double InvErf(double d) throws Exception {
		if (Math.abs(d)>1) {
			throw new Exception ("Allowed values for argument in [-1,1]");
		}
		if (Math.abs(d) == 1) {
			return (d==-1 ? Double.NEGATIVE_INFINITY :
				Double.POSITIVE_INFINITY);
		}
		else {
			if (d==0) {
				return 0;
			}
			BigDecimal bd = new BigDecimal(0, MathContext.UNLIMITED);
			BigDecimal x = new
					BigDecimal(d*Math.sqrt(Math.PI)/2,MathContext.UNLIMITED);
			String[] A092676 = {"1", "1", "7", "127", "4369", "34807",
					"20036983", "2280356863", 
					"49020204823", "65967241200001",
					"15773461423793767",
					"655889589032992201",
					"94020690191035873697", "655782249799531714375489",
					"44737200694996264619809969",
					"10129509912509255673830968079", "108026349476762041127839800617281",
					"10954814567103825758202995557819063",
					"61154674195324330125295778531172438727",
					"54441029530574028687402753586278549396607",
					"452015832786609665624579410056180824562551",
					"2551405765475004343830620568825540664310892263",

					"70358041406630998834159902148730577164631303295543",
					"775752883029173334450858052496704319194646607263417",

			"132034545522738294934559794712527229683368402215775110881"};

			String[] A092677 = {"1", "3", "30", "630", "22680", "178200",
					"97297200", "10216206000", 
					"198486288000", "237588086736000",
					"49893498214560000", 
					"1803293578326240000",
					"222759794969712000000","1329207696584271504000000",
					"77094046401887747232000000",
					"14761242414008506896480000000", "132496911908140357902804480000000",
					"11262237512191930421738380800000000",
					"52504551281838779626144331289600000000",
					"38905872499842535702972949485593600000000",
					"268090886133368733415443853598208000000000",
					"1252532276140582782027102181569679872000000000",
					"28520159927721069946757116674341610685440000000000",

					"259078091444256105986928093487086396226560000000000",
			"36256424429074976496234665114956818633529712640000000000"};

			for (int i = 0; i < A092676.length; i++) {                
				BigDecimal num = new BigDecimal(new BigInteger(A092676[i]),
						50);
				BigDecimal den = new BigDecimal(new BigInteger(A092677[i]),
						50);
				BigDecimal coeff = num.divide(den, RoundingMode.HALF_UP);
				BigDecimal xBD = x.pow(i*2+1, MathContext.UNLIMITED);           
				bd = bd.add(xBD.multiply(coeff, MathContext.UNLIMITED));       
			}            
			return bd.doubleValue();            
		}
	}

	/**
	 * Leggo i campioni presi da un file
	 * Il percorso lo passo come parametro
	 * @param pathIn
	 * @return i campioni letti
	 */
	public static Signal leggiCampioni(String pathIn){
		double[] reale = new double[1000000];
		double[] immaginaria = new double[1000000];
		BufferedReader br = null;
		int indice = 0;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(pathIn));
			while ((sCurrentLine = br.readLine()) != null && indice<reale.length) {
				int i = sCurrentLine.indexOf("\t");
				double re = Double.parseDouble(sCurrentLine.substring(0, i));
				double imm = Double.parseDouble(sCurrentLine.substring(i));
				reale[indice] = re;
				immaginaria[indice] = imm;
				indice++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		Signal signal = new Signal();
		signal.setLength(1000000);
		signal.setImmaginaria(immaginaria);
		signal.setReale(reale);
		return signal;
	}

	/**
	 * Metodo per il calcolo del vettore reale
	 * @param signal
	 * @return vettore reale
	 */
	public static double[][] vettoreReale(Signal signal){
		double[] reale = signal.getReale();
		double[][] arrayReale = new double[1000][1000];
		int c= 0;
		for(int i= 0; i<1000; i++){
			for(int j=0; j<1000; j++){
				arrayReale[i][j] = reale[c];
				c++;				
			}
		}
		return arrayReale;
	}

	/**
	 * Metodo per il calcolo del vettore immaginario
	 * @param signal
	 * @return vettore imamginario
	 */
	public static double[][] vettoreImmaginario(Signal signal){
		double[] immaginaria = signal.getImmaginaria();
		double[][] arrayImmaginaria = new double[1000][1000];
		int c = 0;
		for(int i= 0; i<1000; i++){
			for(int j=0; j<1000; j++){
				arrayImmaginaria[i][j] = immaginaria[c];
				c++;				
			}
		}
		return arrayImmaginaria;
	}	

	/**
	 * Matodo per il calcolo del vettore di energie
	 * @param ar
	 * @param ai
	 * @return vettore di energie
	 */
	public static double[] vettoreEnergie(double[][] ar, double[][] ai){
		double[] energy = new double[1000];
		for(int i=0; i<ar.length; i++){
			double[] reale = new double[1000];
			double[] immagginario = new double[1000];
			for(int j=0; j<reale.length; j++){
				reale[j] = ar[i][j];
				immagginario[j] = ai[i][j];
			}
			double e = energia(reale, immagginario);
			energy[i] = e;
		}
		return energy;
	}

	public static String percentualeDetection(double[] array, double soglia){
		int count = 0;
		for(double a : array)
			if(a > soglia)
				count++;
		double percentualeDetection = ((double)count/array.length)*100;
		String percentuale = percentualeDetection + "%";
		return percentuale;		
	}

	public static void main(String[] args){
		System.out.println("*******************************");
		System.out.println("SEQUENZA 1: -13db");
		double snrDB = -13;
		double soglia = calcoloSoglia(snrDB);
		Signal signal = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_1\\output_SNR=-10dB.dat");
		double[][] vettoreReale =  vettoreReale(signal);
		double[][] vettoreImm =  vettoreImmaginario(signal);
		double[] vettEnergie = vettoreEnergie(vettoreReale, vettoreImm);
		System.out.println("Soglia: "+soglia);
		System.out.println("Percentuale: " + percentualeDetection(vettEnergie, soglia));	
		System.out.println();
		System.out.println("SEQUENZA 1: -8db");
		double snrDB8 = -8;
		double soglia8 = calcoloSoglia(snrDB8);
		Signal signal8 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_1\\output_SNR=-5dB.dat");
		double[][] vettoreR8 =  vettoreReale(signal8);
		double[][] vettoreI8 =  vettoreImmaginario(signal8);
		double[] vettoreEnergie8 = vettoreEnergie(vettoreR8, vettoreI8);
		System.out.println("Soglia: "+soglia8);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie8, soglia8));
		System.out.println();
		System.out.println("SEQUENZA 1: -3db");
		double snrDB3 = -3;
		double soglia3 = calcoloSoglia(snrDB3);
		Signal signal3 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_1\\output_SNR=0dB.dat");
		double[][] vettoreImm3 =  vettoreImmaginario(signal3);
		double[] vettoreEnergie3 = vettoreEnergie(vettoreReale, vettoreImm3);
		System.out.println("Soglia: "+soglia3);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie3, soglia3));
		System.out.println();
		System.out.println("SEQUENZA 1: 2db");
		double snrDB2 = 2;
		double soglia2 = calcoloSoglia(snrDB2);
		Signal signal2 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_1\\output_SNR=5dB.dat");
		double[][] vettoreReale2 =  vettoreReale(signal2);
		double[][] vettoreImm2 =  vettoreImmaginario(signal2);
		double[] vettoreEnergie2 = vettoreEnergie(vettoreReale2, vettoreImm2);
		System.out.println("Soglia: "+soglia2);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie2, soglia2));

		System.out.println();
		System.out.println();
		System.out.println("*******************************");
		System.out.println("SEQUENZA 2: -13db");
		double snrDB213 = -13;
		double soglia213 = calcoloSoglia(snrDB213);
		Signal signal213 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_2\\output_SNR=-10dB.dat");
		double[][] vettoreImm213 =  vettoreImmaginario(signal213);
		double[] vettoreEnergie213 = vettoreEnergie(vettoreReale, vettoreImm213);
		System.out.println("Soglia: "+soglia213);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie213, soglia213));	
		System.out.println();
		System.out.println("SEQUENZA 2: -8db");
		double snrDB28 = -8;
		double soglia28 = calcoloSoglia(snrDB28);
		Signal signal28 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_2\\output_SNR=-5dB.dat");
		double[][] vettoreReale28 =  vettoreReale(signal28);
		double[][] vettoreImm28 =  vettoreImmaginario(signal28);
		double[] vettoreEnergie28 = vettoreEnergie(vettoreReale28, vettoreImm28);
		System.out.println("Soglia: "+soglia28);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie28, soglia28));
		System.out.println();
		System.out.println("SEQUENZA 2: -3db");
		double snrDB23 = -3;
		double soglia23 = calcoloSoglia(snrDB23);
		Signal signal23 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_2\\output_SNR=0dB.dat");
		double[][] vettoreReale23 =  vettoreReale(signal23);
		double[][] vettoreImm23 =  vettoreImmaginario(signal23);
		double[] vettEnergie23 = vettoreEnergie(vettoreReale23, vettoreImm23);
		System.out.println("Soglia: "+soglia23);
		System.out.println("Percentuale: " + percentualeDetection(vettEnergie23, soglia23));
		System.out.println();
		System.out.println("SEQUENZA 2: 2db");
		double snrDB22 = 2;
		double soglia22 = calcoloSoglia(snrDB22);
		Signal signal22 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_2\\output_SNR=5dB.dat");
		double[][] vettoreReale22 =  vettoreReale(signal22);
		double[][] vettoreImm22 =  vettoreImmaginario(signal22);
		double[] vettoreEnergie22 = vettoreEnergie(vettoreReale22, vettoreImm22);
		System.out.println("Soglia: "+soglia22);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie22, soglia22));

		System.out.println();
		System.out.println();
		System.out.println("*******************************");
		System.out.println("SEQUENZA 3: -13db");
		double snrDB313 = -13;
		double soglia313 = calcoloSoglia(snrDB313);
		Signal signal313 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_3\\output_SNR=-10dB.dat");
		double[][] vettoreReale313 =  vettoreReale(signal313);
		double[][] vettoreImm313 =  vettoreImmaginario(signal313);
		double[] vettoreEnergie313 = vettoreEnergie(vettoreReale313, vettoreImm313);
		System.out.println("Soglia: "+soglia313);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie313, soglia313));	
		System.out.println();
		System.out.println("SEQUENZA 3: -8db");
		double snrDB38 = -8;
		double soglia38 = calcoloSoglia(snrDB38);
		Signal signal38 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_3\\output_SNR=-5dB.dat");
		double[][] vettoreReale38 =  vettoreReale(signal38);
		double[][] vettoreImm38 =  vettoreImmaginario(signal38);
		double[] vettoreEnergie38 = vettoreEnergie(vettoreReale38, vettoreImm38);
		System.out.println("Soglia: "+soglia38);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie38, soglia38));
		System.out.println();
		System.out.println("SEQUENZA 3: -3db");
		double snrDB33 = -3;
		double soglia33 = calcoloSoglia(snrDB33);
		Signal signal33 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_3\\output_SNR=0dB.dat");
		double[][] vettoreReale33 =  vettoreReale(signal33);
		double[][] vettoreImm33 =  vettoreImmaginario(signal33);
		double[] vettoreEnergie33 = vettoreEnergie(vettoreReale33, vettoreImm33);
		System.out.println("Soglia: "+soglia33);
		System.out.println("Percentuale: " + percentualeDetection(vettoreEnergie33, soglia33));
		System.out.println();
		System.out.println("SEQUENZA 3: 2db");
		double snrDB32 = 2;
		double soglia32 = calcoloSoglia(snrDB32);
		Signal signal32 = leggiCampioni("C:\\Users\\Alessio\\Desktop\\Sequenza_3\\output_SNR=5dB.dat");
		double[][] vettoreReale32 =  vettoreReale(signal32);
		double[][] vettoreImm32 =  vettoreImmaginario(signal32);
		double[] vettEnergie32 = vettoreEnergie(vettoreReale32, vettoreImm32);
		System.out.println("Soglia: "+soglia32);
		System.out.println("Percentuale: " + percentualeDetection(vettEnergie32, soglia32));
		System.out.println("*******************************");
		System.out.println("*************FINE**************");
		System.out.println("*******************************");
	}
}