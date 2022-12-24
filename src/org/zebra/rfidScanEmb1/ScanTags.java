package org.zebra.rfidScanEmb1;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.mot.rfid.api3.*;

public class ScanTags {
    static Timer timer;
    static HashMap<String, Long> scannedTags = new  HashMap<String, Long>();
    static boolean scannedTagsWriting = false;
    
    // public static HashMap<String, Integer> getScannedTags(){
    // 	return scannedTags;
    // }
    
    // public static void clearScannedTags() {
    // 	scannedTags.clear();
    // 	return;
    // }
    
    // public static boolean getScannedTagsWriting() {
    // 	return scannedTagsWriting;
    // }
    
    // public static void setScannedTagsWriting(boolean val) {
    // 	scannedTagsWriting = val;
    // }
	
	public static void mainScan(RFIDReader reader) throws InterruptedException, InvalidUsageException, OperationFailureException
	{
		
        // webSelenium driverHandle = new webSelenium();
        // driverHandle.setDriver();
        System.out.println("Is Reader Connected?\n" + reader.isConnected());
        //driverHandle.openNoTag();
		
        int count = 0; 
        int green = 3;
        int yellow = 2;
        int red = 1;
        int alarm = 4;


        //Use IP address of reader when you are running this code here
        //String hostname = "169.254.169.229";
        //String hostname = "10.244.3.26";
        String hostname = "localhost";
		List<String> alertOf = new ArrayList<String>();
		MysqlCon connector = new MysqlCon(); 

		//alertOf.add("ABEF00001341D824F1F6FEBA");
        alertOf.add("ABEF0000");
        alertOf.add("20221025");

        GPO_PORT_STATE enable = GPO_PORT_STATE.TRUE;
        GPO_PORT_STATE disable = GPO_PORT_STATE.FALSE;
        reader.Config.GPO.setPortState(green, enable);
        // // Subscribe required status notification
        reader.Events.setInventoryStartEvent(true);
        reader.Events.setInventoryStopEvent(true);
        reader.Events.setAccessStartEvent(true);
        reader.Events.setAccessStopEvent(true);

        //enables tag read notification. if this is set to false, no tag read notification will be send
        reader.Events.setTagReadEvent(true);
        reader.Events.setAntennaEvent(true);
        reader.Events.setBufferFullEvent(true);
        reader.Events.setBufferFullWarningEvent(true);
        reader.Events.setGPIEvent(true);
        reader.Events.setReaderDisconnectEvent(true);

        TagStorageSettings tagStorageSettings = reader.Config.getTagStorageSettings();
        tagStorageSettings.enableAccessReports(true);
        reader.Config.setTagStorageSettings(tagStorageSettings);

        TagData[] tags = null;
        ArrayList<String> deafen = new ArrayList<String>(100);
        
        // dict.put("ax", 123);

        Alarm sound = new Alarm(hostname, reader, enable, disable);
        LogWriter write = new LogWriter();

        long startTime;
        
        
        reader.Config.GPO.setPortState(green, enable);
        reader.Config.GPO.setPortState(red, disable);
        reader.Config.GPO.setPortState(alarm, disable);
        reader.Config.GPO.setPortState(yellow, disable);  

        try{  
                for (int element: reader.ReaderCapabilities.getTransmitPowerLevelValues()) {
                    System.out.println("Max power level value  "+ element);
                }

                for (int element: reader.ReaderCapabilities.getReceiveSensitivityValues()) {
                     System.out.println("Receive Sensitivity value  "+ element);
                }
                System.out.println("GET number of antennas supported  "+ reader.ReaderCapabilities.getNumAntennaSupported());
       
                System.out.println("GET ID reader  "+ reader.ReaderCapabilities.ReaderID.getID());
             
                System.out.println("getRFModeTableInfo "+ reader.ReaderCapabilities.RFModes.getRFModeTableInfo(0).getRFModeTableEntryInfo(1).getMaxTariValue());
                 
                for (int element: reader.Config.Antennas.getAvailableAntennas()) {
                    Antennas.AntennaRfConfig antennaRfConfig = reader.Config.Antennas.getAntennaRfConfig(element); 
                    antennaRfConfig.setTransmitPowerIndex(200);
                    antennaRfConfig.setrfModeTableIndex(0); 
                    antennaRfConfig.setTari(0);
                    antennaRfConfig.setReceiveSensitivityIndex(0); 
                    reader.Config.Antennas.setAntennaRfConfig(element,antennaRfConfig);
                    System.out.println("Antenna properties "+reader.Config.Antennas.AntennaProperties(element).getIndex());
                }
           
            while(true){

                reader.Actions.Inventory.perform();                
                Thread.sleep(500);
                reader.Actions.Inventory.stop();                
                tags = reader.Actions.getReadTags(3000);
                Boolean buzzerActivated = false;
                
                if(tags != null){ 
                   
                    for(int i = 0; i < tags.length; i++){
                        String scannerTypeCode = tags[i].getTagID().substring(0, 8);
                        String scannerCode = tags[i].getTagID();
                        System.out.println("Antenna ID "+i+" "+tags[i].getAntennaID());
                        System.out.println("Channel ID "+i+" "+tags[i].getChannelIndex());
                        if(alertOf.contains(scannerTypeCode) && !deafen.contains(scannerCode)){
                            if (!buzzerActivated) {
                                sound.buzzer();
                                buzzerActivated = true;
                            }
                            
                            if(scannerTypeCode.equals(alertOf.get(0))){
                                System.out.println("MC330K Detected\n\n---- " + tags[i].getTagID() + " ----\n\n");
								write.addNewTagScanner(tags[i].getTagID());
                                //driverHandle.openWeb("scanner");
                            }
                            if(scannerTypeCode.equals(alertOf.get(1))){
                                System.out.println("XPR 3500e Radio Detected\n\n---- " + tags[i].getTagID() + " ----\n\n");
								write.addNewTagRadio(tags[i].getTagID());
                                //driverHandle.openWeb("radio");
                            }
                            
                            try {
								startTime = System.currentTimeMillis();
                                System.out.println("IM HERE");
                                while (!wasFiveMinutesBefore(scannerCode) && (System.currentTimeMillis()-startTime)<100000){
                                    System.out.println("IN WHILE");
                                    if(scannerTypeCode.equals(alertOf.get(0))){
                                        System.out.println("INSIDE IF");
                                        if (connector.insertData(tags[i].getTagID(), "Scanner", "MC33000","mc3300.jpg")) {
                                            scannedTags.put(scannerCode, System.currentTimeMillis());
                                            deafen.add(scannerCode);
                                        }
                                    }
                                    if(scannerTypeCode.equals(alertOf.get(1))){
                                        System.out.println("INSIDE IF");
                                        if (connector.insertData(tags[i].getTagID(), "Radio", "XPR 3500e","radioMotorola.jpg")) {
                                            scannedTags.put(scannerCode, System.currentTimeMillis());
                                            deafen.add(scannerCode);
                                        }
                                    }
                                }
                            } 
                            catch (Exception e) {
                                // TODO: handle exception
                                System.out.println("An error with my SQL has occured\n");
                                try {
                                        
                                    //Write into the file
                                    FileWriter fWriter = new FileWriter("/mnt/data/app/Log.txt");
                                    fWriter.write("ERROR: " + e + "\n");
                                    
                                    //Print the success message
                                    System.out.print("File is created successfully with the content.");
                                    
                                    //Close the file writer object
                                    fWriter.close();
                                } 
                                catch (Exception z) {
                                    // TODO: handle exception
                                    System.out.println("Unable to create Error log: "+ z);
                                }
                            }
                            //driverHandle.openNoTag();
                        }
                        if(deafen.contains(scannerCode)){
                            System.out.println("\nThis scanner was seen before\n" + scannerCode);
                        }
                        
                        else{
                            System.out.println("\nUnknown obj was seen\n" + scannerCode);
                        }
                    }
                    
                    deafen.clear();
                   
                }
                System.out.println("Looping: " + count);
                count++;  
            }
            //driverHandle.closeWeb();
            //reader.disconnect();
        } catch (OperationFailureException ex) {
            System.out.println((" Antenna configuration failed " + ex.getVendorMessage()));
        } catch (Exception e) {
            System.out.println("An error occured forcing the temination of this program.");
            System.out.println(e);
            try{
                reader.Config.GPO.setPortState(green, enable);
                reader.Config.GPO.setPortState(red, disable);
                reader.Config.GPO.setPortState(alarm, disable);
                reader.Config.GPO.setPortState(yellow, disable);
                reader.Actions.Inventory.stop();
                reader.disconnect();
                System.exit(1);
                Runtime.getRuntime().halt(1);
                //driverHandle.closeWeb();

            }catch(Exception e2){
                System.out.println("Unable to stop reader and set LED status. Terminating program.");
                try {
                    reader.disconnect();
                } catch (Exception e3) {
                    System.out.println("Critial Error.");
                    System.out.println(e3);
                    System.exit(1);
                    Runtime.getRuntime().halt(1);
                    //driverHandle.closeWeb();
                } 
                System.out.println(e2);
                System.exit(1);
                Runtime.getRuntime().halt(1);
                //driverHandle.closeWeb();
            }
        }
	}

    public static Boolean wasFiveMinutesBefore(String scannerCode) {
        //reader.Events.set;
        if (!scannedTags.containsKey(scannerCode))
            return false;
        Long timeStamp = scannedTags.get(scannerCode);
        if ((System.currentTimeMillis() - timeStamp) < 5 * 60 * 1000) {
            return true;
        }
        return false;
    }
}