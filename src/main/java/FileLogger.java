import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {




    public static void log(String s) {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;



        try {

            // This block configure the logger with handler and formatter

            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String strDate = dateFormat.format(date).replace(" ","").replace(":","");
            System.out.println(strDate);
            fh = new FileHandler(strDate+".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info(s);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public static void main(String args[]){

    }
}
