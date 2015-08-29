package mr.job;

import mr.combiner.CorrectIDCombiner;
import mr.mapper.CorrectIDMapper;
import mr.mapper.FlattenMapper;
import mr.reducer.CorrectIDReducer;
import mr.reducer.FlattenReducer;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
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
 * Created by 1403035 on 2015/8/24.
 */
public class IntraHD_2 extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {

        //TODO: input and output from CLI
        String flatten_input ="flatten_input/data";
        String flatten_output = "flatten_output/";
        String correct_input = flatten_output+"part-r-00000";
        String correct_output = "correctid_output/";
        String HD_input = correct_output+"part-r-00000";
        String HD_output = "HD_output/";

        Job flattenJob = Job.getInstance(getConf(), "Flatten input job");
        flattenJob.setJarByClass(IntraHD_2.class);
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


        Job correctIDJob = Job.getInstance(getConf(),"CorrectIDs Job");
        correctIDJob.setJarByClass(IntraHD_2.class);
        correctIDJob.setMapperClass(CorrectIDMapper.class);
        correctIDJob.setReducerClass(CorrectIDReducer.class);
        correctIDJob.setCombinerClass(CorrectIDCombiner.class);

        correctIDJob.setInputFormatClass(SequenceFileInputFormat.class);

        correctIDJob.setMapOutputKeyClass(Text.class);
        correctIDJob.setMapOutputValueClass(BytesWritable.class);
        correctIDJob.setOutputKeyClass(Text.class);
        correctIDJob.setOutputValueClass(DoubleWritable.class);
        correctIDJob.setNumReduceTasks(1);


        FileInputFormat.addInputPath(correctIDJob, new Path(HD_input));
        FileOutputFormat.setOutputPath(correctIDJob, new Path(HD_output));




        correctIDJob.setInputFormatClass(KeyValueTextInputFormat.class);
        correctIDJob.setMapOutputKeyClass(Text.class);
        correctIDJob.setMapOutputValueClass(BytesWritable.class);
        correctIDJob.setOutputKeyClass(Text.class);
        correctIDJob.setOutputValueClass(BytesArrayWritable.class);
        correctIDJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        correctIDJob.setNumReduceTasks(1);
        FileInputFormat.addInputPath(flattenJob, new Path(flatten_input));
        FileOutputFormat.setOutputPath(flattenJob, new Path(flatten_output));


        if (correctIDJob.waitForCompletion(true) == false)
            return 1;


        Job HDJob = Job.getInstance(getConf(), "Hamming distance Job");

        return HDJob.waitForCompletion(true) ? 0 : 1;

    }
}
