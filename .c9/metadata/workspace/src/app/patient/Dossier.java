{"changed":false,"filter":false,"title":"Dossier.java","tooltip":"/src/app/patient/Dossier.java","value":"package app.patient;\n\n\nimport java.io.File;\nimport java.sql.ResultSet;\nimport java.sql.SQLException;\nimport java.util.ArrayList;\n\nimport app.dataAcess.OperationData;\nimport app.user.Utilisateur;\n\n\n \n\npublic class Dossier\n{\n    \n    protected String numDossier;\n    private String numHopital;\n    private AdmData data;\n    private ArrayList<Exam> examens;\n    private ArrayList<Rapport> rapports;\n    \n    \n    public Dossier () { \n        numDossier = null;\n        numHopital = null;\n        data = new AdmData();\n        examens = new ArrayList<Exam>();\n        rapports = new ArrayList<Rapport>();\n    }\n    \n    \n    \n    public boolean load(String numDossier) throws SQLException\n    {\n    \tResultSet rs = (ResultSet) OperationData.lireEnBase(\"select * from Dossier where numDossier=\" + numDossier);\n    \trs.next();\n    \tif ((numHopital = rs.getString(\"NumHopital\")) == null) \n    \t{\n    \t    return false;\n    \t}\n    \tthis.numDossier = numDossier;\n\n    \t\n    \tif ((data.load(numDossier)) == false) \n    \t{\n    \t    return false;\n    \t}\n    \t\n    \texamens.clear();\n    \trapports.clear();\n\t\t\n\t\trs = (ResultSet) OperationData.lireEnBase(\"select numElement from Examen where numElement in ( select numElement from Element where numDossier=\" + numDossier + \" )\");\n    \twhile(rs != null && rs.next())\n\t\t{\n\t\t\texamens.add(new Exam(rs.getString(\"numElement\")));\t\n\t\t}\n\t\t\n\t\trs = (ResultSet) OperationData.lireEnBase(\"select numElement from RapportMedicaux where numElement in ( select numElement from Element where numDossier=\" + numDossier + \" )\");\n    \twhile(rs.next())\n\t\t{\n\t\t\trapports.add(new Rapport(numDossier, rs.getString(\"numElement\")));\t\n\t\t}\n    \t\n    \t\n        return true;\n    }\n    \n    public boolean reload() throws SQLException\n    {\n    \treturn load(numDossier);\n    }\n    \n    \n    public boolean loadFiles() \n    {\n\t\tfor (Exam e : examens) \n\t\t{\n\t\t\tif (!(e.loadFile()))\n\t\t\t{\n\t\t\t\treturn false;\n\t\t\t}\n\t\t}\n        return true;\n    }\n    \n\t\n\tpublic boolean saveDos() throws SQLException \n\t{\n\t\t\n    \tif (!OperationData.sauverEnBase(\"update Dossier set NumHopital=\"+numHopital+\" where numDossier=\" + numDossier))\n    \t{\n\t\t\treturn false;\n\t\t}\n\t\t\n\t\t\n    \tif (!OperationData.sauverEnBase(\"UPDATE DonneeAdministrative set NomDonne='\"+data.getNom()+\"', PrenomDonne='\"+data.getPrenom()+\"', DateNaissance='\"+data.getDateNaiss()+\"', PersonneAcontacter='\"+data.getAContacter()+\"', CodePostal='\"+data.getCodePostal()+\"', Ville='\"+data.getVille()+\"', Adresse='\"+data.getAdresse()+\"', Pays='\"+data.getPays()+\"', Civilite='\"+data.getCiv()+\"', LieuNaissance='\"+data.getLieuNaiss()+\"', NumTel='\"+data.getNum()+\"', Mail='\"+data.getMail()+\"' WHERE numDossier=\"+numDossier))\n    \t{\n    \t    return false;\n    \t}\n        \n        for (Rapport r : rapports) \n        {\n            r.save();\n        }\n        \n        for (Exam e : examens) \n        {\n            e.save();\n        }\n    \t\n        return true;\n\t}\n    \n    \n    public boolean clear() throws SQLException\n    {\n    \tnumDossier = null;\n        numHopital = null;\n    \t\n    \tdata.clear();\n    \t\n    \texamens.clear();\n    \trapports.clear();\n    \n        return true;\n    }\n    \n    \n    public boolean setDataAdm(AdmData a) \n    {\n    \tdata = a;\n    \n        return true;\n    }\n    \n    \n    public boolean setHopital(String nh) {\n\t\tnumHopital =nh;\n\t\treturn true;\n\t}\n    \n    \n    public boolean addExam (Exam e, File f) \n    {\n    \texamens.add(e);\n    \n        return true;\n    }\n    \n    public boolean addRapport (Rapport e, File f) \n    {\n    \trapports.add(e);\n    \n        return true;\n    }\n    \n    \n    \n    \n    public String getNumDossier()\n    {\n        return numDossier;\n    }\n    \n    public String getNumHopital()\n    {\n        return numHopital;\n    }\n    \n    public AdmData getData()\n    {\n        return data;\n    }\n    \n    public boolean isAssignedTo(Utilisateur utilisateur)\n    {\n    \ttry\n    \t{\n    \t\tResultSet rs = (ResultSet) OperationData.lireEnBase(\"select 1 from Soigner where numDossier=\" + numDossier + \" and id=\" + utilisateur.getId());\n        \t\n        \tif (rs.next())\n        \t{\n        \t\treturn true;\n        \t}\n    \t}\n    \tcatch (SQLException e)\n    \t{}\n    \treturn false;\n    }\n    \n    public Element getExamen(String numElem)\n    {\n\t\tfor (Exam e : examens) {\n\t\t\tif (e.getNumElement() == numElem) {\n\t\t\t\treturn e;\n\t\t\t}\n\t\t}\n        return null;\n    }\n    \n    public ArrayList<Exam> getExamens()\n    {\n        return examens;\n    }\n    \n    public Rapport[] getRapports()\n    {\n        return rapports.toArray(new Rapport[rapports.size()]);\n    }\n    \n    \n    public static Dossier[] loadAll()\n    {\n    \tArrayList<Dossier> listeDossiers = new ArrayList<Dossier>();\n    \tString numDossier = \"null\";\n    \t\n    \tResultSet rs;\n    \tif (!Utilisateur.getDefaultUser().getRole().equals(\"Admin\"))\n    \t{\n    \t\trs = (ResultSet) OperationData.lireEnBase(\"select numDossier from Dossier natural join Soigner s where s.id = \" + Utilisateur.getDefaultUser().getId());\n    \t}\n    \telse\n    \t{\n    \t\trs = (ResultSet) OperationData.lireEnBase(\"select numDossier from Dossier;\");\n    \t}\n    \t\n    \ttry\n    \t{\n    \t\twhile(rs != null && rs.next())\n    \t\t{\n        \t\tDossier dossier = new Dossier();\n        \t\tnumDossier = rs.getString(\"numDossier\");\n    \t\t\tdossier.load(rs.getString(\"numDossier\"));\n    \t\t\tlisteDossiers.add(dossier);\n    \t\t}\n    \t}\n    \tcatch (SQLException e)\n    \t{\n    \t\te.printStackTrace();\n    \t}\n    \treturn listeDossiers.toArray(new Dossier[listeDossiers.size()]);\n    }\n    \n    \n    public boolean create() throws SQLException\n    {\n    \t//Recherche de tout les logins possibles\n    \tResultSet rs;\n    \trs = (ResultSet) OperationData.lireEnBase(\"select numDossier from Dossier where numDossier='\"+data.getNumDossier()+\"';\");\n    \t\n    \t//Recherche si un login existe déja\n    \tif (rs.next())\n    \t{\n    \t\tthrow new IllegalStateException(\"the medical records's  ID is already taken\");\n    \t}\n    \t\n    \t/*\n    \t * Creation dans la base de donnée de l'utilisateur\n    \t */\n    \t\n    \tif (!OperationData.sauverEnBase(\"INSERT INTO DonneeAdministrative (numDossier, NomDonne, PrenomDonne, DateNaissance, PersonneAcontacter, CodePostal, Ville, Adresse, Pays, Civilite, LieuNaissance, NumTel, Mail) VALUES ('\"+data.getNumDossier()+\"','\"+ data.getNom()+\"','\"+data.getPrenom()+\"','\"+ data.getDateNaiss()+\"','\"+data.getAContacter()+\"','\"+data.getCodePostal()+\"','\"+data.getVille()+\"','\"+ data.getAdresse()+\"','\"+data.getPays()+\"','\"+ data.getCiv()+\"','\"+data.getLieuNaiss()+\"','\"+data.getNum()+\"','\"+data.getMail() +\"');\"))\n    \t{\n    \t    return false;\n    \t}\n    \t\n    \tif (!OperationData.sauverEnBase(\"INSERT INTO Dossier (numDossier, numHopital) VALUES ('\"+data.getNumDossier()+\"','\"+ numHopital +\"');\")) \n    \t{\n    \t    return false;\n    \t}\n        \n        for (Rapport r : rapports) \n        {\n            r.save();\n        }\n        \n        for (Exam e : examens) \n        {\n            e.save();\n        }\n    \t\n    \t\n    \t\n    \treturn true;\n    }\n    \n}\n","undoManager":{"mark":-1,"position":-1,"stack":[]},"ace":{"folds":[],"scrolltop":0,"scrollleft":0,"selection":{"start":{"row":64,"column":5},"end":{"row":64,"column":5},"isBackwards":false},"options":{"guessTabSize":true,"useWrapMode":false,"wrapToView":true},"firstLineState":0},"timestamp":1491133350000}