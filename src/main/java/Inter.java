import mr.job.InterHD;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by ogre0403 on 2015/8/31.
 */
public class Inter {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapreduce.input.fileinputformat.split.maxsize","5242880");
        conf.set("mapreduce.job.queuename","root.MR");
        int res = ToolRunner.run(conf, new InterHD(), args);
        System.exit(res);
    }
}
