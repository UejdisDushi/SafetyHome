package it.upo.reti2s.utils;


import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import com.github.sarxos.webcam.Webcam;
import com.google.gson.Gson;
import de.fh_zwickau.informatik.sensor.IZWayApi;
import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;

import javax.imageio.ImageIO;
import java.io.*;

import static it.upo.reti2s.utils.Util.*;
import static spark.Spark.*;


import java.io.IOException;

/**
 * api.ai Webhook example.
 * It gets all tasks, a specified task from a database and provides the information to
 * the api.ai service. It also allows users to create a new task, through the api.ai
 * conversational interface.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (21/05/2017)
 */
public class SFWebhook
{

    final static String PATH_IMMAGINE = "Images/prova.png";
    final static String FORMATO_IMMAGINE = "PNG";
    final static String PATH_IMMAGINE_DROPBOX = "https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";

    //



//ID CHE MI SERVONO


    final static int ID_APERTURAPORTE = 13;//CORRETTO
    final static int ID_PRESAPILOTATA = 3;// CORRETTO PRESA PILOTATA PER RADIO
    final static int ID_HOLDERLAMPADINA = 21;//CORRETTO EVERSPRING WALL PLUG
    final static int ID_MULTILEVEL_PURPOSE = 6;//CORRETTO DA USARE PER PURPOSE E LUMINOSITA






    //final static int HOLDER_LAMPADINA_ID20 = 20;//NO



    final static String SWITCHBINARY = "SwitchBinary";
    final static String SENSORBINARY = "SensorBinary";
    final static String SENSORMULTILEVEL = "SensorMultilevel";
    final static String MULTILEVEL_LUMINESCENCE = "Luminiscence";
    final static String MULTILEVEL_PURPOSE = "purpose";




