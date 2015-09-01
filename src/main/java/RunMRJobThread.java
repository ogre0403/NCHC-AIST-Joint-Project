import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/**
 * Created by ogre0403 on 2015/9/1.
 */
public abstract class RunMRJobThread implements Runnable{
    protected Path p1;
    protected Configuration conf;
    protected String[] args;
    protected String[] toolArgs;

    public RunMRJobThread(){}

    public  RunMRJobThread(Path p, Configuration conf,
                           String[] orig, String[] remain){
        this.p1 = p;
        this.conf =conf;
        this.args = orig;
        this.toolArgs = remain;
    }
}
