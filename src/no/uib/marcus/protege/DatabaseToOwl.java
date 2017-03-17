package no.uib.marcus.protege;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelAllTripleStoresWriter;
import no.uib.marcus.protege.util.DateUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;


/**
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-03-16
 *         <p>
 *         Code to export a protege 3 database backend programmatically.
 *         References:
 *         http://protegewiki.stanford.edu/wiki/LoadOWLOntologyFromDB (method 2)
 *         http://smi-protege.stanford.edu/svn/owl/trunk/src/edu/stanford/smi/protegex/owl/jena/export/JenaExportPlugin.java?view=markup
 */
public class DatabaseToOwl {

    //Sample parameter values
    private static String driver = "com.mysql.jdbc.Driver";
    private static String dbUrl = "jdbc:mysql://localhost:3306/protege";
    private static String dbTable = "protegeTable";
    private static String username = "hemed";
    private static String password = "hemed";
    private static String owlFileUrl = "file:/E:/Protege-3.5/db2owl/marcus_export_todaysdate.owl";
    private static String outputFilePath = "file:/E:/Protege-3.5/db2owl";
    private static String outputFileUrl;
    private static String[] databaseTables;
    private static final String OWL_EXT = ".owl";

    public static void main(String[] args) throws Exception {

        Log.getLogger().log(Level.INFO, "Given parameters: {0}", Arrays.toString(args));
        if (args.length < 6) {
            Log.getLogger().info("These parameters must exist: " +
                    "outputFilePath dbDriver dbUrl dbTable dbUser dbPassword");
            return;
        }

        //TODO: Validate these inputs
        outputFilePath = args[0];
        driver = args[1];
        dbUrl = args[2];
        //TODO: Pass a list of tables so that we can export more than one tables at once?
        dbTable = args[3];
        username = args[4];
        password = args[5];


        //Seperate string of tables
        if (dbTable.indexOf(',') != -1) {
            databaseTables = dbTable.split(Character.toString(','));
        } else {
            databaseTables[0] = dbTable;
        }


        //Export OWL file for each database table
        for (String table : databaseTables) {
            //TODO: Validation
            //Check if it ends with separator, it starts with file:// and ends with ".owl"
            outputFileUrl = outputFilePath +
                    "/" +
                    table +
                    DateUtil.getCurrentDate("yyyyMMddHHmmss") +
                    OWL_EXT;

            SystemUtilities.logSystemInfo();
            Log.getLogger().log(Level.INFO, "\n===== Starting the export from database on {0}", DateUtil.getCurrentDate());
            Log.getLogger().info("\n===== Reading configurations from convert.properties");
            Log.getLogger().log(Level.INFO, "=== OWL File Path: {0}", outputFilePath);
            Log.getLogger().log(Level.INFO, "=== Database URL: {0}", dbUrl);
            Log.getLogger().log(Level.INFO, "=== Database table: {0}\n", dbTable);

            URI fileURI = new URI(((outputFileUrl)));

            //URI fileURI = new URI("file://" + (new File(arg[0])).getAbsolutePath());

            //Properties properties = new Properties();
            //properties.load(new FileInputStream("MetadataExport.properties"));

            Collection<String> errors = new ArrayList<String>();
            //The "false" argument means that it won't overide the existing table
            //IMPORTANT otherwise will destroy existing database.
            OwlDatabaseCreator creator = new OwlDatabaseCreator(false);
            creator.setDriver(driver);
            creator.setURL(dbUrl);
            creator.setUsername(username);
            creator.setPassword(password);
            creator.setTable(table);
            creator.create(errors);

            if (errors.size() > 0) {
                displayErrors(errors);
            }

            OWLModel owlModel = creator.getOwlModel();
            try {
                OWLModelAllTripleStoresWriter owlWriter = new OWLModelAllTripleStoresWriter(owlModel, fileURI, true);
                owlWriter.write();
            } catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Failed to save file to the path [{0}]", fileURI);
                throw ex;
            }

        }

    }

    /**
     * Display error if exist
     *
     * @param errors error to display
     */
    public static void displayErrors(Collection errors) {
        Iterator i = errors.iterator();
        while (i.hasNext()) {
            Object elem = i.next();
            if (elem instanceof Throwable) {
                Log.getLogger().log(Level.WARNING, "Warnings at loading changes project", (Throwable) elem);
            } else if (elem instanceof MessageError) {
                Log.getLogger().log(Level.WARNING, ((MessageError) elem).getMessage(), ((MessageError) elem).getException());
            } else {
                Log.getLogger().warning(elem.toString());
            }
        }
    }
}