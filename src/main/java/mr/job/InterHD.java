package mr.job;

import mr.combiner.HDCombiner;
import mr.mapper.HDMapper;
import mr.mapper.IdentityMapper;
import mr.reducer.AverageHDReducer;
import mr.reducer.FlattenReducer;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;


/**
 * Created by ogre0403 on 2015/8/31.
 */
public class InterHD extends Configured implements Tool {

    private static Logger logger = Logger.getLogger(InterHD.class);

    @Override
    public int run(String[] args) throws Exception {

        //TODO: input and output from CLI

        String merge_input_dir = "merge_input/";
        String merge_output = "merge_output/";
        String HD_input = merge_output+"part-r-00000";
        String HD_output = "HD_output/";

        FileSystem fs = FileSystem.get(getConf());
        FileStatus[] fileStat = fs.listStatus(new Path(merge_input_dir));


        Job mergeJob = Job.getInstance(getConf(), "Merge CorrectIDs Job");
        mergeJob.setJarByClass(InterHD.class);
        mergeJob.setReducerClass(FlattenReducer.class);

        mergeJob.setMapOutputKeyClass(Text.class);
        mergeJob.setMapOutputValueClass(BytesWritable.class);

        mergeJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        mergeJob.setOutputKeyClass(Text.class);
        mergeJob.setOutputValueClass(BytesArrayWritable.class);
        mergeJob.setNumReduceTasks(1);

        for(FileStatus ff : fileStat) {
            if(ff.isDirectory()) {

                FileStatus[] ss = fs.listStatus(ff.getPath());

                for(FileStatus fff : ss){
                    if(fff.isFile()){
                        logger.info(fff.getPath());
                        MultipleInputs.addInputPath(mergeJob, fff.getPath(),
                                SequenceFileInputFormat.class, IdentityMapper.class);
                    }
                }
            }
        }



        FileOutputFormat.setOutputPath(mergeJob, new Path(merge_output));
        if (mergeJob.waitForCompletion(true) == false)
            return 1;


        Job HDJob = Job.getInstance(getConf(), "Hamming distance job");
        HDJob.setJarByClass(InterHD.class);
        HDJob.setMapperClass(HDMapper.class);
        HDJob.setCombinerClass(HDCombiner.class);
        HDJob.setReducerClass(AverageHDReducer.class);

        HDJob.setInputFormatClass(SequenceFileInputFormat.class);
        HDJob.setMapOutputKeyClass(IntWritable.class);
        HDJob.setMapOutputValueClass(IntPair.class);
        HDJob.setOutputKeyClass(IntWritable.class);
        HDJob.setOutputValueClass(DoubleWritable.class);
        HDJob.setNumReduceTasks(1);


        FileInputFormat.addInputPath(HDJob, new Path(HD_input));
        FileOutputFormat.setOutputPath(HDJob, new Path(HD_output));

        return HDJob.waitForCompletion(true) ? 0 : 1;

    }
}
