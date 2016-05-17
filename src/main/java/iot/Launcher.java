package iot;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import java.io.*;

/**
 * Created by 1403035 on 2016/5/16.
 */
public class Launcher {

    static Options opts;

    static String filename;
    static String ip ;
    static String topic ;
    static boolean isLocal = false;


    public static void main(String[] args) throws IOException {

        try {
            init(args);
        } catch (ParseException e) {
            System.out.println("Arg parse error");
            System.exit(1);
        }


        TailerListener listener  = new ForkMqttListener(ip, topic,isLocal);
        File file = new File(filename);

        Tailer tailer = new Tailer(file, listener, 1000);
        tailer.run();

        tailer.stop();

    }

    static void init(String[] args) throws ParseException {
        opts = new Options();
        opts.addOption("file",true,"");
        opts.addOption("ip",true,"");
        opts.addOption("topic",true,"");
        opts.addOption("local",false,"");


        CommandLine cliParser = new GnuParser().parse(opts, args);
        filename = cliParser.getOptionValue("file");
        ip = cliParser.getOptionValue("ip", "140.110.141.58");
        topic = cliParser.getOptionValue("topic", "test");
        isLocal = cliParser.hasOption("local");
    }

}