    final static String ipAddress = "172.30.1.137";
    final static String username = "admin";
    final static String password = "raz4reti2";
    final static IZWayApi zWayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());

    static final String TELEGRAM_URL = "https://api.telegram.org/bot423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU";
    static final String TELEGRAM_TOKEN = "423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU";
    static final long TELEGRAM_RESPONSE_CHAT_ID = 102856586;

    public static void main(String[] args)
    {

        Gson gson = GsonFactory.getDefaultFactory().getGson();


        /*
        le post sono utilizzate da tutti i metodi di API.ai e di telegram
         */
        post("/", (request, response) -> {
            Fulfillment output = new Fulfillment();
            // the "real" stuff happens here
            // notice that the webook request is a superset of the AIResponse class
            // and it should be created to tackle the differences
            doWebhook(gson.fromJson(request.body(), AIResponse.class), output);
            response.type("application/json");
            // output is automatically converted in JSON thanks to gson
            //output contiene
            //           output.setSpeech(text) output.setDisplayText(text);
            return output;
        }, gson::toJson);

        //metodo testato per inviar e un messaggio
        get("/sendMessage", (request, response) ->
        {

            Gson gson1 = new Gson();
            response.status(200);//200 OK
            response.type("application/json");
            String finalJson = "Invocato il metodo per inviare un messaggio su telegram";
            System.out.println(finalJson);
            Util.sendMessage("attenzione ladro",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            return finalJson;
        }, gson::toJson);






        /*
        METODO USATO COME TEST
            http://localhost:4567/provaRitorno
         */
        get("/provaRitorno", (request, response) ->
        {

            Gson gson1 = new Gson();
            response.status(200);//200 OK
            response.type("application/json");
            String finalJson = "messaggio di ritorno";
            System.out.println("il metodo prova ritorno funziona");
            return finalJson;
        }, gson::toJson);


        get("/scattaFoto", (request, response) ->
        {
            String text = "";
            Gson gson1 = new Gson();
            response.status(200);//200 OK
            response.type("application/json");
            String finalJson = "messaggio di ritorno";
            Webcam webcam = Webcam.getDefault();
            if (webcam != null) {
                System.out.println("Webcam: " + webcam.getName());
                webcam.open();
                try {
                    ImageIO.write(webcam.getImage(), FORMATO_IMMAGINE, new File(PATH_IMMAGINE));
                    webcam.close();
                    finalJson = "Immagine scattata";
                    Util.sendMessage(PATH_IMMAGINE_DROPBOX,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);



                } catch (IOException e)
                {
                    text = "Problema con la cam";
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                    e.printStackTrace();
                }
                if (finalJson == null)
                {
                    finalJson = "Problema con la cam";
                    Util.sendMessage(finalJson,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

                }
                System.out.print(text);
            }
            else
            {
                finalJson = "No webcam detected";
                Util.sendMessage(finalJson,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

                System.out.println();
            }
            return finalJson;
        }, gson::toJson);

        //

        get("/statoPorta",(request, response) ->
        {
            String returnGson = "";
            String statoPorta ="";
            Gson gson1 = new Gson();
            //mi faccio restituire la lista di tutti i device
            DeviceList allDevices = getAllDevices();
            if(allDevices!=null)
            {
                //apertura porta id 13
                Device aperturaPorta = getSensoreAperturaPorta();
                if(aperturaPorta!=null)
                {
                    statoPorta = getPortaAperta(aperturaPorta);
                    if(statoPorta.equalsIgnoreCase("off"))
                    {
                        returnGson ="Porta chiusa, valore : off";
                    }
                    else
                    {
                        returnGson ="Porta aperta, valore : on";
                    }
                    Util.sendMessage(returnGson,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

                }
                else
                {
                    returnGson = "Sensore non trovato";
                    Util.sendMessage(returnGson,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
                }
            }
            else
            {
                returnGson = "Nessun device trovato";
                Util.sendMessage(returnGson,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            }
            return returnGson;
        },gson::toJson);





        /*calendario
        */
/*
        get("/calendar", (request, response) ->
        {

            Gson gson1 = new Gson();
            response.status(200);//200 OK
            response.type("application/json");

            Calendar service = new Timeout.Builder(httpTransport, jsonFactory, credentials)
                    .setApplicationName("applicationName").build();

            Event allEvent = service.event



            String finalJson = "Invocato il metodo per inviare un messaggio su telegram";
            System.out.println(finalJson);
            Util.sendMessage("attenzione ladro",TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            return finalJson;
        }, gson::toJson);

*/
    }//chiude main




    /**
     * The webhook method. It is where the "magic" happens.
     * Please, notice that in this version we ignore the "urgent" field of tasks.
     *
     * @param input  the request body that comes from api.ai
     * @param output the @link(Fulfillment) response to be sent to api.ai
     */
    private static void doWebhook(AIResponse input, Fulfillment output) throws InterruptedException, IOException {

        //In AIResponse input dobbiamo prendere un getResult e getAction, get action avrà lo stesso nome della action dell intent
        //text verrà utilizzato per il ritorno
        /*
        Holder della lampadina ha id=20
            Accendi la luce da  API.ai
            Accendi la luce da Telegram

         */

        //REFACTOR
        if (input.getResult().getAction().equalsIgnoreCase("accendiLuce"))
        {
            String text="Nessun device collegato trovato";
            System.out.println(text);
            DeviceList allDevice = getAllDevices();//aggiungo tutti i device dal metodo in util
            if(allDevice!=null)
            {
                Device devDaAccendere = getDevice(SWITCHBINARY, ID_HOLDERLAMPADINA);
                if(devDaAccendere!=null)
                {
                    devDaAccendere.on();
                    text="Luce accesa";

                }
                else
                {
                    text = "Device non trovato";
                }
            }
            else
            {
                text = "Nessun device collegato trovato";
            }

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            System.out.println(text);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            output.setSpeech(text);
            output.setDisplayText(text);
        }

         /*
        Holder della lampadina ha id=20
            Accendi la luce da  API.ai
            Accendi la luce da Telegram

         */


        //REFACTOR
        if (input.getResult().getAction().equalsIgnoreCase("spegniLuce"))
        {
            String text="";
            System.out.println(text);
            DeviceList allDevice = getAllDevices();//aggiungo tutti i device dal metodo in util
            if(allDevice!=null)
            {
                Device holderLampadina = getHolederLampadina();
                if(holderLampadina!=null)
                {
                    holderLampadina.off();
                    text="Luce accesa";

                    //manca il controllo se la luce è accesa
                    /*
                    if(holderLampadina.getMetrics().getLevel().equalsIgnoreCase("off"))//<--controllare il corretto funzionamento
                    {
                        holderLampadina.off();
                        text="Luce accesa";
                    }
                    else
                    {
                        text = "Luce già spenta";
                    }*/
                }
                else
                {
                    text = "Device non trovato";
                }
            }
            else
            {
                text = "Nessun device collegato trovato";
            }

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            System.out.println(text);
            output.setSpeech(text);
            output.setDisplayText(text);
        }


        /*
            Metodo prova da Telegram
            MEtodo prova da API.ai
            Risposta settata per il metodo telegram
         */

        //confronto con la action che mi interessa

        if (input.getResult().getAction().equalsIgnoreCase("prova"))
        {
            String text="";
            text = "risposta del metodo prova";

            System.out.print(text);
            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante

            output.setSpeech(text);
            output.setDisplayText(text);
        }



        //                  OK  ---------


        if (input.getResult().getAction().equalsIgnoreCase("scattaFoto"))
        {
            String text="";
            Webcam webcam = Webcam.getDefault();
            if (webcam != null)
            {
                System.out.println("Webcam: " + webcam.getName());
                webcam.open();
                try {
                    ImageIO.write(webcam.getImage(), "PNG", new File("Images/prova.png"));
                    webcam.close();
                    text = "https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);


                } catch (IOException e)
                {
                    text="Problema con la cam";
                    e.printStackTrace();
                    Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

                }
            }
            else
            {
                text = "No webcam detected";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            }

            System.out.print(text);
            output.setSpeech(text);
            output.setDisplayText(text);
        }


        /*
        METODO USATO PER IL TEST DELLE VARIABILI PASSATE IN INPUT
         */

        /*

                        OK TESTARE LAB
         */
        if (input.getResult().getAction().equalsIgnoreCase("accendiPresa"))
        {
            String text="";

            //String stanza = input.getResult().getStringParameter("Stanza");
/*
            if(stanza == null)//Se mancano parametri restituisco un halt
            {
                halt(403);
            }
            */

            Device presaPilotata = getPresaPilotata();
            if(presaPilotata!=null)
            {
                accendiDevice(presaPilotata);
                //text = "Accesa Presa Corrente per la stanza: "+stanza;
                text = "Accesa Presa Corrente per la stanza: ";
            }
            else
            {
               // text="Device non trovato Non e stato possibile accendere la presa nella "+stanza;
                text="Device non trovato Non e stato possibile accendere la presa nella ";
                Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            }
            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }





        if (input.getResult().getAction().equalsIgnoreCase("spegniPresa"))
        {
            String text="";
            Device presaPilotata = getPresaPilotata();
            if(presaPilotata!=null)
            {
                accendiDevice(presaPilotata);
                text = "Accesa Presa Corrente per la stanza: ";
            }
            else
            {
                // text="Device non trovato Non e stato possibile accendere la presa nella "+stanza;
                text="Device non trovato Non e stato possibile accendere la presa nella ";
            }
            System.out.println(text);
            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);
            output.setSpeech(text);
            output.setDisplayText(text);
        }


        if (input.getResult().getAction().equalsIgnoreCase("thread"))
        {
            String text="";
            Thread t = new Thread(new SimpleRunner());
            System.out.println( "\n I thread stanno per partire \n\n\n" );
            t.start();//faccio partire il thread per l interrogazione sottostante
            t.join();//attendo la terminazione di

            SimpleRunner r = new SimpleRunner();
            System.out.println("Finite thread");

            //text="https://drive.google.com/file/d/0B1dKXnmV5OuKRk9weWkzRFV3MlE/view?usp=sharing";
            text=PATH_IMMAGINE_DROPBOX;

            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        //SERVIZIO VERIFICA PRESENZA <-- verificare metodo per prelevare valori sensori presenza
        if (input.getResult().getAction().equalsIgnoreCase("verificaPresenza"))
        {
            String text="";
            Device sensorePrenza = getSensorePresenza();
            if(sensorePrenza !=null)
            {
                //verificare se è corretto il metodo
                if(sensorePrenza.getMetrics().getLevel().equalsIgnoreCase("on"))
                {
                    text = "Rilevata presenza";
                }
                else
                {
                    text="Nessuna presenza rilevata";
                }
            }
            else
            {
                text="Sensore presenza non trovato";
            }
            System.out.println(text);
            Util.sendMessage(text,TELEGRAM_RESPONSE_CHAT_ID,TELEGRAM_URL);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }

        //SERVIZIO VERIFICA APERTURA PORTA <-- verificare metodo per prelevare valori sensori presenza
        if (input.getResult().getAction().equalsIgnoreCase("verificaPorta"))
        {
            String text="Errore";
            Device sensoreAperturaPorta = getSensoreAperturaPorta();
            if(sensoreAperturaPorta !=null)
            {
                //verificare se è corretto il metodo
                if(sensoreAperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on"))
                {
                    text = "Porta Aperta";
                }
                else
                {
                    text="Porta Chiusa";
                }
            }
            else
            {
                text="Sensore porta non trovato";
            }
            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }


        //simplirunner non andava


        // da adattare ai nuovi thread creati
        if (input.getResult().getAction().equalsIgnoreCase("attivaServizioMonitoraggioConImmagine"))
        {
           // String text="";
            Thread t = new Thread(new ThreadSorveglianzaConImmagine(10));
            //System.out.println( "\n I thread stanno per partire \n\n\n" );
            t.start();//faccio partire il thread per l interrogazione sottostante
            //t.join();//attendo la terminazione di


            //SimpleRunner r = new SimpleRunner();
            //System.out.println("Finite thread");

            //text="https://drive.google.com/file/d/0B1dKXnmV5OuKRk9weWkzRFV3MlE/view?usp=sharing";
            //text=PATH_IMMAGINE_DROPBOX;

            //System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            //output.setSpeech(text);
            //output.setDisplayText(text);
        }
/*
        // da adattare ai nuovi thread creati
        if (input.getResult().getAction().equalsIgnoreCase("attivaServizioMonitoraggioConImmagine"))
        {
            String text="";
            Thread t = new Thread(new SimpleRunner());
            System.out.println( "\n I thread stanno per partire \n\n\n" );
            t.start();//faccio partire il thread per l interrogazione sottostante
            t.join();//attendo la terminazione di

            SimpleRunner r = new SimpleRunner();
            System.out.println("Finite thread");

            //text="https://drive.google.com/file/d/0B1dKXnmV5OuKRk9weWkzRFV3MlE/view?usp=sharing";
            text=PATH_IMMAGINE_DROPBOX;

            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }
        */


        // da adattare ai nuovi thread creati
        if (input.getResult().getAction().equalsIgnoreCase("attivaServizioMonitoraggioSenzaImmagine"))
        {
            String text="";
            Thread threadSoverglianza = new Thread(new ThreadSorveglianzaSenzaImmagine(10));
            threadSoverglianza.start();//faccio partire il thread per l interrogazione sottostante
            text="Thread attivato";

            //threadSoverglianza.join();//attendo la terminazione di

            //SimpleRunner r = new SimpleRunner();

            //text="https://drive.google.com/file/d/0B1dKXnmV5OuKRk9weWkzRFV3MlE/view?usp=sharing";


            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            //output.setSpeech(text);
            //output.setDisplayText(text);
        }




    }
}
