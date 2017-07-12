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
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

import static it.upo.reti2s.utils.Util.getAllDevices;
import static it.upo.reti2s.utils.Util.getDevice;
import static spark.Spark.*;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

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
            //MULTISENSORE
    final static String MULTILEVEL = "SensorMultilevel";

    final static int MULTILEVEL_ID = 6;
    final static String MULTILEVEL_LUMINESCENCE = "Luminiscence";

    final static String MULTILEVEL_PURPOSE = "purpose";


    final static String BINARY = "SensorBinary";

    final static int APERTURA_PORTE_ID = 13;

    final static int HOLDER_LAMPADINA = 18;
    final static int HOLDER_LAMPADINA_ID20 = 20;

    final static int PRESA_PILOTATA = 0;


    final static String SWITCHBINARY = "SwitchBinary";


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
                    ImageIO.write(webcam.getImage(), "PNG", new File("Images/prova.png"));
                    finalJson = "Immagine scattata";


                } catch (IOException e)
                {
                    text = "Problema con la cam";

                    e.printStackTrace();
                }
                if (finalJson == null)
                {
                    finalJson = "Problema con la cam";
                }
                System.out.print(text);
            }
            else
            {
                finalJson = "No webcam detected";
                System.out.println();
            }
            return finalJson;
        }, gson::toJson);

        //

        get("/statoPorta",(request, response) ->
        {
            String returnGson = "";
            Gson gson1 = new Gson();
            //mi faccio restituire la lista di tutti i device
            DeviceList allDevices = zWayApi.getDevices();
            Device aperturaPorta = null;
            if(allDevices!=null)
            {

                for (Device dev : allDevices.getAllDevices()) {
            /*
            SENSORE APERTURA PORTE
             */

                    if (dev.getNodeId() == 13 && dev.getDeviceType().equalsIgnoreCase("sensorBinary")) {
                        aperturaPorta = dev;
                    }
                    returnGson = aperturaPorta.getMetrics().getLevel();

                    if (returnGson == null)
                    {
                        returnGson = "sensore non trovato";
                    }
                }
            }

            returnGson = "device scollegato";
            return returnGson;
        },gson::toJson);

    }//chiude main



    /**
     * The webhook method. It is where the "magic" happens.
     * Please, notice that in this version we ignore the "urgent" field of tasks.
     *
     * @param input  the request body that comes from api.ai
     * @param output the @link(Fulfillment) response to be sent to api.ai
     */
    private static void doWebhook(AIResponse input, Fulfillment output) throws InterruptedException
    {

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
            String text="";
            System.out.println(text);
            DeviceList allDevice = getAllDevices();//aggiungo tutti i device dal metodo in util
            if(allDevice!=null)
            {
                Device devDaAccendere = getDevice(SWITCHBINARY,HOLDER_LAMPADINA_ID20);
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
            output.setSpeech(text);
            output.setDisplayText(text);
        }

         /*
        Holder della lampadina ha id=20
            Accendi la luce da  API.ai
            Accendi la luce da Telegram

         */


        //confronto con la action che mi interessa
        if (input.getResult().getAction().equalsIgnoreCase("spegniLuce"))
        {
            String text="";
            text="Invocato il metodo spegni la luce";
            System.out.println(text);

            DeviceList allDevice = zWayApi.getDevices();

            Device dev = getDevice(SWITCHBINARY,20);
            dev.off();

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
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
                    text = "https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";
                    webcam.close();


                } catch (IOException e)
                {
                    text="Problema con la cam";
                    e.printStackTrace();
                }
            }
            else
            {
                text = "No webcam detected";
            }


            System.out.print(text);
            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante

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

            String stanza = input.getResult().getStringParameter("Stanza");

            if(stanza == null)
            {
                halt(403);
            }

            text="invocato il metodo accendiPresa per la stanza "+stanza;
            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
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
            text="https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";

            System.out.println(text);

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }
    }
}
