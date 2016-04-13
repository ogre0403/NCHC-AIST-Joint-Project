package puf.type;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import java.util.Arrays;


/**
 * Created by 1403035 on 2015/8/25.
 */
public class IntArrayWritable extends ArrayWritable {

    public IntArrayWritable() {
        super(IntWritable.class);
    }

    public IntArrayWritable(IntWritable[] data) {
        super(IntWritable.class, data);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntArrayWritable){
            IntArrayWritable aa = (IntArrayWritable)obj;
            IntWritable[] a = (IntWritable[])aa.toArray();
            IntWritable[] b = (IntWritable[])this.toArray();
            return Arrays.equals(a, b);
        }

        return false;
    }

    @Override
    public String toString() {
        IntWritable[] aa = (IntWritable[])this.toArray();
        StringBuilder b = new StringBuilder();
        b.append("[");
        for(int i =0;i<aa.length;i++){
            b.append(aa[i].toString());
            if (i == aa.length -1)
                break;
            b.append(":");
        }
        b.append("]");
        return b.toString();
    }

    @Override
    public int hashCode() {
        return 123456;
    }

}
