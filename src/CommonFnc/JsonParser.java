package CommonFnc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Data.Agent.Federation;
import com.google.gson.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import Data.HLAMessages.FederationGenericMsg;
import java.lang.reflect.Type;

/**
 *
 * @author abbas
 *///
public class JsonParser {

// ****************************FederateState of Agent/Federate Enconder/Decoder ***************************************    
//*************************************************************************************************************  
    public static String EncodeGenericMsg(FederationGenericMsg msg) {

        String jsonString;
        Writer writer = new StringWriter();

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        gson.toJson(msg, writer);
        jsonString = writer.toString();

        return jsonString;
    }

    public static <T> FederationGenericMsg<T> DecodeGenericMsg(String jsonString, Type typ) {

        ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes());
        InputStreamReader reader = new InputStreamReader(bais);
        Gson gson = new GsonBuilder().create();
        FederationGenericMsg<T> federationmsg = gson.fromJson(reader, typ);//FederationGenericMsg.class);
        System.out.println(federationmsg);

        return federationmsg;

    }

// ****************************Dependency Graph Enconder/Decoder ***************************************
//*****************************************************************************************************    
    public static String EncodeFederationToJson(Federation fed) {

        String jsonString;
        Writer writer = new StringWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        gson.toJson(fed, writer);
        jsonString = writer.toString();

        return jsonString;
    }

    public static Federation DecodeDepJsonAbsolutePath(String fileabsolutePath) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(fileabsolutePath), "UTF-8");
        Gson gson = new GsonBuilder().create();
        Federation federation = gson.fromJson(reader, Federation.class);
        System.out.println(federation);
        return federation;

    }

    public static Federation DecodeJsonString(String jsonString) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes());
        InputStreamReader reader = new InputStreamReader(bais);
        Gson gson = new GsonBuilder().create();
        Federation federation = gson.fromJson(reader, Federation.class);
        System.out.println(federation);
        return federation;

    }

}
