package it.upo.reti2s.utils;

import com.github.sarxos.webcam.Webcam;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import static it.upo.reti2s.utils.SafetyHomeWebhook.*;
import static it.upo.reti2s.utils.Util.*;

public class ThreadSorveglianzaConImmagine implements Runnable {
    private boolean stopThread = false;
    int durataSecondi;
    String text = "";

    /**
     * Costruttore della classe
     * @param durataSecondi sono i secondi stabiliti per far durare il thread
     */
    public ThreadSorveglianzaConImmagine(int durataSecondi) {
        this.durataSecondi = durataSecondi;
    }

    /**
     * Metodo che avvia il thread per i secondi prefissati dal costruttore.
     * Se dispositivi presenti controlla che la porta sia aperta e il sensone abbia rilevato una presenza.
     * In caso di scarsa luminosità accendi la luce per effettuare delle foto migliori
     */
    public void run() {
        Device sensoreAperturaPorta = getSensoreAperturaPorta();
        Device sensorePresenza  = getSensorePresenza();
        Device sensoreLuminosita = getSensoreLuminosita();
        Device holderLampadina = getHolderLampadina();
        Webcam webcam = Webcam.getDefault();

        if(sensoreAperturaPorta == null || sensorePresenza==null || sensoreLuminosita==null ||holderLampadina==null) {
            if(webcam==null)
                text = "Webcam non rilevata";
            else
                text = "Nessun sensore rilevato";
            try {
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                System.out.println(text);
                this.stopRunning();     //non essendoci nessun dispositivo termino l'esecuzione
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.stopRunning();
        }
        else {
            while (!stopThread) {
                for (int i = 0; i < durataSecondi; i++) {
                    try {
                        Thread.sleep(1000);     //interrompo esecuzione del thread per 1 secondo
                        if(getPortaAperta(sensoreAperturaPorta).equalsIgnoreCase("on") || sensorePresenza.getMetrics().getLevel().equalsIgnoreCase("on")) {
                            if(Double.parseDouble(sensoreLuminosita.getMetrics().getLevel()) <200)
                                holderLampadina.on();

                            else holderLampadina.off();

                            webcam.open();
                            ImageIO.write(webcam.getImage(), FORMATO_IMMAGINE, new File(PATH_IMMAGINE));
                            webcam.close();
                            holderLampadina.off();
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Rilevata Presenza",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                            Util.sendMessage("Immagine disponibile al seguente indirizzo",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("Immagine disponibile al seguente indirizzo",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                            Util.sendMessage("https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                            Util.sendMessage("https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0",TELEGRAM_RESPONSE_CHAT_ID_EDI,TELEGRAM_URL);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                this.stopRunning();
            }
        }
    }

    /**
     * Metodo che interrompe l'esecuzione del thread
     */
    public void stopRunning(){
        stopThread = true;
    }
}


