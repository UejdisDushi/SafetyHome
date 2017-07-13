package it.upo.reti2s.utils;

import com.github.sarxos.webcam.Webcam;
import de.fh_zwickau.informatik.sensor.model.devices.Device;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static it.upo.reti2s.utils.SFWebhook.*;
import static it.upo.reti2s.utils.Util.getSensoreAperturaPorta;
import static it.upo.reti2s.utils.Util.getSensorePresenza;

/**
 * Created by Luca Franciscone on 11/07/2017.
 */
public class ThreadSorveglianzaConImmagine implements Runnable
{
    private boolean stopThread = false;
    public void run()
    {

        String text = "";
        Device sensoreAperturaPorta = getSensoreAperturaPorta();
        Device sensorePresenza  = getSensorePresenza();
        Webcam webcam = Webcam.getDefault();


        if(sensoreAperturaPorta == null || sensorePresenza==null || webcam==null)
        {
            if(webcam==null)
            {
                text = "Webcam non rilevata";
                System.out.println(text);
            }
            else
            {
                text = "Nessun sensore rilevato ";
                System.out.println(text);
            }
            try {
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.stopRunning();
        }
        else
        {
            while (!stopThread) {
            /*
            attivo per 10 minuti con aggiornamento  ogni10 sec
             */
                for (int i = 0; i < 300; i++)//attivo per 5 minuti
                {
                //verificare il metodo
                    try {
                        Thread.sleep(10000);//10 sec
                        if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on")
                                || sensorePresenza.getMetrics().getLevel().equalsIgnoreCase("on"))
                        {
                            //scatta foto
                            webcam.open();
                            ImageIO.write(webcam.getImage(), FORMATO_IMMAGINE, new File(PATH_IMMAGINE));
                            webcam.close();
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Immagine disponibile al seguente indirizzo",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//chiusura for
            }//chiusura while
        }//chiusura else
    }

    public void stopRunning()
    {
        stopThread = true;
    }
}


