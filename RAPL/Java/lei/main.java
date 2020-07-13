import java.io.*;
import java.net.ServerSocket;

public class main {

    public static void main(String[] args) throws InterruptedException{
        //HelloThread c = new HelloThread();
        //Thread b = new Thread(c);
        //b.start();
        //Thread.sleep(60*1000);
        File a = new File("/home/joao/Desktop/RAPL/Java");
        //comando("java -jar new_ch_insta.jar",a);


        for(int i = 0; i< args.length;i= i+2){
            
            String script = args[i]+".jar";
            int tempo  = i+1;
            
            for(int j = 0; j<10;j++){
                String cmd = "java -jar " + script;
                String out = args[i] +j+".csv";
                HelloThread c = new HelloThread(out,tempo);
                Thread b = new Thread(c);
                b.start();
                Thread.sleep(60*1000);
                comando(cmd,a);
                b.join();
                
            }
        }

    }

    public static void fichei(String put){

        BufferedWriter out = null;

        try {
            FileWriter fstream = new FileWriter("/home/joao/Desktop/RAPL/Java/out.txt", false); //true tells to append data.
            out = new BufferedWriter(fstream);
            out.write(put);
            out.flush();
            out.close();

        }

        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    public static void comando(String comando,File dir) {
        String s = null;

        try {
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec(comando,null,dir);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            ;

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
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

