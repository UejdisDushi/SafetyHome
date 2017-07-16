package it.upo.reti2s.utils;

import com.github.sarxos.webcam.Webcam;
import de.fh_zwickau.informatik.sensor.model.devices.Device;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static it.upo.reti2s.utils.SafetyHomeWebhook.*;
import static it.upo.reti2s.utils.Util.getSensoreAperturaPorta;
import static it.upo.reti2s.utils.Util.getSensorePresenza;

/**
 * Created by Luca Franciscone on 11/07/2017.
 */
public class ThreadSorveglianzaConImmagine implements Runnable
{
    private Thread thread;
    private boolean stopThread = false;
    int durataSecondi;
    int durata = durataSecondi/10;
    String text = "";


    //imposta quanto dura la sorveglianza
    public ThreadSorveglianzaConImmagine(int durataSecondi)
    {
        this.durataSecondi = durataSecondi;
    }


    public void run()
    {

        Device sensoreAperturaPorta = getSensoreAperturaPorta();
        //System.out.println("Passata assegnazione sensore apertura porta");
        Device sensorePresenza  = getSensorePresenza();
       // System.out.println("Passata assegnazione sensore apertura presenza");

        System.out.println("valore apertura porta : "+sensoreAperturaPorta + "     valore presenza:"+sensorePresenza);



        Webcam webcam = Webcam.getDefault();
        //System.out.println("Passata assegnazione webcam");


        if(sensoreAperturaPorta == null || sensorePresenza==null)
        {
            if(webcam==null)
            {
                text = "Webcam non rilevata";
            }
            else
            {
                text = "Nessun sensore rilevato ";
            }
            try {
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                System.out.println(text);

                this.stopRunning();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            this.stopRunning();

        }
        else
        {
            while (!stopThread)
            {

                for (int i = 0; i < durataSecondi; i++)//attivo per 5 minuti
                {
                //verificare il metodo
                    try {
                        Thread.sleep(1000);//10 sec
                        if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on")
                                || sensorePresenza.getMetrics().getLevel().equalsIgnoreCase("on"))
                        {
                            //scatta foto
                            webcam.open();
                            ImageIO.write(webcam.getImage(), FORMATO_IMMAGINE, new File(PATH_IMMAGINE));
                            webcam.close();
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                            Util.sendMessage("Immagine disponibile al seguente indirizzo",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Immagine disponibile al seguente indirizzo",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                            Util.sendMessage("https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                        }
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }//chiusura for
                this.stopRunning();
            }//chiusura while
        }//chiusura else
    }


    public void stopRunning()
    {
        stopThread = true;
    }
}


