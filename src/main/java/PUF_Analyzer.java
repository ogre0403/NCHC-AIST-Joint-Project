import mr.BytesArrayWritable;
import mr.HD.AverageHDReducer;
import mr.HD.HDCombiner;
import mr.HD.HDMapper;
import mr.IntPair;
import mr.flatten.FlattenMapper;
import mr.flatten.FlattenReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


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


        Configuration conf = new Configuration();

        //TODO: read queue name from CLI
        conf.set("mapreduce.job.queuename","root.MR");

        String flatten_input ="flatten_input/data";
        String flatten_output = "flatten_output/";
        String HD_input = flatten_output+"part-r-00000";
        String HD_output = "HD_output/";

        // Flatten MR job first


        Job flattenJob = Job.getInstance(conf, "Flatten input job");
        flattenJob.setJarByClass(PUF_Analyzer.class);
        flattenJob.setMapperClass(FlattenMapper.class);
        flattenJob.setReducerClass(FlattenReducer.class);

        flattenJob.setInputFormatClass(KeyValueTextInputFormat.class);
        flattenJob.setMapOutputKeyClass(Text.class);
        flattenJob.setMapOutputValueClass(BytesWritable.class);
        flattenJob.setOutputKeyClass(Text.class);
        flattenJob.setOutputValueClass(BytesArrayWritable.class);
        flattenJob.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(flattenJob, new Path(flatten_input));
        FileOutputFormat.setOutputPath(flattenJob, new Path(flatten_output));

        if (flattenJob.waitForCompletion(true) == false)
            return 1;


        // then calculate HD MR job

        Job HDJob = Job.getInstance(conf, "Hamming distance job");
        HDJob.setJarByClass(PUF_Analyzer.class);
        HDJob.setMapperClass(HDMapper.class);
        HDJob.setCombinerClass(HDCombiner.class);
        HDJob.setReducerClass(AverageHDReducer.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(Text.class);
        HDJob.setMapOutputValueClass(IntPair.class);
        HDJob.setOutputKeyClass(Text.class);
        HDJob.setOutputValueClass(DoubleWritable.class);


        FileInputFormat.addInputPath(HDJob, new Path(HD_input));
        FileOutputFormat.setOutputPath(HDJob, new Path(HD_output));

        return HDJob.waitForCompletion(true) ? 0 : 1;
    }
}
