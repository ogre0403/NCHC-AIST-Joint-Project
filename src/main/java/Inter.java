import mr.job.InterHD;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ToolRunner;

/**
 * main function to launch Inter HD calculation
 */
public class Inter {
    public static void main(String[] args) throws Exception {

        if(args.length < 3) {
            printUsage();
            return ;
        }

        Configuration conf = new Configuration();
        conf.set("mapreduce.input.fileinputformat.split.maxsize","5242880");
        conf.set("mapreduce.job.queuename","root.MR");

        GenericOptionsParser parser = new GenericOptionsParser(args);
        String[] toolArgs = parser.getRemainingArgs();


        int res = ToolRunner.run(conf, new InterHD(toolArgs[0],
                toolArgs[1],toolArgs[2]), args);
        System.exit(res);
    }

    private static void printUsage(){
        System.out.println("merge_input, merge_output, HD_output");

    }
}
