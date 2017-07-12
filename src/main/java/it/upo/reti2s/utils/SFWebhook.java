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
import static spark.Spark.*;

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
        get("/sentMessage", (request, response) ->
        {

            Gson gson1 = new Gson();
            response.status(200);//200 OK
            response.type("application/json");
            String finalJson = "messaggio di ritorno";
            System.out.println("il metodo prova ritorno funziona");
            sendMessage("attenzione ladro",TELEGRAM_RESPONSE_CHAT_ID);

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

        //confronto con la action che mi interessa
        if (input.getResult().getAction().equalsIgnoreCase("accendiLuce"))
        {
            String text="";

            text = "prova stampa";

            System.out.println(text);
            DeviceList allDevice = zWayApi.getDevices();

            if(allDevice!=null)
            {
                for(Device dev: allDevice.getAllDevices())
                {
                    if(dev.getDeviceType().equalsIgnoreCase("SwitchBynary") && dev.getNodeId() == 20)
                    {
                        dev.on();
                        text="luce accesa";
                    }
                }
            }
            else
            {
                text = "device non trovato";
            }

            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
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


                } catch (IOException e) {
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

            //Thread.sleep(2000);//mette in attesa il thread corrente

            SimpleRunner r = new SimpleRunner();
            System.out.println("Finite thread");

            //Scrivere immagine della cam su google drive

            //text="https://drive.google.com/file/d/0B1dKXnmV5OuKRk9weWkzRFV3MlE/view?usp=sharing";
            text="https://www.dropbox.com/s/v7arilbs00h4849/prova.png?dl=0";

            System.out.println(text);


            //faccio passare output come parametro senno posso fare la return lo ritorno nella classe chiamante
            output.setSpeech(text);
            output.setDisplayText(text);
        }
/*
        if (input.getResult().getAction().equalsIgnoreCase("bot"))
        {


           // TelegramBot safetyHomeBot = TelegramBotAdapter.build("423930159:AAF3ES_GcBxl5HmrV5HdfF137_XCfLXc1ZU");


            //safetyHomeBot.
//
           // S/endMessage sendMessage = new SendMessage(safetyHomeBot,"messaggio di risposta");
            //sendMessage.
        }*/


    }

    public static DeviceList getAllDevices()
    {
        return zWayApi.getDevices();
    }

    public static Device getDevice(String deviceType, int id)
    {
        DeviceList lista = getAllDevices();

        for(Device tmp : lista.getAllDevices())
        {
            if(tmp.getDeviceType().equalsIgnoreCase(deviceType) && tmp.getNodeId()== id)
            {
                return tmp;
            }
        }
        return null;

    }

    public static  Device getDevice(String deviceType,String probeTitle, int id)
    {
        DeviceList lista = getAllDevices();

        for(Device tmp : lista.getAllDevices())
        {
            if(tmp.getDeviceType().equalsIgnoreCase(deviceType) && tmp.getNodeId()== id && tmp.getMetrics().getProbeTitle().contains(probeTitle))
            {
                return tmp;
            }
        }
        return null;
    }


    //PRENDERE PER LA CONVERSIONE
    /*
     message è il messaggio in stringa da inviare
     /chatid è l id dell utente a cui rispondere nella chat
          */
    private static void sendMessage(String message, long aChatId) throws SecurityException, IOException
    {
        String response = "";
        String responseJSON = "";
        String converted = "";

        try
        {
            //convert message into utf-8
            converted = convertToUtf(message);
            responseJSON = "{ \"text\" : \"" + converted + "\", \"chat_id\" : " + aChatId+ " }";
            response = eseguiPost(TELEGRAM_URL + "/sendMessage", responseJSON);
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }
    }

    public static String convertToUtf(String s)
    {
        String[] INVALID_UTF8 = {"à", "è", "é", "ì", "ò", "ù", "À", "È", "É", "Ì", "Ò", "Ù"};
        String[] VALIDATED_UTF8 = {"a'", "e'", "e'", "i'", "o'", "u'", "A'", "E'", "E'", "I'", "O'", "U'"};

        for(int i=0; i<INVALID_UTF8.length; i++)
            s = s.replace(INVALID_UTF8[i], VALIDATED_UTF8[i]);

        byte[] bytes = s.getBytes( Charset.forName("UTF-16" ));
        String ret = new String( bytes, Charset.forName("UTF-16") );
        return ret;
    }


    //HTTP POST PARTE CHIAMATA PER L INVIO DEL MESSAGGIO
    public static String eseguiPost(String url, String json) throws Exception
    {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();
        return writeResp(con);
    }

    private static String writeResp(HttpsURLConnection con) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }
}
