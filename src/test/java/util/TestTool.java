package util;

import mr.type.BytesArrayWritable;
import org.apache.hadoop.io.BytesWritable;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/25.
 */
public class TestTool {
    public static BytesArrayWritable toBytesArrayWritable(String[] data){

        StringTokenizer itr = null;
        byte[] ba1 = null;
        BytesWritable[] result = new BytesWritable[data.length];

        for(int i = 0;i<data.length;i++){
            ba1 = new byte[16];
            StringTokenizer itr1 = new StringTokenizer(data[i],",");
            Integer I;
            int index = 0;
            while (itr1.hasMoreTokens()) {
                I = Integer.parseInt(itr1.nextToken().trim());
                ba1[index] = I.byteValue();
                index+=1;
            }
            result[i] = new BytesWritable(ba1);
        }

        return new BytesArrayWritable(result);

    }


    public static ArrayList<BytesWritable> toBytesWritableArray(String[] data){

        ArrayList<BytesWritable> result = new ArrayList<BytesWritable>();

        StringTokenizer itr = null;
        byte[] ba1 = null;

        for(int i = 0;i<data.length;i++){
            ba1 = new byte[16];
            StringTokenizer itr1 = new StringTokenizer(data[i],",");
            Integer I;
            int index = 0;
            while (itr1.hasMoreTokens()) {
                I = Integer.parseInt(itr1.nextToken().trim());
                ba1[index] = I.byteValue();
                index+=1;
            }
            result.add(new BytesWritable(ba1));
        }

        return result;
    }
}
