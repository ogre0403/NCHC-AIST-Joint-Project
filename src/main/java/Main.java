import mr.job.IntraHD_1;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        int v = 0;

        int[] radixa = new int[]{128,64,32,16,8,4,2,1};
        int[] bin = new int[]{1,0,0,0,0,1,0,1};

        for (int j = 0; j < 8; j++) {
//            int b = 7-j;
//            System.out.println(2<<1);
//            double raidx = Math.pow(2 , b);
            v = v + bin[j] * radixa[j];
//            System.out.println("aa" + raidx);


        }
        System.out.println(v);

        /*
        Configuration conf = new Configuration();
        conf.set("mapreduce.input.fileinputformat.split.maxsize","5242880");
        conf.set("mapreduce.job.queuename","root.MR");
        int res = ToolRunner.run(conf, new IntraHD_1(), args);
        System.exit(res);
        */
    }
}
