package org.zebra.rfidScanEmb1;

import com.mot.rfid.api3.*;

public class Alarm{
    //Varibles
    int green = 3;
    int yellow = 2;
    int red = 1;
    int alarm = 4;
    String hostname;
    RFIDReader reader;
    GPO_PORT_STATE enable;
    GPO_PORT_STATE disable;
    

    public Alarm(String hostname, RFIDReader reader, GPO_PORT_STATE enable, GPO_PORT_STATE disable) throws InterruptedException{
       this.hostname = hostname; this.reader = reader; this.enable = enable; this.disable = disable;
    }

    public void buzzer() throws InterruptedException{
        try {
            reader.Config.GPO.setPortState(red, enable);
            reader.Config.GPO.setPortState(alarm, enable);
            Thread.sleep(3000);
              
            reader.Config.GPO.setPortState(red, disable);
            reader.Config.GPO.setPortState(alarm, disable);

        } catch (InvalidUsageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationFailureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}