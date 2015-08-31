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
import org.apache.hadoop.io.IntWritable;
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

    private  Path FLATTEN_INPUT_FILE;
    private  Path FLATTEN_OUTPUT_DIR;
    private  Path HD_INPUT_FILE;
    private  Path HD_OUTPUT_DIR;

    public IntraHD_1(Path flatten_input_file,
                     String flatten_output_dir,
                     String HD_output_dir){
        FLATTEN_INPUT_FILE = flatten_input_file;
        FLATTEN_OUTPUT_DIR = new Path(flatten_output_dir);
        HD_INPUT_FILE = new Path(flatten_output_dir + "/part-r-00000");
        HD_OUTPUT_DIR = new Path(HD_output_dir);

    }

        @Override
    public int run(String[] args) throws Exception {


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

        FileInputFormat.addInputPath(flattenJob, FLATTEN_INPUT_FILE);
        FileOutputFormat.setOutputPath(flattenJob, FLATTEN_OUTPUT_DIR);

        if (flattenJob.waitForCompletion(true) == false)
            return 1;


        // then calculate HD MR job
        Job HDJob = Job.getInstance(getConf(), "Hamming distance job");
        HDJob.setJarByClass(IntraHD_1.class);
        HDJob.setMapperClass(HDMapper.class);
        HDJob.setCombinerClass(HDCombiner.class);
        HDJob.setReducerClass(AverageHDReducer.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(IntWritable.class);
        HDJob.setMapOutputValueClass(IntPair.class);
        HDJob.setOutputKeyClass(IntWritable.class);
        HDJob.setOutputValueClass(DoubleWritable.class);
        HDJob.setNumReduceTasks(1);

        FileInputFormat.addInputPath(HDJob, HD_INPUT_FILE);
        FileOutputFormat.setOutputPath(HDJob, HD_OUTPUT_DIR);

        return HDJob.waitForCompletion(true) ? 0 : 1;
    }
}
