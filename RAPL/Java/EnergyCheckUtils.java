import java.lang.reflect.Field;
import java.util.Properties;
import java.lang.ClassLoader;
import java.io.*;
import java.util.Scanner;
import java.lang.Number;
import java.time.LocalDateTime;

public class EnergyCheckUtils {
	static private Object obj = new Object();
	public native static int scale(int freq);
	public native static int[] freqAvailable();

	public native static double[] GetPackagePowerSpec();
	public native static double[] GetDramPowerSpec();
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);
	public native static int ProfileInit();
	public native static int GetSocketNum();
	public native static String EnergyStatCheck();
	public native static void ProfileDealloc();
	public native static void SetPowerLimit(int ENABLE);
	public static int wraparoundValue;

	public static int socketNum;
	
	static {
		System.setProperty("java.library.path", System.getProperty("user.dir"));
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) {}
		System.loadLibrary("CPUScaler");
		wraparoundValue = ProfileInit();
		socketNum = GetSocketNum();
	}

	/**
	 * @return an array of current energy information.
	 * The first entry is: Dram/uncore gpu energy(depends on the cpu architecture.
	 * The second entry is: CPU energy
	 * The third entry is: Package energy
	 */

	public static double[] getEnergyStats() {
		socketNum = GetSocketNum();
		String EnergyInfo = EnergyStatCheck();
		//System.out.println(EnergyInfo);
		/*One Socket*/
		if(socketNum == 1) {
			double[] stats = new double[3];
			String[] energy = EnergyInfo.split("#");

			stats[0] = Double.parseDouble(energy[0].replaceFirst(",","."));
			stats[1] = Double.parseDouble(energy[1].replaceFirst(",","."));
			stats[2] = Double.parseDouble(energy[2].replaceFirst(",","."));

			return stats;

		} else {
			/*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[] stats = new double[3*socketNum];
			int count = 0;


			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					count = i * 3 + j;	//accumulative count
					stats[count] = Double.parseDouble(energy[j].replaceFirst(",","."));
				}
			}
			return stats;
		}

	}

	public static int fibRecursion(int n) {
		if (n <= 1) return n;
		else return fibRecursion(n-1) + fibRecursion(n-2);
	}

	public static int fibIteration(int n) {
		int x = 0, y = 1, z = 1;
		for (int i = 0; i < n; i++) {
			x = y;
			y = z;
			z = x + y;
		}
		return x;
	}

	public static void fibonacciEnergy(int n){
		double[] before = getEnergyStats();
		//int fib = fibIteration(n);
		int fib = fibRecursion(n);
		double[] after = getEnergyStats();

		System.out.println("Fibonacci of "+n+" is "+fib+" and consumed "+(after[1]-before[1])+ "joules of CPU energy");
		ProfileDealloc();
	}

    /*
    public static void main(String[] args) {
    	fibonacciEnergy(Integer.parseInt(args[0]));
    }*/


	public static void main(String[] args) throws InterruptedException, IOException {

		
		String resultados = "Resultados/"+args[1];
		double[] before = getEnergyStats();
		
		try {

			FileWriter fstream = new FileWriter(resultados, true); //true tells to append data.
			BufferedWriter write = new BufferedWriter(fstream);
			String tempo, flag, flag1, flag2 = "";

			int i = Integer.parseInt(args[0]) * 60* 1000;

			LocalDateTime now = LocalDateTime.now();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			sb.append("Energy  consumption of dram");
			sb.append(';');
			sb.append("Energy consumption of cpu");
			sb.append(';');
			sb.append("Energy consumption of package");
			sb.append(';');
			sb.append("tempo");
			sb.append('\n');
			write.write(sb.toString());
			write.flush();
			double[] mid = null;
			double[] mid_before = before;
			int first_interation = 0;
			while (i > 0) {


				Thread.sleep(100);

				if (first_interation == 0) {
					mid = getEnergyStats();
					first_interation = 1;
				} else {
					mid_before = mid;
					mid = mid = getEnergyStats();
				}

				i = i - 100;

				flag = String.valueOf((mid[0] - mid_before[0]));
				sb1.append(flag);
				sb1.append(';');
				flag1 = String.valueOf((mid[1] - mid_before[1]));
				sb1.append(flag1);
				sb1.append(';');
				flag2 = String.valueOf((mid[2] - mid_before[2]));
				sb1.append(flag2);
				sb1.append(';');
				now = LocalDateTime.now();
				tempo = now.getHour()+":"+now.getMinute()+":"+now.getSecond()+"";
				sb1.append(tempo);
				sb1.append('\n');
				//reader.reset();
			}
			double[] after = getEnergyStats();

			flag = String.valueOf((after[0] - before[0]));
			sb1.append(flag);
			sb1.append(';');
			flag1 = String.valueOf((after[1] - before[1]));
			sb1.append(flag1);
			sb1.append(';');
			flag2 = String.valueOf((after[2] - before[2]));
			sb1.append(flag2);
			sb1.append(';');
			tempo = now.getHour()+":"+now.getMinute()+":"+now.getSecond()+"";
			sb1.append(tempo);
			sb1.append('\n');
			write.write(sb1.toString());
			write.flush();
			write.close();
		
		}
		
	 catch(Exception e){}
		
	 ProfileDealloc();
	}
}



