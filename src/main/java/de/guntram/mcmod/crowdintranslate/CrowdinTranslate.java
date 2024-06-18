package de.guntram.mcmod.crowdintranslate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class CrowdinTranslate extends Thread {
    public static final String NAME = "crowdin-translate";
    public static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, String> mcCodetoCrowdinCode;
    /* The directory to download to. This is used in the mod; main will overwrite this.  */
    private static String rootDir = "ModTranslations";
    private static boolean thisIsAMod = true;
    private static final Set<String> registeredMods;
    enum Tristate { UNKNOWN,YES,NO };
    private static Tristate downloadsAllowed = Tristate.UNKNOWN;
    
    static {
        mcCodetoCrowdinCode = new HashMap<>();
        registeredMods = new HashSet<>();

        add("af_za", "af");
        add("ar_sa", "ar");
        add("ast_es", "ast");
        add("az_az", "az");
        add("ba_ru", "ba");
        //add("bar", "bar");			// Bavaria
        add("be_by", "be");
        add("bg_bg", "bg");
        add("br_fr", "br-FR");
        //add("brb", "brb");			// Brabantian
        add("bs_ba", "bs");
        add("ca_es", "ca");
        add("cs_cz", "cs");
        add("cy_gb", "cy");
        add("da_dk", "da");
        add("de_at", "de-AT,de");
        add("de_ch", "de-CH,de");
        add("de_de", "de");
        add("el_gr", "el");
        add("en_au", "en-AU,en-GB,en-US");
        add("en_ca", "en-CA,en-GB,en-US");
        add("en_gb", "en-GB,en-US");
        add("en_nz", "en-NZ,en-GB,en-US");
        add("en_pt", "en-PT,en-GB,en-US");
        add("en_ud", "en-UD,en-GB,en-US");
        add("en_us", "en-US");
        //add("enp", "enp");			// Anglish
        add("en_ws", "en-WS");
        add("en_7s", "en-PT");
        add("en_ud", "en-UD");
        add("eo_uy", "eo");
        add("es_ar", "es-AR,es-ES");
        add("es_cl", "es-CL,es-ES");
        add("es_ec", "es-EC,es-ES");
        add("es_es", "es-ES");
        add("es_mx", "es-MX,es-ES");
        add("es_uy", "es-UY,es-ES");
        add("es_ve", "es-VE,es-ES");
        //add("esan", "esan");			// Andalusian
        add("et_ee", "et");
        add("eu_es", "eu");
        add("fa_ir", "fa");
        add("fi_fi", "fi");
        add("fil_ph", "fil");
        add("fo_fo", "fo");
        add("fr_ca", "fr-CA,fr");
        add("fr_fr", "fr");
        add("fra_de", "fra-DE");
        add("fy_nl", "fy-NL");
        add("ga_ie", "ga-IE");
        add("gd_gb", "gd");
        add("gl_es", "gl");
        add("haw_us", "haw");
        add("he_il", "he");
        add("hi_in", "hi");
        add("hr_hr", "hr");
        add("hu_hu", "hu");
        add("hy_am", "hy-AM");
        add("id_id", "id");
        add("ig_ng", "ig");
        add("io_en", "ido");
        add("is_is", "is");
        //add("isv", "isv");			// Interslavic
        add("it_it", "it");
        add("ja_jp", "ja");
        add("jbo_en", "jbo");
        add("ka_ge", "ka");
        add("kk_kz", "kk");
        add("kn_in", "kn");
        add("ko_kr", "ko");
        //add("ksh", "ksh");			// Ripuarian
        add("kw_gb", "kw");
        add("la_la", "la-LA");
        add("lb_lu", "lb");
        add("li_li", "li");
        add("lol_us", "lol");
        add("lt_lt", "lt");
        add("lv_lv", "lv");
        //add("lzh", "lzh");			// Classical Chinese
        add("mi_NZ", "mi");
        add("mk_mk", "mk");
        add("mn_mn", "mn");
        add("ms_my", "ms");
        add("mt_mt", "mt");
        add("nds_de", "nds");
        add("nl_be", "nl-BE,nl");
        add("nl_nl", "nl");
        add("nn_no", "nn-NO,no");
        add("no_no", "no,nb");
        add("oc_fr", "oc");
        //add("ovd", "ovd");			// Elfdalian
        add("pl_pl", "pl");
        add("pt_br", "pt-BR,pt-PT");
        add("pt_pt", "pt-PT,pt-BR");
        add("qya_aa", "qya-AA");
        add("ro_ro", "ro");
        //add("rpr", "rpr");			// Russian (pre-revolutionary)
        add("ru_ru", "ru");
        add("se_no", "se");
        add("sk_sk", "sk");
        add("sl_si", "sl");
        add("so_so", "so");
        add("sq_al", "sq");
        add("sr_sp", "sr");
        add("sv_se", "sv-SE");
        //add("sxu", "sxu");			// Upper Saxon German
        //add("szl", "szl");			// Silesian
        add("ta_in", "ta");
        add("th_th", "th");
        add("tl_ph", "tl");
        add("tlh_aa", "tlh-AA");
        add("tr_tr", "tr");
        add("tt_ru", "tt-RU");
        add("uk_ua", "uk");
        add("val_es", "val-ES");
        add("vec_it", "vec");
        add("vi_vn", "vi");
        add("yi_de", "yi");
        add("yo_ng", "yo");
        add("zh_cn", "zh-CN,zh-HK");
        add("zh_hk", "zh-HK,zh-CN");
        add("zh_tw", "zh-TW");
    }
    
    private static void add(String mc, String ci) {
        mcCodetoCrowdinCode.put(mc, ci);
    }
    
    public static void downloadTranslations(String projectName) {
        downloadTranslations(projectName, projectName);
    }
        
    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName) {
        downloadTranslations(crowdinProjectName, minecraftProjectName, false);
    }
    
    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, boolean verbose) {
        downloadTranslations(crowdinProjectName, minecraftProjectName, null, verbose);
    }

    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, String sourceFileOverride) {
        downloadTranslations(crowdinProjectName, minecraftProjectName, sourceFileOverride, false);
    }

    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, String sourceFileOverride, boolean verbose) {
        
        registeredMods.add(minecraftProjectName);
        if (thisIsAMod && ( !downloadsAllowed() || projectDownloadedRecently(minecraftProjectName))) {
            return;
        }
        CrowdinTranslate runner = new CrowdinTranslate(crowdinProjectName, minecraftProjectName);
        if (verbose) {
            runner.setVerbose();
        }
        if (sourceFileOverride != null) {
            runner.setSourceFileOverride(sourceFileOverride);
        }
        runner.start();
        if (!thisIsAMod) {
            try {
                runner.join(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // This is a bit of a stretch, but the method gets called in fabric mod
    // context only. We don't want to add a dependency on a whole json library
    // or anything, so we just check if there's a config/crowdin.txt file.
    // If there is, and it contains "download=no" or "download=false" or "download=0",
    // we don't download anything.
    // This should be synchronized in case Fabric ever initializes two mods at
    // once; they shouldn't access the file at the same time.
    private static synchronized boolean downloadsAllowed() {
        if (downloadsAllowed != Tristate.UNKNOWN) {
            return downloadsAllowed == Tristate.YES;
        }
        File file = new File("config/crowdin.txt");
        if (file.exists()) {
            try (FileReader fr = new FileReader(file);
                 BufferedReader br = new BufferedReader(fr))
            {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("download=")) {
                        String val = line.substring(9);
                        if ("0".equals(val) || "false".equalsIgnoreCase(val) || "no".equalsIgnoreCase(val)) {
                            downloadsAllowed=Tristate.NO;
                            return false;
                        }
                    }
                }
            } catch (IOException ex) {
            }
        } else {
            try (FileWriter fw = new FileWriter(file)) {
                fw.append("#Change this to no to prevent mod translation downloads\ndownload=yes\n");
            } catch (IOException ex) {
            }
        }
        downloadsAllowed=Tristate.YES;
        return true;
    }

    private static void forceClose(Closeable c) {
        try {
            c.close();
        } catch (IOException ex) {
        }
    }
    
    public static Set<String> registeredMods() {
        return registeredMods;
    }
    
    public static String getRootDir() {
        return rootDir;
    }
    
    private String crowdinProjectName, minecraftProjectName;
    private Optional<String> sourceFileOverride = Optional.empty();
    private boolean verbose;
    
    private CrowdinTranslate(String crowdinProjectName, String minecraftProjectName) {
        this.crowdinProjectName = crowdinProjectName;
        this.minecraftProjectName = minecraftProjectName;
        verbose = false;
    }
    
    private void setVerbose() {
        verbose = true;
    }
    
    private void setSourceFileOverride(String name) {
        if (name == null) {
            sourceFileOverride = Optional.empty();
        } else if (name.toLowerCase().endsWith((".json"))) {
            sourceFileOverride = Optional.of(name);
        } else {
            sourceFileOverride = Optional.of(name+".json");
        }
    }
    
    @Override
    public void run() {
        Map<String, byte[]> translations;
        try {
            translations = getCrowdinTranslations(crowdinProjectName);
        } catch (IOException ex) {
            System.err.println("Exception when downloading translations");
            ex.printStackTrace(System.err);
            return;
        }
        
        String assetDir = rootDir+File.separatorChar+"assets"+File.separatorChar
                                +minecraftProjectName+File.separatorChar+"lang";
        new File(assetDir).mkdirs();

        for (Map.Entry<String, String> entry: mcCodetoCrowdinCode.entrySet()) {
            String[] sourcesByPreference = entry.getValue().split(",");
            for (String attemptingSource: sourcesByPreference) {
                byte[] buffer = translations.get(attemptingSource);
                if (buffer != null) {
                    String filePath = assetDir+File.separatorChar+entry.getKey()+".json";
                    if (verbose) {
                        System.out.println("writing "+buffer.length+" bytes from \""+attemptingSource+"\" to MC file "+filePath);
                    }
                    saveBufferToJsonFile(buffer, filePath);
                    break;
                }
            }
        }
        if (thisIsAMod) {
            markDownloadedNow(minecraftProjectName);
        }
    }
        
    private Map<String, byte[]> getCrowdinTranslations(String projectName) throws IOException {    
        ZipInputStream zis = null;
        Pattern pattern = Pattern.compile("^([a-z]{2}(-[A-Z]{2})?)/(.+\\.json)$");
        Map<String, byte[]> zipContents = new HashMap<>();

        try {
            URL url = new URL("https://crowdin.com/backend/download/project/"+projectName+".zip");
            if (verbose) {
                System.out.println("Trying to download "+url);
            }
            zis = new ZipInputStream(url.openStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String path = entry.getName();
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    String crowdinLang = matcher.group(1);
                    String origFileName = matcher.group(3);
                    if (sourceFileOverride.isPresent() && !sourceFileOverride.get().equals(origFileName)) {
                        if (verbose) {
                            System.out.println("Ignoring "+path+", we're looking for "+sourceFileOverride.get());
                        }
                        continue;
                    }
                    if (verbose) {
                        System.out.println("Found translation \""+crowdinLang+"\" for file "+origFileName);
                    }
                    if (entry.getSize() > 10_000_000) {
                        // This is mainly a guard against broken files that
                        // could exhaust our memory.
                        throw new IOException("file too large: "+entry.getName()+": "+entry.getSize());
                    }
                    byte[] zipFileContent = getZipStreamContent(zis, (int) entry.getSize());
                    if (zipContents.containsKey(crowdinLang)) {
                        System.err.println("More than one file for "+crowdinLang+", ignoring "+origFileName);
                        continue;
                    }
                    zipContents.put(matcher.group(1), zipFileContent);
                }
            }
        } catch (IOException ex) {
            if (zis != null) {
                forceClose(zis);
            }
            throw ex;
        }
        return zipContents;
    }
    
    private byte[] getZipStreamContent(InputStream is, int size) throws IOException {
        byte[] buf = new byte[size];
        int toRead = size;
        int totalRead = 0, readNow;
        
        while (toRead > 0) {
            if ((readNow = is.read(buf, totalRead, toRead)) <= 0) {
                throw new IOException("premature end of stream");
            };
            totalRead += readNow;
            toRead -= readNow;
        }
        return buf;
    }
    
    private void saveBufferToJsonFile(byte[] buffer, String filename) {
        
        File file = new File(filename);
        try (FileOutputStream stream = new FileOutputStream(filename)) {
            stream.write(buffer);
        } catch (IOException ex) {
            System.err.println("failed to write "+filename);
            System.err.println("absolute path is "+file.getAbsolutePath());
            ex.printStackTrace(System.err);
        }
    }
    
    private static boolean projectDownloadedRecently(String projectName) {
        File file =  new File(rootDir, projectName+".timestamp");
        if (file.exists() && file.lastModified() > System.currentTimeMillis() - 86400 * 3000) {
            return true;
        }
        return false;
    }
    
    private static void markDownloadedNow(String projectName) {
        File file =  new File(rootDir, projectName+".timestamp");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException ex) {
            // bad luck, we'll just check again next time.
        }
        file.setLastModified(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        boolean verbose = false;
        int startArg = 0;
        
        rootDir = "src/main/resources";
        thisIsAMod = false;
        if (args.length > 0 && args[0].equals("-v")) {
            verbose = true;
            ++startArg;
        }
        if (args.length == startArg+1) {
            downloadTranslations(args[startArg], args[startArg], verbose);
        } else if (args.length == startArg+2) {
            downloadTranslations(args[startArg], args[startArg+1], verbose);
        } else if (args.length == startArg+3) {
            downloadTranslations(args[startArg], args[startArg+1], args[startArg+2], verbose);
        }
        else {
            System.out.println("Usage: CrowdinTranslate [-v] crowdin_project_name [minecraft_project_name]");
            System.out.println("\t-v enables verbose logging");
            System.out.println("\tGet the translations from crowdin.com/project/name\n\tand write them to assets/project/lang");
            System.out.println("\tThe second parameter is only neccesary if the crowdin project name\n\tdoesn't match the minecraft project name");
        }
    }
}
