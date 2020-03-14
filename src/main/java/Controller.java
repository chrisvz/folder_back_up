import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {
    @FXML
    private Button bt1,bt2,bt3,excelbtn;
    @FXML
    private TextField tf1,tf2,tf3;

    @FXML
    private TextArea textArea;

    @FXML
    private ProgressBar progressBar;



    private Task copyWorker;



    @FXML
    public void initialize(){




        excelbtn.setDisable(true);
        textArea.setEditable(false);

        tf1.setEditable(false);
        tf2.setEditable(false);
        tf3.setEditable(false);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));

        textArea.appendText("FileBackup v1 started"+"\n");
        textArea.appendText(cal.getTime()+"\n");
        tf1.setText(PropertyReader.readProperty("EXCEL_PATH"));
        tf2.setText(PropertyReader.readProperty("INPUT_PATH"));
        tf3.setText(PropertyReader.readProperty("OUTPUT_PATH"));

        if(!tf1.getText().equals("") && !tf2.getText().equals("") && !tf3.getText().equals("")){
            excelbtn.setDisable(false);
        }
    }

    @FXML
    public void excelButton() {



        FileChooser  f = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "excel", "*.xls", "*.xlsx");

        f.getExtensionFilters().add(fileExtensions);


        File selected = f.showOpenDialog(bt1.getScene().getWindow());
        if(selected!=null) {
            tf1.setText(selected.getAbsolutePath());

            PropertyReader.changeProperty("EXCEL_PATH", selected.getAbsolutePath());
        }


        if(!tf1.getText().equals("") && !tf2.getText().equals("") && !tf3.getText().equals("")){
            excelbtn.setDisable(false);
        }
    }

    @FXML
    public void sourceButton() {

        DirectoryChooser f = new DirectoryChooser();
        File selected = f.showDialog(bt2.getScene().getWindow());
        if(selected!=null) {
            tf2.setText(selected.getAbsolutePath());
            PropertyReader.changeProperty("INPUT_PATH", selected.getAbsolutePath());
        }
        if(!tf1.getText().equals("") && !tf2.getText().equals("") && !tf3.getText().equals("")){
            excelbtn.setDisable(false);
        }
    }

    @FXML
    public void destinationButton() {
        DirectoryChooser f = new DirectoryChooser();
        File selected = f.showDialog(bt3.getScene().getWindow());
        if(selected!=null) {
            tf3.setText(selected.getAbsolutePath());
            PropertyReader.changeProperty("OUTPUT_PATH", selected.getAbsolutePath());
        }
        if(!tf1.getText().equals("") && !tf2.getText().equals("") && !tf3.getText().equals("")){
            excelbtn.setDisable(false);
        }

    }





    @FXML
    public <K, V> void iterate() {


        String in = PropertyReader.readProperty("INPUT_PATH");
        String out = PropertyReader.readProperty("OUTPUT_PATH");

        System.out.println(in);
        System.out.println(out);
        System.out.println("read excel");


        // GET THE LIST FROM THE EXCEL FILE
        ArrayList<ExcelRow> rowList = readExcel();

//        for(int f=0; f < readExcel().size(); f++){
//            System.out.println(readExcel().get(f).getNames() + " "+readExcel().get(f).getNamingFormat()) ;
//        }

        String format = null;

        ListMultimap<String, String> multiMap = ArrayListMultimap.create();


        for (int i = 0; i < rowList.size(); i++) {

            String[] arr = rowList.get(i).getNames();
            format = rowList.get(i).getNamingFormat();

            for (int x = 0; x < arr.length; x++) {
                if (x == 0) {
                    multiMap.put(arr[x], format);
                } else {
                    multiMap.put(arr[x], format + "-" + x);

                }

            }
        }


        ListMultimap<K, V> f = (ListMultimap<K, V>) multiMap;

        String input = PropertyReader.readProperty("INPUT_PATH");
        String output = PropertyReader.readProperty("OUTPUT_PATH");

        System.out.println(input);
        System.out.println(output);
        int elements = 1;
        for (Map.Entry<K, V> entry : f.entries()) {
            copyFileUsingStream(entry.getKey() + ".jpg", input, output, entry.getValue() + ".jpg");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));

        textArea.appendText("FileBackup v1 finished"+"\n");
        textArea.appendText(cal.getTime()+"\n");
    }



    public ListMultimap<String, String> getMultiMap() {
        String in = PropertyReader.readProperty("INPUT_PATH");
        String out = PropertyReader.readProperty("OUTPUT_PATH");

        System.out.println(in);
        System.out.println(out);
        System.out.println("read excel");


        // GET THE LIST FROM THE EXCEL FILE
        ArrayList<ExcelRow> rowList = readExcel();

//        for(int f=0; f < readExcel().size(); f++){
//            System.out.println(readExcel().get(f).getNames() + " "+readExcel().get(f).getNamingFormat()) ;
//        }

        String format = null;

        ListMultimap<String, String> multiMap = ArrayListMultimap.create();


        for (int i = 0; i < rowList.size(); i++) {

            String[] arr = rowList.get(i).getNames();
            format = rowList.get(i).getNamingFormat();

            for (int x = 0; x < arr.length; x++) {
                if (x == 0) {
                    multiMap.put(arr[x], format);
                } else {
                    multiMap.put(arr[x], format + "-" + x);

                }

            }
        }
        return multiMap;

    }


    @FXML
    public void runButton(ActionEvent actionEvent)  {

        //progressBar.setProgress(0);
        copyWorker = createWorker();


        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());

        Thread t = new Thread(copyWorker);
        t.start();
    }




    public Task createWorker() {

        return new Task() {
            @Override
            protected Object call() throws Exception {

                bt1.setDisable(true);
                bt2.setDisable(true);
                bt3.setDisable(true);
                excelbtn.setDisable(true);

//                for (int i = 0; i < 10; i++) {
//                    Thread.sleep(2000);
//                    updateMessage("2000 milliseconds");
//                    updateProgress(i + 1, 10);
//
//
//                    System.out.println(progressBar.getProgress());
//
//                }


                ListMultimap<String, String> f = getMultiMap();

                String input = PropertyReader.readProperty("INPUT_PATH");
                String output = PropertyReader.readProperty("OUTPUT_PATH");

                System.out.println(input);
                System.out.println(output);


                int elements = 0;

                for (Map.Entry<String, String> entry : f.entries()) {

                    elements++;

                    copyFileUsingStream(entry.getKey() + ".jpg", input, output, entry.getValue() + ".jpg");

                    System.out.println(elements + " /"+f.entries().size());

                    Thread.sleep(350);

                    updateProgress(elements, f.entries().size());
                }


                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                System.out.println(dateFormat.format(cal.getTime()));

                textArea.appendText("FileBackup v1 finished"+"\n");
                textArea.appendText(cal.getTime()+"\n");









      //          iterate();



                System.out.println("done");



                excelbtn.setDisable(false);
                bt1.setDisable(false);
                bt2.setDisable(false);
                bt3.setDisable(false);
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

                return true;
            }
        };






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
            textArea.appendText(filename + " back up as: "+ rename+ " in: "+outFolder+"\n");
            System.out.println(filename + " back up as: "+ rename+ " in: "+outFolder+"\n");
        }
        catch (Exception e){
            textArea.appendText(e.getMessage());
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
                textArea.appendText(e.getMessage());
                e.printStackTrace();
            }
        }
    }









    public ArrayList<ExcelRow> readExcel(){

        //  FileLogger.log("Reading excel file. . .");
        ArrayList<ExcelRow> rowList = new ArrayList<ExcelRow>();
        textArea.appendText("Reading excel file . . ."+ "\n");

        try {
            FileInputStream excelFile = new FileInputStream(new File(PropertyReader.readProperty("EXCEL_PATH")));
            Workbook workbook = new XSSFWorkbook(excelFile);

            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

//            Sheet s = workbook.getSheetAt(0);
//            Row r = s.getRow(0);
            String[] names = null;
            String namingFormat="";

//            Iterator<Row> rowIterator = datatypeSheet.iterator(); // Traversing over each row of XLSX file
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next(); // For each row, iterate through each columns
//                Iterator<Cell> cellIterator = row.cellIterator();
//                while (cellIterator.hasNext()) {
//                    Cell cell = cellIterator.next();
//                    if (cell.getColumnIndex() == 1) {
//                     //   System.out.println(cell.getStringCellValue());
//                    }
//                    if (cell.getColumnIndex() == 0) {
//                        cell.setCellType(Cell.CELL_TYPE_STRING);
//                        System.out.println(cell.getStringCellValue().toString());
//                    }
//                }
//            }






            while(iterator.hasNext()){
                Row currentRow = iterator.next();
                Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = currentRow.iterator();


                while(cellIterator.hasNext()){
                    Cell currentCell = cellIterator.next();


                   // System.out.println(currentCell.getCellType());


                    if(currentCell.getColumnIndex() == 0) {
                        currentCell.setCellType(CellType.STRING);



                        String rich = currentCell.getRichStringCellValue().toString();
                        //rich.split(",");
                        names = rich.split(",");
                    }
                    else if(currentCell.getColumnIndex() == 1){
                        currentCell.setCellType(CellType.STRING);
                     //    Long g = (long)currentCell.getNumericCellValue();
                     //   System.out.println("my value: "+currentCell.getStringCellValue());

                        String rich = currentCell.getRichStringCellValue().toString();
                        namingFormat = currentCell.getStringCellValue();
                    }
                }
                ExcelRow excelRow = new ExcelRow(names,namingFormat);
                rowList.add(excelRow);
            }
        }

        catch (FileNotFoundException e) {
            //   FileLogger.log(e.getMessage());
            e.printStackTrace();
            textArea.appendText(e.getMessage() +"\n");
        } catch (IOException e) {
            e.printStackTrace();
            //  FileLogger.log(e.getMessage());
            textArea.appendText(e.getMessage() +"\n");
        }
        return rowList;
    }


}
