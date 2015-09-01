import mr.job.IntraHD_1;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;


/**
 * Created by 1403035 on 2015/8/24.
 */
public class Intra1 {

    private static Logger logger = Logger.getLogger(Intra1.class);

    public static void main(String[] args) throws Exception {

        if(args.length < 3) {
            printUsage();
            return ;
        }

        Configuration conf = new Configuration();
        conf.set("mapreduce.input.fileinputformat.split.maxsize","5242880");
        conf.set("mapreduce.job.queuename", "root.MR");



        GenericOptionsParser parser = new GenericOptionsParser(args);
        String[] toolArgs = parser.getRemainingArgs();
        FileSystem fs = FileSystem.get(conf);
        String flatten_input_root = toolArgs[0];

        FileStatus[] fileStat = fs.listStatus(new Path(flatten_input_root));
        for(FileStatus fff : fileStat){
            if(fff.isFile()) {
                logger.info(fff.getPath());
                RunIntra1JobThread t1 = new RunIntra1JobThread(fff.getPath(),
                        conf, args,toolArgs);

                new Thread(t1).start();
            }
        }

    }

    private static void printUsage(){
        System.out.println("flatten_input, flatten_output, HD_output");

    }
}

class RunIntra1JobThread extends RunMRJobThread{

    private static Logger logger = Logger.getLogger(RunIntra1JobThread.class);

    public RunIntra1JobThread(Path p, Configuration conf,
                              String[] orig, String[] remain){
        super(p,conf,orig,remain);
    }


    @Override
    public void run() {

        String flatten_output_root = toolArgs[1];
        String HD_output_root = toolArgs[2];
        String sub = p1.getName();

        try {
            ToolRunner.run(conf,
                    new IntraHD_1(p1,
                            flatten_output_root + "/" + sub,
                            HD_output_root + "/" + sub),
                    args);
        }catch (Exception ioe){
            logger.error(ioe.getMessage());
        }
    }
}
