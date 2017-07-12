package it.upo.reti2s.utils;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Luca Franciscone on 11/07/2017.
 */
public class SimpleRunner implements Runnable
{
    private boolean stopThread = false;


    public void run()
    {
        while (! stopThread)
        {
            String text = "";
            Webcam webcam = Webcam.getDefault();

            if (webcam != null)
            {
                for (int i = 0; i < 10; i++)
                {
                    try {
                        Thread.sleep(5000);
                        webcam.open();
                        try {
                            ImageIO.write(webcam.getImage(), "PNG", new File("Images/prova.png"));
                            webcam.close();

                        } catch (IOException e) {
                            text="Problema con la cam";

                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(String.valueOf(4 - i));
                }

            }
            else
            {
                break;
            }


            /*
            System.out.println("Partitol il metodo run");
            // Esegue qualcosa fino a quando la
            // variabile stopThread è false

            System.out.println("Terminato il metodo run");*/
           break;

        }
        // esecuzione di eventuali operazioni di “pulizia”
//…
    }



    public void stopRunning()
    {
        stopThread = true;
    }
}


