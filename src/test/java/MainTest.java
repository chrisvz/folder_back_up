import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainTest {


    public static void main(String args[]){
        MainTest m = new MainTest();

        String input = PropertyReader.readProperty("INPUT_PATH");
        String output = PropertyReader.readProperty("OUTPUT_PATH");


        String filename = "sample1" + ".jpg";
        String inFolder=  input;
        String outFolder = output;
        String rename = "test1" + ".jpg";

        m.backup(filename,inFolder,outFolder,rename);
       // m.copyFileUsingStream(filename,inFolder,outFolder,rename);
    }





    @SuppressWarnings("resource")
    private  void copyFileUsingStream(String filename,String inFolder, String outFolder,String rename)  {
        File oldFile = new File(inFolder, filename);

        System.out.println(oldFile.getPath());
        File newFile = new File(outFolder, rename);


        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {

            inChannel = new FileInputStream(oldFile).getChannel();
            outChannel = new FileOutputStream(newFile).getChannel();
            // Try to change this but this is the number I tried.. for Windows, 64Mb - 32Kb)
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;

            while (position < size) {
                position += inChannel.transferTo(position, maxCount, outChannel);
            }
            System.out.println(filename + " back up as: "+ rename+ " in: "+outFolder+"\n");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            try{
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }


            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }






    public void backup(String filename,String inFolder, String outFolder,String rename) {

        File oldFile = new File( inFolder, filename );
        File newFile = new File( outFolder, rename );



        try
        {
            FileInputStream fis = new FileInputStream( oldFile );
            FileOutputStream fos = new FileOutputStream( newFile );


            try
            {
                int currentByte = fis.read();
                while( currentByte != -1 )
                {
                    fos.write( currentByte );
                    currentByte = fis.read();
                }
            }
            catch( IOException exception )
            {
                //  FileLogger.log(exception.getMessage());
                System.err.println( "IOException occurred!" );
                System.out.println(exception.getMessage()+"\n");
            }
            finally
            {
                System.out.println(filename + " back up as: "+ rename+ " in: "+outFolder+"\n");
                fis.close();
                fos.close();
                System.out.println( "Copied file!" );
            }
        }
        catch( IOException exception )
        {
            //  FileLogger.log(exception.getMessage());
            exception.printStackTrace();
            System.out.println(exception.getMessage()+"\n");
        }

    }

}
