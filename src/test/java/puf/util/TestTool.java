package puf.util;

import puf.type.BytesArrayWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/25.
 */
public class TestTool {


    public static void main(String[] args) throws IOException {

        String[] data = new String[]{
                "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1",
                "2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2"
        };

        SeqFileGen("/Users/ogre0403/correctids", data);
    }

    public static void SeqFileGen(String path, String[] correctids) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        SequenceFile.Writer writer = SequenceFile.createWriter(
                fs, conf, new Path(path),Text.class, BytesWritable.class);


        for(int i=0;i< correctids.length;i++) {
            writer.append(new Text(""+ (i+1)), TestTool.toBytesWritable(correctids[i]));
        }
        writer.close();

    }

    public static byte[] toByteArray(String data){
        byte[] ba1 = new byte[16];
        StringTokenizer itr1 = new StringTokenizer(data,",");
        Integer I;
        int index = 0;
        while (itr1.hasMoreTokens()) {
            I = Integer.parseInt(itr1.nextToken().trim());
            ba1[index] = I.byteValue();
            index+=1;
        }
        return ba1;
    }


    public static BytesWritable toBytesWritable(String data){
        return new BytesWritable(toByteArray(data));
    }


    public static BytesArrayWritable toBytesArrayWritable(String[] data){

        BytesWritable[] result = new BytesWritable[data.length];
        for(int i = 0;i<data.length;i++){
            result[i] = toBytesWritable(data[i]);
        }

        return new BytesArrayWritable(result);
    }


    public static ArrayList<BytesWritable> toBytesWritableArray(String[] data){

        ArrayList<BytesWritable> result = new ArrayList<BytesWritable>();

        for(int i = 0;i<data.length;i++){
            result.add(toBytesWritable(data[i]));
        }

        return result;
    }

    public static IntWritable[] toBinaryIntArray(byte[] bs){
        IntWritable[] result = new IntWritable[bs.length*8];
        for(int i = 0;i<result.length;i++)
            result[i] = new IntWritable(0);

        for(int j = 0;j<bs.length;j++){
            for(int i = 0; i < 8; i++) {
                if(((0x80 >>> i) & bs[j]) > 0 ){
                    result[j*8+i].set(1);
                }
            }
        }
        return  result;
    }
}
