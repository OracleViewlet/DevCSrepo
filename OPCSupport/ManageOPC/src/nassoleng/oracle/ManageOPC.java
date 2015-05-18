package nassoleng.oracle;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

import java.text.DateFormat;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import java.util.Properties;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ManageOPC {
    private String username;
    private String password;
    private String computeZone;
    private String identityDomain;
    private DeleteStorageContainer  manageSC;
    private ManageDBCS  manageDBCS;
    private ManageJCS  manageJCS;
    private ManageCompute  manageCompute;
    private Properties configProperties;
   
    public ManageOPC() {
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
    
    public void initOPC () {
        // Connect to Storage
        this.manageSC = new DeleteStorageContainer ();
        this.manageSC.setOpcUsername(this.getUsername());
        this.manageSC.setOpcPassword(this.getPassword());
        this.manageSC.setOpcDomain(this.getIdentityDomain());
        // Connect to Compute
        this.manageCompute = new ManageCompute ();
        this.manageCompute.setUsername(this.getUsername());
        this.manageCompute.setPassword(this.getPassword());
        this.manageCompute.setComputeZone(this.getComputeZone());
        this.manageCompute.setIdentityDomain(this.getIdentityDomain());
        // Connect to DBCS
        this.manageDBCS = new ManageDBCS ();
        this.manageDBCS.setUsername(this.getUsername());
        this.manageDBCS.setPassword(this.getPassword());
        this.manageDBCS.setIdentityDomain(this.getIdentityDomain());
        // Connect to JCS
        this.manageJCS = new ManageJCS ();
        this.manageJCS.setUsername(this.getUsername());
        this.manageJCS.setPassword(this.getPassword());
        this.manageJCS.setIdentityDomain(this.getIdentityDomain());        
    }

    public void reviewAccount () {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;

        System.out.println ("\n*******************************************");
        System.out.println ("Review of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");                    
        containerNames = manageSC.getContainerNames();
        System.out.println ("Storage Container Names = " + containerNames);    
        System.out.println ("\n*******************************************");
        System.out.println ("Print Orchestrations");
        System.out.println ("*******************************************\n");                    
        manageCompute.printOrchestrations();
        System.out.println ("\n*******************************************");
        System.out.println ("Print Security Applications / Protocols");
        System.out.println ("*******************************************\n");                    
        manageCompute.printSecurityApplications();
        System.out.println ("\n*******************************************");
        System.out.println ("Print Security Rules");
        System.out.println ("*******************************************\n");                    
        manageCompute.printSecurityRules();
        dbcsNames = manageDBCS.getDBCSInstanceNames();
        System.out.println ("DBCS Instance Name = " + dbcsNames);      
        manageDBCS.getDBCSInstanceIPs();
        jcsNames = manageJCS.getJCSInstanceNames();
        System.out.println ("JCS Instance Name = " + jcsNames);                
        manageJCS.getJCSInstanceIPs();
        System.out.println ("\n*************************************************************");
        System.out.println ("Review of OPC Account " + this.getIdentityDomain() + " has completed...");
        System.out.println ("*************************************************************\n");                    
    }

    public Boolean verifyCleanAccount () {
        Boolean accountStatus = true;
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;
        
        containerNames = manageSC.getContainerNames();
        if (containerNames.size() != 0) {
            accountStatus = false;
        }
        dbcsNames = manageDBCS.getDBCSInstanceNames();
        if (dbcsNames.size() != 0) {
            accountStatus = false;
        }
        jcsNames = manageJCS.getJCSInstanceNames();
        if (jcsNames.size() != 0) {
            accountStatus = false;
        }
        return accountStatus;
    }
    
    public void cleanupAccount () {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;

        System.out.println ("\n*******************************************");
        System.out.println ("Cleanup of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");     
        this.manageJCS.deleteAllJCS();
        this.manageDBCS.deleteAllDBCS();
        this.manageSC.DeleteAllContainers();
        try {
            Thread.sleep(1000 * 10); // Sleep 10 seconds
        } catch (InterruptedException e) {
        }
        reviewAccount();
        System.out.println ("\n*************************************************************");
        System.out.println ("Cleanup of OPC Account " + this.getIdentityDomain() + " has completed...");
        System.out.println ("*************************************************************\n");                    

    }

    public void setupDBCSWorkshopAccount (String studentNumber) {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        Boolean accountClean;

        System.out.println ("\n*******************************************");
        System.out.println ("Setup of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");                    
        
        if (studentNumber.equals("01")) {
            accountClean = verifyCleanAccount();
        } else 
            accountClean = true;
        if (!accountClean) {
            System.out.println ("Unable to perform Setup as Account is not clean!!!!");            
        } else {
            containerNames = this.manageSC.opcWorkshopCreateContainers(studentNumber);
            System.out.println ("\nStorage Container Names = " + containerNames);
            this.manageDBCS.createDBCS(studentNumber);
            dbcsNames = this.manageDBCS.getDBCSInstanceNames();
            System.out.println ("DBCS Instance Name = " + dbcsNames);      
        }
        try {
            Thread.sleep(1000 * 10); // Sleep 10 seconds
        } catch (InterruptedException e) {
        }
        reviewAccount();
        System.out.println ("\n*************************************************************");
        System.out.println ("Setup of OPC Account " + this.getIdentityDomain() + " has completed...");
        System.out.println ("*************************************************************\n");                    
    }
    
    public void setupJCSWorkshopAccount (String studentNumber) {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;
        Boolean accountClean;

        System.out.println ("\n*******************************************");
        System.out.println ("Setup of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");                    
        
        if (studentNumber.equals("01")) {
            accountClean = verifyCleanAccount();
        } else 
            accountClean = true;
        if (!accountClean) {
            System.out.println ("Unable to perform Setup as Account is not clean!!!!");            
        } else {
            containerNames = this.manageSC.opcWorkshopCreateContainers(studentNumber);
            System.out.println ("\nStorage Container Names = " + containerNames);
            this.manageDBCS.createDBCS(studentNumber);
            dbcsNames = this.manageDBCS.getDBCSInstanceNames();
            System.out.println ("DBCS Instance Name = " + dbcsNames);      
            setupAlphaSchema ("Alpha" + studentNumber + "A-DBCS");
            this.manageJCS.createAlphaJCSDriver(studentNumber);
            jcsNames = this.manageJCS.getJCSInstanceNames();
            System.out.println ("\nJCS Instance Name = " + jcsNames);                
        }
        try {
            Thread.sleep(1000 * 10); // Sleep 10 seconds
        } catch (InterruptedException e) {
        }
        reviewAccount();
        System.out.println ("\n*************************************************************");
        System.out.println ("Setup of OPC Account " + this.getIdentityDomain() + " has completed...");
        System.out.println ("*************************************************************\n");                    
    }
    
    public void setupAlphaSchema (String dbcsName) {
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JSONObject dbcsInstance = null;
        String dbcsIP = null;

        try {
            dbcsInstance = this.manageDBCS.getDBCSInstanceInfo(dbcsName);
            dbcsIP = dbcsInstance.getString("em_url").substring(8);
            dbcsIP = dbcsIP.substring(0,dbcsIP.indexOf(":"));
            batchFile = new File(this.getConfigProperties().getProperty("scriptLocation") + "runOPCWorkshopDatabaseSetup.sh");
            procBuilder =
                    new ProcessBuilder(batchFile.getAbsolutePath(), dbcsIP);
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            }
            in.close();
        } catch (JSONException e) {
            e.printStackTrace();
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

    public void setComputeZone(String computeZone) {
        this.computeZone = computeZone;
    }

    public String getComputeZone() {
        return computeZone;
    }

    public void setIdentityDomain(String identityDomain) {
        this.identityDomain = identityDomain;
    }

    public String getIdentityDomain() {
        return identityDomain;
    }

    public void setManageSC(DeleteStorageContainer manageSC) {
        this.manageSC = manageSC;
    }

    public DeleteStorageContainer getManageSC() {
        return manageSC;
    }

    public void setManageDBCS(ManageDBCS manageDBCS) {
        this.manageDBCS = manageDBCS;
    }

    public ManageDBCS getManageDBCS() {
        return manageDBCS;
    }

    public void setManageJCS(ManageJCS manageJCS) {
        this.manageJCS = manageJCS;
    }

    public ManageJCS getManageJCS() {
        return manageJCS;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public void setManageCompute(ManageCompute manageCompute) {
        this.manageCompute = manageCompute;
    }

    public ManageCompute getManageCompute() {
        return manageCompute;
    }

    public static void main(String[] args) {
        String method;
        
        if (args.length < 5) {
            System.out.println("Usage: java ManageOPC username password identityDomain zone method\n");
        } else {            
            ManageOPC manageOPC = new ManageOPC ();
            manageOPC.setUsername(args[0]);
            manageOPC.setPassword(args[1]);
            manageOPC.setIdentityDomain(args[2]);
            manageOPC.setComputeZone(args[3]);
            manageOPC.initOPC();
            method = args[4];

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date startDate = new Date();
            System.out.println("Start Time = " + dateFormat.format(startDate));
             
            if (method.contains("ReviewAccount")) {
                manageOPC.reviewAccount();
            } else if (method.contains("CleanupAccount")) {
                manageOPC.cleanupAccount();
            } else if (method.contains("SetupJCSWorkshopAccount")) {
                manageOPC.setupJCSWorkshopAccount("01");
            } else if (method.contains("SetupJCSWorkshopOnsiteAccount")) {
                if (args.length < 6) {
                    System.out.println("Usage: java ManageOPC username password identityDomain zone method StudentNumber\n");
                    System.out.println("This method requires an additional parameter - StudentNumber\n");
                } else {                    
                    manageOPC.setupJCSWorkshopAccount(args[5]);
                }
            } else if (method.contains("SetupDBCSWorkshopAccount")) {
                manageOPC.setupDBCSWorkshopAccount("01");
            } else if (method.contains("SetupDBCSWorkshopOnsiteAccount")) {
                if (args.length < 6) {
                    System.out.println("Usage: java ManageOPC username password identityDomain zone method StudentNumber\n");
                    System.out.println("This method requires an additional parameter - StudentNumber\n");
                } else {                    
                    manageOPC.setupDBCSWorkshopAccount(args[5]);
                }
            } else if (method.contains("VerifyCleanAccount")) {
                manageOPC.verifyCleanAccount();
            } else if (method.contains("SetupAlphaSchema")) {
                manageOPC.setupAlphaSchema("AlphaDBCS");
            }
            Date endDate = new Date();
            System.out.println("End Time = " + dateFormat.format(endDate));
        }
    }
}
