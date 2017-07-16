package it.upo.reti2s.utils;

import de.fh_zwickau.informatik.sensor.model.devices.Device;

import java.io.IOException;

import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_RESPONSE_CHAT_ID;
import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_RESPONSE_CHAT_ID_EDI;
import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_URL;
import static it.upo.reti2s.utils.Util.getSensoreAperturaPorta;
import static it.upo.reti2s.utils.Util.getSensorePresenza;

/**
 * Created by Luca Franciscone on 11/07/2017.
 */
public class ThreadSorveglianzaSenzaImmagine implements Runnable
{
    private Thread thread;
    private boolean stopThread = false;
    int durataSecondi;
    int durata = durataSecondi/10;
    String text = "";


    //imposta quanto dura la sorveglianza
    public ThreadSorveglianzaSenzaImmagine(int durataSecondi)
    {
        this.durataSecondi = durataSecondi;
    }


    public void run()
    {

        Device sensoreAperturaPorta = getSensoreAperturaPorta();
        Device sensorePresenza  = getSensorePresenza();

        if(sensoreAperturaPorta == null || sensorePresenza==null)
        {
            text = "Nessun sensore rilevato ";
            System.out.println(text);
            try {
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.stopRunning();
        }
        else
        {
            while (!stopThread)
            {
            /*
            attivo per 10 minuti con aggiornamento  ogni10 sec
             */
                for (int i = 0; i < durataSecondi; i++)//attivo per 5 minuti
                {
                //verificare il metodo
                    try {
                        Thread.sleep(1000);//10 sec
                        if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on")
                                || sensorePresenza.getMetrics().getLevel().equalsIgnoreCase("on"))
                        {
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.stopRunning();
                }//chiusura for
            }//chiusura while
        }//chiusura else
    }

    public void stopRunning()
    {
        stopThread = true;
    }
}


