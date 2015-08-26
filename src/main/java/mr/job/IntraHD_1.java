package mr.job;

import mr.type.BytesArrayWritable;
import mr.reducer.AverageHDReducer;
import mr.combiner.HDCombiner;
import mr.mapper.HDMapper;
import mr.type.IntPair;
import mr.mapper.FlattenMapper;
import mr.reducer.FlattenReducer;
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


/**
 * Created by 1403035 on 2015/8/13.
 */
public class IntraHD_1 extends Configured implements Tool {

        @Override
    public int run(String[] args) throws Exception {

        //TODO: input and output from CLI
        String flatten_input ="flatten_input/data";
        String flatten_output = "flatten_output/";
        String HD_input = flatten_output+"part-r-00000";
        String HD_output = "HD_output/";

        // Flatten MR job first

        Job flattenJob = Job.getInstance(getConf(), "Flatten input job");
        flattenJob.setJarByClass(IntraHD_1.class);
        flattenJob.setMapperClass(FlattenMapper.class);
        flattenJob.setReducerClass(FlattenReducer.class);

        flattenJob.setInputFormatClass(KeyValueTextInputFormat.class);
        flattenJob.setMapOutputKeyClass(Text.class);
        flattenJob.setMapOutputValueClass(BytesWritable.class);
        flattenJob.setOutputKeyClass(Text.class);
        flattenJob.setOutputValueClass(BytesArrayWritable.class);
        flattenJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        flattenJob.setNumReduceTasks(1);
        FileInputFormat.addInputPath(flattenJob, new Path(flatten_input));
        FileOutputFormat.setOutputPath(flattenJob, new Path(flatten_output));

        if (flattenJob.waitForCompletion(true) == false)
            return 1;


        // then calculate HD MR job
        Job HDJob = Job.getInstance(getConf(), "Hamming distance job");
        HDJob.setJarByClass(IntraHD_1.class);
        HDJob.setMapperClass(HDMapper.class);
        HDJob.setCombinerClass(HDCombiner.class);
        HDJob.setReducerClass(AverageHDReducer.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(Text.class);
        HDJob.setMapOutputValueClass(IntPair.class);
        HDJob.setOutputKeyClass(Text.class);
        HDJob.setOutputValueClass(DoubleWritable.class);
        HDJob.setNumReduceTasks(1);


        FileInputFormat.addInputPath(HDJob, new Path(HD_input));
        FileOutputFormat.setOutputPath(HDJob, new Path(HD_output));

        return HDJob.waitForCompletion(true) ? 0 : 1;
    }
}
