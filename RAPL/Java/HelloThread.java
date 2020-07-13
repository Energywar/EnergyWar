import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class HelloThread implements Runnable {

    public int i;
    public String outs;

    public HelloThread(String outs,int i){
        this.outs = outs;
        this.i = i;
    }


    public void run()   {

            String cmd = "sudo ";
    
            comando("sudo -S modprobe msr");
            comando("sudo -S make all");
            comando("sudo -S javac EnergyCheckUtils.java");
            comando("sudo -S java EnergyCheckUtils "+i+" "+ outs);    
            
    }

    public void comando(String comando){
        String s = null;

        try {
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec(comando,null);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));;

            /*
            String pas = "jackass20\n";

            writer.write(pas);
            writer.flush();
            */


            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

