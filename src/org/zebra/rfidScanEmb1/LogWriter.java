package org.zebra.rfidScanEmb1;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




public class LogWriter {

    
    Date date;
	String currentTime, currentDate, dateFormatString = "EEE, MMM d, yy", timeFormatString = "hh:mm:ss a";
	DateFormat timeFormat = new SimpleDateFormat(timeFormatString), dateFormat = new SimpleDateFormat(dateFormatString);

    public void addNewTagScanner(String tags) throws IOException{
        date = new Date();
		currentTime = timeFormat.format(date);
		currentDate = dateFormat.format(date);
        try {
            FileWriter fWriter =  new FileWriter("/mnt/data/app/TagsFound.txt", true);
            fWriter.write(currentDate + " (" +  currentTime + ") Scanner Tag found! --" + tags + "--\n");
            System.out.println("/nADDED TAG");
            fWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println( "Unable to write to log. Error: " + e + "\n\n");
            try {
                FileWriter fWriter =  new FileWriter("/mnt/data/app/TagsFound.txt");
                fWriter.write(currentDate + " (" +  currentTime + ") Scanner Tag found! --" + tags + "--\n");
                fWriter.close();
            } catch (Exception z) {
                // TODO: handle exception
                System.out.println("Im deleting this whole program " + z);
            }
        }
    }

    public void addNewTagRadio(String tags) throws InterruptedException{
        date = new Date();
		currentTime = timeFormat.format(date);
		currentDate = dateFormat.format(date);
        try {
            FileWriter fWriter =  new FileWriter("/mnt/data/app/TagsFound.txt", true);
            fWriter.write(currentDate + " (" +  currentTime + ") Radio Tag found!--" + tags + "--\n");
            System.out.println("/nADDED TAG");
            fWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(" Unable to write to log. Error: " + e + "\n\n");
            try {
                FileWriter fWriter =  new FileWriter("/mnt/data/app/TagsFound.txt");
                fWriter.write(currentDate + " (" +  currentTime + ") Radio Tag found!--" + tags + "--\n");
                fWriter.close();
            } catch (Exception z) {
                // TODO: handle exception
                System.out.println("Im deleting this whole program " + z);
            }
        }
    }
}
