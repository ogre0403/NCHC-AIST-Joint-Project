import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import util.HammingDistance;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class PUF_Analyzer extends Configured implements Tool {



    public static void main(String[] args) throws  Exception{
        int res = ToolRunner.run(new Configuration(), new PUF_Analyzer(), args);
        System.exit(res);

    }

    @Override
    public int run(String[] args) throws Exception {
        // Flatten MR job first
        // then calculate HD MR job

        return 0;
    }
}
