package it.upo.reti2s.utils;

import de.fh_zwickau.informatik.sensor.model.devices.Device;

import java.io.IOException;

import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_RESPONSE_CHAT_ID;
import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_RESPONSE_CHAT_ID_EDI;
import static it.upo.reti2s.utils.SafetyHomeWebhook.TELEGRAM_URL;
import static it.upo.reti2s.utils.Util.getSensoreAperturaPorta;
import static it.upo.reti2s.utils.Util.getSensorePresenza;


public class ThreadSorveglianzaSenzaImmagine implements Runnable {
    private boolean stopThread = false;
    int durataSecondi;
    String text = "";

    /**
     * Costruttore della classe
     * @param durataSecondi sono i secondi stabiliti per far durare il thread
     */
    public ThreadSorveglianzaSenzaImmagine(int durataSecondi){
        this.durataSecondi = durataSecondi;
    }

    /**
     * Metodo che avvia il thread per i secondi prefissati dal costruttore.
     * Se dispositivi presenti controlla che la porta sia aperta e il sensone abbia rilevato una presenza.
     */
    public void run()
    {
        Device sensoreAperturaPorta = getSensoreAperturaPorta();
        Device sensorePresenza  = getSensorePresenza();
        if(sensoreAperturaPorta == null || sensorePresenza==null){
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
        else {
            while (!stopThread) {
                for (int i = 0; i < durataSecondi; i++) {
                    try {
                        Thread.sleep(1000);
                        if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on")
                                || sensorePresenza.getMetrics().getLevel().equalsIgnoreCase("on")) {
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.stopRunning();
                }
            }
        }
    }

    /**
     * Metodo che interrompe l'esecuzione del thread
     */
    public void stopRunning()
    {
        stopThread = true;
    }
}


