package it.upo.reti2s.utils;

/**
 * Created by Luca Franciscone on 11/07/2017.
 */

public class ThreadController
{
    private SimpleRunner r = new SimpleRunner();

    //creati da me
    private ThreadSorveglianzaConImmagine sorveglianzaConImmagine  = new ThreadSorveglianzaConImmagine();
    private ThreadSorveglianzaSenzaImmagine sorveglianzaSenzaImmagine  = new ThreadSorveglianzaSenzaImmagine();
    //--fine creati da me s

    private Thread t = new Thread(r);



    //private Thread tVideoSorveglianzaConImmagine = new Thread(ThreadSorveglianzaConImmagine);
    public void startThread()
    {
        System.out.println("Il thread inizia l esecuzione!");

        t.start();
    }

    public void stopThread()
    {
        System.out.println("thread ha finito l'esecuzione!");
        r.stopRunning();
    }
}