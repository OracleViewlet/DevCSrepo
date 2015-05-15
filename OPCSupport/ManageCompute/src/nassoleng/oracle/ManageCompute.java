package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

// Paul import
import java.io.IOException;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import javax.ws.rs.core.NewCookie;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class ManageCompute {
    private String username;
    private String password;
    private String identityDomain;
    private String computeZone;
    private String opcComputeURL;
    private String nimbulaCookie;
    private Properties configProperties;    

    public ManageCompute() {
        super();
        readConfigProperties ();
    }

    public void readConfigProperties () {
        InputStream input = null;
        
        try {
            this.configProperties = new Properties ();
            
            input = new FileInputStream ("config.properties");
            
            this.configProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace ();
        }
    }
    
    public JSONObject getOrchestrations() {
        JSONObject orchestrationInstances = null;

        try {
            CloseableHttpClient httpclient = ManageComputeUtil.getClient (this.getUsername(), this.getPassword(), this.getIdentityDomain());

            HttpGet httpGet =
                new HttpGet("https://api-z12.compute.us2.oraclecloud.com/orchestration/Compute-usoracle16033/");
            httpGet.setHeader("Accept", "application/oracle-compute-v3+json");

            CloseableHttpResponse response = httpclient.execute(httpGet);
            System.out.println("StatusLine:" + response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();

            orchestrationInstances = ManageComputeUtil.getJSONObject (responseEntity);

            response.close();
            httpclient.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return orchestrationInstances;
    }

    public void printOrchestrations() {
        JSONObject orchestrationInstances = null;

        orchestrationInstances = getOrchestrations();
        System.out.println (orchestrationInstances);
    }
    
    public void testComputeOrchestrationCLI (String dbcsName) {
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JSONObject orchestrationInstances = null;
        JSONObject orchestrationInstance = null;
        JSONArray orchestrationArray = null;

        String dbcsIP = null;

        try {
            // dbcsInstance = this.manageDBCS.getDBCSInstanceInfo(dbcsName);
            // dbcsIP = dbcsInstance.getString("em_url").substring(8);
            // dbcsIP = dbcsIP.substring(0,dbcsIP.indexOf(":"));
            // batchFile = new File(this.getConfigProperties().getProperty("scriptLocation") + "runOPCWorkshopDatabaseSetup.sh");
            batchFile = new File("/u01/OPCWorkshop/lab/GSEScripts/reviewComputeOrchestration.sh");
            procBuilder =
                //new ProcessBuilder(batchFile.getAbsolutePath(), dbcsIP);
                new ProcessBuilder(batchFile.getAbsolutePath(), "z12","Alpha2014_");
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(procIn));
            String jsonString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();
            // System.out.println(jsonString);
            if (!jsonString.equals("") ) {
                try {
                    orchestrationInstances = new JSONObject(jsonString);
                    int spacesToIndentEachLevel = 2;
                    //String prettyJson = new JSONObject(jsonString).toString(spacesToIndentEachLevel);
                    //System.out.println(prettyJson);
                    
                    orchestrationArray = orchestrationInstances.getJSONArray("list");
                    System.out.println("List Array ="+orchestrationArray.length());
                    
                    for (int i = 0; i < orchestrationArray.length(); i++) {
                        orchestrationInstance = orchestrationArray.getJSONObject(i);
                        
                        String status = orchestrationInstance.getString("status");
                        
                        if ( status.equals("stopping")) {
                            System.out.println("\n!!!!! ERROR !!!!!");
                            System.out.println("Object #"+i+" = "+orchestrationInstance.getString("uri"));
                            System.out.println("  errors="+orchestrationInstance.getJSONArray("oplans").getJSONObject(0).getJSONObject("info").getString("errors"));
                            System.out.println("  oplans="+orchestrationInstance.getString("oplans"));
                            System.out.println("  fullObject="+orchestrationInstance.toString());
                            

                            System.out.println("!!!!!!!!!!!!!!!!!\n");


                        } else {
                            System.out.println("Object #"+i+" = "+orchestrationInstance.toString());

                        }
                        
                        /*
                        if (orchestrationInstance.getString("status").equals("Running")) {
                            dbcsName = dbcsInstance.getString("service_name");
                            dbcsInfo = getDBCSInstanceInfo(dbcsName);
                            dbIP = dbcsInfo.getString("em_url").substring(8);
                            dbIP = dbIP.substring(0,dbIP.indexOf(":"));
                            System.out.println (dbcsName + " DB IP = " + dbIP);
                        }
                        */
                    }
                    
                   

                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            

        //} catch (JSONException e) {
        //    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testSecapplicationCLI (String dbcsName) {
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JSONObject orchestrationInstances = null;
        JSONObject orchestrationInstance = null;
        JSONArray orchestrationArray = null;

        String dbcsIP = null;

        try {
            // dbcsInstance = this.manageDBCS.getDBCSInstanceInfo(dbcsName);
            // dbcsIP = dbcsInstance.getString("em_url").substring(8);
            // dbcsIP = dbcsIP.substring(0,dbcsIP.indexOf(":"));
            // batchFile = new File(this.getConfigProperties().getProperty("scriptLocation") + "runOPCWorkshopDatabaseSetup.sh");
            batchFile = new File("/u01/OPCWorkshop/lab/GSEScripts/reviewSecapplication.sh");
            procBuilder =
                //new ProcessBuilder(batchFile.getAbsolutePath(), dbcsIP);
                new ProcessBuilder(batchFile.getAbsolutePath(), "z12","Alpha2014_");
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(procIn));
            String jsonString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();
            // System.out.println(jsonString);
            if (!jsonString.equals("") ) {
                try {
                    orchestrationInstances = new JSONObject(jsonString);
                    int spacesToIndentEachLevel = 2;
                    //String prettyJson = new JSONObject(jsonString).toString(spacesToIndentEachLevel);
                    //System.out.println(prettyJson);
                    
                    orchestrationArray = orchestrationInstances.getJSONArray("list");
                    System.out.println("List Array ="+orchestrationArray.length());
                    
                    for (int i = 0; i < orchestrationArray.length(); i++) {
                        orchestrationInstance = orchestrationArray.getJSONObject(i);
                        
                        String dport = orchestrationInstance.getString("dport");
                        String protocol = orchestrationInstance.getString("protocol");
                        String description = orchestrationInstance.getString("description");
                        String uri = orchestrationInstance.getString("uri");
                        
                        if ( dport.equals("8080") && protocol.equals("tcp")) {
                            System.out.println("\n!!!!! ERROR !!!!!");
                            System.out.println("Object #"+i+" = "+description);
                            System.out.println("  protocol="+protocol);
                            System.out.println("  uri="+uri);
                            

                            System.out.println("!!!!!!!!!!!!!!!!!\n");


                        } else {
                            System.out.println("Object #"+i+" = "+orchestrationInstance.toString());

                        }
                        
                        /*
                        if (orchestrationInstance.getString("status").equals("Running")) {
                            dbcsName = dbcsInstance.getString("service_name");
                            dbcsInfo = getDBCSInstanceInfo(dbcsName);
                            dbIP = dbcsInfo.getString("em_url").substring(8);
                            dbIP = dbIP.substring(0,dbIP.indexOf(":"));
                            System.out.println (dbcsName + " DB IP = " + dbIP);
                        }
                        */
                    }
                    
                   

                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            

        //} catch (JSONException e) {
        //    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
       
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setIdentityDomain(String identityDomain) {
        this.identityDomain = identityDomain;
    }

    public String getIdentityDomain() {
        return identityDomain;
    }

    public void setComputeZone(String computeZone) {
        this.computeZone = computeZone;
        this.setOpcComputeURL("https://api-" + computeZone + ".compute.us2.oraclecloud.com");
    }

    public String getComputeZone() {
        return computeZone;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public void setOpcComputeURL(String opcComputeURL) {
        this.opcComputeURL = opcComputeURL;
    }

    public String getOpcComputeURL() {
        return opcComputeURL;
    }

    public void setNimbulaCookie(String nimbulaCookie) {
        this.nimbulaCookie = nimbulaCookie;
    }

    public String getNimbulaCookie() {
        return nimbulaCookie;
    }
   

    public static void main(String[] args) { 
        ManageCompute manageCompute = new ManageCompute ();
            
        List <String> jcsNames = null;
        JSONObject jcsInstance = null;
        
        // ./runManageOPC_JCS.sh pat.davies@oracle.com Alpha2014_ usoracle16033 Z12 Rev
     
         // Connect to Compute
        manageCompute = new ManageCompute ();
        manageCompute.setUsername("pat.davies@oracle.com");
        manageCompute.setPassword("Alpha2014_");
        manageCompute.setComputeZone("Z12");
        manageCompute.setIdentityDomain("usoracle16033");
        
        //manageCompute.securityApplication();
        
        //manageCompute.testComputeOrchestrationCLI("null");
        //manageCompute.testSecapplicationCLI("null");
    }    
}
